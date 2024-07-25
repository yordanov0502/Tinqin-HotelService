package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class HotelControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getIdsOfAvailableRoomsOk() throws Exception {
        MvcResult getIdsOfAvailableRooms = mvc.perform(get(RestApiRoutes.GET_IDS_OF_AVAILABLE_ROOMS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-10")
                        .param("endDate","2024-07-15")
                        .param("bedCount","5")
                        .param("bedSize","double")
                        .param("bathroomType","private")
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,getIdsOfAvailableRooms.getResponse().getStatus());
    }

    @Test
    void getIdsOfAvailableRoomsNotFound() throws Exception {
        MvcResult getIdsOfAvailableRooms = mvc.perform(get(RestApiRoutes.GET_IDS_OF_AVAILABLE_ROOMS+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-15")
                        .param("endDate","2024-07-15")
                        .param("bedCount","5")
                        .param("bedSize","double")
                        .param("bathroomType","private")
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,getIdsOfAvailableRooms.getResponse().getStatus());
    }

    @Test
    void getRoomByIdOk() throws Exception {
        MvcResult getRoomInfo = mvc.perform(get(RestApiRoutes.GET_INFO_FOR_ROOM,"11A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,getRoomInfo.getResponse().getStatus());
    }

    @Test
    void getRoomByIdNotFound() throws Exception {
        MvcResult getRoomInfo = mvc.perform(get(RestApiRoutes.GET_INFO_FOR_ROOM+"/wrong","11A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,getRoomInfo.getResponse().getStatus());
    }

    @Test
    void bookRoomCreated() throws Exception {

        BookRoomInput input = BookRoomInput.builder()
                .roomId("11A")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .firstName("Todor")
                .lastName("Yordanov")
                .phoneNumber("0882987134")
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult bookRoom = mvc.perform(post(RestApiRoutes.BOOK_ROOM,"11A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(201,bookRoom.getResponse().getStatus());
    }

    @Test
    void bookRoomBadRequest() throws Exception {
        BookRoomInput input = BookRoomInput.builder()
                .roomId("11A")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .firstName("")
                .lastName("Yordanov")
                .phoneNumber("0882987134")
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult bookRoom = mvc.perform(post(RestApiRoutes.BOOK_ROOM,"11A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,bookRoom.getResponse().getStatus());
    }

    @Test
    void bookRoomNotFound() throws Exception {
        BookRoomInput input = BookRoomInput.builder()
                .roomId("11A")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .firstName("")
                .lastName("Yordanov")
                .phoneNumber("0882987134")
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult bookRoom = mvc.perform(post(RestApiRoutes.BOOK_ROOM+"/wrong","11A")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,bookRoom.getResponse().getStatus());
    }

    @Test
    void unbookRoomOk() throws Exception {
        MvcResult bookRoom = mvc.perform(delete(RestApiRoutes.UNBOOK_ROOM,"113")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,bookRoom.getResponse().getStatus());
    }

    @Test
    void unbookRoomNotFound() throws Exception {
        MvcResult bookRoom = mvc.perform(delete(RestApiRoutes.UNBOOK_ROOM+"/wrong","113")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,bookRoom.getResponse().getStatus());
    }
}