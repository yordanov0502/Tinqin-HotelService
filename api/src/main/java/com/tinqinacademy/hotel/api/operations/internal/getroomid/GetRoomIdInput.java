package com.tinqinacademy.hotel.api.operations.internal.getroomid;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetRoomIdInput implements OperationInput {
    @JsonIgnore
    @NotBlank
    private String roomNumber;
}
