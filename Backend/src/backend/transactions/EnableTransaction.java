package backend.transactions;

import java.util.Map;

import backend.Account;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * Represents an enable account transaction that can be applied to the accounts Map.
 */
public class EnableTransaction extends Transaction {
  private final int accountNumber;
  private final String accountHolder;

  public EnableTransaction(
    int accountNumber, String accountHolder, int amount, String misc,
    TransactionContext context
  ) {
    if (!context.currentUserIsAdministrator) {
      throw new InvalidTransactionException(
        "Must be logged in as administrator"
      );
    }
    if (accountHolder.equals("")) {
      throw new InvalidTransactionException(
        "Expected account holder name, got empty string"
      );
    }
    if (!misc.equals("")) {
      throw new InvalidTransactionException(
        "Expected misc to be empty, got \"" + misc + "\""
      );
    }
    if (amount != 0) {
      throw new InvalidTransactionException(
        "Expected amount to be 0, got " + amount
      );
    }

    this.accountNumber = accountNumber;
    this.accountHolder = accountHolder;
  }

  @Override
  public void apply(Map<Integer, Account> accounts) {
    // Get the account from accounts and make sure it exists.
    Account account = accounts.get(accountNumber);
    if (account == null) {
      throw new ViolatedConstraintException(
        "Tried to enable a non-existent account " + accountNumber
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
    // Check that the account is not already enabled.
    if (account.isEnabled()) {
      throw new ViolatedConstraintException(
        "Account " + accountNumber + " is already enabled"
      );
    }

    // Enable the account.
    account.setEnabled(true);
  }
}
