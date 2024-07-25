package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToCreateRoomOutput extends BaseConverter<Room, CreateRoomOutput, RoomToCreateRoomOutput> {

    public RoomToCreateRoomOutput() {
        super(RoomToCreateRoomOutput.class);
    }

    @Override
    protected CreateRoomOutput convertObj(Room room) {

        CreateRoomOutput createRoomOutput = CreateRoomOutput.builder()
                .roomId(room.getId().toString())
                .build();

        log.info("End RoomToCreateRoomOutput converter output:{}", createRoomOutput);

        return createRoomOutput;
    }
}
