package com.tinqinacademy.hotel.api.operations.internal.getroomid;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomIdOutput implements OperationOutput {
    private String roomId;
}