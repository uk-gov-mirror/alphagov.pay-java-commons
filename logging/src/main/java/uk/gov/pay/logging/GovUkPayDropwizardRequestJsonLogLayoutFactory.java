package uk.gov.pay.logging;

import ch.qos.logback.access.spi.IAccessEvent;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.json.AccessJsonLayoutBaseFactory;
import io.dropwizard.logging.json.layout.AccessJsonLayout;
import io.dropwizard.logging.json.layout.TimestampFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * This programmatically configures the dropwizard request log json logging to be equivalent to:
 *
 *       - type: console
 *         layout:
 *           type: access-json
 *           timestampFormat: "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
 *           CUSTOM_FIELD_NAMES:
 *             timestamp: "@timestamp"
 *             userAgent: "user_agent"
 *             requestTime: "response_time"
 *             uri: "url"
 *             protocol: "http_version"
 *             status: "status_code"
 *             contentLength: "content_length"
 *             remoteAddress: "remote_address"
 *           additionalFields:
 *             "@version": 1
 *             
 * More additional fields can be added as required; however it is mandatory to add a "container" key as this is 
 * standard across all our dropwizard apps.
 */
@JsonTypeName("govuk-pay-access-json")
public class GovUkPayDropwizardRequestJsonLogLayoutFactory extends AccessJsonLayoutBaseFactory {

    private static final Map<String, String> CUSTOM_FIELD_NAMES = Map.of(
            "timestamp", "@timestamp",
            "userAgent", "user_agent",
            "requestTime", "response_time",
            "uri", "url",
            "protocol", "http_version",
            "status", "status_code",
            "contentLength", "content_length",
            "remoteAddress", "remote_address"
    );

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public LayoutBase<IAccessEvent> build(LoggerContext context, TimeZone timeZone) {
        if (!this.getAdditionalFields().containsKey("container")) {
            throw new RuntimeException("When using govuk-pay-access-json, an additional field with the key of " +
                    "\"container\" must be present");
        }
        var additionalFields = new HashMap<>(this.getAdditionalFields());
        additionalFields.put("@version", 1);
        var jsonLayout = new AccessJsonLayout(this.createDropwizardJsonFormatter(), createTimestampFormatter(timeZone),
                getIncludes(), CUSTOM_FIELD_NAMES, additionalFields);
        jsonLayout.setContext(context);
        jsonLayout.setRequestHeaders(getRequestHeaders());
        jsonLayout.setResponseHeaders(getResponseHeaders());
        return jsonLayout;
    }

    @Override
    protected TimestampFormatter createTimestampFormatter(TimeZone timeZone) {
        return new TimestampFormatter(TIMESTAMP_FORMAT, timeZone.toZoneId());
    }
}

