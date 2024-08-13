package com.tinqinacademy.hotel.core.operations.hotel;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.api.exceptions.custom.BookedRoomException;
import com.tinqinacademy.hotel.api.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.entity.User;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class BookRoomOperationProcessor extends BaseOperationProcessor implements BookRoomOperation {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookRoomOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Either<Errors, BookRoomOutput> process(BookRoomInput input) {

        return Try.of(() -> {
            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);
            validateDates(input.getStartDate(),input.getEndDate());

            Room room = findRoomById(input.getRoomId());

            checkRoomAvailability(room.getId(),input.getStartDate(),input.getEndDate());

            long days = input.getEndDate().toEpochDay() - input.getStartDate().toEpochDay();
            BigDecimal priceOfBooking = room.getPrice().multiply(BigDecimal.valueOf(days));

            Booking newBooking = conversionService.convert(input,Booking.BookingBuilder.class)
                    .totalPrice(priceOfBooking)
                    .room(room)
                    .build();
            bookingRepository.save(newBooking);

            BookRoomOutput output = BookRoomOutput.builder().build();

            log.info(String.format("End %s %s output: %s",this.getClass().getSimpleName(),LoggingUtils.getMethodName(), output));
            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {

        log.info(String.format("Start %s %s input: %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),startDate,endDate));

        if(startDate.isEqual(endDate)) {
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if(startDate.isAfter(endDate)) {
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        log.info(String.format("End %s %s",this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private Room findRoomById(String roomId) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomId));

        Room room = roomRepository
                .findById(UUID.fromString(roomId))
                .orElseThrow(() -> new NotFoundException(String.format("Room with id[%s] doesn't exist.", roomId)));

        log.info(String.format("End %s %S output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),room.toString()));

        return room;
    }

    private void checkRoomAvailability(UUID roomId, LocalDate startDate, LocalDate endDate) {

        log.info(String.format("Start %s %s input: %s,%s,%s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomId,startDate,endDate));

        if(bookingRepository.isRoomBooked(roomId,startDate,endDate)){
            throw new BookedRoomException(String.format("Room with id[%s] is not available for [%s/%s].",
                    roomId,startDate,endDate));
        }

        log.info(String.format("End %s %s.", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
    }

}