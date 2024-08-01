package com.tinqinacademy.hotel.api.operations.system.updateroompartially;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomPartiallyOutput implements OperationOutput {
    private String roomId;
}
