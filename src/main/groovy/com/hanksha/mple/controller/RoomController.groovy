package com.hanksha.mple.controller

import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.Room
import com.hanksha.mple.data.model.message.editor.LevelOperation
import com.hanksha.mple.data.model.request.CommitRequest
import com.hanksha.mple.data.model.request.CreateRoomRequest
import com.hanksha.mple.exception.ForbiddenRoomAccessException
import com.hanksha.mple.exception.RoomNotFoundException
import com.hanksha.mple.service.RoomManager
import groovy.json.JsonOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.messaging.Message
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

import javax.validation.Valid
import java.security.Principal

@Controller
@RequestMapping('/api/rooms')
class RoomController {

    static Logger logger = LoggerFactory.getLogger(RoomController)

    @Autowired
    SimpMessagingTemplate messaging

    @Autowired
    RoomManager roomManager

    @GetMapping('')
    ResponseEntity listRooms() {
        ResponseEntity.ok(roomManager.getRooms())
    }

    @GetMapping('/{id}')
    ResponseEntity getRoom(@PathVariable int id) {
        try {
            Room room = roomManager.getRoom(id)

            new ResponseEntity(room, HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @GetMapping('/{id}/level')
    ResponseEntity getRoomLevel(@PathVariable int id) {
        try {
            Level level = roomManager.getRoom(id).level
            new ResponseEntity(level, HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        } catch(ForbiddenRoomAccessException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.FORBIDDEN)
        }
    }

    @PostMapping('')
    ResponseEntity createRoom(@Valid @RequestBody CreateRoomRequest request, Principal user) {
        try {
            Room room = roomManager.createRoom(
                    request.name, request.projectName, request.levelName, request.levelVersion, user.name)
            new ResponseEntity(room, HttpStatus.OK)
        } catch(Exception ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping('/{id}')
    ResponseEntity deleteRoom(@PathVariable int id) {
        try {
            roomManager.destroyRoom(id)
            new ResponseEntity(HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping('/connect/{id}')
    ResponseEntity connect(@PathVariable int id, Principal user) {
        try {
            roomManager.connectToRoom(id, user.name)
            new ResponseEntity(HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @PostMapping('/disconnect/{id}')
    ResponseEntity disconnect(@PathVariable int id, Principal user) {
        try {
            roomManager.disconnectFromRoom(id, user.name)
            new ResponseEntity(HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @PatchMapping('/commit/{id}')
    ResponseEntity commitRoom(@PathVariable int id, @RequestBody CommitRequest commitRequest, Principal user) {
        try {
            roomManager.commitRoom(id, commitRequest.message, user.name)
            new ResponseEntity(HttpStatus.OK)
        } catch(RoomNotFoundException ex) {
            return new ResponseEntity(JsonOutput.toJson(ex.message), HttpStatus.NOT_FOUND)
        }
    }

    @MessageMapping('/editor/{roomId}')
    void handleMessage(@DestinationVariable int roomId, Message<LevelOperation> message, Principal user) {
        logger.info("Received message for $roomId from ${user.name}")
        roomManager.handleOperation(roomId, message.payload, user.name)
    }
}
