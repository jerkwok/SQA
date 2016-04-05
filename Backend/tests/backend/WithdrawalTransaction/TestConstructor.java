package tests.backend.WithdrawalTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.transactions.WithdrawalTransaction;

public class TestConstructor {
  @Test
  public void knownGoodValue() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    new WithdrawalTransaction(1, "John Doe", 500, "", context);
  }
  
  @Test
  public void nonEmptyMisc() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = false;
    
    try {
      new WithdrawalTransaction(1, "John Doe", 500, "A", context);
      fail(
        "Expected InvalidTransactionException due to unused misc code"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(), "Expected misc to be empty, got \"A\""
      );
    }
  }
  
  @Test
  public void noAccountName() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    
    try {
      new WithdrawalTransaction(1, "", 500, "", context);
      fail(
        "Expected InvalidTransactionException due to empty account name"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(), "Expected account holder name, got empty string"
      );
    }
  }
  
  @Test
  public void mismatchedAccountNames() {
    TransactionContext context = new TransactionContext(1);
    context.currentUser = "John Doe";
    
    try {
      new WithdrawalTransaction(1, "Billy Bob", 1, "", context);
      fail(
        "Expected InvalidTransactionException due to mismatched names"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be \"John Doe\", got \"Billy Bob\""
      );
    }
  }

}
