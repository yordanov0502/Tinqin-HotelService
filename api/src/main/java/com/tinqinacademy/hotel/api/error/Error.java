package com.tinqinacademy.hotel.api.error;

import lombok.*;
import org.springframework.http.HttpStatus;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String errMsg;
    private HttpStatus httpStatus;
}
