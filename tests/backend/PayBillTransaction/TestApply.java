package tests.backend.PayBillTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.BackEnd;
import backend.Account;
import backend.Transaction;
import backend.transactions.PayBillTransaction;
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
    
    Transaction transaction = new PayBillTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 100, "EC", context
    );
    
    transaction.apply(accounts);
  }
  
  @Test
  public void nullAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      2, account.getAccountHolder(), 100, "CQ", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
      e.getMessage(), "Tried to pay using a non-existent account 2"
      );
    }
  }
  
  @Test
  public void enabledAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 100000, false, false, 0);

    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 
      100, "EC", context
    );
    
    try {
      transaction.apply(accounts);
    fail(
      "Expected ViolatedConstraintException due to disabled account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to pay bill with disabled account 1"
      );
    }
  }
  
  @Test
  public void nameOwnsAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 100000, true, false, 0);
    Account account2 = new Account(2, "Billy Bob", 100000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      2, account.getAccountHolder(), 100, "TV", context
    );
    
    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to account ownership"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "User \"John Doe\" tried to perform transaction on account 2 " +
          "which belongs to user \"Billy Bob\""
      );
    }
  }
  
  @Test
  // Failed due to PayBillTransaction adding instead of subtracting the amount.
  public void feePayable() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 100000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 
      100000, "EC", context
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
    Account account =  new Account(1, "John Doe", 500000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 
      200500, "EC", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to exceeding daily limit"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Final paybill total should be <= 200000, got 200500"
      );
    }
  }
  
  @Test
  public void updateTotal() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 500000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    Transaction transaction = new PayBillTransaction(
      account.getAccountNumber(), account.getAccountHolder(), 
      200000, "EC", context
    );
    transaction.apply(accounts);
    
    assertEquals(account.getPayBillTotal(0), 200000);
  }

}
