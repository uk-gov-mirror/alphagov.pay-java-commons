package uk.gov.pay.commons.model;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class SupportedLanguageJpaConverter implements AttributeConverter<SupportedLanguage, String> {

    @Override
    public String convertToDatabaseColumn(SupportedLanguage supportedLanguage) {
        return supportedLanguage.toString();
    }

    @Override
    public SupportedLanguage convertToEntityAttribute(String supportedLanguage) {
        return SupportedLanguage.fromIso639AlphaTwoCode(supportedLanguage);
    }

}
