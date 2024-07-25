package com.tinqinacademy.hotel.persistence.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BedSize {
    SINGLE("single",1),
    SMALL_DOUBLE("smallDouble",2),
    DOUBLE("double",2),
    QUEEN_SIZE("queenSize",3),
    KING_SIZE("kingSize",4),
    UNKNOWN("",0);

    private final String code;
    private final Integer capacity;

    BedSize(String code,Integer capacity){
        this.code=code;
        this.capacity=capacity;
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
