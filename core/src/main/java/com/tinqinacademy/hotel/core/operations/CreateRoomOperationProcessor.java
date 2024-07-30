package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.error.Error;
import com.tinqinacademy.hotel.core.exception.error.ErrorService;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.exception.exceptions.DuplicateValueException;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class CreateRoomOperationProcessor implements CreateRoomOperation {

    private final String className = this.getClass().getSimpleName();
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ConversionService conversionService;
    private final ErrorService errorService;


    @Override
    public Either<Error, CreateRoomOutput> process(CreateRoomInput input) {

        Either<Error,CreateRoomOutput> either = Try.of( () -> {

            log.info(String.format("Start %s %s input: %s", className,LoggingUtils.getMethodName(),input));

            checkForExistingRoomNumber(input.getRoomNo());

            List<Bed> bedList = findBeds(BedSize.getByCode(input.getBedSize().getCode()), input.getBedCount());
            Room room = conversionService.convert(input, Room.RoomBuilder.class)
                    .beds(bedList)
                    .build();

            Room savedRoom = roomRepository.save(room);
            CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

            log.info(String.format("End %s %s output: %s", className,LoggingUtils.getMethodName(),output));

            return output;})
                        .toEither()
                .mapLeft(errorService::handle);

        return either;
    }

    private void checkForExistingRoomNumber(String roomNumber) {
        log.info(String.format("Start %s %s input: %s", className,LoggingUtils.getMethodName(),roomNumber));

        if (roomRepository.existsByRoomNumber(roomNumber)) {
            throw new DuplicateValueException(String.format("Room number: %s already exists in the database.",roomNumber));
        }

        log.info(String.format("End %s %s.", className,LoggingUtils.getMethodName()));
    }

    private List<Bed> findBeds(BedSize bedSize, Integer bedCount) {

        log.info(String.format("Start %s %s input: %s %s", className,LoggingUtils.getMethodName(),bedSize,bedCount));

        Bed bed = bedRepository
                .findByBedSize(bedSize)
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        List<Bed> bedList = IntStream
                .range(0, bedCount)
                .mapToObj(i -> bed)
                .toList();

        log.info(String.format("End %s %s output: %s", className,LoggingUtils.getMethodName(),bedList));

        return bedList;
    }

}