package com.tinqinacademy.hotel.core.operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOperation;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.exceptions.custom.DuplicateValueException;
import com.tinqinacademy.hotel.core.exceptions.custom.NotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UpdateRoomPartiallyOperationProcessor extends BaseOperationProcessor implements UpdateRoomPartiallyOperation {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final ObjectMapper objectMapper;

    public UpdateRoomPartiallyOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, RoomRepository roomRepository, BedRepository bedRepository, ObjectMapper objectMapper) {
        super(conversionService, exceptionService, validator);
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Either<Errors, UpdateRoomPartiallyOutput> process(UpdateRoomPartiallyInput input) {

        Either<Errors,UpdateRoomPartiallyOutput> either = Try.of( ()-> {

            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);

            Room currentRoom = findRoomById(input.getRoomId());
            if(!currentRoom.getRoomNumber().equals(input.getRoomNo())) {checkForExistingRoomNumber(input.getRoomNo());}

            int numberOfBedsToAdd = input.getBedCount() != null ? input.getBedCount() : currentRoom.getBeds().size();

            Bed bedToAdd = currentRoom.getBeds().getFirst();
            if(input.getBedSize() != null){
                bedToAdd = findBedByBedSize(BedSize.getByCode(input.getBedSize().toString()));
            }

            Room newRoom = conversionService.convert(input, Room.class);
            if(input.getBedCount() != null || input.getBedSize() != null){
                List<Bed> newBeds = new ArrayList<>();
                for(int i = 0;i < numberOfBedsToAdd; i++){
                    newBeds.add(bedToAdd);
                }
                newRoom.setBeds(newBeds);
            }

            JsonNode currentRoomNode = objectMapper.valueToTree(currentRoom);
            JsonNode newRoomNode = objectMapper.valueToTree(newRoom);

            try{
                JsonMergePatch patch = JsonMergePatch.fromJson(newRoomNode);
                newRoom = objectMapper.treeToValue(patch.apply(currentRoomNode), Room.class);
            }
            catch (JsonPatchException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            Room updatedRoom = roomRepository.save(newRoom);

            UpdateRoomPartiallyOutput output = conversionService.convert(updatedRoom, UpdateRoomPartiallyOutput.class);

            log.info("End updateRoomPartially output:{}", output);

            return output;})
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

    private void checkForExistingRoomNumber(String roomNumber) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),roomNumber));

        if(roomRepository.existsByRoomNumber(roomNumber)) {
            throw new DuplicateValueException(String.format("Room number: %s already exists in the database.", roomNumber));
        }

        log.info(String.format("End %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private Bed findBedByBedSize(BedSize bedSize) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),bedSize));

        Bed bed = bedRepository
                .findByBedSize(bedSize)
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),bed));

        return bed;
    }
}
