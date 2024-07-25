package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import org.springframework.stereotype.Component;

@Component
public class CreateRoomInputToRoom extends BaseConverter<CreateRoomInput, Room.RoomBuilder, CreateRoomInputToRoom> {

    public CreateRoomInputToRoom() {
        super(CreateRoomInputToRoom.class);
    }

    @Override
    protected Room.RoomBuilder convertObj(CreateRoomInput input) {

        Room.RoomBuilder roomBuilder = Room.builder()
                .roomNo(input.getRoomNo())
                .floor(input.getFloor())
                .price(input.getPrice())
                .bathroomType(BathroomType.getByCode(input.getBathroomType().toString()));

        return roomBuilder;
    }
}
