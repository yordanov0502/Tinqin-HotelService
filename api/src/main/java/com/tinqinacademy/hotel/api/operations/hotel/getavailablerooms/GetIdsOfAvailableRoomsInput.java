package com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms;


import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.validation.bathroomtype.BathroomTypeCode;
import com.tinqinacademy.hotel.api.validation.bedsize.BedSizeCode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetIdsOfAvailableRoomsInput implements OperationInput {

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @Min(value = 1, message = "bedCount should be at least 1.")
    @Max(value = 10, message = "bedCount should be maximum 10.")
    private Integer bedCount;
    @BedSizeCode
    private BedSize bedSize;
    @BathroomTypeCode
    private BathroomType bathroomType;
}
