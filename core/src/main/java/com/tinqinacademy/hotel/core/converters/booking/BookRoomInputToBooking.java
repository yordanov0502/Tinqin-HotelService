package com.tinqinacademy.hotel.core.converters.booking;

import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookRoomInputToBooking extends BaseConverter<BookRoomInput, Booking.BookingBuilder, BookRoomInputToBooking> {

    public BookRoomInputToBooking() {
        super(BookRoomInputToBooking.class);
    }

    @Override
    protected Booking.BookingBuilder convertObj(BookRoomInput input) {

        Booking.BookingBuilder bookingBuilder = Booking.builder()
                .startDate(input.getStartDate())
                .endDate(input.getEndDate());

        return bookingBuilder;
    }

}