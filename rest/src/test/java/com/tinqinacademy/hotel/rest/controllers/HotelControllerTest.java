package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.persistence.initializr.BedInitializer;
import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
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
import org.springframework.test.web.servlet.MvcResult;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY, connection = EmbeddedDatabaseConnection.H2)
class HotelControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private BedRepository bedRepository;
    @Autowired
    private RoomRepository roomRepository;
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

        Bed bed = bedRepository.findByBedSize(BedSize.SINGLE).get();

        Room room = Room.builder()
                .roomNumber("1A")
                .floor(1)
                .price(BigDecimal.valueOf(100))
                .bathroomType(BathroomType.PRIVATE)
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
    void getIdsOfAvailableRoomsOk() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_IDS_OF_AVAILABLE_ROOMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-11-27")
                        .param("endDate","2024-11-30")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableRoomsIds", hasSize(1)));
    }

    @Test
    void getIdsOfAvailableRoomsNotFound() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_IDS_OF_AVAILABLE_ROOMS+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-15")
                        .param("endDate","2024-07-15")
                        .param("bedCount","5")
                        .param("bedSize","double")
                        .param("bathroomType","private")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRoomByIdOk() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        MvcResult getRoomInfo = mvc.perform(get(RestApiRoutes.GET_INFO_FOR_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = getRoomInfo.getResponse().getContentAsString();
        RoomInfoOutput roomInfoOutput = mapper.readValue(jsonResponse, RoomInfoOutput.class);

        assertEquals(1, roomInfoOutput.getFloor());
        assertEquals("private", roomInfoOutput.getBathroomType().toString());
        assertEquals(2, roomInfoOutput.getBedCount());
    }

    @Test
    void getRoomByIdBadRequest() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_INFO_FOR_ROOM,"noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoomByIdNotFound() throws Exception {
        mvc.perform(get(RestApiRoutes.GET_INFO_FOR_ROOM+"/wrong","noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void bookRoomCreated() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        BookRoomInput input = BookRoomInput.builder()
                .roomId(roomId)
                .startDate(LocalDate.of(2024,10,10))
                .endDate(LocalDate.of(2024,10,15))
                .userId(UUID.randomUUID().toString())
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.BOOK_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }

    @Test
    void bookRoomBadRequest() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        BookRoomInput input = BookRoomInput.builder()
                .roomId(roomId)
                .startDate(LocalDate.of(2024,10,10))
                .endDate(LocalDate.of(2024,10,15))
                .userId(UUID.randomUUID().toString())
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.BOOK_ROOM,"noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void bookRoomNotFound() throws Exception {
        String roomId = roomRepository.findAll().get(0).getId().toString();

        BookRoomInput input = BookRoomInput.builder()
                .roomId(roomId)
                .startDate(LocalDate.of(2024,10,10))
                .endDate(LocalDate.of(2024,10,15))
                .userId(UUID.randomUUID().toString())
                .build();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(post(RestApiRoutes.BOOK_ROOM+"/wrong","noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unbookRoomOk() throws Exception {
        UnbookRoomInput input = UnbookRoomInput.builder()
                .userId("c57fcf0e-dbd4-41a4-96f0-18b0778c0712")
                .build();

        String bookingId = bookingRepository.findAll().get(0).getId().toString();

        String serializedInput = mapper.writeValueAsString(input);

        mvc.perform(delete(RestApiRoutes.UNBOOK_ROOM,bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void unbookRoomBadRequest() throws Exception {
        mvc.perform(delete(RestApiRoutes.UNBOOK_ROOM,"noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unbookRoomNotFound() throws Exception {
        mvc.perform(delete(RestApiRoutes.UNBOOK_ROOM+"/wrong","noId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNotFound());
    }
}