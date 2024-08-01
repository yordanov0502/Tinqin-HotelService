package com.tinqinacademy.hotel.core.operations.system;

import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorOperation;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsInput;
import com.tinqinacademy.hotel.api.operations.system.getvisitors.GetVisitorsOutput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import com.tinqinacademy.hotel.persistence.model.entity.Room;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GetVisitorsOperationProcessor extends BaseOperationProcessor implements GetVisitorOperation {

    private final EntityManager entityManager;

    public GetVisitorsOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, EntityManager entityManager) {
        super(conversionService, exceptionService, validator);
        this.entityManager = entityManager;
    }

    @Override
    public Either<Errors, GetVisitorsOutput> process(GetVisitorsInput input) {

        return Try.of(() -> {
            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);
            validateBookingDates(input.getStartDate(), input.getEndDate());

            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Booking> query = cb.createQuery(Booking.class);
            Root<Booking> booking = query.from(Booking.class);
            Join<Booking, Room> room = booking.join("room", JoinType.LEFT);
            Join<Booking, Guest> guest = booking.join("guests", JoinType.LEFT);

            List<Predicate> predicates = buildPredicates(cb, booking, room, guest, input);
            query.where(cb.and(predicates.toArray(new Predicate[0])));

            List<Booking> bookingList = entityManager.createQuery(query).getResultList();
            filterGuests(bookingList, input);

            GetVisitorsOutput output = conversionService.convert(bookingList, GetVisitorsOutput.class);
            log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),output));

            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private void validateBookingDates(LocalDate startDate, LocalDate endDate) {

        log.info(String.format("Start %s %s input: %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),startDate,endDate));

        if (startDate.isEqual(endDate)) {
            throw new BookingDatesException("Start date and end date of booking cannot be equal.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        log.info(String.format("End %s %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Booking> booking, Join<Booking, Room> room, Join<Booking, Guest> guest, GetVisitorsInput input) {

        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),input));

        List<Predicate> predicates = new ArrayList<>();

        addPredicateIfPresent(predicates, Optional.of(input.getStartDate()), startDate -> cb.greaterThanOrEqualTo(booking.get("startDate"), startDate));
        addPredicateIfPresent(predicates, Optional.of(input.getEndDate()), endDate -> cb.lessThanOrEqualTo(booking.get("endDate"), endDate));
        addPredicateIfPresent(predicates, input.getFirstName(), firstName -> cb.equal(guest.get("firstName"), firstName));
        addPredicateIfPresent(predicates, input.getLastName(), lastName -> cb.equal(guest.get("lastName"), lastName));
        addPredicateIfPresent(predicates, input.getPhoneNumber(), phoneNumber -> cb.equal(guest.get("phoneNumber"), phoneNumber));
        addPredicateIfPresent(predicates, input.getIdCardNumber(), idCardNumber -> cb.equal(guest.get("idCardNumber"), idCardNumber));
        addPredicateIfPresent(predicates, input.getIdCardValidity(), idCardValidity -> cb.equal(guest.get("idCardValidity"), idCardValidity));
        addPredicateIfPresent(predicates, input.getIdCardIssueAuthority(), idCardAuthority -> cb.equal(guest.get("idCardIssueAuthority"), idCardAuthority));
        addPredicateIfPresent(predicates, input.getIdCardIssueDate(), idCardIssueDate -> cb.equal(guest.get("idCardIssueDate"), idCardIssueDate));
        addPredicateIfPresent(predicates, input.getRoomNumber(), roomNumber -> cb.equal(room.get("roomNumber"), roomNumber));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),predicates));

        return predicates;
    }

    private void filterGuests(List<Booking> bookingList, GetVisitorsInput input) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),input));
        for (Booking booking : bookingList) {
            Set<Guest> filteredGuests = booking.getGuests()
                    .stream()
                    .filter(g -> matchesCriteria(g, input))
                    .collect(Collectors.toSet());
            booking.setGuests(filteredGuests);
        }
        log.info(String.format("End %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private boolean matchesCriteria(Guest guest, GetVisitorsInput input) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),input));

        boolean isCriteriaMatched =
                (input.getFirstName().isEmpty() || input.getFirstName().get().equals(guest.getFirstName())) &&
                (input.getLastName().isEmpty() || input.getLastName().get().equals(guest.getLastName())) &&
                (input.getPhoneNumber().isEmpty() || input.getPhoneNumber().get().equals(guest.getPhoneNumber())) &&
                (input.getIdCardNumber().isEmpty() || input.getIdCardNumber().get().equals(guest.getIdCardNumber())) &&
                (input.getIdCardValidity().isEmpty() || input.getIdCardValidity().get().equals(guest.getIdCardValidity())) &&
                (input.getIdCardIssueAuthority().isEmpty() || input.getIdCardIssueAuthority().get().equals(guest.getIdCardIssueAuthority())) &&
                (input.getIdCardIssueDate().isEmpty() || input.getIdCardIssueDate().get().equals(guest.getIdCardIssueDate()));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),isCriteriaMatched));
        return isCriteriaMatched;
    }

    private <T> void addPredicateIfPresent(List<Predicate> predicates, Optional<T> value, Function<T, Predicate> function) {
        log.info(String.format("Start %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
        value.ifPresent(v -> predicates.add(function.apply(v)));
        log.info(String.format("End %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }
}
