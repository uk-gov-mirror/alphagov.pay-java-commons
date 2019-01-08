package uk.gov.pay.commons.testing.pact.providers;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pay.commons.testing.pact.providers.PactPublishingTest.PACT_BROKER_PORT;
import static uk.gov.pay.commons.testing.pact.providers.TestPactRunner.pactBroker;

/**
 * This test is here because it was originally thought pacts failed to publish due to a bug with the pact libraries.
 * It turns out it was Pay's fault. See:
 * https://github.com/alphagov/pay-connector/commit/f2f69fd35771a92bffe96831729a310c6eca7af5
 * 
 * I think this test is still useful just in case. To run it, uncomment the @Ignore and run from the command line:
 * mvn test -Dtest=PactPublishingTest -Dpact.verifier.publishResults=true -Dpact.provider.version=abc123
 */
@Ignore
@RunWith(TestPactRunner.class)
@Provider("provider-service")
@PactBroker(scheme = "http", host = "localhost", port = PACT_BROKER_PORT, tags = "master")
public class PactPublishingTest {

    static final String PACT_BROKER_PORT = "50000";
    static final int SERVER_PORT = 50001;
    
    public static String publishVerificationResultsUrl = "/pacts/provider/provider-service/consumer/consumer-service/pact-version/0529f385b1a9b5d8c2d93d547ed6a5d95451a84a/verification-results";

    @TestTarget
    public static Target target = new HttpTarget(SERVER_PORT);

    public static WireMockServer wireMockRule = new WireMockServer(SERVER_PORT);

    @BeforeClass
    public static void setup() {
        wireMockRule.start();
        configureFor("localhost", SERVER_PORT);
        wireMockRule.stubFor(get("/current-string").willReturn(
                aResponse()
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)
                        .withStatus(200)
                        .withBody("{\"current_string\": \"hello\"}")));
    }
    
    @AfterClass
    public static void assertStuff() {
        pactBroker.verify(postRequestedFor(
                urlEqualTo(publishVerificationResultsUrl))
                .withRequestBody(matching("\\{\"success\":true,\"providerApplicationVersion\":\"abc123\"\\}")));
    }
}
