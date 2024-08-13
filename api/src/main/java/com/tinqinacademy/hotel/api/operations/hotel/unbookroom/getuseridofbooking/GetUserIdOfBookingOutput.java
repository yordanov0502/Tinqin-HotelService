package com.tinqinacademy.hotel.api.operations.hotel.unbookroom.getuseridofbooking;

import com.tinqinacademy.hotel.api.base.OperationOutput;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetUserIdOfBookingOutput implements OperationOutput {
    private String userId;
}
