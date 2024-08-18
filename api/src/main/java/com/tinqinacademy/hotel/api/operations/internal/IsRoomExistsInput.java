package com.tinqinacademy.hotel.api.operations.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class IsRoomExistsInput implements OperationInput {
    @JsonIgnore
    @NotBlank
    @UUID
    private String roomId;
}
