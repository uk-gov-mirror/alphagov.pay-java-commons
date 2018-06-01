package uk.gov.pay.commons.testing.pact.consumers;

import au.com.dius.pact.consumer.*;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit.JUnitTestSupport;
import au.com.dius.pact.model.FileSource;
import au.com.dius.pact.model.MockProviderConfig;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockserver.socket.PortFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import static au.com.dius.pact.consumer.ConsumerPactRunnerKt.runConsumerTest;
import static au.com.dius.pact.model.PactReader.loadPact;
import static com.google.common.io.Resources.getResource;
import static java.lang.String.format;
import static java.util.Arrays.stream;

public class PactProviderRule extends ExternalResource {

    protected final String provider;
    protected final Object target;
    protected MockProviderConfig config;
    private Map<String, RequestResponsePact> pacts;
    private MockServer mockServer;

    public PactProviderRule(String provider, Object target) {
        this.target = target;
        this.provider = provider;
        config = MockProviderConfig.httpConfig("localhost", PortFactory.findFreePort(), PactSpecVersion.V3);
    }

    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                PactVerifications pactVerifications = description.getAnnotation(PactVerifications.class);
                if (pactVerifications != null) {
                    evaluatePactVerifications(pactVerifications, base);
                    return;
                }

                PactVerification pactDef = description.getAnnotation(PactVerification.class);
                // no pactVerification? execute the test normally
                if (pactDef == null) {
                    base.evaluate();
                    return;
                }

                Map<String, RequestResponsePact> pacts = getPacts(description.getMethodName());
                Optional<RequestResponsePact> pact;
                if (pactDef.value().length == 1 && StringUtils.isEmpty(pactDef.value()[0])) {
                    pact = pacts.values().stream().findFirst();
                } else {
                    pact = Arrays.stream(pactDef.value()).map(pacts::get)
                            .filter(Objects::nonNull).findFirst();
                }
                if (!pact.isPresent()) {
                    base.evaluate();
                    return;
                }

                PactVerificationResult result = runPactTest(base, pact.get());
                JUnitTestSupport.validateMockServerResult(result);
            }
        };
    }

    private void evaluatePactVerifications(PactVerifications pactVerifications, Statement base) throws Throwable {
        Optional<PactVerification> possiblePactVerification = findPactVerification(pactVerifications);
        if (!possiblePactVerification.isPresent()) {
            base.evaluate();
            return;
        }

        PactVerification pactVerification = possiblePactVerification.get();
        Optional<Method> possiblePactMethod = findPactMethod(pactVerification);
        if (!possiblePactMethod.isPresent()) {
            throw new UnsupportedOperationException("Could not find method with @Pact for the provider " + provider);
        }

        Method method = possiblePactMethod.get();
        Pact pactAnnotation = method.getAnnotation(Pact.class);
        PactDslWithProvider dslBuilder = ConsumerPactBuilder.consumer(pactAnnotation.consumer()).hasPactWith(provider);
        RequestResponsePact pact;
        try {
            pact = (RequestResponsePact) method.invoke(target, dslBuilder);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke pact method", e);
        }
        PactVerificationResult result = runPactTest(base, pact);
        JUnitTestSupport.validateMockServerResult(result);
    }

    private Optional<PactVerification> findPactVerification(PactVerifications pactVerifications) {
        PactVerification[] pactVerificationValues = pactVerifications.value();
        return Arrays.stream(pactVerificationValues).filter(p -> {
            String[] providers = p.value();
            if (providers.length != 1) {
                throw new IllegalArgumentException(
                        "Each @PactVerification must specify one and only provider when using @PactVerifications");
            }
            String provider = providers[0];
            return provider.equals(this.provider);
        }).findFirst();
    }

    private Optional<Method> findPactMethod(PactVerification pactVerification) {
        String pactFragment = pactVerification.fragment();
        for (Method method : target.getClass().getMethods()) {
            Pact pact = method.getAnnotation(Pact.class);
            if (pact != null && pact.provider().equals(provider)
                    && (pactFragment.isEmpty() || pactFragment.equals(method.getName()))) {

                validatePactSignature(method);
                return Optional.of(method);
            }
        }
        return Optional.empty();
    }

    private void validatePactSignature(Method method) {
        boolean hasValidPactSignature =
                RequestResponsePact.class.isAssignableFrom(method.getReturnType())
                        && method.getParameterTypes().length == 1
                        && method.getParameterTypes()[0].isAssignableFrom(PactDslWithProvider.class);

        if (!hasValidPactSignature) {
            throw new UnsupportedOperationException("Method " + method.getName() +
                    " does not conform required method signature 'public RequestResponsePact xxx(PactDslWithProvider builder)'");
        }
    }

    private PactVerificationResult runPactTest(final Statement base, RequestResponsePact pact) {
        return runConsumerTest(pact, config, mockServer -> {
            this.mockServer = mockServer;
            base.evaluate();
            this.mockServer = null;
        });
    }

    protected Map<String, RequestResponsePact> getPacts(String methodName) {
        HashMap<String, RequestResponsePact> pacts = new HashMap<>();
        for (Method m : target.getClass().getMethods()) {
            if (m.getName().equals(methodName)) {
                Optional.ofNullable(m.getAnnotation(Pacts.class)).ifPresent(pactsAnnotation -> stream(pactsAnnotation.pacts()).forEach(fileName -> {
                    if (fileName.contains(provider)) {
                        RequestResponsePact pact = (RequestResponsePact) loadPact(new FileSource<>(new File(getResource(format("pacts/%s.json", fileName)).getFile())));
                        pacts.put(provider, pact);
                    }
                }));
            }
        }
        return pacts;
    }

    public String getUrl() {
        return mockServer == null ? null : mockServer.getUrl();
    }
}
