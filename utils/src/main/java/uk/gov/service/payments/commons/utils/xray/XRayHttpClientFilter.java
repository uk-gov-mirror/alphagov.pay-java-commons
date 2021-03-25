package uk.gov.service.payments.commons.utils.xray;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorder;
import com.amazonaws.xray.entities.Namespace;
import com.amazonaws.xray.entities.Segment;
import com.amazonaws.xray.entities.Subsegment;
import com.amazonaws.xray.entities.TraceHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;

@Provider
public class XRayHttpClientFilter implements ClientRequestFilter, ClientResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(XRayHttpClientFilter.class);
    private static final String AWS_TRACE_HEADER = "X-Amzn-Trace-Id";
    private static final int TOO_MANY_REQUESTS = 429;

    private final AWSXRayRecorder recorder = AWSXRay.getGlobalRecorder();

    @Override
    public void filter(ClientRequestContext requestContext) {

        if (recorder.getCurrentSegmentOptional().isPresent()) {
            Subsegment subsegment = recorder.beginSubsegment(requestContext.getUri().getHost());
            if (subsegment != null) {
                try {
                    subsegment.setNamespace(Namespace.REMOTE.toString());
                    requestContext.getHeaders().add(AWS_TRACE_HEADER, generateTraceHeader(subsegment));
                    HashMap requestInformation = new HashMap();
                    requestInformation.put("url", requestContext.getUri());
                    requestInformation.put("method", requestContext.getMethod());
                    subsegment.putHttp("request", requestInformation);
                } catch (Exception exception) {
                    subsegment.addException(exception);
                } finally {
                    subsegment.end();
                }
            }
        } else {
            logger.info("No segment found, proceed to executing http request.");
        }
    }

    @Override
    public void filter(ClientRequestContext requestContext,
                       ClientResponseContext responseContext) {

        if (recorder.getCurrentSegmentOptional().isPresent()) {
            Subsegment subsegment = recorder.getCurrentSubsegment();
            if (subsegment != null) {
                try {
                    int responseCode = responseContext.getStatus();
                    switch (responseCode / 100) {
                        case 4:
                            subsegment.setError(true);
                            if (responseCode == TOO_MANY_REQUESTS) {
                                subsegment.setThrottle(true);
                            }
                            break;
                        case 5:
                            subsegment.setFault(true);
                    }
                    HashMap responseInformation = new HashMap();
                    responseInformation.put("status", Integer.valueOf(responseCode));
                    responseInformation.put("content_length", Long.valueOf(responseContext.getLength()));
                    subsegment.putHttp("response", responseInformation);
                } catch (Exception exception) {
                    subsegment.addException(exception);
                } finally {
                    subsegment.end();
                }
            }
        } else {
            logger.info("No segment found, proceed to executing http request.");
        }
    }

    private String generateTraceHeader(Subsegment subsegment) {
        Segment parentSegment = subsegment.getParentSegment();
        return new TraceHeader(
                parentSegment.getTraceId(),
                parentSegment.isSampled() ?
                        subsegment.getId() :
                        null,
                parentSegment.isSampled() ?
                        TraceHeader.SampleDecision.SAMPLED :
                        TraceHeader.SampleDecision.NOT_SAMPLED
        ).toString();
    }
}
