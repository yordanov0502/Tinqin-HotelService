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
        log.info(String.format("Start %s convert input: %s", type.getSimpleName(),source));
        T target = convertObj(source);
        log.info(String.format("End %s convert output: %s", type.getSimpleName(),target));
        return target;
    }

    protected abstract T convertObj(S source);
}
