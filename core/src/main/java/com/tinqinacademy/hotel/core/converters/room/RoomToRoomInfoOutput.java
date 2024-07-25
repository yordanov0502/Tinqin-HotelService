package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomToRoomInfoOutput extends BaseConverter<Room, RoomInfoOutput.RoomInfoOutputBuilder,RoomToRoomInfoOutput> {
    public RoomToRoomInfoOutput() {
        super(RoomToRoomInfoOutput.class);
    }

    @Override
    protected RoomInfoOutput.RoomInfoOutputBuilder convertObj(Room room) {

        RoomInfoOutput.RoomInfoOutputBuilder output = RoomInfoOutput.builder()
                .roomId(room.getId().toString())
                .price(room.getPrice())
                .floor(room.getFloor())
                .bedSize(BedSize.getByCode(room.getBeds().getFirst().getBedSize().getCode()))
                .bathroomType(BathroomType.getByCode(room.getBathroomType().toString()))
                .bedCount(room.getBeds().size());

        return output;
    }
}
