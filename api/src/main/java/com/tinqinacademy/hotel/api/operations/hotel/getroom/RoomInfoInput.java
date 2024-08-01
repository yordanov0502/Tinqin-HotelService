package com.tinqinacademy.hotel.api.operations.hotel.getroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoInput implements OperationInput {
    @NotBlank
    private String roomId;
}
