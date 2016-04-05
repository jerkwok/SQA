package tests.backend.Transaction;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.Transaction;
import backend.TransactionContext;
import backend.TransferContext;
import backend.exceptions.InvalidTransactionException;
import backend.transactions.ChangePlanTransaction;
import backend.transactions.CreateTransaction;
import backend.transactions.DeleteTransaction;
import backend.transactions.DepositTransaction;
import backend.transactions.DisableTransaction;
import backend.transactions.EnableTransaction;
import backend.transactions.PayBillTransaction;
import backend.transactions.TransferTransaction;
import backend.transactions.WithdrawalTransaction;

public class TestFromLine {

  private void checkTransactionContext(
    TransactionContext context, String currentUser,
    boolean currentUserIsAdministrator, int highestAccountId)
  {
    assertEquals(context.currentUser, currentUser);
    assertEquals(context.currentUserIsAdministrator, currentUserIsAdministrator);
    assertEquals(context.highestAccountId, highestAccountId);
  }

  private void checkTransferContext(
    TransferContext context, String sourceAccountHolder,
    int sourceAccountNumber, int amount
  ) {
    assertEquals(context.sourceAccountHolder, sourceAccountHolder);
    assertEquals(context.sourceAccountNumber, sourceAccountNumber);
    assertEquals(context.amount, amount);
  }

  @Test
  public void testBadlyFormedLines() {
    String[] badLines = {
      "010                      00000 00000.00 A ",
      "0                      00000 00000.00 A ",
      "10                     00000 00000.00 A ",
      "10                       00000 00000.00 A ",
      "10                      000000 00000.00 A ",
      "10                      0000 00000.00 A ",
      "10                      00000 000000.00 A ",
      "10                      00000 0000.00 A ",
      "10                      00000 00000.00 A",
      "10                      00000 00000.00 A  "
    };

    for (String badLine : badLines) {
      try {
        TransactionContext context = new TransactionContext(1);
        Transaction.fromLine(badLine, context);
        fail(
          "Expected InvalidTransactionException due to malformed line"
        );
      } catch (InvalidTransactionException e) {
        assertEquals(e.getMessage(), "Line does not match expected format");
      }
    }
  }

  @Test
  public void testInvalidTransactionCodes() {
    int[] badCodes = { 11, 12, 99 };

    for (int badCode : badCodes) {
      try {
        TransactionContext context = new TransactionContext(1);
        context.currentUserIsAdministrator = true;
        Transaction.fromLine(
          String.format("%02d                      00000 00000.00   ", badCode),
          context
        );
        fail(
          "Expected InvalidTransactionException due to invalid code " + badCode
        );
      } catch (InvalidTransactionException e) {
        assertEquals(e.getMessage(), "Invalid transaction code: " + badCode);
      }
    }
  }

