package com.hanksha.mple.controller

import com.hanksha.mple.data.TilesetRepository
import com.hanksha.mple.data.model.Tileset
import com.hanksha.mple.service.TilesetManager
import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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
        Tileset tileset = tilesetManager.getTileset(name)
        new ResponseEntity(tileset, HttpStatus.OK)
    }

    @PostMapping('')
    ResponseEntity createTileset(@Valid @RequestBody Tileset tileset) {
        tilesetManager.createTileset(tileset)
        new ResponseEntity(JsonOutput.toJson('Tileset created'), HttpStatus.OK)
    }

    @DeleteMapping('/{name}')
    ResponseEntity deleteTileset(@PathVariable String name) {
        tilesetManager.deleteTileset(name)

        new ResponseEntity(JsonOutput.toJson('Tileset deleted'), HttpStatus.OK)
    }

}
