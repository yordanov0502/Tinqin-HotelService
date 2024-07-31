package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookedRoomException;
import com.tinqinacademy.hotel.core.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class DeleteRoomOperationProcessor extends BaseOperationProcessor implements DeleteRoomOperation {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public DeleteRoomOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository, BookingRepository bookingRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<Errors, DeleteRoomOutput> process(DeleteRoomInput input) {

        Either<Errors,DeleteRoomOutput> either = Try.of( () -> {

            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);

            Room room = findRoomById(input.getRoomId());
            checkIfRoomIsBooked(room.getId());
            bookingRepository.setRoomToNullForBookingsByRoomId(room.getId());
            roomRepository.delete(room);
            DeleteRoomOutput output = DeleteRoomOutput.builder().build();

            log.info(String.format("End %s %s output:{}", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),output));
            return output;
        })
                .toEither()
                .mapLeft(exceptionService::handle);

        return either;
    }

    private Room findRoomById(String roomId) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomId));

        Room room = roomRepository
                .findById(UUID.fromString(roomId))
                .orElseThrow(() -> new NotFoundException(String.format("Room with id[%s] doesn't exist.", roomId)));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),room));

        return room;
    }

    private void checkIfRoomIsBooked(UUID roomId) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomId));

        boolean isRoomBooked = bookingRepository
                .findAllByRoomId(roomId)
                .stream()
                .anyMatch(booking -> !LocalDate.now().isAfter(booking.getEndDate()));

        if(isRoomBooked){
            throw new BookedRoomException(String.format("Room with id[%s] cannot be deleted, because it is booked.",roomId));
        }

        log.info(String.format("End %s %s checkIfRoomIsBooked.",this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

}
