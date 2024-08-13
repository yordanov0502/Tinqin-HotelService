package com.tinqinacademy.hotel.api.operations.hotel.unbookroom.getuseridofbooking;

import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetUserIdOfBookingInput implements OperationInput {
    @NotNull
    @UUID
    private String bookingId;
}
