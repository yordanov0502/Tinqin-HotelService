package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnbookRoomInput implements OperationInput {
    @NotNull
    private String bookingId;
}
