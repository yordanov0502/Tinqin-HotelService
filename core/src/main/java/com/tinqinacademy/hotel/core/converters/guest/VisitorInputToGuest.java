package com.tinqinacademy.hotel.core.converters.guest;

import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import org.springframework.stereotype.Component;

@Component
public class VisitorInputToGuest extends BaseConverter<VisitorInput, Guest, VisitorInputToGuest> {

    public VisitorInputToGuest() {
        super(VisitorInputToGuest.class);
    }

    @Override
    protected Guest convertObj(VisitorInput input) {
        Guest newGuest = Guest.builder()
                .firstName(input.getFirstName())
                .lastName(input.getLastName())
                .phoneNumber(input.getPhoneNumber())
                .dateOfBirth(input.getDateOfBirth())
                .idCardNumber(input.getIdCardNumber())
                .idCardValidity(input.getIdCardValidity())
                .idCardIssueAuthority(input.getIdCardIssueAuthority())
                .idCardIssueDate(input.getIdCardIssueDate())
                .build();

        return newGuest;
    }
}