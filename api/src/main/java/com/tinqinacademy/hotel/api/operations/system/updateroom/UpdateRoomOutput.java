package com.tinqinacademy.hotel.api.operations.system.updateroom;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomOutput {
    private String roomId;
}
