package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomToUpdateRoomPartiallyOutput extends BaseConverter<Room, UpdateRoomPartiallyOutput, RoomToUpdateRoomPartiallyOutput> {

    public RoomToUpdateRoomPartiallyOutput() {
        super(RoomToUpdateRoomPartiallyOutput.class);
    }

    @Override
    protected UpdateRoomPartiallyOutput convertObj(Room room) {
        UpdateRoomPartiallyOutput output = UpdateRoomPartiallyOutput.builder()
                .roomId(room.getId().toString())
                .build();

        return output;
    }
}
