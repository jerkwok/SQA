package backend.transactions;

import java.util.Map;

import backend.Account;
import backend.Constants;
import backend.Transaction;
import backend.TransactionContext;
import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * Represents a pay bill transaction that can be applied to the accounts Map.
 */
public class PayBillTransaction extends Transaction {
  /**
   * Enum class for the different company codes.
   */
  private enum Company {
    EC  (0, "EC"),
    CQ  (1, "CQ"),
    TV  (2, "TV");

    public int index;
    public String code;

    private Company(int index, String code) {
      this.index = index;
      this.code = code;
    }

    public static Company fromCode(String code) {
      for (Company company : Company.values()) {
        if (company.code.equals(code)) {
          return company;
        }
      }
      throw new InvalidTransactionException("Invalid company code: " + code);
    }
  }

  private final int accountNumber;
  private final String accountHolder;
  private final int amount;
  private final Company company;
  private final boolean adminInitiated;

  public PayBillTransaction(
    int accountNumber, String accountHolder, int amount, String misc,
    TransactionContext context
  ) {
    if (context.currentUserIsAdministrator) {
      if (accountHolder.equals("")) {
        throw new InvalidTransactionException(
          "Expected account holder name, got empty string"
        );
      }
    } else {
      if (!accountHolder.equals(context.currentUser)) {
        throw new InvalidTransactionException(
          "Expected account holder name to be "+
          "\"" + context.currentUser + "\", got \"" + accountHolder + "\""
        );
      }
    }

    PayBillTransaction.Company company =
      PayBillTransaction.Company.fromCode(misc);

    this.accountNumber = accountNumber;
    this.accountHolder = accountHolder;
    this.amount = amount;
    this.company = company;
    this.adminInitiated = context.currentUserIsAdministrator;
  }

  @Override
  public void apply(Map<Integer, Account> accounts) {
    // Get the account from accounts and make sure it exists.
    Account account = accounts.get(accountNumber);
    if (account == null) {
      throw new ViolatedConstraintException(
        "Tried to pay using a non-existent account " + accountNumber
      );
    }

    // Make sure the account is enabled.
    if (!account.isEnabled()) {
      throw new ViolatedConstraintException(
        "Tried to pay bill with disabled account " + accountNumber
      );
    }

    // Check that the account belongs to accountHolder.
    if (!account.getAccountHolder().equals(accountHolder)) {
      throw new ViolatedConstraintException(
        "User \"" + accountHolder + "\" tried to perform transaction " +
        "on account " + accountNumber +
        " which belongs to user \"" + account.getAccountHolder() + "\""
      );
    }

    // Compute fee based on whether it was admin initiated or not
    //   and whether they are a student or not.
    int fee = (adminInitiated) ? 0 : (
      (account.isStudent()) ? Constants.STUDENT_FEE : Constants.NORMAL_FEE
    );

    // Check that the final balance after removing the amount and fee is
    //   greater than zero.
    int finalBalance = account.getBalance() - amount - fee;
    if (finalBalance < 0) {
      throw new ViolatedConstraintException(
        "Final balance should be >= 0" + ", " +
        "got " + finalBalance
      );
    }

    // If the transaction isn't admin initiated, check that the final
    //  total paid is less than the paybill limit.
    int finalPaybillTotal = 0;

    if (!adminInitiated) {
      finalPaybillTotal = account.getPayBillTotal(company.index) + amount;

      if (finalPaybillTotal > Constants.PAYBILL_LIMIT) {
        throw new ViolatedConstraintException(
          "Final paybill total should be <= " + Constants.PAYBILL_LIMIT +
          ", got " + finalPaybillTotal
          );
        }
    }

    // Remove the funds and fee.
    account.setBalance(finalBalance);

    // If the transaction isn't admin initiated update paybill total
    //   and increment the transaction count.
    if (!adminInitiated) {
      account.setPayBillTotal(company.index, finalPaybillTotal);
      account.incrementTransactionCount();
    }
  }
}
