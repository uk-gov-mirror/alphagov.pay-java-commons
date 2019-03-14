package uk.gov.pay.commons.model;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatters {
    public final static DateTimeFormatter MILLISECOND_ISO_INSTANT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
}
