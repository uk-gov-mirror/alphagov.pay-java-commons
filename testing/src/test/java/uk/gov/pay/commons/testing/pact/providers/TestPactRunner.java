package uk.gov.pay.commons.testing.pact.providers;

import au.com.dius.pact.provider.junit.PactRunner;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.io.Resources;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static java.lang.Integer.parseInt;
import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static uk.gov.pay.commons.testing.pact.providers.PactPublishingTest.PACT_BROKER_PORT;
import static uk.gov.pay.commons.testing.pact.providers.PactPublishingTest.publishVerificationResultsUrl;

class TestPactRunner extends PactRunner {

    public static WireMockServer pactBroker = new WireMockServer(parseInt(PACT_BROKER_PORT));
    
    static {
        try {
            pactBroker.start();
            configureFor("localhost", parseInt(PACT_BROKER_PORT));
            
            String pacts = Resources.toString(Resources.getResource("pact-publishing-test/pacts.json"), UTF_8).trim();
            pactBroker.stubFor(get(urlPathEqualTo("/"))
                    .willReturn(aResponse().withHeader(CONTENT_TYPE, APPLICATION_JSON).withStatus(200).withBody(pacts)));
            
            String providerPacts = Resources.toString(Resources.getResource("pact-publishing-test/provider-service-pacts.json"), UTF_8).trim();
            pactBroker.stubFor(get(urlPathEqualTo("/pacts/provider/provider-service/latest/master"))
                    .willReturn(aResponse().withHeader(CONTENT_TYPE, APPLICATION_JSON).withStatus(200).withBody(providerPacts)));

            String pact = Resources.toString(Resources.getResource("pact-publishing-test/pact-from-broker-2.json"), UTF_8).trim();
            pactBroker.stubFor(get(urlPathEqualTo("/pacts/provider/provider-service/consumer/consumer-service/version/2f2b6a9755cfaefe87567548272049d335678f95"))
                    .willReturn(aResponse().withHeader(CONTENT_TYPE, APPLICATION_JSON).withStatus(200).withBody(pact)));

            pactBroker.stubFor(post(publishVerificationResultsUrl).withHeader(CONTENT_TYPE, matching(APPLICATION_JSON))
                    .willReturn(aResponse().withStatus(200)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    
    public TestPactRunner(Class clazz) {
        super(clazz);
    }
}
