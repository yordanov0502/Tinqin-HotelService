package com.tinqinacademy.hotel.core.services;


import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.BookedInterval;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.services.HotelService;
import com.tinqinacademy.hotel.core.exception.exceptions.BookedRoomException;
import com.tinqinacademy.hotel.core.exception.exceptions.BookingDatesException;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
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
    public AvailableRoomsIdsOutput getIdsOfAvailableRooms(GetIdsOfAvailableRoomsInput input) {

        log.info("Start getIdsOfAvailableRoomsInput input:{}",input);

        List<UUID> availableRoomIds = bookingRepository
                .findAvailableRooms(
                        input.getStartDate(),
                        input.getEndDate(),
                        input.getBedCount(),
                        input.getBedSize() != null ? BedSize.getByCode(input.getBedSize().toString()) : null,
                        input.getBathroomType() != null ? BathroomType.getByCode(input.getBathroomType().toString()) : null);

        AvailableRoomsIdsOutput output = conversionService.convert(availableRoomIds, AvailableRoomsIdsOutput.class);

        log.info("End getIdsOfAvailableRooms output:{}",output);

        return output;
    }

    @Override
    public RoomInfoOutput getRoomInfo(RoomInfoInput input) {

        log.info("Start getRoomInfo input:{}",input);

        Room room = roomRepository
                .findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new NotFoundException("Room with id["+input.getRoomId()+"] doesn't exist."));

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

        if(input.getStartDate().isEqual(input.getEndDate())){
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if(input.getStartDate().isAfter(input.getEndDate())){
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        Room room = roomRepository
                .findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new NotFoundException("Room with id[" + input.getRoomId() + "] doesn't exist."));

        User user = userRepository
                .findByFirstNameAndLastNameAndPhoneNumber(
                        input.getFirstName(),
                        input.getLastName(),
                        input.getPhoneNumber())
                .orElseThrow(() -> new NotFoundException("User with name: " +
                        input.getFirstName() +" "+
                        input.getLastName() + " and phone number: "+
                        input.getPhoneNumber() + " doesn't exist."));

        if(bookingRepository.isRoomBooked(room.getId(),input.getStartDate(),input.getEndDate())){
            throw new BookedRoomException("Room with id["+room.getId()+"] is not available " +
                    "for ["+input.getStartDate()+"/"+input.getEndDate()+"].");
        }

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

        Booking booking = bookingRepository
                .findById(UUID.fromString(input.getBookingId()))
                .orElseThrow(() -> new NotFoundException("Booking with id["+input.getBookingId()+"] doesn't exist."));

        bookingRepository.delete(booking);

        UnbookRoomOutput output = UnbookRoomOutput.builder().build();

        log.info("End unbookRoom output:{}",output);

        return output;
    }




}
