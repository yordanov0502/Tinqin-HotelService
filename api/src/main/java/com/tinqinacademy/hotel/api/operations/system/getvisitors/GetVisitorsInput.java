package com.tinqinacademy.hotel.api.operations.system.getvisitors;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

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
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String idCardNumber;
    @NotNull
    private LocalDate idCardValidity;
    @NotBlank
    private String idCardIssueAuthority;
    @NotNull
    private LocalDate idCardIssueDate;
    @NotBlank
    private String roomNumber;

}
