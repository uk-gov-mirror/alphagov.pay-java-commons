package uk.gov.pay.commons.truststoreloader;

import org.postgresql.ssl.WrappedFactory;

public class TrustingSSLSocketFactory extends WrappedFactory {
    public TrustingSSLSocketFactory() {
        this._factory = TrustStoreLoader.getSSLContext().getSocketFactory();
    }
}
