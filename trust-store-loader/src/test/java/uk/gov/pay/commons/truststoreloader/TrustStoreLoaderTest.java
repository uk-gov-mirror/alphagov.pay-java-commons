package uk.gov.pay.commons.truststoreloader;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TrustStoreLoaderTest {

    @Test
    public void shouldCreateADefaultTrustStoreWithNoPassword() throws Exception {
        assertThat(TrustStoreLoader.getTrustStore().getType(), is("jks"));
        assertThat(TrustStoreLoader.getSSLContext().getProtocol(), is("TLS"));
    }
}
