package com.tinqinacademy.hotel.api.operations.hotel.getroom;

import lombok.*;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookedInterval {
    private LocalDate startDate;
    private LocalDate endDate;
}
