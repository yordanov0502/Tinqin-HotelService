package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoomToUpdateRoomOutput extends BaseConverter<Room, UpdateRoomOutput, RoomToUpdateRoomOutput> {

    public RoomToUpdateRoomOutput() {
        super(RoomToUpdateRoomOutput.class);
    }

    @Override
    protected UpdateRoomOutput convertObj(Room room) {

        UpdateRoomOutput output = UpdateRoomOutput.builder()
                .roomId(room.getId().toString())
                .build();

        return output;
    }
}
