package com.hanksha.mple.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hanksha.mple.data.LevelRepository
import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.LevelMeta
import com.hanksha.mple.data.model.Project
import com.hanksha.mple.exception.LevelAlreadyExistsException
import com.hanksha.mple.exception.LevelNotFoundException
import com.hanksha.mple.exception.ProjectNotFoundException
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
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

    String getLevelAsString(int id, String version) {
        LevelMeta levelMeta = levelRepo.findOne(id)

        if(!levelMeta)
            throw new LevelNotFoundException(id)

        Project project = projectRepo.findOne(levelMeta.projectId)

        FileUtils.getFile(new File(ProjectManager.ROOT_PROJECT_FOLDER), project.name, levelMeta.name + '.json').text
    }

    List<LevelMeta> listLevelForProject(String projectName) {
        int projectId = projectRepo.findOne(projectName)?.id

        if(projectId == 0)
            throw new ProjectNotFoundException(projectName)

        List<LevelMeta> levels = levelRepo.findAll().findAll {it.projectId = projectId}

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

        String author = SecurityContextHolder.getContext().getAuthentication().getName()

        // stage level file
        git.add().addFilepattern(level.name + '.json').call()
        // commit file
        git.commit().setAuthor(author, '').setMessage("Added level $levelName").call()
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
