package backend.transactions;

import java.util.Map;

import backend.Account;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * Represents a create account transaction that can be applied to the accounts Map.
 */
public class CreateTransaction extends Transaction {
  private final int accountNumber;
  private final String accountHolder;
  private final int initialBalance;

  public CreateTransaction(
    int accountNumber, String accountHolder, int amount, String misc,
    TransactionContext context
  ) {
    if (accountNumber != 0) {
      throw new InvalidTransactionException(
        "Expected account ID to be 0, got " + accountNumber
      );
    }
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

    this.accountNumber = ++(context.highestAccountId);
    this.accountHolder = accountHolder;
    this.initialBalance = amount;
  }

  @Override
  public void apply(Map<Integer, Account> accounts) {
    // Make sure the accountNumber is not already in use.
    if (accounts.containsKey(accountNumber)) {
      throw new ViolatedConstraintException(
        "Tried to create an account with a number that is " +
        "already in use: " + accountNumber
      );
    }

    // Create the account and add it to accounts.
    Account account = new Account(
      accountNumber, accountHolder, initialBalance, true, false, 0
    );
    accounts.put(accountNumber, account);
  }
}
