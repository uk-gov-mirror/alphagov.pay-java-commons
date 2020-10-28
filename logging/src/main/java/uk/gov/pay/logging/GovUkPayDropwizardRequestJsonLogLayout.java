package uk.gov.pay.logging;

import ch.qos.logback.access.spi.IAccessEvent;
import io.dropwizard.logging.json.layout.AbstractJsonLayout;
import io.dropwizard.logging.json.layout.JsonFormatter;
import io.dropwizard.logging.json.layout.TimestampFormatter;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.pay.logging.LoggingKeys.HTTP_STATUS;
import static uk.gov.pay.logging.LoggingKeys.MDC_REQUEST_ID_KEY;
import static uk.gov.pay.logging.LoggingKeys.METHOD;
import static uk.gov.pay.logging.LoggingKeys.RESPONSE_TIME;
import static uk.gov.pay.logging.LoggingKeys.URL;

public class GovUkPayDropwizardRequestJsonLogLayout extends AbstractJsonLayout<IAccessEvent> {

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_USER_AGENT = "User-Agent";

    private int logVersion;
    private final Map<String, Object> additionalFields;
    private TimestampFormatter timestampFormatter;

    public GovUkPayDropwizardRequestJsonLogLayout(JsonFormatter jsonFormatter,
                                                  TimestampFormatter timestampFormatter,
                                                  int logVersion,
                                                  Map<String, Object> additionalFields) {
        super(jsonFormatter);
        this.timestampFormatter = timestampFormatter;
        this.logVersion = logVersion;
        this.additionalFields = additionalFields;
    }

    @Override
    protected Map<String, Object> toJsonMap(IAccessEvent event) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("@version", logVersion);
        jsonMap.put(RESPONSE_TIME, event.getElapsedTime());
        jsonMap.put(HTTP_STATUS, event.getStatusCode());
        jsonMap.put("content_length", event.getContentLength());
        putIfNotNull(jsonMap, "@timestamp", formatTimeStamp(event.getTimeStamp()));
        putIfNotNull(jsonMap, URL, event.getRequestURI());
        putIfNotNull(jsonMap, METHOD, event.getMethod());
        putIfNotNull(jsonMap, "remote_address", event.getRemoteAddr());
        putIfNotNull(jsonMap,"http_version", event.getProtocol());
        putIfNotNull(jsonMap, "user_agent", event.getRequestHeader(HEADER_USER_AGENT));
        putIfNotNull(jsonMap, MDC_REQUEST_ID_KEY, event.getRequestHeader(HEADER_REQUEST_ID));
        
        jsonMap.putAll(additionalFields);
        
        return jsonMap;
    }

    private Object formatTimeStamp(long timeStamp) {
        if (timeStamp > 0) {
            return timestampFormatter.format(timeStamp);
        }

        return null;
    }

    private void putIfNotNull(Map<String, Object> jsonMap, String key, Object value) {
        if (value != null) {
            jsonMap.put(key, value);
        }
    }
}
