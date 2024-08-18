package com.tinqinacademy.hotel.core.operations.internal;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.api.operations.internal.IsRoomExistsInput;
import com.tinqinacademy.hotel.api.operations.internal.IsRoomExistsOperation;
import com.tinqinacademy.hotel.api.operations.internal.IsRoomExistsOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class IsRoomExistsOperationProcessor extends BaseOperationProcessor implements IsRoomExistsOperation {

    private final RoomRepository roomRepository;

    public IsRoomExistsOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, IsRoomExistsOutput> process(IsRoomExistsInput input) {
        return Try.of(() -> {
                    log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), input));
                    validate(input);

                    checkIfRoomExists(input.getRoomId());

                    IsRoomExistsOutput output = IsRoomExistsOutput.builder().build();
                    log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), output));
                    return output;
                })
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private void checkIfRoomExists(String roomId){
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), roomId));

        boolean isRoomExists = roomRepository.existsById(UUID.fromString(roomId));
        if(!isRoomExists){
            throw new NotFoundException(String.format("Room with id[%s] doesn't exist.", roomId));
        }

        log.info(String.format("End %s %s.", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
    }
}
