package com.tinqinacademy.hotel.api.operations.system.deleteroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoomInput implements OperationInput {
    @NotBlank
    @UUID
    private String roomId;
}
