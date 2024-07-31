package com.tinqinacademy.hotel.core.operations;

import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOperation;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.core.exception.error.ExceptionService;
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
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class CreateRoomOperationProcessor extends BaseOperationProcessor implements CreateRoomOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    public CreateRoomOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository, BedRepository bedRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
    }


    @Override
    public Either<Errors, CreateRoomOutput> process(CreateRoomInput input) {

        Either<Errors,CreateRoomOutput> either = Try.of( () -> {

            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),input));

            validate(input);

            checkForExistingRoomNumber(input.getRoomNo());

            List<Bed> bedList = findBeds(BedSize.getByCode(input.getBedSize().getCode()), input.getBedCount());
            Room room = conversionService.convert(input, Room.RoomBuilder.class)
                    .beds(bedList)
                    .build();

            Room savedRoom = roomRepository.save(room);
            CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

            log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),output));

            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);

        return either;
    }

    private void checkForExistingRoomNumber(String roomNumber) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomNumber));

        if (roomRepository.existsByRoomNumber(roomNumber)) {
            throw new DuplicateValueException(String.format("Room number: %s already exists in the database.",roomNumber));
        }

        log.info(String.format("End %s %s.", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private List<Bed> findBeds(BedSize bedSize, Integer bedCount) {

        log.info(String.format("Start %s %s input: %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),bedSize,bedCount));

        Bed bed = bedRepository
                .findByBedSize(bedSize)
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        List<Bed> bedList = IntStream
                .range(0, bedCount)
                .mapToObj(i -> bed)
                .toList();

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),bedList));

        return bedList;
    }

}