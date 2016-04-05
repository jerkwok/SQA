package backend.transactions;

import java.util.Map;

import backend.Account;
import backend.Constants;
import backend.Transaction;
import backend.TransactionContext;
import backend.TransferContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * Represents a transfer funds transaction that can be applied to the accounts Map.
 */
public class TransferTransaction extends Transaction {
  private final int sourceAccountNumber;
  private final String sourceAccountHolder;
  private final int destinationAccountNumber;
  private final String destinationAccountHolder;
  private final int amount;
  private final boolean adminInitiated;

  public TransferTransaction(
    int accountNumber, String accountHolder, int amount, String misc,
    TransactionContext context
  ) {
    TransferContext tc = context.transferContext;
    context.transferContext = null;

    if (accountHolder.equals("")) {
      throw new InvalidTransactionException(
        "Expected account holder name to be non-empty"
      );
    }
    if (tc.amount != amount) {
      throw new InvalidTransactionException(
        "Expected second transaction amount (" + amount + ") "+
        "to equal first transfer amount (" + tc.amount + ")"
      );
    }

    this.sourceAccountNumber = tc.sourceAccountNumber;
    this.sourceAccountHolder = tc.sourceAccountHolder;
    this.destinationAccountNumber = accountNumber;
    this.destinationAccountHolder = accountHolder;
    this.amount = amount;
    this.adminInitiated = context.currentUserIsAdministrator;
  }

  @Override
  public void apply(Map<Integer, Account> accounts) {
    // Get the source and destination accounts from accounts and
    //   make sure they exist.
    Account sourceAccount = accounts.get(sourceAccountNumber);
    if (sourceAccount == null) {
      throw new ViolatedConstraintException(
        "Tried to transfer from non-existent account " + sourceAccountNumber
      );
    }

    Account destinationAccount = accounts.get(destinationAccountNumber);
    if (destinationAccount == null) {
      throw new ViolatedConstraintException(
        "Tried to transfer to non-existent account " + destinationAccountNumber
      );
    }

    // Make sure the source account is enabled.
    if (!sourceAccount.isEnabled()) {
      throw new ViolatedConstraintException(
        "Tried to transfer from disabled account " + sourceAccountNumber
      );
    }

    // Make sure the destination account is enabled.
    if (!destinationAccount.isEnabled()) {
      throw new ViolatedConstraintException(
        "Tried to transfer to disabled account " + destinationAccountNumber
      );
    }

    // Check that the source account belongs to sourceAccountHolder.
    if (!sourceAccount.getAccountHolder().equals(sourceAccountHolder)) {
      throw new ViolatedConstraintException(
        "User \"" + sourceAccountHolder + "\" tried to perform transaction " +
        "on account " + sourceAccountNumber +
        " which belongs to user \"" + sourceAccount.getAccountHolder() + "\""
      );
    }

    // Check that the destination account belongs to destinationAccountHolder.
    if (!destinationAccount.getAccountHolder().equals(destinationAccountHolder)) {
      throw new ViolatedConstraintException(
        String.format(
          "Expected account to belong to \"%s\", belongs to \"%s\" instead",
          destinationAccountHolder, destinationAccount.getAccountHolder()
        )
      );
    }

    // Compute fee based on whether it was admin initiated or not
    //   and whether they are a student or not.
    int fee = (adminInitiated) ? 0 : (
      (sourceAccount.isStudent()) ? Constants.STUDENT_FEE : Constants.NORMAL_FEE
    );

    // Check that the final balance of the source account after removing the
    //   funds and fee is greater than zero.
    int finalSourceBalance = sourceAccount.getBalance() - amount - fee;
    if (finalSourceBalance < 0) {
      throw new ViolatedConstraintException(
        "Final source account balance should be >= 0, got " + finalSourceBalance
      );
    }

    // Check that the final balance of the destination account after adding
    //   the funds is not greater than 99,999,99.
    int finalDestinationBalance = destinationAccount.getBalance() + amount - fee;
    if (finalDestinationBalance > Constants.MAX_BALANCE) {
      throw new ViolatedConstraintException(
        "Final balance should be <= " + Constants.MAX_BALANCE + ", " +
        "got " + finalDestinationBalance
      );
    }

    // If the transaction isn't admin initiated, check that the final transfer
    //   total is less than the transfer limit.
    int finalTransferTotal = 0;

    if (!adminInitiated) {
      finalTransferTotal = sourceAccount.getTransferTotal() + amount;

      if (finalTransferTotal > Constants.TRANSFER_LIMIT) {
        throw new ViolatedConstraintException(
          "Final transfer total should be <= " + Constants.TRANSFER_LIMIT +
          ", got " + finalTransferTotal
        );
      }
    }

    // Remove the funds and fee from the source account.
    sourceAccount.setBalance(finalSourceBalance);

    // If the transaction isn't admin initiated, update the transfer total
    //   and increment the transaction count.
    if (!adminInitiated) {
      sourceAccount.setTransferTotal(finalTransferTotal);
      sourceAccount.incrementTransactionCount();
    }

    // Add the funds to the destination account.
    destinationAccount.setBalance(finalDestinationBalance);
  }
}
