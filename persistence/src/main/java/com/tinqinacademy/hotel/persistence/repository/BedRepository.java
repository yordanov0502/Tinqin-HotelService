package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BedRepository extends JpaRepository<Bed, UUID> {
    Optional<Bed> findByBedSize(BedSize bedSize);
    boolean existsByBedSize(BedSize bedSize);
}
