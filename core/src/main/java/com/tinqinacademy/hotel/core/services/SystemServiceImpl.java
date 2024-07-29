package com.tinqinacademy.hotel.core.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.content.VisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.services.SystemService;
import com.tinqinacademy.hotel.core.exception.exceptions.BookedRoomException;
import com.tinqinacademy.hotel.core.exception.exceptions.BookingDatesException;
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
import jakarta.persistence.EntityManager;
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
    private final EntityManager entityManager;

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
    public GetVisitorsOutput getVisitors(GetVisitorsInput input) {
        log.info("Start getVisitors input:{}", input);

        if(input.getStartDate().isEqual(input.getEndDate())){
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if(input.getStartDate().isAfter(input.getEndDate())){
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        List<Booking> bookingList = bookingRepository.findAllByVariousCriteria(
                input.getStartDate(),
                input.getEndDate(),
                input.getFirstName(),
                input.getLastName(),
                input.getPhoneNumber(),
                input.getIdCardNumber(),
                input.getIdCardValidity(),
                input.getIdCardIssueAuthority(),
                input.getIdCardIssueDate(),
                input.getRoomNumber()
        );

        List<VisitorOutput> visitorOutputs = new ArrayList<>();
        for(Booking booking: bookingList){
            for(Guest guest: booking.getGuests()){
                if(input.getFirstName() != null && !input.getFirstName().equals(guest.getFirstName())) {continue;}
                if(input.getLastName() != null && !input.getLastName().equals(guest.getLastName())) {continue;}
                if(input.getPhoneNumber() != null && !input.getPhoneNumber().equals(guest.getPhoneNumber())) {continue;}
                if(input.getIdCardNumber() != null && !input.getIdCardNumber().equals(guest.getIdCardNumber())) {continue;}
                if(input.getIdCardValidity() != null && !input.getIdCardValidity().equals(guest.getIdCardValidity())) {continue;}
                if(input.getIdCardIssueAuthority() != null && !input.getIdCardIssueAuthority().equals(guest.getIdCardIssueAuthority())) {continue;}
                if(input.getIdCardIssueDate() != null && !input.getIdCardIssueDate().equals(guest.getIdCardIssueDate())) {continue;}

                VisitorOutput visitorOutput = conversionService.convert(guest, VisitorOutput.VisitorOutputBuilder.class)
                        .phoneNumber(guest.getPhoneNumber())
                        .startDate(booking.getStartDate())
                        .endDate(booking.getEndDate())
                        .roomNumber(booking.getRoom().getRoomNumber())
                        .build();

                visitorOutputs.add(visitorOutput);
            }
        }

        GetVisitorsOutput output = GetVisitorsOutput.builder()
                .visitorOutputList(visitorOutputs)
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

        if(!isRoomExists(input.getRoomId())) {throw new NotFoundException("Room with id[" + input.getRoomId() + "] doesn't exist.");}

        List<Bed> bedList = findBeds(BedSize.getByCode(input.getBedSize().getCode()), input.getBedCount());
        Room room = conversionService.convert(input, Room.RoomBuilder.class)
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

        Room currentRoom = findRoomById(input.getRoomId());

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

        return output;
    }

    @Override
    public DeleteRoomOutput deleteRoom(DeleteRoomInput input) {

        log.info("Start deleteRoom input:{}", input);

        Room room = findRoomById(input.getRoomId());
        checkIfRoomIsBooked(room.getId());

        bookingRepository.setRoomToNullForBookingsByRoomId(room.getId());
        roomRepository.delete(room);

        DeleteRoomOutput output = DeleteRoomOutput.builder().build();

        log.info("End deleteRoom output:{}", output);

        return output;
    }

    private boolean isRoomExists (String roomId) {

        log.info("Start isRoomExists input:{}", roomId);

        boolean isRoomExists = roomRepository.existsById(UUID.fromString(roomId));

        log.info("End isRoomExists output:{}", isRoomExists);

        return isRoomExists;
    }

    private boolean isRoomWithNumberExists(String roomNumber) {

        log.info("Start isRoomWithNumberExists input:{}", roomNumber);

        boolean isRoomExists = roomRepository.existsByRoomNumber(roomNumber);

        log.info("End isRoomWithNumberExists output:{}", isRoomExists);

        return isRoomExists;
    }

    private Room findRoomById(String roomId) {

        log.info("Start findRoomById input:{}", roomId);

        Room room = roomRepository
                .findById(UUID.fromString(roomId))
                .orElseThrow(() -> new NotFoundException("Room with id[" + roomId + "] doesn't exist."));

        log.info("End findRoomById output:{}", room);

        return room;
    }

    private Bed findBedByBedSize(BedSize bedSize) {

        log.info("Start findBedByBedSize input:{}",bedSize);

        Bed bed = bedRepository
                .findByBedSize(bedSize)
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        log.info("End findBedByBedSize output:{}",bed);

        return bed;
    }

    private List<Bed> findBeds(BedSize bedSize, Integer bedCount) {

        log.info("Start findBeds input:{},{}",bedSize, bedCount);

        Bed bed = bedRepository
                .findByBedSize(bedSize)
                .orElseThrow(() -> new NotFoundException("Bed doesn't exist (UNKNOWN bedSize)."));

        List<Bed> bedList = IntStream
                .range(0, bedCount)
                .mapToObj(i -> bed)
                .toList();

        log.info("End findBeds output:{}",bedList);

        return bedList;
    }

    private void checkIfRoomIsBooked(UUID roomId) {

        log.info("Start checkIfRoomIsBooked input:{}",roomId);

        boolean isRoomBooked = bookingRepository
                .findAllByRoomId(roomId)
                .stream()
                .anyMatch(booking -> !LocalDate.now().isAfter(booking.getEndDate()));

        if(isRoomBooked){
            throw new BookedRoomException("Room with id[" + roomId.toString() + "] cannot be deleted, because it is booked.");
        }

        log.info("End checkIfRoomIsBooked.");
    }

}
