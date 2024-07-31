package com.tinqinacademy.hotel.core.services;

import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.api.services.SystemService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemServiceImpl implements SystemService {

    private final ConversionService conversionService;
    private final EntityManager entityManager;


    private <T> void addPredicateIfPresent(List<Predicate> predicates, Optional<T> value, Function<T,Predicate> function) {
        value.ifPresent(v -> predicates.add(function.apply(v)));
    }

    private boolean matchesCriteria(Guest guest, GetVisitorsInput input) {
        return (input.getFirstName().isEmpty() || input.getFirstName().get().equals(guest.getFirstName())) &&
                (input.getLastName().isEmpty() || input.getLastName().get().equals(guest.getLastName())) &&
                (input.getPhoneNumber().isEmpty() || input.getPhoneNumber().get().equals(guest.getPhoneNumber())) &&
                (input.getIdCardNumber().isEmpty() || input.getIdCardNumber().get().equals(guest.getIdCardNumber())) &&
                (input.getIdCardValidity().isEmpty() || input.getIdCardValidity().get().equals(guest.getIdCardValidity())) &&
                (input.getIdCardIssueAuthority().isEmpty() || input.getIdCardIssueAuthority().get().equals(guest.getIdCardIssueAuthority())) &&
                (input.getIdCardIssueDate().isEmpty() || input.getIdCardIssueDate().get().equals(guest.getIdCardIssueDate()));
    }

    @Override
    public GetVisitorsOutput getVisitors(GetVisitorsInput input) {
        log.info("Start getVisitors input:{}", input);

        if(input.getStartDate().isEqual(input.getEndDate())){
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if(input.getStartDate().isAfter(input.getEndDate())){
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
        Root<Booking> booking = query.from(Booking.class);
        Join<Booking,Room> room = booking.join("room", JoinType.LEFT);
        Join<Booking,Guest> guest = booking.join("guests", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        addPredicateIfPresent(predicates, Optional.of(input.getStartDate()), startDate -> cb.greaterThanOrEqualTo(booking.get("startDate"),startDate) );
        addPredicateIfPresent(predicates, Optional.of(input.getEndDate()), endDate -> cb.lessThanOrEqualTo(booking.get("endDate"),endDate) );
        addPredicateIfPresent(predicates, input.getFirstName(), firstName -> cb.equal(guest.get("firstName"),firstName));
        addPredicateIfPresent(predicates, input.getLastName(), lastName -> cb.equal(guest.get("lastName"),lastName));
        addPredicateIfPresent(predicates, input.getPhoneNumber(), phoneNumber -> cb.equal(guest.get("phoneNumber"),phoneNumber));
        addPredicateIfPresent(predicates, input.getIdCardNumber(), idCardNumber -> cb.equal(guest.get("idCardNumber"),idCardNumber));
        addPredicateIfPresent(predicates, input.getIdCardValidity(), idCardValidity -> cb.equal(guest.get("idCardValidity"),idCardValidity));
        addPredicateIfPresent(predicates, input.getIdCardIssueAuthority(), idCardAuthority -> cb.equal(guest.get("idCardIssueAuthority"),idCardAuthority));
        addPredicateIfPresent(predicates, input.getIdCardIssueDate(), idCardIssueDate -> cb.equal(guest.get("idCardIssueDate"),idCardIssueDate));
        addPredicateIfPresent(predicates, input.getRoomNumber(), roomNumber -> cb.equal(room.get("roomNumber"),roomNumber));

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        List<Booking> bookingList = entityManager.createQuery(query).getResultList();

        for (Booking b : bookingList) {
            Set<Guest> filteredGuests = b.getGuests()
                    .stream()
                    .filter(g -> matchesCriteria(g, input))
                    .collect(Collectors.toSet());
            b.setGuests(filteredGuests);
        }

        GetVisitorsOutput output = conversionService.convert(bookingList,GetVisitorsOutput.class);

        log.info("End getVisitors output:{}", output);

        return output;
    }

}