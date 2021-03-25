package uk.gov.service.payments.common.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

import static uk.gov.service.payments.common.model.ApiResponseDateTimeFormatter.ISO_INSTANT_MILLISECOND_PRECISION;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiResponseInstantSerializer extends StdSerializer<Instant> {

    public ApiResponseInstantSerializer() {
        this(null);
    }

    private ApiResponseInstantSerializer(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeString(ISO_INSTANT_MILLISECOND_PRECISION.format(value));
    }

}
