package tests.backend.ChangePlanTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.transactions.ChangePlanTransaction;
import backend.Account;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;


public class TestApply {

  @Test
  public void testGoodValues() {
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new ChangePlanTransaction(account.getAccountNumber(), account.getAccountHolder(), 0, "", context);

    transaction.apply(accounts);
  }

  @Test
  public void testAccountDisabled() {
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(1, "John Doe", 0, false, false, 0);

    accounts.put(account.getAccountNumber(), account);
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new ChangePlanTransaction(account.getAccountNumber(), account.getAccountHolder(), 0, "", context);

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to account being disabled"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to change plan of disabled account 1"
      );
    }
  }

  @Test
  public void testNonExistentAccount() {
    Map<Integer, Account> accounts = new HashMap<>();
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;

    Transaction transaction = new ChangePlanTransaction(1, "John Doe", 0, "", context);

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to change plan of a non-existent account 1"
      );
    }
  }

  @Test
  public void testMismatchedAccountNames() {
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new ChangePlanTransaction(account.getAccountNumber(), "Jane Doe", 0, "", context);

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to mismatched account holder names"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "User \"Jane Doe\" tried to perform transaction on account 1" 
        +" which belongs to user \"" + account.getAccountHolder() + "\""
      );
    }
  }
}
