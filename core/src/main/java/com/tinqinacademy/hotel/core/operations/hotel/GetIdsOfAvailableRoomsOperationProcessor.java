package com.tinqinacademy.hotel.core.operations.hotel;

import com.tinqinacademy.hotel.api.error.Errors;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.AvailableRoomsIdsOutput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsInput;
import com.tinqinacademy.hotel.api.operations.hotel.getavailablerooms.GetIdsOfAvailableRoomsOperation;
import com.tinqinacademy.hotel.core.exceptions.ExceptionService;
import com.tinqinacademy.hotel.core.exceptions.custom.BookingDatesException;
import com.tinqinacademy.hotel.core.operations.BaseOperationProcessor;
import com.tinqinacademy.hotel.core.utils.LoggingUtils;
import com.tinqinacademy.hotel.persistence.model.enums.BathroomType;
import com.tinqinacademy.hotel.persistence.model.enums.BedSize;
import com.tinqinacademy.hotel.persistence.repository.BookingRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GetIdsOfAvailableRoomsOperationProcessor extends BaseOperationProcessor implements GetIdsOfAvailableRoomsOperation {

    private final BookingRepository bookingRepository;

    public GetIdsOfAvailableRoomsOperationProcessor(ConversionService conversionService, ExceptionService exceptionService, Validator validator, BookingRepository bookingRepository) {
        super(conversionService, exceptionService, validator);
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Either<Errors, AvailableRoomsIdsOutput> process(GetIdsOfAvailableRoomsInput input) {

        return Try.of(() -> {
            log.info(String.format("Start %s %s input: %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName(),input));

            validate(input);
            validateDates(input.getStartDate(),input.getEndDate());

            List<UUID> availableRoomIds = bookingRepository
                    .findAvailableRooms(
                            input.getStartDate(),
                            input.getEndDate(),
                            input.getBedCount(),
                            input.getBedSize() != null ? BedSize.getByCode(input.getBedSize().toString()) : null,
                            input.getBathroomType() != null ? BathroomType.getByCode(input.getBathroomType().toString()) : null);

            AvailableRoomsIdsOutput output = conversionService.convert(availableRoomIds, AvailableRoomsIdsOutput.class);

            log.info(String.format("End %s %s output: %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),output));

            return output;})
                .toEither()
                .mapLeft(exceptionService::handle);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {

        log.info(String.format("Start %s %s input: %s %s", this.getClass().getSimpleName(),LoggingUtils.getMethodName(),startDate,endDate));

        if (startDate.isEqual(endDate)) {
            throw new BookingDatesException("Start date and end date cannot be equal.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BookingDatesException("Start date cannot be after the end date.");
        }

        log.info(String.format("End %s %s", this.getClass().getSimpleName(), LoggingUtils.getMethodName()));
    }

}