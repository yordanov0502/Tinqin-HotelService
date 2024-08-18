package com.tinqinacademy.hotel.core.operations.internal;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdInput;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdOperation;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GetRoomIdByNumberOperationProcessor extends BaseOperationProcessor implements GetRoomIdOperation {

    private final RoomRepository roomRepository;

    public GetRoomIdByNumberOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
    }

    @Override
    public Either<Errors, GetRoomIdOutput> process(GetRoomIdInput input) {
        return Try.of(() -> {
                    log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), input));
                    validate(input);

                    String roomId = getRoomIdByNumber(input.getRoomNumber());

                    GetRoomIdOutput output = GetRoomIdOutput.builder()
                            .roomId(roomId)
                            .build();
                    log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), output));
                    return output;
                })
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private String getRoomIdByNumber(String roomNumber){
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(), roomNumber));

        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new NotFoundException(String.format("Room with number: %s doesn't exist.", roomNumber)));

        log.info(String.format("End %s %s.", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
        return room.getId().toString();
    }

}