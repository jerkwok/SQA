package tests.backend.PayBillTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.transactions.PayBillTransaction;

public class TestConstructor {

  @Test
  public void knownGoodValue() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    new PayBillTransaction(1, "John Doe", 1, "EC", context);
  }
  
  @Test
  public void emptyAcctHolder() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
      
    try {
      new PayBillTransaction(1, "", 1, "EC", context);
      fail(
        "Expected InvalidTransactionException due to empty account name"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name, got empty string"
      );
    }    
  }
  
  @Test
  public void mismatchedAccount() {
    TransactionContext context = new TransactionContext(1);
    context.currentUser = "John Doe";

    try {
      new PayBillTransaction(1, "Billy Bob", 1, "EC", context);
      fail(
      "Expected InvalidTransactionException due to mismatched account name"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be \"John Doe\", got \"Billy Bob\""
      );
    }
  }

}
