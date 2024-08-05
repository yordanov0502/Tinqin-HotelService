package com.tinqinacademy.hotel.core.operations.system;

import com.tinqinacademy.hotel.api.exceptions.Errors;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorInput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOperation;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.RegisterVisitorOutput;
import com.tinqinacademy.hotel.api.operations.system.registervisitor.content.VisitorInput;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.api.exceptions.custom.DuplicateValueException;
import com.tinqinacademy.hotel.api.exceptions.custom.NotFoundException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.entity.Booking;
import com.tinqinacademy.hotel.persistence.model.entity.Guest;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import com.tinqinacademy.hotel.persistence.repository.GuestRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RegiserVisitorOperationProcessor extends BaseOperationProcessor implements RegisterVisitorOperation {

    private final GuestRepository guestRepository;
    private final BookingRepository bookingRepository;

    public RegiserVisitorOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, GuestRepository guestRepository, BookingRepository bookingRepository) {
        super(conversionService, exceptionService, validator);
        this.guestRepository = guestRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @Override
    public Either<Errors, RegisterVisitorOutput> process(RegisterVisitorInput input) {

        return Try.of(() -> {
            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);

            List<String> idCardNumberList = getListOfIdCardNumbers(input.getVisitorInputList());
            Map<String, Guest> existingGuestsMap = getExistingGuestsByIdCardNumbers(idCardNumberList);

            input.getVisitorInputList().forEach(visitor -> {

                Booking booking = bookingRepository
                        .findByRoomIdAndStartDateAndEndDate(
                                UUID.fromString(visitor.getRoomId()),
                                visitor.getStartDate(),
                                visitor.getEndDate())
                        .orElseThrow(() -> new NotFoundException(String.format("Booking with roomId[%s] ,start date: %s and " +
                                "end date: %s doesn't exist.",visitor.getRoomId(),visitor.getStartDate(),visitor.getEndDate())));

                Guest guest;
                if(visitor.getIdCardNumber() != null && existingGuestsMap.containsKey(visitor.getIdCardNumber())){
                    guest = existingGuestsMap.get(visitor.getIdCardNumber());
                }
                else{
                    checkForExistingPhoneNumber(visitor.getPhoneNumber());
                    guest = conversionService.convert(visitor, Guest.class);
                    guestRepository.save(guest);
                }
                booking.getGuests().add(guest);
                bookingRepository.save(booking);
            });

            RegisterVisitorOutput output = RegisterVisitorOutput.builder().build();

            log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),output));

            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private void checkForExistingPhoneNumber(String phoneNumber) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),phoneNumber));

        if(guestRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateValueException(String.format("Phone number: %s already exists in the database.",phoneNumber));
        }

        log.info(String.format("End %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName()));
    }

    private List<String> getListOfIdCardNumbers(List<VisitorInput> visitorInputList){
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),visitorInputList));

        List<String> idCardNumberList = visitorInputList
                .stream()
                .map(VisitorInput::getIdCardNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),idCardNumberList));
        return idCardNumberList;
    }

    private Map<String,Guest> getExistingGuestsByIdCardNumbers(List<String> idCardNumberList) {
        log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),idCardNumberList));

        Map<String, Guest> existingGuestsMap = guestRepository
                .findAllByIdCardNumberIn(idCardNumberList)
                .stream()
                .collect(Collectors.toMap(Guest::getIdCardNumber, guest -> guest));

        log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),existingGuestsMap));
        return existingGuestsMap;
    }

}