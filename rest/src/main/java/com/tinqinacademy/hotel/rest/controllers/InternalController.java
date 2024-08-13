package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.getuseridofbooking.GetUserIdOfBookingInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.getuseridofbooking.GetUserIdOfBookingOperation;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.getuseridofbooking.GetUserIdOfBookingOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InternalController extends BaseController{

    private final GetUserIdOfBookingOperation getUserIdOfBookingOperation;


    @Operation(summary = "Get userId of booking.",
            description = "Returns the userId of a booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned userId."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.GET_USERID_OF_BOOKING)
    public ResponseEntity<?> getUserIdOfBooking(@PathVariable String bookingId) {

        GetUserIdOfBookingInput input = GetUserIdOfBookingInput.builder()
                .bookingId(bookingId)
                .build();

        Either<Errors, GetUserIdOfBookingOutput> either = getUserIdOfBookingOperation.process(input);

        return mapToResponseEntity(either, HttpStatus.OK);
    }
}
