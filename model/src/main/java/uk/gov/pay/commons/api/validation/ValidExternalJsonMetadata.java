package uk.gov.pay.commons.api.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExternalJsonMetadataValidator.class)
public @interface ValidExternalJsonMetadata {

    String message() default "Invalid metadata";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
