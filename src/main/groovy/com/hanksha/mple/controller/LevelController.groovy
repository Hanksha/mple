package com.hanksha.mple.controller

import com.hanksha.mple.data.model.CreateLevelRequestContent
import com.hanksha.mple.data.model.LevelMeta
import com.hanksha.mple.exception.LevelAlreadyExistsException
import com.hanksha.mple.exception.ProjectNotFoundException
import com.hanksha.mple.service.LevelManager
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController
@RequestMapping('/api/projects/{projectName}/levels')
class LevelController {

    @Autowired
    LevelManager levelManager

    @RequestMapping(value = '/{id}', method = RequestMethod.GET)
    ResponseEntity getLevel(@PathVariable int id) {

        File file = levelManager.getLevel(id, "")

        HttpHeaders headers = new HttpHeaders()
        headers.add(HttpHeaders.CONTENT_TYPE, 'application/json')

        if(file)
            new ResponseEntity(file.text, headers, HttpStatus.OK)
        else
            new ResponseEntity("Could not find level file with id '$id'",HttpStatus.NOT_FOUND)
    }

    @RequestMapping(value = '', method = RequestMethod.GET)
    ResponseEntity listLevels(@PathVariable String projectName) {
        try {
            List<LevelMeta> levels = levelManager.listLevelForProject(projectName)
            return new ResponseEntity(levels, HttpStatus.OK)
        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity createLevel(@PathVariable String projectName, @Valid @RequestBody CreateLevelRequestContent content) {
        try {
             levelManager.createLevel(
                    projectName, content.name, content.tileset,
                     content.width, content.height, content.tileWidth, content.tileHeight)

        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        } catch(LevelAlreadyExistsException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.CONFLICT)
        }

        ResponseEntity.ok(JsonOutput.toJson('Level created'))
    }

}
