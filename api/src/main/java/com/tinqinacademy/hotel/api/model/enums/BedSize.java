package com.tinqinacademy.hotel.api.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedSize {
    SINGLE("single"),
    SMALL_DOUBLE("smallDouble"),
    DOUBLE("double"),
    QUEEN_SIZE("queenSize"),
    KING_SIZE("kingSize"),
    UNKNOWN("");

    private final String code;

    BedSize(String code){
        this.code=code;
    }

    @JsonCreator
    public static BedSize getByCode(String code){

        return Arrays.stream(BedSize.values())
                .filter(bedSize -> bedSize.code.equals(code))
                .findFirst()
                .orElse(UNKNOWN);
    }


    @JsonValue
    public String toString(){
        return code;
    }
}
