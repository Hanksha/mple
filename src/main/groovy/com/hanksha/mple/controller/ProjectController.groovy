package com.hanksha.mple.controller

import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.model.Project
import com.hanksha.mple.service.ProjectManager
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping('/api/projects')
class ProjectController {

    static Logger logger = LoggerFactory.getLogger(ProjectController)

    @Autowired
    ProjectManager projectManager

    @Autowired
    ProjectRepository projectRepo

    @RequestMapping(value = '', method = RequestMethod.GET)
    ResponseEntity listProjects() {
        new ResponseEntity(projectRepo.findAll(), HttpStatus.OK)
    }

    @RequestMapping(value = '/{name}', method = RequestMethod.GET)
    ResponseEntity getProject(@PathVariable String name) {
        Project project = projectManager.getProject(name)
        new ResponseEntity(project, HttpStatus.OK)
    }

    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity createProject(@RequestBody String name) {
        projectManager.createProject(name)

        ResponseEntity.ok(JsonOutput.toJson('Project created'))
    }

    @RequestMapping(value = '/{name}', method = RequestMethod.DELETE)
    ResponseEntity deleteProject(@PathVariable String name) {
        projectManager.deleteProject(name)
        ResponseEntity.ok(JsonOutput.toJson('Project deleted'))
    }

    @RequestMapping(value = '/{name}/revert/{commit}', method = RequestMethod.PATCH)
    ResponseEntity revertCommit(@PathVariable String name, @PathVariable String commit) {
        projectManager.revertCommit(name, commit)
        new ResponseEntity(HttpStatus.OK)
    }
}
