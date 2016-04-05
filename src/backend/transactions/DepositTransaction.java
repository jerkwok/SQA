package backend.transactions;

import java.util.Map;

import backend.Account;
import backend.Constants;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * Represents a deposit transaction that can be applied to the accounts Map.
 */
public class DepositTransaction extends Transaction {
  private final int accountNumber;
  private final String accountHolder;
  private final int amount;
  private final boolean adminInitiated;

  public DepositTransaction(
    int accountNumber, String accountHolder, int amount, String misc,
    TransactionContext context
  ) {
    if (!misc.equals("")) {
      throw new InvalidTransactionException(
        "Expected misc to be empty, got \"" + misc + "\""
      );
    }
    if (context.currentUserIsAdministrator) {
      if (accountHolder.equals("")) {
        throw new InvalidTransactionException(
          "Expected account holder name, got empty string"
        );
      }
    } else {
      if (!accountHolder.equals(context.currentUser)) {
        throw new InvalidTransactionException(
          "Expected account holder name to be "+
          "\"" + context.currentUser + "\", got \"" + accountHolder + "\""
        );
      }
    }

    this.accountNumber = accountNumber;
    this.accountHolder = accountHolder;
    this.amount = amount;
    this.adminInitiated = context.currentUserIsAdministrator;
  }

  @Override
  public void apply(Map<Integer, Account> accounts) {
    // Get the account from accounts and make sure it exists.
    Account account = accounts.get(accountNumber);
    if (account == null) {
      throw new ViolatedConstraintException(
        "Tried to deposit into non-existent account " + accountNumber
      );
    }

    // Make sure the account is enabled.
    if (!account.isEnabled()) {
      throw new ViolatedConstraintException(
        "Tried to deposit into disabled account " + accountNumber
      );
    }

    // Check that the account belongs to accountHolder.
    if (!account.getAccountHolder().equals(accountHolder)) {
      throw new ViolatedConstraintException(
        "User \"" + accountHolder + "\" tried to perform transaction " +
        "on account " + accountNumber +
        " which belongs to user \"" + account.getAccountHolder() + "\""
      );
    }

    // Compute fee based on whether it was admin initiated or not
    //   and whether they are a student or not.
    int fee = (adminInitiated) ? 0 : (
      (account.isStudent()) ? Constants.STUDENT_FEE : Constants.NORMAL_FEE
    );

    // Check that the final balance after the deposit (minus the fee)
    //   is greater than 0 and less than the max balance.
    int finalBalance = account.getBalance() + amount - fee;
    if (finalBalance < 0 || finalBalance > Constants.MAX_BALANCE) {
      throw new ViolatedConstraintException(
        "Final balance should be >= 0 and <= " + Constants.MAX_BALANCE + ", " +
        "got " + finalBalance
      );
    }

    // Add the funds to the account and remove the fee.
    account.setBalance(finalBalance);

    // If the transaction wasn't admin initiated then increment the
    //   transaction counter.
    if (!adminInitiated) {
      account.incrementTransactionCount();
    }
  }
}
