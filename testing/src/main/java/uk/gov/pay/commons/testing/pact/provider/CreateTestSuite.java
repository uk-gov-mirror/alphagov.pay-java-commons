package uk.gov.pay.commons.testing.pact.provider;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.lang.String.format;

public class CreateTestSuite {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTestSuite.class);

    public static TestSuite create(Map<String, JUnit4TestAdapter> map) {
        String consumer = System.getProperty("CONSUMER");
        TestSuite suite = new TestSuite();

        if (consumer == null || consumer.isBlank()) {
            LOGGER.info("Running all contract tests.");
            map.forEach((key, value) -> suite.addTest(value));
        } else if (map.containsKey(consumer)) {
            LOGGER.info("Running {}-connector contract tests only.", consumer);
            suite.addTest(map.get(consumer));
        } else {
            throw new RuntimeException(format("Error running provider contract tests. ${CONSUMER} system property was %s.", consumer));
        }

        return suite;
    }
}
