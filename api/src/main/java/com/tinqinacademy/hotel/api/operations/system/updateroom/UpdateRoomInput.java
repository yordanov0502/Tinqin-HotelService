package com.tinqinacademy.hotel.api.operations.system.updateroom;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomInput {

    @JsonIgnore
    private String roomId;
    @Positive
    @NotNull
    private Integer bedCount;
    @NotNull
    private BedSize bedSize;
    @NotNull
    private BathroomType bathroomType;
    @Positive
    @NotNull
    private Integer floor;
    @NotBlank
    private String roomNo;
    @Positive
    @NotNull
    private BigDecimal price;
}
