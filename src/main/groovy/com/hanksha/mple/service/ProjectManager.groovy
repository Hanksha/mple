package com.hanksha.mple.service

import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.UserRoleRepository
import com.hanksha.mple.data.model.Commit
import com.hanksha.mple.data.model.Project
import com.hanksha.mple.exception.ProjectAlreadyExistsException
import com.hanksha.mple.exception.ProjectNotFoundException
import com.hanksha.mple.exception.ProjectPermissionDeniedException
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class ProjectManager {

    public static final String ROOT_PROJECT_FOLDER = 'storage/projects'

    @Autowired
    ResourceLoader resourceLoader

    @Autowired
    ProjectRepository projectRepo

    @Autowired
    UserRoleRepository userRoleRepo

    @PostConstruct
    void init() {
        if(!Files.exists(Paths.get(ROOT_PROJECT_FOLDER)))
            Files.createDirectory(Paths.get(ROOT_PROJECT_FOLDER))
    }

    Project getProject(String name) {
        Project project = projectRepo.findOne(name)

        if(!project)
            throw new ProjectNotFoundException(name)

        project.commits = []

        // get git repo
        Git git = Git.open(FileUtils.getFile(new File(ROOT_PROJECT_FOLDER), name))

        // get all the commits
        git.log().all().call().forEach({RevCommit rev ->

            Commit commit = new Commit(
                    name: rev.getName().substring(0, 8),
                    author: rev.getAuthorIdent().getName(),
                    message: rev.getFullMessage(),
                    time: rev.getCommitterIdent().getWhen(),
                    changedFileNames: []
            )

            project.commits << commit

            ObjectReader reader = git.getRepository().newObjectReader()


            // commit tree
            CanonicalTreeParser newTree = new CanonicalTreeParser()
            ObjectId newObjectId = git.getRepository().resolve(rev.getName() + '^{tree}')
            newTree.reset(reader, newObjectId)

            // previous commit tree
            CanonicalTreeParser oldTree = new CanonicalTreeParser()
            ObjectId oldObjectId = git.getRepository().resolve(rev.getName() + '^^{tree}')
            oldTree.reset(reader, oldObjectId?:newObjectId)

            // get files affected by the commit
            List<DiffEntry> diffs = git.diff().setNewTree(newTree).setOldTree(oldTree).call()

            String changeType
            String filePath

            for(diff in diffs) {
                changeType = diff.changeType.toString()
                filePath = diff.getNewPath() == '/dev/null'?diff.getOldPath():diff.getNewPath()
                commit.changedFileNames << changeType  + ' ' + filePath
            }
        })

        project
    }

    void createProject(String name) {

        // checks if already
        if(projectRepo.findOne(name))
            throw new ProjectAlreadyExistsException(name)

        String owner = SecurityContextHolder.context.authentication.name

        // add to database
        projectRepo.save(new Project(name: name, dateCreated: new Date(), owner: owner))

        Path path = Paths.get(ROOT_PROJECT_FOLDER, name)

        // create folder
        FileUtils.deleteDirectory(new File(path.toString()))
        Files.createDirectory(path)

        // create git repository
        Git.init().setDirectory(FileUtils.getFile(new File(ROOT_PROJECT_FOLDER), name)).call().close()

        Git git = Git.open(FileUtils.getFile(new File(ROOT_PROJECT_FOLDER), name))

        git.add().addFilepattern('.').call()
        git.commit().setAuthor(owner, '').setMessage('Initial commit').call()
        git.close()
    }

    void deleteProject(String name) {

        Project project = projectRepo.findOne(name)

        if(!project)
            throw new ProjectNotFoundException(name)

        // verify permission
        String username = SecurityContextHolder.context.authentication.name
        List<String> roles = userRoleRepo.findRoles(username)

        // only an admin or project owner can delete a project
        if(project.owner != username && !roles.contains('ROLE_ADMIN'))
            throw new ProjectPermissionDeniedException(name)

        // delete project from database
        projectRepo.delete(name)

        // delete project's folder (recursively)
        FileUtils.deleteDirectory(FileUtils.getFile(new File(ROOT_PROJECT_FOLDER), name))
    }

}
