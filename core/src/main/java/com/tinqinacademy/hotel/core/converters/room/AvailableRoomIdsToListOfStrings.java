package com.tinqinacademy.hotel.core.converters.room;

import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AvailableRoomIdsToListOfStrings extends BaseConverter<List<UUID>, AvailableRoomsIdsOutput,AvailableRoomIdsToListOfStrings> {
    public AvailableRoomIdsToListOfStrings() {
        super(AvailableRoomIdsToListOfStrings.class);
    }

    @Override
    protected AvailableRoomsIdsOutput convertObj(List<UUID> listOfAvailableRoomIds) {

        AvailableRoomsIdsOutput output = AvailableRoomsIdsOutput.builder()
                .availableRoomsIds(listOfAvailableRoomIds
                        .stream()
                        .map(UUID::toString)
                        .toList()).build();

        return output;
    }
}