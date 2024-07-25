package com.tinqinacademy.hotel.core.converters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseConverter<S,T,C> implements Converter<S,T> {

    private final Class<C> type;

    @Override
    public T convert(S source){
        log.info("Start "+type.getSimpleName()+" convert input{}:",source);
        T result = convertObj(source);
        log.info("End "+type.getSimpleName()+" convert output{}:",result);
        return result;
    }

    protected abstract T convertObj(S source);
}
