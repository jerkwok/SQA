package backend;

/**
 * An immutable data class that holds information from the first transfer
 * transaction in a pair, so that when the second is parsed it can be combined
 * with this data to form a single TransferTransaction.
 *
 * The source account, account holder, and amount from the first transaction
 * are used to create a TransferContext. Then the destination account and amount
 * is read from the second transaction. The amounts are validated against each
 * other, then the source account, source account holder, destination account,
 * and amount are used to create a TransferTransaction and the TransferContext
 * is discarded.
 */
public class TransferContext {
  public final String sourceAccountHolder;
  public final int sourceAccountNumber;
  public final int amount;

  public TransferContext(
    int sourceAccountNumber, String sourceAccountHolder, int amount
  ) {
    this.sourceAccountHolder = sourceAccountHolder;
    this.sourceAccountNumber = sourceAccountNumber;
    this.amount = amount;
  }
}
