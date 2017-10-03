package uk.gov.pay.commons.transactionflow;


public class TransactionalException extends RuntimeException {

    public TransactionalException(String message) {
        super(message);
    }

    public TransactionalException(Exception exception) {
        super(exception);
    }
}
