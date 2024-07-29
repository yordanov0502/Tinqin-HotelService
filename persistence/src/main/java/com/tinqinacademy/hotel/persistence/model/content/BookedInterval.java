package com.tinqinacademy.hotel.persistence.model.content;

import lombok.*;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookedInterval {
    private LocalDate startDate;
    private LocalDate endDate;

    public BookedInterval(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
