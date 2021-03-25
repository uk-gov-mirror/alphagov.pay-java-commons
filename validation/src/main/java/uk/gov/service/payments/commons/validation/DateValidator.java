package uk.gov.service.payments.commons.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return isValid(value);
    }

    public static boolean isValid(String value) {
        return isBlank(value) || DateTimeUtils.toUTCZonedDateTime(value).isPresent();
    }
}
