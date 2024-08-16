package com.tinqinacademy.hotel.api.operations.system.updateroompartially;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.hotel.api.base.OperationInput;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.validation.bathroomtype.BathroomTypeCode;
import com.tinqinacademy.hotel.api.validation.bedsize.BedSizeCode;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.UUID;

import java.math.BigDecimal;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRoomPartiallyInput implements OperationInput {

    @JsonIgnore
    @UUID
    private String roomId;
    @Min(value = 1)
    @Max(value = 10)
    private Integer bedCount;
    @BedSizeCode
    private BedSize bedSize;
    @BathroomTypeCode
    private BathroomType bathroomType;
    @Min(value = 1)
    @Max(value = 30)
    private Integer floor;
    private String roomNo;
    @Positive
    private BigDecimal price;
}
