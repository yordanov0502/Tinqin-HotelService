package com.tinqinacademy.hotel.api.operations.hotel.getroom;


import com.tinqinacademy.hotel.api.base.OperationOutput;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.content.BookedInterval;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfoOutput implements OperationOutput {

    private String roomId;
    private BigDecimal price;
    private Integer floor;
    private BedSize bedSize;
    private BathroomType bathroomType;
    private Integer bedCount;
    private List<BookedInterval> datesOccupied;
}
