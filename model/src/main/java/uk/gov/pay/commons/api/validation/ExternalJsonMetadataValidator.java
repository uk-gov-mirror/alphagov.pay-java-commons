package uk.gov.pay.commons.api.validation;

import com.fasterxml.jackson.databind.JsonNode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ExternalJsonMetadataValidator implements ConstraintValidator<ValidExternalJsonMetadata, JsonNode> {

    private static final int MAXIMUM_KEYS = 10;
    private static final int MAXIMUM_VALUE_LENGTH = 50;
    private static final int MAXIMUM_KEY_LENGTH = 30;

    @Override
    public boolean isValid(JsonNode jsonNode, ConstraintValidatorContext constraintValidatorContext) {

        if (jsonNode == null) {
            return true;
        }

        constraintValidatorContext.disableDefaultConstraintViolation();

        if (jsonNode.isArray()) {
            addConstraint("must be an object of JSON key-value pairs", constraintValidatorContext);
            return false;
        }

        var errorMessages = validateKeyValuePairs(jsonNode);

        if (errorMessages.size() > 0) {
            addConstraints(errorMessages, constraintValidatorContext);
            return false;
        }

        return true;
    }

    private Set<String> validateKeyValuePairs(JsonNode keyPairs) {
        Set<String> errorMessages = new HashSet<>();

        if (keyPairs.size() > MAXIMUM_KEYS) {
            errorMessages.add(String.format("cannot have more than %d key-value pairs", MAXIMUM_KEYS));
        }

        keyPairs.fields()
                .forEachRemaining(
                        (entry) -> errorMessages.addAll(validateKeyAndValue(entry.getKey(), entry.getValue()))
                );

        return errorMessages;
    }

    private Set<String> validateKeyAndValue(String key, JsonNode value) {
        Set<String> errorMessages = new HashSet<>();

        if (key == null) {
            errorMessages.add("keys must not be null");
        } else if (keyLengthIsInvalid(key)) {
            errorMessages.add(String.format("keys must be between 1 and %d characters long", MAXIMUM_KEY_LENGTH));
        }

        if (valueTypeIsNotAllowed(value)) {
            errorMessages.add(String.format("value for '%s' must be of type string, boolean or number", key));
        }

        if (valueIsTooLong(value)) {
            errorMessages.add(String.format("value for '%s' must be a maximum of %d characters", key, MAXIMUM_VALUE_LENGTH));
        }
        return errorMessages;
    }

    private boolean keyLengthIsInvalid(String key) {
        return key.length() < 1 || key.length() > MAXIMUM_KEY_LENGTH;
    }

    private boolean valueIsTooLong(JsonNode value) {
        return value.asText().length() > MAXIMUM_VALUE_LENGTH;
    }

    private boolean valueTypeIsNotAllowed(JsonNode value) {
        return !(value.isBoolean() || value.isNumber() || value.isTextual());
    }

    private void addConstraints(Set<String> errorMessages, ConstraintValidatorContext context) {
        errorMessages.iterator().forEachRemaining(errorMessage -> addConstraint(errorMessage, context));
    }

    private void addConstraint(String errorMessage, ConstraintValidatorContext context) {
        context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
    }
}
