package com.tinqinacademy.hotel.api.operations.system.getvisitors;

import lombok.*;

import java.time.LocalDate;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VisitorOutput {
    private LocalDate startDate;
    private LocalDate endDate;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String idCardNumber;
    private LocalDate idCardValidity;
    private String idCardIssueAuthority;
    private LocalDate idCardIssueDate;
    //TODO: Room which was occupied by the visitor
}
