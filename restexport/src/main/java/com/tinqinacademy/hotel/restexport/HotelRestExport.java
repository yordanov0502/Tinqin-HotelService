package com.tinqinacademy.hotel.restexport;


import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;


@Headers({"Content-Type: application/json"})
public interface HotelRestExport {

    @RequestLine("GET "+RestApiRoutes.GET_INFO_FOR_ROOM)
    RoomInfoOutput getRoomById(@Param String roomId);

    @RequestLine("POST "+RestApiRoutes.BOOK_ROOM)
    BookRoomOutput bookRoom(@Param String roomId, @RequestBody BookRoomInput input);


}