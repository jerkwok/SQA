package backend;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Holds all data needed to represent an account in-memory and contains methods
 * to support converting from and to the various data formats (master accounts
 * file format and current accounts file format).
 */
public class Account {
  private final int accountNumber;
  private final String accountHolder;
  private int balance;
  private boolean isEnabled;
  private boolean isStudent;
  private int transactionCount;

  // Used to enforce daily withdrawal, transfer, etc. limits.
  private int withdrawalTotal = 0;
  private int transferTotal = 0;
  private int[] payBillTotals = {0, 0, 0};

  public Account(
    int accountNumber, String accountHolder, int balance, boolean isEnabled,
    boolean isStudent, int transactionCount
  ) {
    this.accountNumber = accountNumber;
    this.accountHolder = accountHolder;
    this.balance = balance;
    this.isEnabled = isEnabled;
    this.isStudent = isStudent;
    this.transactionCount = transactionCount;
  }

  public int getAccountNumber()             { return accountNumber;           }
  public String getAccountHolder()          { return accountHolder;           }
  public int getBalance()                   { return balance;                 }
  public boolean isEnabled()                { return isEnabled;               }
  public boolean isStudent()                { return isStudent;               }
  public int getWithdrawalTotal()           { return withdrawalTotal;         }
  public int getTransferTotal()             { return transferTotal;           }
  public int getPayBillTotal(int company)   { return payBillTotals[company];  }

  public void setBalance(int balance)       { this.balance = balance;         }
  public void setEnabled(boolean isEnabled) { this.isEnabled = isEnabled;     }
  public void setStudent(boolean isStudent) { this.isStudent = isStudent;     }
  public void incrementTransactionCount()   { this.transactionCount++;        }
  public void setWithdrawalTotal(int total) { this.withdrawalTotal = total;   }
  public void setTransferTotal(int total)   { this.transferTotal = total;     }

  public void setPayBillTotal(int company, int total) {
    this.payBillTotals[company] = total;
  }

  // Pattern used to parse accounts from the master accounts file format.
  private static final Pattern masterAccountsPattern = Pattern.compile(
    "(\\d{5}) (.{20}) (A|D) (\\d{5}).(\\d{2}) (\\d{4}) (S|N)"
  );

  /**
   * Converts a line from the master accounts file into an Account.
   * <p>
   * Throws an IllegalArgumentException if the line is malformed.
   */
  public static Account fromMasterAccountsLine(String line) {
    Matcher matcher = masterAccountsPattern.matcher(line);

    if (!matcher.matches()) {
      throw new IllegalArgumentException("line does not match expected format");
    }

    int accountNumber = Integer.parseInt(matcher.group(1), 10);
    String accountHolder = matcher.group(2).trim();
    boolean isEnabled = matcher.group(3).equals("A");

    int balance;
    {
      int balanceDollars = Integer.parseInt(matcher.group(4), 10);
      int balanceCents = Integer.parseInt(matcher.group(5), 10);
      balance = balanceDollars * 100 + balanceCents;
    }

    int transactionCount = Integer.parseInt(matcher.group(6), 10);
    boolean isStudent = matcher.group(7).equals("S");

    return new Account(
      accountNumber, accountHolder, balance, isEnabled, isStudent,
      transactionCount
    );
  }

  /**
   * Returns a representation of the Account in the current accounts file format.
   */
  public String toCurrentAccountsLine() {
    return String.format("%05d %-20s %c %05d.%02d %c\n",
      accountNumber,          // %05d
      accountHolder,          // %-20s
      isEnabled ? 'A' : 'D',  // %c
      balance / 100,          // %05d
      balance % 100,          // %02d
      isStudent ? 'S' : 'N'   // %c
    );
  }

  /**
   * Returns a representation of the Account in the master accounts file format.
   */
  public String toMasterAccountsLine() {
    return String.format("%05d %-20s %c %05d.%02d %04d %c\n",
      accountNumber,          // %05d
      accountHolder,          // %-20s
      isEnabled ? 'A' : 'D',  // %c
      balance / 100,          // %05d
      balance % 100,          // %02d
      transactionCount,       // %04d
      isStudent ? 'S' : 'N'   // %c
    );
  }
}
