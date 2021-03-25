package uk.gov.service.payments.common.model;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class ApiResponseDateTimeFormatter {

    /**
     * DateTimeFormatter that produces a standard ISO-8601 format date and time
     * in UTC with millisecond precision (three fractional second digits, zero
     * right-padded if necessary), for example 2015-10-21T07:28:00.000Z
     */
    public static final DateTimeFormatter ISO_INSTANT_MILLISECOND_PRECISION =
            new DateTimeFormatterBuilder().appendInstant(3).toFormatter(Locale.ENGLISH);

}
