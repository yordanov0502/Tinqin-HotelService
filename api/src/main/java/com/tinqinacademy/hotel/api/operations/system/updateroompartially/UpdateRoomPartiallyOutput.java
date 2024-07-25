package com.tinqinacademy.hotel.api.operations.system.updateroompartially;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomPartiallyOutput {
    private String roomId;
}
