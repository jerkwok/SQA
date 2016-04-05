package tests.backend.CreateTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.transactions.CreateTransaction;
import backend.Account;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

public class TestApply {

  @Test
  public void testGoodValues() {
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(0, "John Doe", 0, false, false, 0);
    accounts.put(account.getAccountNumber(), account);
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.currentUser = account.getAccountHolder();

    Transaction transaction = new CreateTransaction(0, "Billy Bob", 10, "", context);

    transaction.apply(accounts);
  }


  @Test
  public void testTakenAccountID() {
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(1, "John Doe", 0, false, false, 0);
    accounts.put(account.getAccountNumber(), account);
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.highestAccountId = 0;

    Transaction transaction = new CreateTransaction(0, "Jane Doe", 0, "", context);

    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to account number being taken"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(e.getMessage(), "Tried to create an account with a number that is " +
        "already in use: " + 1);
    }
  }
}
