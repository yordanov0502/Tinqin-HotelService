package com.tinqinacademy.hotel.restexport;


import com.tinqinacademy.hotel.api.RestApiRoutes;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.bookroom.BookRoomOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getroom.RoomInfoOutput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomInput;
import com.tinqinacademy.hotel.api.operations.hotel.unbookroom.UnbookRoomOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
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

    @RequestLine("POST "+RestApiRoutes.REGISTER_VISITOR)
    RegisterVisitorOutput registerVisitors(@RequestBody RegisterVisitorInput input);

    @RequestLine("GET /api/v1/system/register?startDate={startDate}&endDate={endDate}&firstName={firstName}&" +
            "lastName={lastName}&phoneNumber={phoneNumber}&idCardNumber={idCardNumber}&idCardValidity={idCardValidity}&" +
            "idCardIssueAuthority={idCardIssueAuthority}&idCardIssueDate={idCardIssueDate}&roomNumber={roomNumber}")
    GetVisitorsOutput getVisitors(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("phoneNumber") String phoneNumber,
            @Param("idCardNumber") String idCardNumber,
            @Param("idCardValidity") LocalDate idCardValidity,
            @Param("idCardIssueAuthority") String idCardIssueAuthority,
            @Param("idCardIssueDate") LocalDate idCardIssueDate,
            @Param("roomNumber") String roomNumber
    );
}