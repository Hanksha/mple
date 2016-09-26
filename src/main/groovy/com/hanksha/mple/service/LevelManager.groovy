package com.hanksha.mple.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hanksha.mple.data.LevelRepository
import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.LevelMeta
import com.hanksha.mple.data.model.Project
import com.hanksha.mple.exception.CommitNotFoundException
import com.hanksha.mple.exception.LevelAlreadyExistsException
import com.hanksha.mple.exception.LevelNotFoundException
import com.hanksha.mple.exception.ProjectNotFoundException
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class LevelManager {

    @Autowired
    LevelRepository levelRepo

    @Autowired
    ProjectRepository projectRepo

    @Autowired
    ObjectMapper objectMapper

    String getLevelAsString(String projectName, String levelName, String version) {

        Project project = projectRepo.findOne(projectName)

        if(!project)
            throw new ProjectNotFoundException(projectName)

        LevelMeta levelMeta = levelRepo.findOne(project.id, levelName)

        if(!levelMeta)
            throw new LevelNotFoundException(projectName, levelName)

        File folder = FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), project.name)

        String levelText = ''

        if(version && !version.equalsIgnoreCase('latest')) {

            Repository repository = Git.open(folder).repository

            ObjectId commitId = repository.resolve(version);

            RevWalk revWalk = new RevWalk(repository)

            RevCommit commit = revWalk.parseCommit(commitId);
            RevTree tree = commit.getTree()

            TreeWalk treeWalk = new TreeWalk(repository)
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(levelMeta.name + '.json'));

            if (!treeWalk.next()) {
                throw new CommitNotFoundException(projectName, version)
            }

            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);

            ByteArrayOutputStream out = new ByteArrayOutputStream()
            loader.copyTo(out)

            levelText = out.toString()

            revWalk.dispose();
        }
        else
            levelText = FileUtils.getFile(folder, levelMeta.name + '.json').text

        levelText
    }

    List<LevelMeta> listLevelForProject(String projectName) {
        Project project = projectRepo.findOne(projectName)

        if(!project)
            throw new ProjectNotFoundException(projectName)

        List<LevelMeta> levels = levelRepo.findAll().findAll {it.projectId == project.id}

        levels
    }

    void createLevel(String projectName, String levelName,
                     String tileset, int width, int height,
                     int tileWidth, int tileHeight) {

        Project project = projectRepo.findOne(projectName)

        if(!project)
            throw new ProjectNotFoundException(projectName)

        if(levelRepo.findOne(project.id, levelName))
            throw new LevelAlreadyExistsException(projectName, levelName)

        Level level = new Level(levelName, tileset, width, height, tileWidth, tileHeight)

        levelRepo.save(new LevelMeta(projectId: project.id, name: levelName, dateCreated: new Date()))

        File folder = FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), projectName)
        Git git = Git.open(folder)

        objectMapper.writeValue(FileUtils.getFile(folder, level.name + '.json'), level)

        String author = SecurityContextHolder.context.authentication.name

        // stage level file
        git.add().addFilepattern(level.name + '.json').call()
        // commit file
        git.commit().setAuthor(author, '').setMessage("Added level $levelName").call()
        git.close()
    }

    void importLevel(String projectName, String levelSrc) {
        Project project = projectRepo.findOne(projectName)

        if(!project)
            throw new ProjectNotFoundException(projectName)

        Level level = objectMapper.readValue(levelSrc.decodeBase64(), Level)

        if(levelRepo.findOne(project.id, level.name))
            throw new LevelAlreadyExistsException(projectName, level.name)

        levelRepo.save(new LevelMeta(projectId: project.id, name: level.name, dateCreated: new Date()))

        File folder = FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), projectName)

        objectMapper.writeValue(FileUtils.getFile(folder, level.name + '.json'), level)

        Git git = Git.open(folder)

        String author = SecurityContextHolder.context.authentication.name

        // stage level file
        git.add().addFilepattern(level.name + '.json').call()
        // commit file
        git.commit().setAuthor(author, '').setMessage("Added level ${level.name}").call()
        git.close()
    }

    void deleteLevel(String projectName, String levelName) {
        Project project = projectRepo.findOne(projectName)

        if(!project)
            throw new ProjectNotFoundException(projectName)

        LevelMeta level = levelRepo.findOne(project.id, levelName)

        if(!level)
            throw new LevelNotFoundException(projectName, levelName)

        // delete from database
        levelRepo.delete(level.id)

        // delete from folder
        File folder = FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), projectName)
        File file = FileUtils.getFile(folder, levelName + '.json')
        file.delete()

        // commit
        Git git = Git.open(folder)
        String author = SecurityContextHolder.context.authentication.name
        git.rm().addFilepattern(levelName + '.json').call()
        git.commit().setAuthor(author, '').setMessage("Deleted ${levelName}.json").call()
        git.close()
    }

    void commit(String projectName, String commitMessage, String author, Level level) {

        if(!projectRepo.findOne(projectName))
            throw new ProjectNotFoundException(projectName)

        File folder = FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), projectName)
        Git git = Git.open(folder)

        objectMapper.writeValue(FileUtils.getFile(folder, level.name + '.json'), level)

        // stage level file
        git.add().addFilepattern(level.name + '.json').call()

        // commit changes
        git.commit().setAuthor(author, '').setMessage(commitMessage).call()

        git.close()
    }

}
