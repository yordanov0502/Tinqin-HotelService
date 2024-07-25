package com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms;


import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
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
public class GetIdsOfAvailableRoomsInput {

    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @Positive
    private Integer bedCount;
    @NotNull
    private BedSize bedSize;
    @NotNull
    private BathroomType bathroomType;
}
