package com.tinqinacademy.hotel.api.operations.system.deleteroom;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoomInput {
    @NotBlank
    private String roomId;
}
