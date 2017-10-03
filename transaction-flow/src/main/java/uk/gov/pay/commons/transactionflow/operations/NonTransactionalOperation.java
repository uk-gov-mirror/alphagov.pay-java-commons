package uk.gov.pay.commons.transactionflow.operations;

/**
 * Represents a block of code that should be executed outside of a transaction boundary
 * @param <TransactionContext>
 * @param <R> return value
 */
@FunctionalInterface
public interface NonTransactionalOperation<TransactionContext, R> extends ManagedOperation<TransactionContext, R> {

}
