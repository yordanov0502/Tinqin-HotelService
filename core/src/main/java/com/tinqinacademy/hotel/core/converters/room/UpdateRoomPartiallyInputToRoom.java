package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UpdateRoomPartiallyInputToRoom extends BaseConverter<UpdateRoomPartiallyInput, Room, UpdateRoomPartiallyInputToRoom> {


    public UpdateRoomPartiallyInputToRoom() {
        super(UpdateRoomPartiallyInputToRoom.class);
    }

    @Override
    protected Room convertObj(UpdateRoomPartiallyInput input) {
        Room room = Room.builder()
                .id(UUID.fromString(input.getRoomId()))
                .roomNumber(input.getRoomNo())
                .floor(input.getFloor())
                .price(input.getPrice())
                .bathroomType(input.getBathroomType() == null
                        ? null
                        :BathroomType.getByCode(input.getBathroomType().toString()))
                .build();

        return room;
    }
}
