package uk.gov.pay.commons.testing.pact.providers;

import au.com.dius.pact.model.BrokerUrlSource;
import au.com.dius.pact.model.FileSource;
import au.com.dius.pact.model.Pact;
import au.com.dius.pact.model.PactSource;
import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.pay.commons.testing.pact.providers.PayInteractionRunner;
import uk.gov.pay.commons.testing.pact.providers.PayPactRunner;

import java.io.File;
import java.util.List;
import java.util.Map;

import static au.com.dius.pact.model.PactReader.loadPact;
import static com.google.common.io.Resources.getResource;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PayInteractionRunnerTest {
    
    @Mock
    Pact pact;
    
    Map<String, Map<String, Object>> attributes = ImmutableMap.of("pb:publish-verification-results", ImmutableMap.of("href", "http://pact-broker-url"));
    Map<String, List<String>> options = ImmutableMap.of("authentication", Lists.newArrayList("something", "dummyUsername", "dummyPassword"));
    
    @Before
    public void setup() {
        System.setProperty("PROVIDER_SHA", "39idsjgfk32");
    }
    
    @Test
    public void canInstantiate() throws InitializationError {
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, options);
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }
    
    @Test(expected = RuntimeException.class)
    public void failIfPactSourceIsNotInstanceOfBrokerUrlSource() throws InitializationError {
        Pact pact = loadPact(new FileSource<>(new File(getResource("pact-from-broker.json").getFile())));
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, options);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);

    }

    @Test(expected = RuntimeException.class)
    public void failIfProviderShaPropertyNotSet() throws InitializationError {
        System.clearProperty("PROVIDER_SHA");
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, options);
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }
    
    @Test(expected = AssertionError.class)
    public void failIfMissingPublishVerificationResultsAttribute() throws InitializationError {
        PactSource pactSource = new BrokerUrlSource("localhost", "0", ImmutableMap.of(), options);
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }
    
    @Test(expected = AssertionError.class)
    public void failIfMissingHrefAttribute() throws InitializationError {
        Map<String, Map<String, Object>> attributes = ImmutableMap.of("pb:publish-verification-results", ImmutableMap.of());
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, options);
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }
    
    @Test(expected = AssertionError.class)
    public void failIfNoPactBrokerUsernameOrPassword() throws InitializationError {
        Map<String, List<String>> options = ImmutableMap.of("authentication", emptyList());
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, options);
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }

    @Test(expected = AssertionError.class)
    public void failIfNoPactBrokerAuthenticationDetails() throws InitializationError {
        PactSource pactSource = new BrokerUrlSource("localhost", "0", attributes, ImmutableMap.of());
        when(pact.getSource()).thenReturn(pactSource);
        new PayInteractionRunner(new TestClass(DummyContractTest.class), pact, pactSource);
    }

    @RunWith(PayPactRunner.class)
    @Provider("provider-service")
    @PactBroker(protocol = "https", host = "localhost", port = "8080", tags = {"test"},
            authentication = @PactBrokerAuth(username = "fake-username", password = "fake-password"))
    public static class DummyContractTest {
        @TestTarget
        public static Target target;
    }
    
}
