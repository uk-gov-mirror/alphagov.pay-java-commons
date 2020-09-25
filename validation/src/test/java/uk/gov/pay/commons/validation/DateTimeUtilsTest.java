package uk.gov.pay.commons.validation;

import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateTimeUtilsTest {

    public static final DateTimeFormatter ISO_INSTANT_MILLISECOND_PRECISION =
            new DateTimeFormatterBuilder()
                    .appendInstant(3)
                    .toFormatter(Locale.ENGLISH)
                    .withZone(ZoneOffset.UTC);
    
    @Test
    public void shouldConvertUTCZonedDateTimeToAISO_8601_UTCString() {
        ZonedDateTime localDateTime = ZonedDateTime.of(2010, 11, 13, 12, 0, 0, 999, ZoneId.of("Z"));

        String dateString = ISO_INSTANT_MILLISECOND_PRECISION.format(localDateTime);
        assertThat(dateString, is("2010-11-13T12:00:00.000Z"));
    }

    @Test
    public void shouldConvertUTCZonedDateTimeToLocalDateString() {
        ZonedDateTime localDateTime = ZonedDateTime.of(2010, 11, 13, 12, 0, 0, 0, ZoneId.of("Z"));

        String dateString = DateTimeUtils.toLocalDateString(localDateTime);
        assertThat(dateString, is("2010-11-13"));
    }

    @Test
    public void shouldConvertNonUTCZonedDateTimeToAISO_8601_UTCString() {
        ZonedDateTime localDateTime = ZonedDateTime.of(2010, 11, 13, 12, 0, 0, 999, ZoneId.of("Europe/Paris"));

        String dateString = ISO_INSTANT_MILLISECOND_PRECISION.format(localDateTime);
        assertThat(dateString, is("2010-11-13T11:00:00.000Z"));
    }

    @Test
    public void shouldConvertUTCZonedISO_8601StringToADateTime() {
        String aDate = "2010-01-01T12:00:00Z";
        Optional<ZonedDateTime> result = DateTimeUtils.toUTCZonedDateTime(aDate);
        assertTrue(result.isPresent());
        assertThat(result.get().toString(), endsWith("Z"));

        aDate = "2010-12-31T23:59:59.132Z";
        result = DateTimeUtils.toUTCZonedDateTime(aDate);
        assertTrue(result.isPresent());
        assertThat(result.get().toString(), endsWith("Z"));
    }

    @Test
    public void shouldConvertNonUTCZonedISO_8601StringToADateTime() {
        String aDate = "2010-01-01T12:00:00+01:00[Europe/Paris]";
        Optional<ZonedDateTime> result = DateTimeUtils.toUTCZonedDateTime(aDate);
        assertTrue(result.isPresent());
        assertThat(result.get().toString(), endsWith("Z"));

        aDate = "2010-12-31T23:59:59.132+01:00[Europe/Paris]";
        result = DateTimeUtils.toUTCZonedDateTime(aDate);
        assertTrue(result.isPresent());
        assertThat(result.get().toString(), endsWith("Z"));
    }

    @Test
    public void shouldCovertNonZonedDateStringZonedISO_8601StringToADateTime() {
        String date = "2020-09-25";
        Optional<ZonedDateTime> result = DateTimeUtils.fromLocalDateOnlyString(date);
        assertThat(result.isPresent(), is(true));
        assertThat(result.get().toString(), is("2020-09-25T00:00Z"));
    }

    @Test
    public void shouldNotCovertNonZonedDateStringToADateTimeIfNotISO_8601() {
        String date = "2020/09/25";
        Optional<ZonedDateTime> result = DateTimeUtils.fromLocalDateOnlyString(date);
        assertThat(result.isPresent(), is(false));
    }
}
