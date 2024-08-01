package com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AvailableRoomsIdsOutput implements OperationOutput {

    private List<String> availableRoomsIds;
}
