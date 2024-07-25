package com.tinqinacademy.hotel.rest.controllers;


import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.createroom.CreateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomInput;
import com.tinqinacademy.hotel.api.operations.system.deleteroom.DeleteRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomInput;
import com.tinqinacademy.hotel.api.operations.system.updateroom.UpdateRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyInput;
import com.tinqinacademy.hotel.api.operations.system.updateroompartially.UpdateRoomPartiallyOutput;
import com.tinqinacademy.hotel.api.services.SystemService;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @Operation(summary = "Register visitors.",
            description = "Register visitors as room renters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered visitor."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @PostMapping(RestApiRoutes.REGISTER_VISITOR)
    public ResponseEntity<?> registerVisitor(@Valid @RequestBody RegisterVisitorInput input) {

        RegisterVisitorOutput output = systemService.registerVisitors(input);

        return new ResponseEntity<>(output,HttpStatus.CREATED);
    }



    @Operation(summary = "Get visitors.",
            description = "Admin only. Provides a report based on various criteria. Provides info when room was occupied and by whom. Can report when when a user has occupied rooms.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got info for visitors."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.GET_VISITORS)
    public ResponseEntity<?> getVisitors(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String idCardNumber,
            @RequestParam LocalDate idCardValidity,
            @RequestParam String idCardIssueAuthority,
            @RequestParam LocalDate idCardIssueDate,
            @RequestParam String roomNumber
    ) {

        GetVisitorsInput input = GetVisitorsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .firstName(firstName)
                .lastName(lastName)
                .idCardNumber(idCardNumber)
                .idCardValidity(idCardValidity)
                .idCardIssueAuthority(idCardIssueAuthority)
                .idCardIssueDate(idCardIssueDate)
                .roomNumber(roomNumber)
                .build();

        GetVisitorsOutput output = systemService.getVisitors(input);

        return new ResponseEntity<>(output, HttpStatus.OK);
    }



    @Operation(summary = "Create room.",
            description = "Admin creates a new room with the specified parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found."),
    })
    @PostMapping(RestApiRoutes.CREATE_ROOM)
    public ResponseEntity<?> createRoom(@Valid @RequestBody CreateRoomInput input) {

        CreateRoomOutput output = systemService.createRoom(input);
        return new ResponseEntity<>(output,HttpStatus.CREATED);
    }



    @Operation(summary = "Update room.",
            description = "Admin updates the info regarding a certain room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated info for room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @PutMapping(RestApiRoutes.UPDATE_ROOM)
    public ResponseEntity<?> updateRoom(@PathVariable String roomId, @Valid @RequestBody UpdateRoomInput inputArg) {

        UpdateRoomInput input = inputArg.toBuilder()
                .roomId(roomId)
                .build();

        UpdateRoomOutput output = systemService.updateRoom(input);

        return new ResponseEntity<>(output,HttpStatus.OK);
    }



    @Operation(summary = "Partial update for room.",
            description = "Admin partial update of room data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated room partially."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @PatchMapping(value = RestApiRoutes.UPDATE_ROOM_PARTIALLY, consumes = "application/json-patch+json")
    public ResponseEntity<?> updateRoomPartially(@PathVariable String roomId,@Valid @RequestBody UpdateRoomPartiallyInput inputArg) {

        UpdateRoomPartiallyInput input = inputArg.toBuilder()
                .roomId(roomId)
                .build();

        UpdateRoomPartiallyOutput output = systemService.updateRoomPartially(input);

        return new ResponseEntity<>(output,HttpStatus.OK);
    }



    @Operation(summary = "Delete room.",
            description = "Deletes a room.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @DeleteMapping(RestApiRoutes.DELETE_ROOM)
    public ResponseEntity<?> deleteRoom(@PathVariable String roomId) {

        DeleteRoomInput input = DeleteRoomInput.builder()
                .roomId(roomId)
                .build();

        DeleteRoomOutput output = systemService.deleteRoom(input);


        return new ResponseEntity<>(output,HttpStatus.OK);
    }

}
