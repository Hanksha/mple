package com.hanksha.mple.controller

import com.hanksha.mple.exception.*
import groovy.json.JsonOutput
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlingController {

    @ExceptionHandler([
        CommitNotFoundException,
        LevelNotFoundException,
        ProjectNotFoundException,
        RoomNotFoundException,
        TilesetNotFoundException
    ])
    ResponseEntity handleNotFoundException(Exception ex) {
        new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler([
        LevelAlreadyExistsException,
        ProjectAlreadyExistsException,
        TilesetAlreadyExistsException
    ])
    ResponseEntity handleAlreadyExistException(Exception ex) {
        new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.CONFLICT)
    }

    @ExceptionHandler([
        ForbiddenRoomAccessException,
        ProjectPermissionDeniedException
    ])
    ResponseEntity handlePermissionException(Exception ex) {
        new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(ConcurrentLevelModificationException)
    ResponseEntity handleConcurrentLevelModificationException(Exception ex) {
        new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.BAD_REQUEST)
    }
}
