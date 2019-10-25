package uk.gov.pay.logging;

import org.junit.Test;

import java.util.TimeZone;

public class GovUkPayDropwizardRequestJsonLogLayoutFactoryTest {
    
    @Test(expected = RuntimeException.class)
    public void throw_runtime_exception_if_no_additional_field_named_container() {
        var jsonLogLayoutFactory = new GovUkPayDropwizardRequestJsonLogLayoutFactory();
        jsonLogLayoutFactory.build(null, TimeZone.getDefault());
    }
}
