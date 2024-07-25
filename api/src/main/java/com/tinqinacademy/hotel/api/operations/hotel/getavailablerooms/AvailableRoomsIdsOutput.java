package com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms;

import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AvailableRoomsIdsOutput {

    private List<String> availableRoomsIds;
}
