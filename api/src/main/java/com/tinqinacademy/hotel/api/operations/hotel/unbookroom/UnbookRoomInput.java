package com.tinqinacademy.hotel.api.operations.hotel.unbookroom;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UnbookRoomInput {
    @NotNull
    private String bookingId;
}
