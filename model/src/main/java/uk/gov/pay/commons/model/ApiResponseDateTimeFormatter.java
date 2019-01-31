package uk.gov.pay.commons.model;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

/**
 * Custom ZonedDateTime formatter that will format to millisecond precision.
 * This will be padding with zeroes missing milliseconds and will drop
 * anything after the milliseconds field
 */
public class ApiResponseDateTimeFormatter {

    public static final DateTimeFormatter ISO_INSTANT_MILLISECOND_PRECISION =
            new DateTimeFormatterBuilder()
                    .appendInstant(3)
                    .toFormatter(Locale.ENGLISH)
                    .withZone(ZoneOffset.UTC);

}
