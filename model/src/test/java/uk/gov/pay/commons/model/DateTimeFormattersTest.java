package uk.gov.pay.commons.model;


import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class DateTimeFormattersTest {

    @Test
    public void shouldFormatToMillisecondPrecision() {
        ZonedDateTime aZonedDateTime = ZonedDateTime.of(1969, 7, 20, 20, 18, 4, 111111111, ZoneId.of("UTC"));
        assertThat(aZonedDateTime.format(DateTimeFormatters.MILLISECOND_ISO_INSTANT_FORMATTER), is("1969-07-20T20:18:04.111Z"));
    }

}
