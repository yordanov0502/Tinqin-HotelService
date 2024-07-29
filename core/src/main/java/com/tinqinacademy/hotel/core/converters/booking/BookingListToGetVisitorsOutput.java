package com.tinqinacademy.hotel.core.converters.booking;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.content.VisitorOutput;
import com.tinqinacademy.hotel.core.converters.BaseConverter;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingListToGetVisitorsOutput extends BaseConverter<List<Booking>, GetVisitorsOutput, BookingListToGetVisitorsOutput> {


    public BookingListToGetVisitorsOutput() {
        super(BookingListToGetVisitorsOutput.class);
    }

    @Override
    protected GetVisitorsOutput convertObj(List<Booking> bookingList) {

        List<VisitorOutput> visitorOutputList = new ArrayList<>();

        for(Booking booking: bookingList){

            if(booking.getGuests().isEmpty()) {
                VisitorOutput visitorOutput = VisitorOutput.builder()
                        .startDate(booking.getStartDate())
                        .endDate(booking.getEndDate())
                        .roomNumber(booking.getRoom().getRoomNumber())
                        .build();
                visitorOutputList.add(visitorOutput);
            }
           else{
                for(Guest guest: booking.getGuests()){
                    VisitorOutput visitorOutput = VisitorOutput.builder()
                            .startDate(booking.getStartDate())
                            .endDate(booking.getEndDate())
                            .firstName(guest.getFirstName())
                            .lastName(guest.getLastName())
                            .phoneNumber(guest.getPhoneNumber())
                            .idCardNumber(guest.getIdCardNumber())
                            .idCardValidity(guest.getIdCardValidity())
                            .idCardIssueAuthority(guest.getIdCardIssueAuthority())
                            .idCardIssueDate(guest.getIdCardIssueDate())
                            .roomNumber(booking.getRoom().getRoomNumber())
                            .build();
                    visitorOutputList.add(visitorOutput);
                }
            }
        }

        GetVisitorsOutput getVisitorsOutput = GetVisitorsOutput.builder()
                .visitorOutputList(visitorOutputList)
                .build();

        return getVisitorsOutput;
    }
}
