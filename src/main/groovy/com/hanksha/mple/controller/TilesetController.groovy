package com.hanksha.mple.controller

import com.hanksha.mple.data.TilesetRepository
import com.hanksha.mple.data.model.Tileset
import com.hanksha.mple.exception.TilesetAlreadyExistsException
import com.hanksha.mple.exception.TilesetNotFoundException
import com.hanksha.mple.service.TilesetManager
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import javax.validation.Valid

@RestController
@RequestMapping('/api/tilesets')
class TilesetController {

    @Autowired
    TilesetManager tilesetManager

    @Autowired
    TilesetRepository tilesetRepo

    @GetMapping('')
    ResponseEntity listTilesets() {
        List<Tileset> tilesetList = tilesetRepo.findAll()

        new ResponseEntity(tilesetList, HttpStatus.OK)
    }

    @GetMapping('/{name}')
    ResponseEntity getTileset(@PathVariable String name) {
        try {
            Tileset tileset = tilesetManager.getTileset(name)
            new ResponseEntity(tileset, HttpStatus.OK)
        } catch(TilesetNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping('')
    ResponseEntity createTileset(@Valid @RequestBody Tileset tileset) {
        try {
            tilesetManager.createTileset(tileset)
        } catch(TilesetAlreadyExistsException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.CONFLICT)
        }

        new ResponseEntity(JsonOutput.toJson('Tileset created'), HttpStatus.OK)
    }

    @DeleteMapping('/{name}')
    ResponseEntity deleteTileset(@PathVariable String name) {
        try {
            tilesetManager.deleteTileset(name)
        } catch(TilesetNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }

        new ResponseEntity(JsonOutput.toJson('Tileset deleted'), HttpStatus.OK)
    }

}
