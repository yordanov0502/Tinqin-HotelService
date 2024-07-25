package com.tinqinacademy.hotel.api.operations.system.createroom;

import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomOutput {
    private String roomId;
}
