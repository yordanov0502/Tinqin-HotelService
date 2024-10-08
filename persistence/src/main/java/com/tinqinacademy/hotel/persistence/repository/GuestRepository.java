package com.tinqinacademy.hotel.persistence.repository;

import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GuestRepository extends JpaRepository<Guest, UUID> {
    boolean existsByPhoneNumber(String phoneNumber);
    List<Guest> findAllByIdCardNumberIn(List<String> idCardNumbers);
}