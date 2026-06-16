package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.Unit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Converts a string value from a MongoDB document into a Unit enum value.
 *
 * @author Huw
 */
@ReadingConverter
@Component
public class UnitStringConverter implements Converter<String, Unit> {

    @Override
    public Unit convert(String value) {
        return Unit.fromString(value);
    }
}
