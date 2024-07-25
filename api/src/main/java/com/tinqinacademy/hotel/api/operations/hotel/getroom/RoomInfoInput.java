package com.tinqinacademy.hotel.api.operations.hotel.getroom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoInput {
    @NotBlank
    private String roomId;
}
