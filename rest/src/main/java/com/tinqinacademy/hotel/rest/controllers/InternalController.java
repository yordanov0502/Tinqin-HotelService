package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdInput;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdOperation;
import com.tinqinacademy.hotel.api.operations.internal.getroomid.GetRoomIdOutput;
import com.tinqinacademy.hotel.api.operations.internal.isroomexists.IsRoomExistsInput;
import com.tinqinacademy.hotel.api.operations.internal.isroomexists.IsRoomExistsOperation;
import com.tinqinacademy.hotel.api.operations.internal.isroomexists.IsRoomExistsOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class InternalController extends BaseController{

    private final IsRoomExistsOperation isRoomExistsOperation;
    private final GetRoomIdOperation getRoomIdOperation;

    @Operation(summary = "Checks whether room with id exists.",
            description = "Room by id is searched, and if room with such id doesn't exist, then an error is sent.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Room exists."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.IS_ROOM_EXISTS)
    public ResponseEntity<?> isRoomExists(@PathVariable String roomId) {
        IsRoomExistsInput input = IsRoomExistsInput.builder()
                .roomId(roomId)
                .build();
        Either<Errors, IsRoomExistsOutput> either = isRoomExistsOperation.process(input);
        return mapToResponseEntity(either, HttpStatus.OK);
    }

    @Operation(summary = "Get roomId by room number.",
            description = "Returns roomId of the room with the given number.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got roomId."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.GET_ROOMID_BY_NUMBER)
    public ResponseEntity<?> getRoomIdByNumber(@PathVariable String roomNumber) {
        GetRoomIdInput input = GetRoomIdInput.builder()
                .roomNumber(roomNumber)
                .build();
        Either<Errors, GetRoomIdOutput> either = getRoomIdOperation.process(input);
        return mapToResponseEntity(either, HttpStatus.OK);
    }
}
