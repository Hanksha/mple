package com.hanksha.mple.controller

import com.hanksha.mple.data.model.CreateLevelRequest
import com.hanksha.mple.data.model.LevelMeta
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
        String level = levelManager.getLevelAsString(projectName, name, version)
        HttpHeaders headers = new HttpHeaders()
        headers.add(HttpHeaders.CONTENT_TYPE, 'application/json')
        new ResponseEntity(level, headers, HttpStatus.OK)
    }

    @RequestMapping(value = '', method = RequestMethod.GET)
    ResponseEntity listLevels(@PathVariable String projectName) {
        List<LevelMeta> levels = levelManager.listLevelForProject(projectName)
        new ResponseEntity(levels, HttpStatus.OK)
    }

    @DeleteMapping('/{name}')
    ResponseEntity deleteLevel(@PathVariable String projectName, @PathVariable String name) {
        levelManager.deleteLevel(projectName, name)
        ResponseEntity.ok(JsonOutput.toJson('Level deleted'))
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity createLevel(@PathVariable String projectName, @Valid @RequestBody CreateLevelRequest content) {
        levelManager.createLevel(
            projectName, content.name, content.tileset,
            content.width, content.height, content.tileWidth, content.tileHeight)
        ResponseEntity.ok(JsonOutput.toJson('Level created'))
    }

    @PostMapping('/import')
    ResponseEntity importLevel(@PathVariable String projectName, @RequestBody String levelSrc) {
        levelManager.importLevel(projectName, levelSrc)
        new ResponseEntity(JsonOutput.toJson('Level imported'), HttpStatus.OK)
    }

}
