package com.hanksha.mple.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.hanksha.mple.data.model.Level
import com.hanksha.mple.data.model.Room
import com.hanksha.mple.data.model.message.editor.LevelOperation
import com.hanksha.mple.exception.ConcurrentLevelModificationException
import com.hanksha.mple.exception.ForbiddenRoomAccessException
import com.hanksha.mple.exception.RoomNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct


@Service
class RoomManager {

    @Autowired
    ObjectMapper objectMapper

    @Autowired
    LevelManager levelManager

    @Autowired
    SimpMessagingTemplate messaging

    List<Room> rooms

    int currentIndex

    @PostConstruct
    void init() {
        rooms = []
        currentIndex = 1
    }

    Room getRoom(int id) {
        Room room = rooms.find {it.id == id}

        if(!room)
            throw new RoomNotFoundException(id)

        room
    }

    Level getRoomLevel(int id, String username) {
        Room room = rooms.find {it.id == id}

        if(!room)
            throw new RoomNotFoundException(id)

        if(!room.users.any {it == username})
            throw new ForbiddenRoomAccessException(id)

        room.level
    }

    Room createRoom(String roomName, String projectName, String levelName, String levelVersion, String username) {
        Room currentRoom = rooms.find {it.level.name == levelName}

        if(currentRoom)
            throw new ConcurrentLevelModificationException(projectName, levelName, currentRoom.name)

        Room room = new Room(name: roomName, projectName: projectName)

        room.id = currentIndex++

        room.users = [username]

        room.level = objectMapper.readValue(levelManager.getLevelAsString(projectName, levelName, levelVersion), Level)

        rooms << room

        room
    }

    void destroyRoom(int roomId) {
        rooms.removeAll {it.id = roomId}

        if(!rooms)
            currentIndex = 1
    }

    void connectToRoom(int roomId, String username) {
        Room room = rooms.find {it.id = roomId}

        if(!room)
            throw new RoomNotFoundException(roomId)

        if(!room.users.any {it == username})
        room.users << username
    }

    void disconnectFromRoom(int roomId, String username) {
        Room room = rooms.find {it.id = roomId}

        if(!room)
            throw new RoomNotFoundException(roomId)

        room.users.removeAll {it == username}

        if(!room.users)
            destroyRoom(room.id)
    }

    void commitRoom(int roomId, String commitMessage, String username) {
        Room room = rooms.find {it.id = roomId}

        if(!room)
            throw new RoomNotFoundException(roomId)

        String authors = room.users.join(', ')

        levelManager.commit(room.projectName, commitMessage, authors, room.level)
    }

    void handleOperation(int roomId, LevelOperation operation, String username) {
        Room room = rooms.find {it.id = roomId}

        if(!room)
            throw new RoomNotFoundException(roomId)

        if(!room.users.any {it == username})
            return

        operation.modify(room.level)

//        println room.level.dump()

        messaging.convertAndSend('/topic/editor/' + roomId,
                [type: operation.class.simpleName, operation: operation])
    }
}
