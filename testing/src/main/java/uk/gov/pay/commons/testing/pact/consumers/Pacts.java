package uk.gov.pay.commons.testing.pact.consumers;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(MultiPacts.class)
public @interface Pacts {
    String[] pacts();

    boolean publish() default true;
}
