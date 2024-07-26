package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.model.other.BookedInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId")
    List<Booking> findAllByRoomId(@Param("roomId") UUID roomId);

    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.room = NULL WHERE b.room.id = :roomId")
    void setRoomToNullForBookingsByRoomId(@Param("roomId") UUID roomId);

    Optional<Booking> findByRoomIdAndStartDateAndEndDate(UUID roomId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId AND " +
            "((b.startDate < :startDate AND b.endDate >= :startDate AND b.endDate <= :endDate) OR " +
            "(b.startDate >= :startDate AND b.endDate <= :endDate) OR " +
            "(b.startDate >= :startDate AND b.startDate <= :endDate AND b.endDate > :endDate) OR " +
            "(b.startDate < :startDate AND b.endDate > :endDate) OR " +
            "(:startDate < b.startDate AND :endDate > b.endDate))")
    boolean isRoomBooked(@Param("roomId") UUID roomId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("select new com.tinqinacademy.hotel.persistence.model.other.BookedInterval(b.startDate, b.endDate) " +
            "from Booking b where b.room.id = :roomId")
    List<BookedInterval> findAllBookedIntervalsByRoomId(@Param("roomId") UUID roomId);

    @Query("SELECT r.id FROM Room r " +
            "WHERE (:bedCount IS NULL OR :bedCount = SIZE(r.beds)) AND " +
            "(:bedSize IS NULL OR :bedSize IN (SELECT b.bedSize FROM r.beds b)) AND " +
            "(:bathroomType IS NULL OR r.bathroomType = :bathroomType) AND " +
            "NOT EXISTS (" +
            "    SELECT 1 FROM Booking b " +
            "    WHERE b.room = r AND (" +
            "        (b.startDate >= :startDate AND b.startDate <= :endDate) OR " +
            "        (b.endDate >= :startDate AND b.endDate <= :endDate) OR " +
            "        (b.startDate <= :startDate AND b.endDate >= :endDate)" +
            "    )" +
            ")"
    )
    List<UUID> findAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("bedCount") Integer bedCount,
            @Param("bedSize") BedSize bedSize,
            @Param("bathroomType") BathroomType bathroomType
    );
}