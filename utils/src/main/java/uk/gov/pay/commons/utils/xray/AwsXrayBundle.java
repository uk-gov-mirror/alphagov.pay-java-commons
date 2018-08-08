package uk.gov.pay.commons.utils.xray;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.ECSPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import io.dropwizard.Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.net.URL;
import static java.util.EnumSet.of;

import static javax.servlet.DispatcherType.REQUEST;

public class AwsXrayBundle implements Bundle {
    
    private final String[] urlPatternsForXrayFiltering;
    private final String xrayServletFilterName;

    public AwsXrayBundle(String xrayServletFilterName, String... urlPatternsForXrayFiltering) {
        this.urlPatternsForXrayFiltering = urlPatternsForXrayFiltering;
        this.xrayServletFilterName = xrayServletFilterName;
    }

    /**
     * @see <a href="http://docs.aws.amazon.com/xray/latest/devguide/xray-sdk-java-configuration.html">Configuring the X-Ray SDK for Java</a>
     */
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new ECSPlugin());
        URL ruleFile = this.getClass().getResource("/sampling-rules.json");
        builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));
        AWSXRay.setGlobalRecorder(builder.build());
    }

    @Override
    public void run(Environment environment) {
        environment.servlets().addFilter("AWSXRayServletFilter", new AWSXRayServletFilter(xrayServletFilterName))
                .addMappingForUrlPatterns(of(REQUEST), true, urlPatternsForXrayFiltering);
    }
}
