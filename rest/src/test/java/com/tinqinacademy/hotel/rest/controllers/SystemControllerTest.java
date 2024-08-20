package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.persistence.initializr.BedInitializer;
import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.RoomRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class SystemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BedInitializer bedInitializer;

    @BeforeEach
    public void setup() {
        //? Manually invoke the BedInitializer
        bedInitializer.run(new ApplicationArguments() {
            @Override
            public String[] getSourceArgs() {
                return new String[0];
            }

            @Override
            public Set<String> getOptionNames() {
                return Collections.emptySet();
            }

            @Override
            public boolean containsOption(String name) {
                return false;
            }

            @Override
            public List<String> getOptionValues(String name) {
                return null;
            }

            @Override
            public List<String> getNonOptionArgs() {
                return Collections.emptyList();
            }
        });

        Bed bed = bedRepository.findByBedSize(com.tinqinacademy.hotel.persistence.model.enums.BedSize.SINGLE).get();

        Room room = Room.builder()
                .roomNumber("1A")
                .floor(1)
                .price(BigDecimal.valueOf(100))
                .bathroomType(com.tinqinacademy.hotel.persistence.model.enums.BathroomType.PRIVATE)
                .beds(List.of(bed,bed))
                .build();
        room = roomRepository.save(room);

        Booking booking = Booking.builder()
                .startDate(LocalDate.of(2024, 11, 22))
                .endDate(LocalDate.of(2024, 11, 25))
                .userId(UUID.fromString("c57fcf0e-dbd4-41a4-96f0-18b0778c0712"))
                .totalPrice(BigDecimal.valueOf(300))
                .room(room)
                .guests(new HashSet<>())
                .build();
        booking = bookingRepository.save(booking);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        bedRepository.deleteAll();
    }

    @Test
    void registerVisitorCreated() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        VisitorInput visitor1 = VisitorInput.builder()
                .roomId(roomId)
                .startDate(LocalDate.of(2024, 11, 22))
                .endDate(LocalDate.of(2024, 11, 25))
                .firstName("Katya")
                .lastName("Yordanova")
                .dateOfBirth(LocalDate.of(1975,9,27))
                .phoneNumber("0894735633")
                .idCardNumber("1234")
                .idCardValidity(LocalDate.of(2028, 8, 8))
                .idCardIssueAuthority("МВР-Варна")
                .idCardIssueDate(LocalDate.of(2018, 8, 8))
                .build();

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(List.of(visitor1))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }

    @Test
    void registerVisitorBadRequest() throws Exception {
        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(null)
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerVisitorNotFound() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        VisitorInput visitor1 = VisitorInput.builder()
                .roomId(roomId)
                .startDate(LocalDate.of(2024, 11, 22))
                .endDate(LocalDate.of(2024, 11, 25))
                .firstName("Katya")
                .lastName("Yordanova")
                .dateOfBirth(LocalDate.of(1975,9,27))
                .phoneNumber("0894735633")
                .idCardNumber("1234")
                .idCardValidity(LocalDate.of(2028, 8, 8))
                .idCardIssueAuthority("МВР-Варна")
                .idCardIssueDate(LocalDate.of(2018, 8, 8))
                .build();

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(List.of(visitor1))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.REGISTER_VISITOR+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getVisitorsOk() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_VISITORS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-11-22")
                        .param("endDate","2024-11-25")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitorOutputList", hasSize(1)));
    }

    @Test
    void getVisitorsNotFound() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_VISITORS+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-10")
                        .param("endDate","2024-07-15")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createRoomCreated() throws Exception {
        Integer newBedCount = 5;
        Integer newFloor = 4;
        String newRoomNumber = "4C";
        BigDecimal newPrice = BigDecimal.valueOf(125.44);

        CreateRoomInput input = CreateRoomInput.builder()
                .bedCount(newBedCount)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(newFloor)
                .roomNo(newRoomNumber)
                .price(newPrice)
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());

        String roomId = roomRepository.findAll().get(1).getId().toString();
        Room newRoom = roomRepository.findById(UUID.fromString(roomId)).get();

        assertEquals(newRoom.getBeds().size(),newBedCount);
        assertEquals(newRoom.getFloor(),newFloor);
        assertEquals(newRoom.getRoomNumber(),newRoomNumber);
        assertEquals(newRoom.getPrice(),newPrice);
    }

    @Test
    void createRoomBadRequest() throws Exception {
        CreateRoomInput input = CreateRoomInput.builder()
                .bedCount(-5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoomNotFound() throws Exception {
        CreateRoomInput input = CreateRoomInput.builder()
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post("/wrong/path")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoomOk() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();
        Integer newBedCount = 1;
        Integer newFloor = 1;
        String newRoomNumber = "2A";
        BigDecimal newPrice = BigDecimal.valueOf(125.44);

        UpdateRoomInput input = UpdateRoomInput.builder()
                .bedCount(newBedCount)
                .bedSize(BedSize.SINGLE)
                .bathroomType(BathroomType.PRIVATE)
                .floor(newFloor)
                .roomNo(newRoomNumber)
                .price(newPrice)
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.UPDATE_ROOM, roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(roomId));

        Room updatedRoom = roomRepository.findById(UUID.fromString(roomId)).get();

        assertEquals(updatedRoom.getBeds().size(),newBedCount);
        assertEquals(updatedRoom.getFloor(),newFloor);
        assertEquals(updatedRoom.getRoomNumber(),newRoomNumber);
        assertEquals(updatedRoom.getPrice(),newPrice);
    }

    @Test
    void updateRoomBadRequest() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(-4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.UPDATE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomNotFound() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(-4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(put(RestApiRoutes.UPDATE_ROOM+"/wrong",roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRoomPartiallyOk() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();
        BigDecimal newPrice = BigDecimal.valueOf(99.44);

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .price(newPrice)
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY,roomId)
                        .contentType("application/json-patch+json")
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        Room updatedRoom = roomRepository.findById(UUID.fromString(roomId)).get();
        assertEquals(updatedRoom.getPrice(),newPrice);
    }

    @Test
    void updateRoomPartiallyBadRequest() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .roomId(roomId)
                .floor(-1)
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY,roomId)
                        .contentType("application/json-patch+json")
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomPartiallyNotFound() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .price(BigDecimal.valueOf(225.44))
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY+"/wrong",roomId)
                        .contentType("application/json-patch+json")
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoomOk() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();
        bookingRepository.deleteAll(); //? Booking is deleted so the room can be successfully deleted

        mvc.perform(delete(RestApiRoutes.DELETE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRoomBadRequest() throws Exception {
        mvc.perform(delete(RestApiRoutes.DELETE_ROOM,"noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRoomNotFound() throws Exception {
        mvc.perform(delete(RestApiRoutes.DELETE_ROOM+"/wrong","noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }
}