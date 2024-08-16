package com.tinqinacademy.hotel.core.operations.hotel;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.api.exceptions.custom.UnbookException;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookOperation;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class UnbookRoomOperationProcessor extends BaseOperationProcessor implements UnbookOperation {

    private final BookingRepository bookingRepository;

    public UnbookRoomOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, BookingRepository bookingRepository) {
        super(conversionService, exceptionService, validator);
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<Errors, UnbookRoomOutput> process(UnbookRoomInput input) {

        return Try.of(() -> {
                    log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), input));
                    validate(input);

                    Booking booking = findBookingById(input.getBookingId());
                    validateDates(booking.getStartDate());
                    checkBookingCreator(input.getUserId(),booking.getUserId().toString());
                    bookingRepository.delete(booking);

                    UnbookRoomOutput output = UnbookRoomOutput.builder().build();
                    log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), output));
                    return output;
                })
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private Booking findBookingById(String bookingId) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), bookingId));

        Booking booking = bookingRepository
                .findById(UUID.fromString(bookingId))
                .orElseThrow(() -> new NotFoundException(String.format("Booking with id[%s] doesn't exist.", bookingId)));

        log.info(String.format("End %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), booking.toString()));

        return booking;
    }

    private void validateDates(LocalDate startDate) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),startDate));

        LocalDate today = LocalDate.now();

        if(!startDate.isAfter(today)) {
            throw new BookingDatesException("You cannot unbook a room, because your booking has already started or passed.");
        }

        log.info(String.format("End %s %s",this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private void checkBookingCreator(String authUserId, String userIdOfBooking){
        log.info(String.format("Start %s %s input: %s %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), authUserId, userIdOfBooking));

        if(!authUserId.equals(userIdOfBooking)){
            throw new UnbookException("You cannot unbook a room, which you haven't booked.");
        }

        log.info(String.format("End %s %s.", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
    }
}
