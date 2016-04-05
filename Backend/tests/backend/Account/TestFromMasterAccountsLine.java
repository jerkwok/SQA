package tests.backend.Account;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.Account;

public class TestFromMasterAccountsLine {

  @Test
  public void testGoodValue() {
    Account account = Account.fromMasterAccountsLine(
      "00001 John Doe             A 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testAccountNumberMinimum() {
    Account account = Account.fromMasterAccountsLine(
      "00000 John Doe             A 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 0);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testAccountNumberMaximum() {
    Account account = Account.fromMasterAccountsLine(
      "99999 John Doe             A 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 99999);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testAccountNumberLessThanFiveDigits() {
    try {
      Account.fromMasterAccountsLine(
        "1 John Doe             A 00000.00 0000 N"
      );
      fail(
        "Expected IllegalArgumentException due to non-5-digit account number"
      );
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "line does not match expected format");
    }
  }

  @Test
  public void testAccountNumberMoreThanFiveDigits() {
    try {
      Account.fromMasterAccountsLine(
        "000001 John Doe             A 00000.00 0000 N"
      );
      fail(
        "Expected IllegalArgumentException due to non-5-digit account number"
      );
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "line does not match expected format");
    }
  }

  @Test
  public void testAccountNumberNonDigits() {
    try {
      Account.fromMasterAccountsLine(
        "0000a John Doe             A 00000.00 0000 N"
      );
      fail(
        "Expected IllegalArgumentException due to non-numeric account number"
      );
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "line does not match expected format");
    }
  }

  @Test
  public void testAccountHolderMinimumLength() {
    Account account = Account.fromMasterAccountsLine(
      "00001 A                    A 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "A");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testAccountHolderMaximumLength() {
    Account account = Account.fromMasterAccountsLine(
      "00001 AAAABBBBAAAABBBBAAAA A 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "AAAABBBBAAAABBBBAAAA");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testAccountHolderMoreThanTwentyCharacters() {
    try {
      Account.fromMasterAccountsLine(
        "00001 AAAABBBBAAAABBBBAAAAB A 00000.00 0000 N"
      );
      fail(
        "Expected IllegalArgumentException due to 21-character " +
        "account holder name"
      );
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "line does not match expected format");
    }
  }

  @Test
  public void testAccountHolderLessThanTwentyCharacters() {
    try {
      Account.fromMasterAccountsLine(
        "00001 AAAABBBBAAAABBBBAAA A 00000.00 0000 N"
      );
      fail(
        "Expected IllegalArgumentException due to 19-character " +
        "account holder name"
      );
    } catch (IllegalArgumentException e) {
      assertEquals(e.getMessage(), "line does not match expected format");
    }
  }

  @Test
  public void testDisabledFlag() {
    Account account = Account.fromMasterAccountsLine(
      "00001 John Doe             D 00000.00 0000 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertFalse(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testInvalidActiveFlag() {
    for (char c = 32; c <= 126; c++) {
      if (c == 'A' || c == 'D') {
        continue;
      }
      try {
        Account.fromMasterAccountsLine(
          "00001 John Doe             " + c + " 00000.00 0000 N"
        );
        fail(
          "Expected IllegalArgumentException due to invalid active flag " +
          "(" + c + ")"
        );
      } catch (IllegalArgumentException e) {
        assertEquals(e.getMessage(), "line does not match expected format");
      }
    }
  }

  @Test
  public void testMaximumBalance() {
    Account account = Account.fromMasterAccountsLine(
      "00001 John Doe             A 99999.99 0000 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 9999999);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testMaximumTransactionCount() {
    Account account = Account.fromMasterAccountsLine(
      "00001 John Doe             A 00000.00 9999 N"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertFalse(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testStudentFlag() {
    Account account = Account.fromMasterAccountsLine(
      "00001 John Doe             A 00000.00 0000 S"
    );

    assertEquals(account.getAccountNumber(), 1);
    assertEquals(account.getAccountHolder(), "John Doe");
    assertTrue(account.isEnabled());
    assertEquals(account.getBalance(), 0);
    assertTrue(account.isStudent());
    assertEquals(account.getWithdrawalTotal(), 0);
    assertEquals(account.getTransferTotal(), 0);

    for (int i = 0; i < 3; i++) {
      assertEquals(account.getPayBillTotal(i), 0);
    }
  }

  @Test
  public void testInvalidStudentFlag() {
    for (char c = 32; c <= 126; c++) {
      if (c == 'N' || c == 'S') {
        continue;
      }
      try {
        Account.fromMasterAccountsLine(
          "00001 John Doe             A 00000.00 0000 " + c
        );
        fail(
          "Expected IllegalArgumentException due to invalid student flag " +
          "(" + c + ")"
        );
      } catch (IllegalArgumentException e) {
        assertEquals(e.getMessage(), "line does not match expected format");
      }
    }
  }

}
