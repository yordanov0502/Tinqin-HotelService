package com.tinqinacademy.hotel.persistence.initializr;

import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BedRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Order(1)
@RequiredArgsConstructor
@Component
public class BedInitializer implements ApplicationRunner {

    private final BedRepository bedRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start BedInitializer.");
        for (BedSize bedSize : BedSize.values()) {

            if (bedSize.getCode().isEmpty()) {
                continue;
            }

            if (!bedRepository.existsByBedSize(bedSize)) {
                Bed bed = Bed.builder()
                        .bedSize(bedSize)
                        .capacity(bedSize.getCapacity())
                        .build();
                bedRepository.save(bed);
            }
        }
        log.info("End BedInitializer.");
    }
}