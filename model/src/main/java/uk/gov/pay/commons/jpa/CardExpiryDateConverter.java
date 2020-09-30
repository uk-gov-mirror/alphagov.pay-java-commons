package uk.gov.pay.commons.jpa;

import uk.gov.pay.commons.model.CardExpiryDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CardExpiryDateConverter implements AttributeConverter<CardExpiryDate, String> {

    @Override
    public String convertToDatabaseColumn(CardExpiryDate cardExpiryDate) {
        if (cardExpiryDate == null) {
            return null;
        }

        return cardExpiryDate.toString();
    }

    @Override
    public CardExpiryDate convertToEntityAttribute(String cardExpiryDate) {
        if (cardExpiryDate == null) {
            return null;
        }

        return CardExpiryDate.valueOf(cardExpiryDate);
    }

}
