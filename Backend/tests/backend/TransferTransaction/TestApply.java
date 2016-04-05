package tests.backend.TransferTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Map;
import java.util.HashMap;
import backend.Account;
import backend.Transaction;
import backend.TransactionContext;
import backend.transactions.TransferTransaction;
import backend.TransferContext;
import backend.exceptions.ViolatedConstraintException;

public class TestApply {

  @Test
  public void knownGoodValue() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    
    Account account = new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 = new Account(2, "Billy Bob", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    
    context.currentUser = account.getAccountHolder();
    context.transferContext = new TransferContext(1, "John Doe", 100);
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(), 100, "", context
    );
  }
  
  @Test
  public void sourceExists() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(2, "Billy Bob", 100);
    
    Transaction transaction = new TransferTransaction(
      1, account.getAccountHolder(), 100, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null source account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Tried to transfer from non-existent account 2"
      );
    }
  }
  
  @Test
  public void destinationExists() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 100
    );
    
    Transaction transaction = new TransferTransaction(
      2, "Billy Bob",
      100, "", context
    );
    
    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to null destination account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Tried to transfer to non-existent account 2"
      );
    }
  }
  
  @Test
  public void sourceEnabled() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, false, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 100
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(),
      100, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to disabled source account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to transfer from disabled account 1"
      );
    }
  }
  
  @Test
  public void destinationEnabled() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, false, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 100
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(),
      100, "", context
    );
    
    try {
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to" + 
          " disabled destination account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Tried to transfer to disabled account 2"
      );
    }
  }
  
  @Test
  public void sourceOwnsAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account2.getAccountHolder(), 100
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(), 100, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to mismatched accounts"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "User \"Billy Bob\" tried to perform transaction " +
          "on account 1 which belongs to user \"John Doe\""
      );
    }
  }
  
  @Test
  public void destOwnsAccount() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 100
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account.getAccountHolder(), 100, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to mismatched accounts"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Expected account to belong to \"John Doe\", belongs to " +
          "\"Billy Bob\" instead"
      );
    }
  }
  
  @Test
  public void sourceFeePayable() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, true, false, 0);
    
    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 10000
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(),
      10000, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to insufficient funds " +
        "of source account"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Final source account balance should be >= 0, got -10"
      );
    }
  }
  
  @Test
  public void destinationMaxBalance() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 = new Account(2, "Billy Bob", 9999900, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 110
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(), 110, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to destination account's " +
          " balance exceeding the maximum allowed"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(), "Final balance should be <= 9999999, got 10000000"
      );
    }
  }
  
  @Test
  public void sourceDailyLimit() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 1000000, true, false, 0);
    Account account2 =  new Account(2, "Billy Bob", 10000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 110000
    );
    
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(),
      110000, "", context
    );
    
    try {      
      transaction.apply(accounts);
      fail(
        "Expected ViolatedConstraintException due to daily limit being exceeded"
      );
    } catch (ViolatedConstraintException e) {
      assertEquals(
        e.getMessage(),
        "Final transfer total should be <= 100000, got 110000"
      );
    }
  }
  
  @Test
  public void updateTotal() {
    TransactionContext context = new TransactionContext(1);
    Map<Integer, Account> accounts = new HashMap<>();
    Account account =  new Account(1, "John Doe", 10000, true, false, 0);
    Account account2 = new Account(2, "Billy Bob", 10000, true, false, 0);

    accounts.put(account.getAccountNumber(), account);
    accounts.put(account2.getAccountNumber(), account2);
    context.currentUser = account.getAccountHolder();
    
    context.transferContext = new TransferContext(
      account.getAccountNumber(), account.getAccountHolder(), 100
    );
    Transaction transaction = new TransferTransaction(
      account2.getAccountNumber(), account2.getAccountHolder(),
      100, "", context
    );
    transaction.apply(accounts);
    assertEquals(account.getTransferTotal(), 100);
  }

}
