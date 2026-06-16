package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.ScoreUnit;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Converts a string value from a MongoDB document into a ScoreUnit enum value.
 *
 * @author Huw
 */
@ReadingConverter
@Component
public class ScoreUnitStringConverter implements Converter<String, ScoreUnit> {

    @Override
    public ScoreUnit convert(String value) {
        return ScoreUnit.fromString(value);
    }
}
