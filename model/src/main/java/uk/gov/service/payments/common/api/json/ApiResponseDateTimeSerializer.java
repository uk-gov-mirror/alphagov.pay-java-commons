package uk.gov.service.payments.common.api.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.gov.service.payments.common.model.ApiResponseDateTimeFormatter;

import java.io.IOException;
import java.time.ZonedDateTime;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ApiResponseDateTimeSerializer extends StdSerializer<ZonedDateTime> {

    public ApiResponseDateTimeSerializer() {
        this(null);
    }

    private ApiResponseDateTimeSerializer(Class<ZonedDateTime> t) {
        super(t);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider sp) throws IOException {
        gen.writeString(ApiResponseDateTimeFormatter.ISO_INSTANT_MILLISECOND_PRECISION.format(value));
    }
}
