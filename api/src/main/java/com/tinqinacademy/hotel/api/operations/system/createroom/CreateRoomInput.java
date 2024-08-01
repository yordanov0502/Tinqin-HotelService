package com.tinqinacademy.hotel.api.operations.system.createroom;


import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.validation.bathroomtype.BathroomTypeCode;
import com.tinqinacademy.hotel.api.validation.bedsize.BedSizeCode;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreateRoomInput implements OperationInput {

    @Min(value = 1, message = "bedCount should be at least 1.")
    @Max(value = 10, message = "bedCount should be maximum 10.")
    private Integer bedCount;
    @NotNull(message = "bedSize must not be null.")
    @BedSizeCode
    private BedSize bedSize;
    @NotNull(message = "bedSize must not be null.")
    @BathroomTypeCode
    private BathroomType bathroomType;
    @Positive(message = "floor must be positive.")
    private Integer floor;
    @NotBlank(message = "roomNo must not be blank.")
    private String roomNo;
    @Positive(message = "price must be positive number.")
    private BigDecimal price;
}
