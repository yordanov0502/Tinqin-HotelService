package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateRoomInputToRoom extends BaseConverter<UpdateRoomInput, Room.RoomBuilder, UpdateRoomInputToRoom> {

    public UpdateRoomInputToRoom() {
        super(UpdateRoomInputToRoom.class);
    }

    @Override
    protected Room.RoomBuilder convertObj(UpdateRoomInput input) {

        Room.RoomBuilder roomBuilder = Room.builder()
                .id(UUID.fromString(input.getRoomId()))
                .roomNumber(input.getRoomNo())
                .floor(input.getFloor())
                .price(input.getPrice())
                .bathroomType(BathroomType.getByCode(input.getBathroomType().toString()));

        return roomBuilder;
    }
}
