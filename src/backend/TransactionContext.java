package backend;

/**
 * A data class used to hold all the state during parsing of the transactions
 * file that exists outside of individual lines.
 */
public class TransactionContext {
  /**
   * The highest account ID that currently exists.
   *
   * Used when parsing create transactions to assign them a new account ID.
   */
  public int highestAccountId;
  /**
   * The name of the user that is currently logged in, or null if no one or an
   * admin is logged in.
   */
  public String currentUser = null;
  /**
   * Is set to true when an admin is logged in, otherwise false.
   */
  public boolean currentUserIsAdministrator = false;
  /**
   * Stores additional information needed by transfer transactions since they
   * span two separate lines.
   */
  public TransferContext transferContext = null;

  public TransactionContext(int highestAccountId) {
    this.highestAccountId = highestAccountId;
  }
}
