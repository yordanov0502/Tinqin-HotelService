package com.tinqinacademy.hotel.core.converters.guest;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.content.VisitorOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import org.springframework.stereotype.Component;

@Component
public class GuestToVisitorOutput extends BaseConverter<Guest, VisitorOutput.VisitorOutputBuilder, GuestToVisitorOutput> {
    public GuestToVisitorOutput() {
        super(GuestToVisitorOutput.class);
    }

    @Override
    protected VisitorOutput.VisitorOutputBuilder convertObj(Guest guest) {

        VisitorOutput.VisitorOutputBuilder visitorOutputBuilder = VisitorOutput.builder()
                .firstName(guest.getFirstName())
                .lastName(guest.getLastName())
                .idCardNumber(guest.getIdCardNumber())
                .idCardIssueDate(guest.getIdCardIssueDate())
                .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                .idCardValidity(guest.getIdCardValidity());

        return visitorOutputBuilder;
    }
}