  @Test
  public void testLogin() {
    TransactionContext context = new TransactionContext(1);
    Transaction transaction = Transaction.fromLine(
      "10                      00000 00000.00 A ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    transaction = Transaction.fromLine(
      "10 John Doe             00000 00000.00 S ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, "John Doe", false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      context.currentUserIsAdministrator = true;
      Transaction.fromLine(
        "10                      00000 00000.00 A ", context
      );
      fail(
        "Expected InvalidTransactionException because someone is already logged"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Tried to login while logged in");
    }

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "10 John Doe             00000 00000.00 A ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-empty account holder"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be empty, got \"John Doe\""
      );
    }

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "10                      00000 00000.00 S ", context
      );
      fail(
        "Expected InvalidTransactionException due to empty account holder"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name, got empty string"
      );
    }

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "10 John Doe             99999 00000.00 S ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-zero account ID"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected account number to be 0, got 99999");
    }

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "10 John Doe             00000 99999.99 S ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-zero amount"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected amount to be 0, got 9999999");
    }

    for (char c = 65; c <= 90; c++) {
      if (c == 'A' || c == 'S') {
        continue;
      }
      try {
        context = new TransactionContext(1);
        Transaction.fromLine(
          "10 John Doe             00000 00000.00 " + c + " ", context
        );
        fail(
          "Expected InvalidTransactionException due to invalid misc (" + c + ")"
        );
      } catch (InvalidTransactionException e) {
        assertEquals(e.getMessage(), "Unknown login type: " + c);
      }
    }
  }

  @Test
  public void testLogout() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "00                      00000 00000.00   ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, null, false, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    context.currentUser = "John Doe";
    transaction = Transaction.fromLine(
      "00 John Doe             00000 00000.00   ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, null, false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      context.currentUserIsAdministrator = true;
      Transaction.fromLine(
        "00 John Doe             00000 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-empty account holder"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be empty, got \"John Doe\""
      );
    }

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "00 John Doe             00000 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }

    try {
      context = new TransactionContext(1);
      context.currentUser = "John Doe";
      Transaction.fromLine(
        "00                      00000 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to empty account holder"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be \"John Doe\", got \"\""
      );
    }

    try {
      context = new TransactionContext(1);
      context.currentUser = "John Doe";
      Transaction.fromLine(
        "00 Billy Joe            00000 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to mismatched account holder"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected account holder name to be \"John Doe\", got \"Billy Joe\""
      );
    }

    try {
      context = new TransactionContext(1);
      context.currentUserIsAdministrator = true;
      Transaction.fromLine(
        "00                      99999 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-zero account ID"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected account ID to be 0, got 99999");
    }

    try {
      context = new TransactionContext(1);
      context.currentUserIsAdministrator = true;
      Transaction.fromLine(
        "00                      00000 99999.99   ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-zero balance"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected amount to be 0, got 9999999");
    }

    try {
      context = new TransactionContext(1);
      context.currentUserIsAdministrator = true;
      Transaction.fromLine(
        "00                      00000 00000.00 A ", context
      );
      fail(
        "Expected InvalidTransactionException due to non-empty misc"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(e.getMessage(), "Expected misc to be empty, got \"A\"");
    }
  }

  @Test
  public void testDeposit() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "04 John Doe             00001 00001.00   ", context
    );
    assertTrue(transaction instanceof DepositTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    context.currentUser = "John Doe";
    transaction = Transaction.fromLine(
      "04 John Doe             00001 00001.00   ", context
    );
    assertTrue(transaction instanceof DepositTransaction);
    checkTransactionContext(context, "John Doe", false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "04 John Doe             00001 00001.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testWithdrawal() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "01 John Doe             00001 00001.00   ", context
    );
    assertTrue(transaction instanceof WithdrawalTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    context.currentUser = "John Doe";
    transaction = Transaction.fromLine(
      "01 John Doe             00001 00001.00   ", context
    );
    assertTrue(transaction instanceof WithdrawalTransaction);
    checkTransactionContext(context, "John Doe", false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "01 John Doe             00001 00001.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testTransfer() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "02 John Doe             00001 00001.00   ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, null, true, 1);
    checkTransferContext(context.transferContext, "John Doe", 1, 100);

    transaction = Transaction.fromLine(
      "02 Jane Doe             00002 00001.00   ", context
    );
    assertTrue(transaction instanceof TransferTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    context.currentUser = "John Doe";
    transaction = Transaction.fromLine(
      "02 John Doe             00001 00001.00   ", context
    );
    assertNull(transaction);
    checkTransactionContext(context, "John Doe", false, 1);
    checkTransferContext(context.transferContext, "John Doe", 1, 100);

    transaction = Transaction.fromLine(
      "02 Jane Doe             00002 00001.00   ", context
    );
    assertTrue(transaction instanceof TransferTransaction);
    checkTransactionContext(context, "John Doe", false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "02 John Doe             00001 00001.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testPayBill() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "03 John Doe             00001 00001.00 CQ", context
    );
    assertTrue(transaction instanceof PayBillTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    context = new TransactionContext(1);
    context.currentUser = "John Doe";
    transaction = Transaction.fromLine(
      "03 John Doe             00001 00001.00 CQ", context
    );
    assertTrue(transaction instanceof PayBillTransaction);
    checkTransactionContext(context, "John Doe", false, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "03 John Doe             00001 00001.00 CQ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testCreate() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "05 John Doe             00000 00000.00   ", context
    );
    assertTrue(transaction instanceof CreateTransaction);
    checkTransactionContext(context, null, true, 2);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "05 John Doe             00000 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testDelete() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "06 John Doe             00001 00000.00   ", context
    );
    assertTrue(transaction instanceof DeleteTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "06 John Doe             00001 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testEnable() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "09 John Doe             00001 00000.00   ", context
    );
    assertTrue(transaction instanceof EnableTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "09 John Doe             00001 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testDisable() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "07 John Doe             00001 00000.00   ", context
    );
    assertTrue(transaction instanceof DisableTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "07 John Doe             00001 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

  @Test
  public void testChangePlan() {
    TransactionContext context = new TransactionContext(1);
    context.currentUserIsAdministrator = true;
    Transaction transaction = Transaction.fromLine(
      "08 John Doe             00001 00000.00   ", context
    );
    assertTrue(transaction instanceof ChangePlanTransaction);
    checkTransactionContext(context, null, true, 1);
    assertNull(context.transferContext);

    try {
      context = new TransactionContext(1);
      Transaction.fromLine(
        "08 John Doe             00001 00000.00   ", context
      );
      fail(
        "Expected InvalidTransactionException due to no one logged in"
      );
    } catch (InvalidTransactionException e) {
      assertEquals(
        e.getMessage(),
        "Expected login before non-login transaction"
      );
    }
  }

}
