package tests.backend.Account;

import static org.junit.Assert.*;
import org.junit.Test;
import backend.Account;

public class TestToMasterAccountsLine {

  @Test
  public void testKnownGoodValue() {
    Account account = new Account(1, "John Doe", 0, true, false, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 John Doe             A 00000.00 0000 N\n"
    );
  }

  @Test
  public void testAccountNumber() {
    Account account = new Account(99999, "John Doe", 0, true, false, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "99999 John Doe             A 00000.00 0000 N\n"
    );
  }

  @Test
  public void testAccountHolderName() {
    Account account = new Account(1, "AAAABBBBAAAABBBBAAAA", 0, true, false, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 AAAABBBBAAAABBBBAAAA A 00000.00 0000 N\n"
    );
  }

  @Test
  public void testBalance() {
    Account account = new Account(1, "John Doe", 9999999, true, false, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 John Doe             A 99999.99 0000 N\n"
    );
  }

  @Test
  public void testDisabled() {
    Account account = new Account(1, "John Doe", 0, false, false, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 John Doe             D 00000.00 0000 N\n"
    );
  }

  @Test
  public void testTransactionCount() {
    Account account = new Account(1, "John Doe", 0, true, true, 9999);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 John Doe             A 00000.00 9999 S\n"
    );
  }

  @Test
  public void testStudent() {
    Account account = new Account(1, "John Doe", 0, true, true, 0);

    assertEquals(
      account.toMasterAccountsLine(),
      "00001 John Doe             A 00000.00 0000 S\n"
    );
  }

}
