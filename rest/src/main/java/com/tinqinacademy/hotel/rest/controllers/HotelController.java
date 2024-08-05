package com.tinqinacademy.hotel.rest.controllers;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.model.enums.BathroomType;
import com.tinqinacademy.hotel.api.model.enums.BedSize;



import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsOperation;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.GetRoomOperation;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoInput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookOperation;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.RestApiRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class HotelController extends BaseController{

    private final GetIdsOfAvailableRoomsOperation getAvailableRoomsOperation;
    private final GetRoomOperation getRoomOperation;
    private final BookRoomOperation bookRoomOperation;
    private final UnbookOperation unbookOperation;

    @Operation(summary = "Get ids of available rooms.",
            description = "Checks whether a room is available for a certain period. Bed requirements should come as query parameters in URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned list with ids of available rooms."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.GET_IDS_OF_AVAILABLE_ROOMS)
    public ResponseEntity<?> getIdsOfAvailableRooms(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(required = false) Integer bedCount,
            @RequestParam(required = false) String bedSize,
            @RequestParam(required = false) String bathroomType) {

        GetIdsOfAvailableRoomsInput input = GetIdsOfAvailableRoomsInput.builder()
                .startDate(startDate)
                .endDate(endDate)
                .bedCount(bedCount)
                .bedSize(bedSize != null ? BedSize.getByCode(bedSize) : null)
                .bathroomType(bathroomType != null ? BathroomType.getByCode(bathroomType) : null)
                .build();

        Either<Errors,AvailableRoomsIdsOutput> either = getAvailableRoomsOperation.process(input);

        return mapToResponseEntity(either,HttpStatus.OK);
    }



    @Operation(summary = "Get info for room by its id.",
            description = "Returns basic info for a room with the specific id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned info for a room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @GetMapping(RestApiRoutes.GET_INFO_FOR_ROOM)
    public ResponseEntity<?> getRoomById(@PathVariable String roomId) {

        RoomInfoInput input = RoomInfoInput.builder()
                .roomId(roomId)
                .build();

        Either<Errors,RoomInfoOutput> either = getRoomOperation.process(input);

        return mapToResponseEntity(either,HttpStatus.OK);
    }


    @Operation(summary = "Book a room.",
            description = "Books the room specified.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully booked room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @PostMapping(RestApiRoutes.BOOK_ROOM)
    public ResponseEntity<?> bookRoom(@PathVariable String roomId, @RequestBody BookRoomInput inputArg) {

        BookRoomInput input = inputArg.toBuilder()
                .roomId(roomId)
                .build();

        Either<Errors,BookRoomOutput> either = bookRoomOperation.process(input);

        return mapToResponseEntity(either,HttpStatus.CREATED);
        }



    @Operation(summary = "Unbook a room from hotel.",
            description = "Unbooks a room that the user has already been booked.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully unbooked room."),
            @ApiResponse(responseCode = "400", description = "Bad request."),
            @ApiResponse(responseCode = "404", description = "Not found.")
    })
    @DeleteMapping(RestApiRoutes.UNBOOK_ROOM)
    public ResponseEntity<?> unbookRoom(@PathVariable String bookingId) {

        UnbookRoomInput input = UnbookRoomInput.builder()
                .bookingId(bookingId)
                .build();

        Either<Errors,UnbookRoomOutput> either = unbookOperation.process(input);

        return mapToResponseEntity(either,HttpStatus.OK);
    }



}
