package com.tinqinacademy.hotel.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SystemControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerVisitorCreated() throws Exception {
        VisitorInput visitor1 = VisitorInput.builder()
                .startDate(LocalDate.of(2024, 7, 10))
                .endDate(LocalDate.of(2024, 7, 15))
                .firstName("Todor")
                .lastName("Yordanov")
                .phoneNumber("0882987231")
                .idCardNumber("132123")
                .idCardValidity(LocalDate.of(2024, 8, 10))
                .idCardIssueAuthority("abcd")
                .idCardIssueDate(LocalDate.of(2022, 7, 10))
                .build();

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(List.of(visitor1))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult registerVisitor = mvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(201,registerVisitor.getResponse().getStatus());
    }

    @Test
    void registerVisitorBadRequest() throws Exception {
        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(null)
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult registerVisitor = mvc.perform(post(RestApiRoutes.REGISTER_VISITOR)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,registerVisitor.getResponse().getStatus());
    }

    @Test
    void registerVisitorNotFound() throws Exception {
        VisitorInput visitor1 = VisitorInput.builder()
                .startDate(LocalDate.of(2024, 7, 10))
                .endDate(LocalDate.of(2024, 7, 15))
                .firstName("Todor")
                .lastName("Yordanov")
                .phoneNumber("0882987231")
                .idCardNumber("132123")
                .idCardValidity(LocalDate.of(2024, 8, 10))
                .idCardIssueAuthority("abcd")
                .idCardIssueDate(LocalDate.of(2022, 7, 10))
                .build();

        RegisterVisitorInput input = RegisterVisitorInput.builder()
                .visitorInputList(List.of(visitor1))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult registerVisitor = mvc.perform(post(RestApiRoutes.REGISTER_VISITOR+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,registerVisitor.getResponse().getStatus());
    }

    @Test
    void getVisitorsOk() throws Exception {
        MvcResult getVisitors = mvc.perform(get(RestApiRoutes.GET_VISITORS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-10")
                        .param("endDate","2024-07-15")
                        .param("firstName","Todor")
                        .param("lastName","Yordanov")
                        .param("phoneNumber","0882983123")
                        .param("idCardNumber","1234")
                        .param("idCardValidity","2024-07-20")
                        .param("idCardIssueAuthority","abcd")
                        .param("idCardIssueDate","2022-08-08")
                        .param("roomNumber","12A")
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,getVisitors.getResponse().getStatus());
    }

    @Test
    void getVisitorsNotFound() throws Exception {
        MvcResult getVisitors = mvc.perform(get(RestApiRoutes.GET_VISITORS+"/wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("startDate","2024-07-10")
                        .param("endDate","2024-07-15")
                        .param("firstName","Todor")
                        .param("lastName","Yordanov")
                        .param("phoneNumber","0882983123")
                        .param("idCardNumber","1234")
                        .param("idCardValidity","2024-07-20")
                        .param("idCardIssueAuthority","abcd")
                        .param("idCardIssueDate","2022-08-08")
                        .param("roomNumber","12A")
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,getVisitors.getResponse().getStatus());
    }

    @Test
    void createRoomCreated() throws Exception {
        CreateRoomInput input = CreateRoomInput.builder()
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult createRoom = mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(201,createRoom.getResponse().getStatus());
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

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult createRoom = mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,createRoom.getResponse().getStatus());
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

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult createRoom = mvc.perform(post("/wrong/path")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,createRoom.getResponse().getStatus());
    }

    @Test
    void updateRoomOk() throws Exception {

        String roomId = "0789ef57-e130-4db4-a951-811622858142";

        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoom = mvc.perform(put(RestApiRoutes.UPDATE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,updateRoom.getResponse().getStatus());
    }

    @Test
    void updateRoomBadRequest() throws Exception {

        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(-4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoom = mvc.perform(put(RestApiRoutes.UPDATE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,updateRoom.getResponse().getStatus());
    }

    @Test
    void updateRoomNotFound() throws Exception {

        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        UpdateRoomInput input = UpdateRoomInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(-4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoom = mvc.perform(put(RestApiRoutes.UPDATE_ROOM+"/wrong",roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,updateRoom.getResponse().getStatus());
    }

    @Test
    void updateRoomPartiallyOk() throws Exception {
        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoomPartially = mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,updateRoomPartially.getResponse().getStatus());
    }

    @Test
    void updateRoomPartiallyBadRequest() throws Exception {
        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(-1)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoomPartially = mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,updateRoomPartially.getResponse().getStatus());
    }

    @Test
    void updateRoomPartiallyNotFound() throws Exception {
        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        UpdateRoomPartiallyInput input = UpdateRoomPartiallyInput.builder()
                .roomId(roomId)
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult updateRoomPartially = mvc.perform(patch(RestApiRoutes.UPDATE_ROOM_PARTIALLY+"/wrong",roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,updateRoomPartially.getResponse().getStatus());
    }

    @Test
    void deleteRoomOk() throws Exception {
        CreateRoomInput input = CreateRoomInput.builder()
                .bedCount(5)
                .bedSize(BedSize.getByCode("queenSize"))
                .bathroomType(BathroomType.getByCode("private"))
                .floor(4)
                .roomNo("13C")
                .price(BigDecimal.valueOf(125.44))
                .build();

        String serializedInput = objectMapper.writeValueAsString(input);

        MvcResult createRoomResult = mvc.perform(post(RestApiRoutes.CREATE_ROOM)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(serializedInput)
                        .characterEncoding("UTF-8"))
                .andReturn();


        CreateRoomOutput createRoomOutput = objectMapper.readValue(createRoomResult.getResponse().getContentAsString(), CreateRoomOutput.class);
        String roomId = createRoomOutput.getRoomId();

        MvcResult deleteRoom = mvc.perform(delete(RestApiRoutes.DELETE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(200,deleteRoom.getResponse().getStatus());
    }

    @Test
    void deleteRoomBadRequest() throws Exception {
        String roomId = "88f22514-aa69-4121-a47e-bfb814b3bb84";

        MvcResult deleteRoom = mvc.perform(patch(RestApiRoutes.DELETE_ROOM,roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(400,deleteRoom.getResponse().getStatus());
    }

    @Test
    void deleteRoomNotFound() throws Exception {
        String roomId = "11A";

        MvcResult deleteRoom = mvc.perform(delete(RestApiRoutes.DELETE_ROOM+"/wrong",roomId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andReturn();

        assertEquals(404,deleteRoom.getResponse().getStatus());
    }
}