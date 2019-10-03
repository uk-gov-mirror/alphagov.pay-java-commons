package uk.gov.pay.commons.testing.pact.provider;

import com.google.common.collect.ImmutableSetMultimap;
import junit.framework.JUnit4TestAdapter;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateTestSuiteTest {

    private ImmutableSetMultimap<String, JUnit4TestAdapter> consumerToJUnitTest = ImmutableSetMultimap.of(
            "consumer1", new JUnit4TestAdapter(String.class),
            "consumer1", new JUnit4TestAdapter(Integer.class),
            "consumer2", new JUnit4TestAdapter(String.class));
    
    @After
    public void after() {
        System.clearProperty("CONSUMER");
    }
    
    @Test
    public void addAllContractTests() {
        assertThat(CreateTestSuite.create(consumerToJUnitTest).countTestCases()).isEqualTo(3);
    }
    
    @Test
    public void addSpecificContractTest() {
        System.setProperty("CONSUMER", "consumer2");
        assertThat(CreateTestSuite.create(consumerToJUnitTest).countTestCases()).isEqualTo(1);
    }
    
    @Test(expected = RuntimeException.class)
    public void throwIfConsumerIsUnknown() {
        System.setProperty("CONSUMER", "unknown");
        CreateTestSuite.create(consumerToJUnitTest);
    }
}
