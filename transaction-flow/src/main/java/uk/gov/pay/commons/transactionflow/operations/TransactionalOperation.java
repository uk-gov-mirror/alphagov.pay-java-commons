package uk.gov.pay.commons.transactionflow.operations;

/**
 * Represents a block of code that must be executed outside inside a transaction boundary
 * @param <TransactionContext>
 * @param <R> return value
 */

@FunctionalInterface
public interface TransactionalOperation<TransactionContext, R> extends ManagedOperation<TransactionContext, R> {

}
