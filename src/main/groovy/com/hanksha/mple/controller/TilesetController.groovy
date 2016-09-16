package com.hanksha.mple.controller

import com.hanksha.mple.data.TilesetRepository
import com.hanksha.mple.data.model.Tileset
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Created by vivien on 8/28/16.
 */
@RestController
@RequestMapping('/api/tilesets')
class TilesetController {

    public static final String ROOT_FOLDER = 'storage/tilesets'

    @Autowired
    ResourceLoader resourceLoader

    @Autowired
    TilesetRepository tilesetRepo

    @PostConstruct
    void init() {
        if(!Files.exists(Paths.get(ROOT_FOLDER)))
            Files.createDirectory(Paths.get(ROOT_FOLDER))
    }

    @GetMapping('')
    ResponseEntity listTileset() {
        List<Tileset> tilesetList = tilesetRepo.findAll()

        new ResponseEntity(tilesetList, HttpStatus.OK)
    }

    @RequestMapping(value = '/{name}', method = RequestMethod.GET)
    ResponseEntity tileset(@PathVariable String name) {
        Tileset tileset = tilesetRepo.findOne(name)

        String imgSrc = resourceLoader.getResource('file:' + Paths.get(ROOT_FOLDER, tileset.fileName).toString()).file.bytes.encodeBase64()

        new ResponseEntity([tileset: tileset, img: imgSrc], HttpStatus.OK)
    }

    @RequestMapping(value = '', method = RequestMethod.POST)
    ResponseEntity uploadFile(@RequestParam MultipartFile file) {
        if(!file.isEmpty()) {
            Files.copy(file.getInputStream(), Paths.get(ROOT_FOLDER, file.getOriginalFilename()));
            new ResponseEntity(HttpStatus.OK)
        }
        else
            new ResponseEntity(HttpStatus.BAD_REQUEST)
    }

}
