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
import backend.exceptions.InvalidTransactionException;

public class TestConstructor {
  
  @Test
  public void knownGoodValue() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    context.transferContext = new TransferContext(1, "John Doe", 100);
    new TransferTransaction(2, "Billy Bob", 100, "", context);
  }
  
  @Test
  public void noAccountName() {
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
    
    try {      
      new TransferTransaction(2, "", 100, "", context);
        fail(
        "Expected InvalidTransactionException due to empty account name"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(), "Expected account holder name to be non-empty"
      );
    }
  }
  
  @Test
  public void mismatchedAmounts() {
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
    try {
      new TransferTransaction(2, account2.getAccountHolder(), 200, "", context);
      fail(
        "Expected InvalidTransactionException due to mismatched amounts"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected second transaction amount (200) to equal " +
          "first transfer amount (100)"
      );
    }
  }
  
}
