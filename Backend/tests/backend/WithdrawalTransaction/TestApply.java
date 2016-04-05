package tests.backend.WithdrawalTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.Account;
import backend.Transaction;
import backend.transactions.WithdrawalTransaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

public class TestApply {

  @Test
  public void knownGoodValue() {
    Account account = new Account(1, "John Doe", 1000, true, false, 0);
    Map<Integer, Account> accounts = new HashMap<>();
    accounts.put(account.getAccountNumber(), account);
    
    TransactionContext context = new TransactionContext(1);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 500, "", context
    );
  }
  
  @Test
  public void nullAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      2, account.getAccountHolder(), 500, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
      e.getMessage(), "Tried to withdraw from non-existent account 2"
      );
    }
  }
  
  @Test
  public void enabledAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000, false, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      1, account.getAccountHolder(), 500, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to disabled account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
      e.getMessage(), "Tried to withdraw from disabled account 1"
      );
    }
  }
  
  @Test
  public void nameOwnsAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000, true, false, 0);
    Account account2 = new Account(2, "Billy Bob", 1000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      2, account.getAccountHolder(), 500, "", context
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
        "belongs to user \"Billy Bob\""
      );
    }
  }
  
  @Test
  public void feePayable() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account = new Account(1, "John Doe", 500, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 500, "", context
    );
    
    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to insufficient funds"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Final balance should be >= 0, got -10"
      );
    }
  }
  
  @Test
  public void withinDailyLimit() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 
      55000, "", context
    );
    
    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due exceeding daily limit"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Final withdrawal total should be <= 50000, got 55000"
      );
    }
  }
  
  @Test
  public void updateTotal() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new WithdrawalTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 500, "", context
    );
    
    transaction.apply(accounts);
    assertEquals(account.getWithdrawalTotal(), 500);
  }
}
