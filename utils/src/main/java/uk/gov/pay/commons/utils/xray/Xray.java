package uk.gov.pay.commons.utils.xray;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.AWSXRayRecorderBuilder;
import com.amazonaws.xray.javax.servlet.AWSXRayServletFilter;
import com.amazonaws.xray.plugins.ECSPlugin;
import com.amazonaws.xray.strategy.sampling.LocalizedSamplingStrategy;
import io.dropwizard.setup.Environment;

import java.net.URL;
import java.util.Optional;

import static java.util.EnumSet.of;
import static javax.servlet.DispatcherType.REQUEST;

public class Xray {

    public static void init(Environment environment, String xrayServletFilterName, Optional<URL> samplingRules, String... urlPatternsForXrayFiltering) {
        AWSXRayRecorderBuilder builder = AWSXRayRecorderBuilder.standard().withPlugin(new ECSPlugin());
        URL ruleFile = samplingRules.orElse(Xray.class.getResource("/sampling-rules.json"));
        builder.withSamplingStrategy(new LocalizedSamplingStrategy(ruleFile));
        AWSXRay.setGlobalRecorder(builder.build());

        environment.servlets().addFilter("AWSXRayServletFilter", new AWSXRayServletFilter(xrayServletFilterName))
                .addMappingForUrlPatterns(of(REQUEST), true, urlPatternsForXrayFiltering);
    }
}
