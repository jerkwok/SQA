package tests.backend.DisableTransaction;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.transactions.DisableTransaction;
import backend.exceptions.InvalidTransactionException;
import backend.TransactionContext;

public class TestConstructor {

  @Test
  public void testGoodValues() {
    TransactionContext context = new TransactionContext(0);
    context.currentUserIsAdministrator = true;
    new DisableTransaction(0, "John Doe", 0, "", context);
  }

  @Test
  public void testUserIsNotAdmin() {
    try {
      TransactionContext context = new TransactionContext(0);
      context.currentUserIsAdministrator = false;
      DisableTransaction transaction = new DisableTransaction(1, "John Doe", 0, "", context);

      fail(
        "Expected InvalidTransactionException due to user not being admin"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Must be logged in as administrator");
    }
  }

  @Test
  public void testNoAccountHolderName() {
    try {
      TransactionContext context = new TransactionContext(0);
      context.currentUserIsAdministrator = true;
      DisableTransaction transaction = new DisableTransaction(1, "", 0, "", context);

      fail(
        "Expected InvalidTransactionException due to account name being empty"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected account holder name, got empty string");
    }
  }

  @Test
  public void testNonEmptyMisc() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;

    try {
      new DisableTransaction(1, "John Doe", 0, "A", context);
      fail(
        "Expected InvalidTransactionException due to unused misc code"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected misc to be empty, got \"A\"");
    }
  }

  @Test
  public void testNonZeroAmount() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;

    try {
      new DisableTransaction(1, "John Doe", 50, "", context);
      fail(
        "Expected InvalidTransactionException due to non zero transaction amount"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected amount to be 0, got 50");
    }
  }

}
