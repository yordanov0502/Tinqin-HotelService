package com.tinqinacademy.hotel.core.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.VisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.VisitorInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.services.SystemService;
import com.tinqinacademy.hotel.core.exception.exceptions.BookedRoomException;
import com.tinqinacademy.hotel.core.exception.exceptions.NotFoundException;
import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemServiceImpl implements SystemService {

    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final ConversionService conversionService;
    private final ObjectMapper objectMapper;

    @Transactional
    @Override
    public RegisterVisitorOutput registerVisitors(RegisterVisitorInput input) {

        log.info("Start registerVisitors input:{}", input);

        List<String> idCardNumberList = input.getVisitorInputList()
                .stream()
                .map(VisitorInput::getIdCardNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, Guest> existingGuestsMap = guestRepository
                .findAllByIdCardNumberIn(idCardNumberList)
                .stream()
                .collect(Collectors.toMap(Guest::getIdCardNumber, guest -> guest));

        input.getVisitorInputList().forEach(visitor -> {

            Guest guest;

            if(visitor.getIdCardNumber() != null && existingGuestsMap.containsKey(visitor.getIdCardNumber())){
                guest = existingGuestsMap.get(visitor.getIdCardNumber());
            }
            else{
                guest = conversionService.convert(visitor, Guest.class);
                guestRepository.save(guest);
            }

            Booking booking = bookingRepository
                    .findByRoomIdAndStartDateAndEndDate(
                            UUID.fromString(visitor.getRoomId()),
                            visitor.getStartDate(),
                            visitor.getEndDate())
                    .orElseThrow(() -> new NotFoundException("Booking with roomId[" + visitor.getRoomId() + "] ," +
                            "start date: " + visitor.getStartDate() + " and " +
                            "end date: " + visitor.getEndDate() + " doesn't exist."));
            booking.getGuests().add(guest);
            bookingRepository.save(booking);
        });

        RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();

        log.info("End registerVisitors output:{}", output);

        return output;
    }

    @Override
    public GetVisitorsOutput getVisitors(GetVisitorsInput input) { //? optional

        log.info("Start getVisitors input:{}", input);

        GetVisitorsOutput output = GetVisitorsOutput.builder()
                .visitorInputList(List.of(
                        VisitorOutput.builder()
                                .startDate(input.getStartDate())
                                .endDate(input.getEndDate())
                                .firstName(input.getFirstName())
                                .lastName(input.getLastName())
                                .idCardNumber(input.getIdCardNumber())
                                .idCardValidity(input.getIdCardValidity())
                                .idCardIssueAuthority(input.getIdCardIssueAuthority())
                                .idCardIssueDate(input.getIdCardIssueDate())
                                .build()))
                .build();

        log.info("End getVisitors output:{}", output);

        return output;
    }

    @Override
    public CreateRoomOutput createRoom(CreateRoomInput input) {
        log.info("Start createRoom input:{}", input);

        List<Bed> bedList = findBeds(BedSize.getByCode(input.getBedSize().getCode()), input.getBedCount());
        Room room = conversionService.convert(input,Room.RoomBuilder.class)
                .beds(bedList)
                .build();
        Room savedRoom = roomRepository.save(room);
        CreateRoomOutput output = conversionService.convert(savedRoom, CreateRoomOutput.class);

        log.info("End createRoom output:{}", output);

        return output;
    }

    @Override
    public UpdateRoomOutput updateRoom(UpdateRoomInput input) {

        log.info("Start updateRoom input:{}", input);

        Room currRoom = roomRepository
                .findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new NotFoundException("Room with id[" + input.getRoomId() + "] doesn't exist."));

        List<Bed> bedList = findBeds(BedSize.getByCode(input.getBedSize().getCode()), input.getBedCount());
        Room room = conversionService.convert(input, Room.RoomBuilder.class)
                .createdAt(currRoom.getCreatedAt())
                .beds(bedList)
                .build();
        Room updatedRoom = roomRepository.save(room);
        UpdateRoomOutput output = conversionService.convert(updatedRoom, UpdateRoomOutput.class);

        log.info("End updateRoom output:{}", output);

        return output;
    }

    @Override
    public UpdateRoomPartiallyOutput updateRoomPartially(UpdateRoomPartiallyInput input) {

        log.info("Start updateRoomPartially input:{}", input);

        Room currentRoom = roomRepository
                .findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new NotFoundException("Room with id[" + input.getRoomId() + "] doesn't exist."));

        int numberOfBedsToAdd =
                input.getBedCount() != null
                        ? input.getBedCount()
                        : currentRoom.getBeds().size();

        Bed bedToAdd = currentRoom.getBeds().getFirst();
        if(input.getBedSize() != null){
            bedToAdd = bedRepository.findByBedSize(BedSize.getByCode(input.getBedSize().toString()))
                    .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));
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

        return output;
    }

    @Override
    public DeleteRoomOutput deleteRoom(DeleteRoomInput input) {

        log.info("Start deleteRoom input:{}", input);

        Room room = roomRepository
                .findById(UUID.fromString(input.getRoomId()))
                .orElseThrow(() -> new NotFoundException("Room with id[" + input.getRoomId() + "] doesn't exist."));

        boolean isRoomBooked = bookingRepository
                .findAllByRoomId(room.getId())
                .stream()
                .anyMatch(booking -> !LocalDate.now().isAfter(booking.getEndDate()));

        if(isRoomBooked){
            throw new BookedRoomException("Room with id[" + room.getId().toString() + "] cannot be deleted, because it is booked.");
        }

        bookingRepository.setRoomToNullForBookingsByRoomId(room.getId());
        roomRepository.delete(room);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();

        log.info("End deleteRoom output:{}", output);

        return output;
    }


    private List<Bed> findBeds(BedSize bedSize, Integer bedCount) {
        Bed bed = bedRepository
                .findByBedSize(BedSize.getByCode(bedSize.toString()))
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        return IntStream.range(0, bedCount)
                .mapToObj(i -> bed)
                .toList();
    }

}
