package com.hanksha.mple.service

import com.hanksha.mple.data.TilesetRepository
import com.hanksha.mple.data.model.Tileset
import com.hanksha.mple.exception.TilesetAlreadyExistsException
import com.hanksha.mple.exception.TilesetNotFoundException
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct
import java.nio.file.Files
import java.nio.file.Paths

@Service
class TilesetManager {

    public static final String ROOT_TILESET_FOLDER = 'storage/tilesets'

    @Autowired
    TilesetRepository tilesetRepo

    @PostConstruct
    void init() {
        if(!Files.exists(Paths.get(ROOT_TILESET_FOLDER)))
            Files.createDirectory(Paths.get(ROOT_TILESET_FOLDER))
    }

    Tileset getTileset(String name) {
        Tileset tileset = tilesetRepo.findOne(name)

        if(!tileset)
            throw new TilesetNotFoundException(name)

        File file = FileUtils.getFile(new File(ROOT_TILESET_FOLDER), tileset.name + '.png')

        tileset.imgSrc = file.bytes.encodeBase64()

        tileset
    }

    void createTileset(Tileset tileset) {

        if(tilesetRepo.findOne(tileset.name))
            throw new TilesetAlreadyExistsException(tileset.name)

        tileset.dateCreated = new Date()

        tilesetRepo.save(tileset)

        File file = FileUtils.getFile(new File(ROOT_TILESET_FOLDER), tileset.name + '.png')
        file.bytes = tileset.imgSrc.decodeBase64()
    }

    void deleteTileset(String name) {
        Tileset tileset = tilesetRepo.findOne(name)

        if(!tileset)
            throw new TilesetNotFoundException(name)

        tilesetRepo.delete(tileset.id)

        Files.delete(Paths.get(ROOT_TILESET_FOLDER, name + '.png'))
    }

}
