package com.tinqinacademy.hotel.restexport;


import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;


@Headers({"Content-Type: application/json"})
public interface HotelRestExport {

    @RequestLine("GET /api/v1/hotel/rooms?startDate={startDate}&endDate={endDate}&bedCount={bedCount}&bedSize={bedSize}&bathroomType={bathroomType}")
    AvailableRoomsIdsOutput getAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("bedCount") Integer bedCount,
            @Param("bedSize") String bedSize,
            @Param("bathroomType") String bathroomType);

    @RequestLine("GET "+RestApiRoutes.GET_INFO_FOR_ROOM)
    RoomInfoOutput getRoomById(@Param String roomId);

    @RequestLine("POST "+RestApiRoutes.BOOK_ROOM)
    BookRoomOutput bookRoom(@Param String roomId, @RequestBody BookRoomInput input);

    @RequestLine("DELETE "+RestApiRoutes.UNBOOK_ROOM)
    UnbookRoomOutput unbookRoom(@Param String bookingId, @RequestBody UnbookRoomInput input);
}