package com.tinqinacademy.hotel.api.operations.system.createroom;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomOutput implements OperationOutput {
    private String roomId;
}
