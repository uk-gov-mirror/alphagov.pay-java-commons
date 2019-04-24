package uk.gov.pay.commons.api.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Before;
import org.junit.Test;
import uk.gov.pay.commons.model.charge.ExternalMetadata;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ExternalMetadataDeserialiserTest {

    private ObjectMapper objectMapper;

    @Before
    public void before() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(ExternalMetadata.class, new ExternalMetadataDeserialiser());
        objectMapper.registerModule(simpleModule);
    }
    
    @Test
    public void shouldDeserialise() throws Exception {
        String metadata = "{\"ledger_code\":\"123\", \"fund_code\":\"1234\"}";
        ExternalMetadata externalMetadata = objectMapper.readValue(metadata, ExternalMetadata.class);
        assertNotNull(externalMetadata.getMetadata());
        assertEquals(externalMetadata.getMetadata().size(), 2);
        assertEquals(externalMetadata.getMetadata().get("ledger_code"), "123");
        assertEquals(externalMetadata.getMetadata().get("fund_code"), "1234");
    }
}
