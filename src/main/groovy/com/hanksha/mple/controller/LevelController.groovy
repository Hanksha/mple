package com.hanksha.mple.controller

import com.hanksha.mple.data.model.CreateLevelRequest
import com.hanksha.mple.data.model.LevelMeta
import com.hanksha.mple.exception.LevelAlreadyExistsException
import com.hanksha.mple.exception.LevelNotFoundException
import com.hanksha.mple.exception.ProjectNotFoundException
import com.hanksha.mple.service.LevelManager
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import javax.validation.Valid

@RestController
@RequestMapping('/api/projects/{projectName}/levels')
class LevelController {

    @Autowired
    LevelManager levelManager

    @RequestMapping(value = '/{name}', method = RequestMethod.GET)
    ResponseEntity getLevel(@PathVariable String projectName, @PathVariable String name, @RequestParam String version) {
        try {
            String level = levelManager.getLevelAsString(projectName, name, version)
            HttpHeaders headers = new HttpHeaders()
            headers.add(HttpHeaders.CONTENT_TYPE, 'application/json')
            return new ResponseEntity(level, headers, HttpStatus.OK)

        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        } catch(LevelNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @RequestMapping(value = '', method = RequestMethod.GET)
    ResponseEntity listLevels(@PathVariable String projectName) {
        try {
            List<LevelMeta> levels = levelManager.listLevelForProject(projectName)
            return new ResponseEntity(levels, HttpStatus.OK)
        } catch(ProjectNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping('/{name}')
    ResponseEntity deleteLevel(@PathVariable String projectName, @PathVariable String name) {
        try {
            levelManager.deleteLevel(projectName, name)
        } catch(LevelNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }

        ResponseEntity.ok(JsonOutput.toJson('Level deleted'))
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity createLevel(@PathVariable String projectName, @Valid @RequestBody CreateLevelRequest content) {
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

    @PostMapping('/import')
    ResponseEntity importLevel(@PathVariable String projectName, @RequestBody String levelSrc) {
        try {
            levelManager.importLevel(projectName, levelSrc)
        } catch (ProjectNotFoundException ex){
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        } catch (LevelAlreadyExistsException ex){
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.CONFLICT)
        } catch (Exception ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.BAD_REQUEST)
        }

        return new ResponseEntity(JsonOutput.toJson('Level imported'), HttpStatus.OK)
    }

}
