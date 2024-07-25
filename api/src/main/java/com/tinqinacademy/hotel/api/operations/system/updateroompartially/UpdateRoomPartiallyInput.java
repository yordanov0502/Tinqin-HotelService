package com.tinqinacademy.hotel.api.operations.system.updateroompartially;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomPartiallyInput {

    @JsonIgnore
    private String roomId;
    @Positive
    @Min(value = 1)
    @Max(value = 10)
    private Integer bedCount;
    private BedSize bedSize;
    private BathroomType bathroomType;
    @Positive
    @Min(value = 1)
    @Max(value = 30)
    private Integer floor;
    private String roomNo;
    @Positive
    private BigDecimal price;
}
