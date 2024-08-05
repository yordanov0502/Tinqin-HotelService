package com.tinqinacademy.hotel.core.operations.hotel;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.GetRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.content.BookedInterval;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GetRoomOperationProcessor extends BaseOperationProcessor implements GetRoomOperation {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public GetRoomOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository, BookingRepository bookingRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<Errors, RoomInfoOutput> process(RoomInfoInput input) {

        return Try.of(() -> {
            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);

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

            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private Room findRoomById(String roomId) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomId));

        Room room = roomRepository
                .findById(UUID.fromString(roomId))
                .orElseThrow(() -> new NotFoundException(String.format("Room with id[%s] doesn't exist.", roomId)));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),room.toString()));

        return room;
    }
}
