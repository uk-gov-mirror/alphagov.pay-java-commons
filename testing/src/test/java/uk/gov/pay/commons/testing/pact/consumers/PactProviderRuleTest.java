package uk.gov.pay.commons.testing.pact.consumers;

import au.com.dius.pact.consumer.PactVerification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.fluent.Request;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class PactProviderRuleTest {

    @Rule
    public PactProviderRule stockQuoteService = new PactProviderRule("stock-quote-service", this);

    @Rule
    public PactProviderRule weatherService = new PactProviderRule("weather-service", this);

    @Rule
    public PactProviderRule orderService = new PactProviderRule("order-service", this);
    
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
    @Pacts(pacts = {"consumer-stock-quote-service-v"})
    public void getVisaPrice() throws Exception {
        HashMap map = jsonToMap(Request.Get(stockQuoteService.getUrl() + "/stocks/V").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("Visa");
        assertThat(map.get("price")).isEqualTo("100.00");
        assertThat(map.get("currency")).isEqualTo("USD");
    }

    @Test
    @PactVerification({"weather-service"})
    @Pacts(pacts = {"consumer-weather-service"})
    public void getLondonWeather() throws Exception {
        HashMap map = jsonToMap(Request.Get(weatherService.getUrl() + "/weather/London").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("London");
        assertThat(map.get("temperature")).isEqualTo("25");
        assertThat(map.get("unit")).isEqualTo("degrees");
    }

    @Test
    @PactVerification({"weather-service", "stock-quote-service"})
    @Pacts(pacts = {"consumer-weather-service", "consumer-stock-quote-service-axp"})
    public void callTwoProviders() throws Exception {
        HashMap map = jsonToMap(Request.Get(stockQuoteService.getUrl() + "/stocks/AXP").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("American Express");
        assertThat(map.get("price")).isEqualTo("200.00");
        assertThat(map.get("currency")).isEqualTo("USD");

        map = jsonToMap(Request.Get(weatherService.getUrl() + "/weather/London").execute().returnContent().asString());
        assertThat(map.get("name")).isEqualTo("London");
        assertThat(map.get("temperature")).isEqualTo("25");
        assertThat(map.get("unit")).isEqualTo("degrees");
    }

    @Test
    @PactVerification({"weather-service", "order-service"})
    @Pacts(pacts = {"consumer-weather-service"})
    @Pacts(pacts = {"consumer-order-service"}, publish = false)
    public void specifyIfPactShouldBeWritten() throws IOException {
        Request.Get(weatherService.getUrl() + "/weather/London").execute();
        Request.Get(orderService.getUrl() + "/orders").execute();
        assertThat(new File(Paths.get("", "target", "pacts", "consumer-weather-service.json").toAbsolutePath().toString()).exists()).isTrue();
        assertThat(new File(Paths.get("", "target", "pacts", "consumer-order-service.json").toAbsolutePath().toString()).exists()).isFalse();
    }

    private HashMap jsonToMap(String respBody) throws IOException {
        if (respBody.isEmpty()) {
            return new HashMap();
        }
        return new ObjectMapper().readValue(respBody, HashMap.class);
    }
}
