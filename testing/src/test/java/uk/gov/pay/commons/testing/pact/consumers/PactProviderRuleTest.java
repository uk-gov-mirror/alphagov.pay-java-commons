package uk.gov.pay.commons.testing.pact.consumers;

import au.com.dius.pact.consumer.PactVerification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Request;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class PactProviderRuleTest {

    @Rule
    public PactProviderRule stockQuoteService = new PactProviderRule("stock-quote-service", this);

    @Test
    @PactVerification({"stock-quote-service"})
    @Pacts(pacts = {"consumer-stock-quote-service-axp"})
    public void getAmexPrice() throws Exception {
        HashMap map = jsonToMap(Request.Get(stockQuoteService.getUrl() + "/stocks/AXP").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("American Express");
        assertThat(map.get("price")).isEqualTo("200.00");
        assertThat(map.get("currency")).isEqualTo("USD");
    }

    @Test
    @PactVerification({"stock-quote-service"})
    @Pacts(pacts = {"consumer-stock-quote-service-visa"})
    public void getVisaPrice() throws Exception {
        HashMap map = jsonToMap(Request.Get(stockQuoteService.getUrl() + "/stocks/V").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("Visa");
        assertThat(map.get("price")).isEqualTo("200.00");
        assertThat(map.get("currency")).isEqualTo("USD");
    }

    private HashMap jsonToMap(String respBody) throws IOException {
        if (respBody.isEmpty()) {
            return new HashMap();
        }
        return new ObjectMapper().readValue(respBody, HashMap.class);
    }
}
