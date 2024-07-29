package com.tinqinacademy.hotel.api.operations.system.getvisitors;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Optional;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class GetVisitorsInput {
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Optional<String> firstName;
    private Optional<String> lastName;
    private Optional<String> phoneNumber;
    private Optional<String> idCardNumber;
    private Optional<LocalDate> idCardValidity;
    private Optional<String> idCardIssueAuthority;
    private Optional<LocalDate> idCardIssueDate;
    private Optional<String> roomNumber;

}
