package com.snookerup.model.converters;

import com.snookerup.model.db.nosql.BallStriking;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

/**
 * Converts a string value from a MongoDB document into a BallStriking enum value.
 *
 * @author Huw
 */
@ReadingConverter
@Component
public class BallStrikingStringConverter implements Converter<String, BallStriking> {

    @Override
    public BallStriking convert(String value) {
        return BallStriking.fromString(value);
    }
}
