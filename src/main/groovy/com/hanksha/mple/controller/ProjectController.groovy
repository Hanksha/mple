package com.hanksha.mple.controller

import com.hanksha.mple.data.ProjectRepository
import com.hanksha.mple.data.model.Project
import com.hanksha.mple.exception.ProjectAlreadyExistsException
import com.hanksha.mple.exception.ProjectNotFoundException
import com.hanksha.mple.service.ProjectManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import java.security.Principal

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
        try {
            Project project = projectManager.getProject(name)

            return new ResponseEntity(project, HttpStatus.OK)
        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity createProject(@RequestBody String name, Principal principal) {

        try {
            projectManager.createProject(name, principal.getName())
        } catch(ProjectAlreadyExistsException ex) {
            return new ResponseEntity(ex.message, HttpStatus.CONFLICT)
        }

        ResponseEntity.ok('Project created')
    }

    @RequestMapping(value = '/{name}', method = RequestMethod.DELETE)
    ResponseEntity deleteProject(@PathVariable String name) {

        try {
            projectManager.deleteProject(name)
        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(ex.message, HttpStatus.NOT_FOUND)
        }

        ResponseEntity.ok('Project deleted')
    }

}
