package com.tinqinacademy.hotel.persistence.initializr;

import com.tinqinacademy.hotel.persistence.model.entity.Bed;
import com.tinqinacademy.hotel.persistence.model.entity.User;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Order(2)
@RequiredArgsConstructor
@Component
public class UserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Start UserInitializer.");

        List<User> users = userRepository.findAll();

        if(users.isEmpty()){
            User newUser = User.builder()
                    .username("yordanov5.0")
                    .email("yordanovtodor281@gmail.com")
                    .password("12345")
                    .firstName("Todor")
                    .lastName("Yordanov")
                    .phoneNumber("0882960199")
                    .dateOfBirth(LocalDate.of(2001,6,30))
                    .build();

            userRepository.save(newUser);
        }

        log.info("End UserInitializer.");
    }
}
