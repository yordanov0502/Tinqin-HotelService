package com.tinqinacademy.hotel.core.services;


import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.services.HotelService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookedRoomException;
import com.tinqinacademy.hotel.core.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.core.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.entity.User;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class HotelServiceImpl implements HotelService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final ConversionService conversionService;


    @Override
    public UnbookRoomOutput unbookRoom(UnbookRoomInput input) {

        log.info("Start unbookRoom input:{}",input);

        Booking booking = findBookingById(input.getBookingId());

        bookingRepository.delete(booking);

        UnbookRoomOutput output = UnbookRoomOutput.builder().build();

        log.info("End unbookRoom output:{}",output);

        return output;
    }





    private Booking findBookingById(String bookingId) {

        log.info("Start findBookingById input:{}", bookingId);

        Booking booking = bookingRepository
                .findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new NotFoundException("Booking with id["+bookingId+"] doesn't exist."));

        log.info("End findBookingById input:{}", booking.toString());

        return booking;
    }

}
