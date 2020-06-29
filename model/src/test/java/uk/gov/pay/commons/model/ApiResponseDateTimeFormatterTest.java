package uk.gov.pay.commons.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static java.time.Month.OCTOBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.gov.pay.commons.model.ApiResponseDateTimeFormatter.ISO_INSTANT_MILLISECOND_PRECISION;

public class ApiResponseDateTimeFormatterTest {

    @Test
    public void shouldConvertUtcToIsoStringWithMillisecondPrecision() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.of(2015, OCTOBER, 21),
                LocalTime.of(7, 28, 0, 123456789), ZoneOffset.UTC);

        String result = ISO_INSTANT_MILLISECOND_PRECISION.format(zonedDateTime);

        assertThat(result, is("2015-10-21T07:28:00.123Z"));
    }

    @Test
    public void shouldConvertNonUtcToIsoStringWithMillisecondPrecision() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.of(2015, OCTOBER, 21),
                LocalTime.of(7, 28, 0, 123456789), ZoneId.of("America/Los_Angeles"));

        String result = ISO_INSTANT_MILLISECOND_PRECISION.format(zonedDateTime);

        assertThat(result, is("2015-10-21T14:28:00.123Z"));
    }

    @Test
    public void shouldConvertExactlyOnTheSecondToIsoStringWithMillisecondPrecision() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.of(2015, OCTOBER, 21),
                LocalTime.of(7, 28, 0), ZoneOffset.UTC);

        String result = ISO_INSTANT_MILLISECOND_PRECISION.format(zonedDateTime);

        assertThat(result, is("2015-10-21T07:28:00.000Z"));
    }

}
