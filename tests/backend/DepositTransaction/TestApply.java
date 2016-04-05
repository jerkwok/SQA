package tests.backend.DepositTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.Account;
import backend.Transaction;
import backend.transactions.DepositTransaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;


public class TestApply {

  @Test
  public void knownGoodValue() {
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new DepositTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 100, "", context
    );

    transaction.apply(accounts);
  }

  @Test
  public void nullAccount() {
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new DepositTransaction(
      2, account.getAccountHolder(), 100, "", context
    );

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to deposit into non-existent account 2"
      );
    }
  }

  @Test
  public void enabledAccount() {
    Account account = new Account(1, "John Doe", 0, false, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new DepositTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 100, "", context
    );

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to disabled account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Tried to deposit into disabled account 1"
      );
    }
  }

  @Test
  public void nameOwnsAccount() {
    Account account1 = new Account(1, "John Doe", 0, true, false, 0);
    Account account2 = new Account(2, "Jane Doe", 0, true, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account1.getAccountNumber(), account1);
    accounts.put(account2.getAccountNumber(), account2);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account1.getAccountHolder();

    Transaction transaction = new DepositTransaction(
      account2.getAccountNumber(), account1.getAccountHolder(), 100, "", context
    );

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to account ownership"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "User \"John Doe\" tried to perform transaction on account 2 which " +
        "belongs to user \"Jane Doe\""
      );
    }
  }

  @Test
  public void feePayable() {
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new DepositTransaction(
      account.getAccountNumber(), account.getAccountHolder(),
      5, "", context
    );

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to insufficient funds"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Final balance should be >= 0 and <= 9999999, got -5"
      );
    }
  }

  @Test
  public void maxBalance() {
    Account account = new Account(1, "John Doe", 2000010, true, false, 0);

    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);

    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();

    try {
      Transaction transaction = new DepositTransaction(
        account.getAccountNumber(), account.getAccountHolder(),
        9000000, "", context
      );
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to insufficient funds"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Final balance should be >= 0 and <= 9999999, got 11000000"
      );
    }
  }
}
