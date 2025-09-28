package com.snookerup.model;

import java.text.SimpleDateFormat;

/**
 * Stores the various date-time formats used throughout the application.
 *
 * @author Huw
 */
public interface DateTimeFormats {

    /** Long-form date, e.g. "Friday, 1 August 2025". */
    SimpleDateFormat LONG_FORM_DATE_FORMAT = new SimpleDateFormat("EEEE, d MMMM yyyy");

    /** Short-form date, e.g. "1/8/25". */
    SimpleDateFormat SHORT_FORM_DATE_FORMAT = new SimpleDateFormat("d/M/yy");

    /** Short-form date and time, e.g. "1/8/25 12:25". */
    SimpleDateFormat SHORT_FORM_DATE_AND_TIME_FORMAT = new SimpleDateFormat("d/M/yy HH:mm");

    /** Time format, e.g. "18:30". */
    SimpleDateFormat JUST_TIME_FORMAT = new SimpleDateFormat("HH:mm");
}
