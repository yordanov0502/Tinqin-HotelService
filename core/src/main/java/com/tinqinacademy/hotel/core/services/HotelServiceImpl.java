package com.tinqinacademy.hotel.core.services;


import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.content.BookedInterval;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.services.HotelService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookedRoomException;
import com.tinqinacademy.hotel.core.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.core.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.entity.User;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
    public RoomInfoOutput getRoomInfo(RoomInfoInput input) {

        log.info("Start getRoomInfo input:{}",input);

        Room room = findRoomById(input.getRoomId());

        List<BookedInterval> roomBookedIntervals = bookingRepository
                .findAllBookedIntervalsByRoomId(room.getId())
                .stream()
                .map(b -> new BookedInterval(b.getStartDate(),b.getEndDate()))
                .toList();

        RoomInfoOutput output = conversionService.convert(room,RoomInfoOutput.RoomInfoOutputBuilder.class)
                .datesOccupied(roomBookedIntervals)
                .build();

        log.info("End getRoomInfo output:{}",output);

        return output;
    }


    @Override
    public BookRoomOutput bookRoom(BookRoomInput input){

        log.info("Start bookRoom input:{}",input);

        validateDates(input.getStartDate(),input.getEndDate());

        Room room = findRoomById(input.getRoomId());

        User user = findUserByFirstAndLastNameAndPhone(input.getFirstName(),input.getLastName(),input.getPhoneNumber());

        checkRoomAvailability(room.getId(),input.getStartDate(),input.getEndDate());

        long days = input.getEndDate().toEpochDay() - input.getStartDate().toEpochDay();
        BigDecimal priceOfBooking = room.getPrice().multiply(BigDecimal.valueOf(days));

        Booking newBooking = conversionService.convert(input,Booking.BookingBuilder.class)
                .totalPrice(priceOfBooking)
                .room(room)
                .user(user)
                .build();
        bookingRepository.save(newBooking);

        BookRoomOutput output = BookRoomOutput.builder().build();

        log.info("End bookRoom output:{}",output);

        return output;
    }

    @Override
    public UnbookRoomOutput unbookRoom(UnbookRoomInput input) {

        log.info("Start unbookRoom input:{}",input);

        Booking booking = findBookingById(input.getBookingId());

        bookingRepository.delete(booking);

        UnbookRoomOutput output = UnbookRoomOutput.builder().build();

        log.info("End unbookRoom output:{}",output);

        return output;
    }

    private Room findRoomById(String roomId) {

        log.info("Start findRoomById input:{}", roomId);

        Room room = roomRepository
                .findById(UUID.fromString(roomId))
                .orElseThrow(() -> new NotFoundException("Room with id[" + roomId + "] doesn't exist."));

        log.info("End findRoomById output:{}", room.toString());

        return room;
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {

        log.info("Start validateDates input:{},{}", startDate, endDate);

        if(startDate.isEqual(endDate)) {
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if(startDate.isAfter(endDate)) {
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        log.info("End validateDates.");
    }

    private void checkRoomAvailability(UUID roomId, LocalDate startDate, LocalDate endDate) {

        log.info("Start checkRoomAvailability input:{},{},{}", roomId, startDate, endDate);

        if(bookingRepository.isRoomBooked(roomId,startDate,endDate)){
            throw new BookedRoomException("Room with id["+roomId+"] is not available for ["+startDate+"/"+endDate+"].");
        }

        log.info("End checkRoomAvailability.");
    }

    private User findUserByFirstAndLastNameAndPhone(String firstName, String lastName, String phoneNumber) {

        log.info("Start findUserByFirstAndLastNameAndPhone input:{},{},{}", firstName, lastName, phoneNumber);

        User user = userRepository
                .findByFirstNameAndLastNameAndPhoneNumber(
                        firstName,
                        lastName,
                        phoneNumber)
                .orElseThrow(() -> new NotFoundException("User with name: " +
                        firstName +" "+
                        lastName + " and phone number: "+
                        phoneNumber + " doesn't exist."));

        log.info("End findUserByFirstAndLastNameAndPhone output:{}", user.toString());

        return user;
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
