package com.hanksha.mple.controller

import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.Room
import com.hanksha.mple.data.model.message.editor.LevelOperation
import com.hanksha.mple.data.model.request.CommitRequest
import com.hanksha.mple.data.model.request.CreateRoomRequest
import com.hanksha.mple.service.RoomManager
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
import org.springframework.web.bind.annotation.*

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
        Room room = roomManager.getRoom(id)
        new ResponseEntity(room, HttpStatus.OK)
    }

    @GetMapping('/{id}/level')
    ResponseEntity getRoomLevel(@PathVariable int id) {
        Level level = roomManager.getRoom(id).level
        new ResponseEntity(level, HttpStatus.OK)
    }

    @PostMapping('')
    ResponseEntity createRoom(@Valid @RequestBody CreateRoomRequest request, Principal user) {
        Room room = roomManager.createRoom(
                request.name, request.projectName, request.levelName, request.levelVersion, user.name)
        new ResponseEntity(room, HttpStatus.OK)
    }

    @DeleteMapping('/{id}')
    ResponseEntity deleteRoom(@PathVariable int id) {
        roomManager.destroyRoom(id)
        new ResponseEntity(HttpStatus.OK)
    }

    @PostMapping('/connect/{id}')
    ResponseEntity connect(@PathVariable int id, Principal user) {
        roomManager.connectToRoom(id, user.name)
        new ResponseEntity(HttpStatus.OK)
    }

    @PostMapping('/disconnect/{id}')
    ResponseEntity disconnect(@PathVariable int id, Principal user) {
        roomManager.disconnectFromRoom(id, user.name)
        new ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping('/commit/{id}')
    ResponseEntity commitRoom(@PathVariable int id, @RequestBody CommitRequest commitRequest, Principal user) {
        roomManager.commitRoom(id, commitRequest.message, user.name)
        new ResponseEntity(HttpStatus.OK)
    }

    @MessageMapping('/editor/{roomId}')
    void handleMessage(@DestinationVariable int roomId, Message<LevelOperation> message, Principal user) {
        roomManager.handleOperation(roomId, message.payload, user.name)
    }
}
