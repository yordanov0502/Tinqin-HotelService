package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import org.springframework.stereotype.Component;

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
