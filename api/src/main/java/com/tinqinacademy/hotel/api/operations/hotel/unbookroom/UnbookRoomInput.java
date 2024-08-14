package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnbookRoomInput implements OperationInput {
    @JsonIgnore
    @NotBlank
    @UUID
    private String bookingId;
    @NotBlank
    @UUID
    private String userId;
}
