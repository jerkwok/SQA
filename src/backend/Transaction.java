package backend;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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

/**
 * Base class for all transaction types. Contains the abstract apply method used
 * to apply a transaction to a Map of Accounts, and a static method that
 * converts from the transactions file format into Transactions.
 *
 * This should really just be an interface, but an abstract class was used
 * instead for two reasons: classes can contain private fields, and support for
 * static fields in interfaces was only added recently in Java 8.
 */
public abstract class Transaction {
  /**
   * Applies a transaction to a Map of Accounts, mutating the Map or an Account
   * within it.
   */
  public abstract void apply(Map<Integer, Account> accounts);

  // Constants for the various transaction types. Used for parsing.
  private static final int LOGOUT     = 0;
  private static final int WITHDRAWAL = 1;
  private static final int TRANSFER   = 2;
  private static final int PAYBILL    = 3;
  private static final int DEPOSIT    = 4;
  private static final int CREATE     = 5;
  private static final int DELETE     = 6;
  private static final int DISABLE    = 7;
  private static final int CHANGEPLAN = 8;
  private static final int ENABLE     = 9;
  private static final int LOGIN      = 10;

  // Pattern used to parse the transactions file format.
  private static final Pattern transactionPattern = Pattern.compile(
    "(\\d{2}) (.{20}) (\\d{5}) (\\d{5}).(\\d{2}) ([A-Z ]{2})"
  );

  /**
   * Parses a line from the transactions file, possibly returning a Transaction
   * when appropriate (non-login/logout, second transfer line), otherwise
   * returning null.
   *
   * A context must be supplied, which contains all the state that must be
   * maintained between individual lines during parsing (who's logged in, etc.).
   *
   * @throws InvalidTransactionException when line is an invalid transaction
   */
  public static Transaction fromLine(String line, TransactionContext context) {
    Matcher matcher = transactionPattern.matcher(line);

    if (!matcher.matches()) {
      throw new InvalidTransactionException(
        "Line does not match expected format"
      );
    }

    int code = Integer.parseInt(matcher.group(1), 10);
    String accountHolder = matcher.group(2).trim();
    int accountNumber = Integer.parseInt(matcher.group(3), 10);

    int amount;
    {
      int amountDollars = Integer.parseInt(matcher.group(4), 10);
      int amountCents = Integer.parseInt(matcher.group(5), 10);
      amount = amountDollars * 100 + amountCents;
    }

    String misc = matcher.group(6).trim();

    // If context.transferContext is not null then ensure that the
    //   current transaction is the second transfer transaction.
    if (context.transferContext != null && code != 2) {
      context.transferContext = null;
      throw new InvalidTransactionException(
        "Expected transfer transaction after previous transfer transaction"
      );
    }

    // If there is no current user and the transaction is not a login,
    //   throw an exception.
    if (!context.currentUserIsAdministrator &&
        context.currentUser == null &&
        code != 10
    ) {
      throw new InvalidTransactionException(
        "Expected login before non-login transaction"
      );
    }

    // Do further validation based on what type of transaction it is.
    switch (code) {
      case LOGOUT: {
        // If the current user is an admin...
        if (context.currentUserIsAdministrator) {
          // ... the account holder field should be empty.
          if (!accountHolder.equals("")) {
            throw new InvalidTransactionException(
              "Expected account holder name to be empty, "+
              "got \"" + accountHolder + "\""
            );
          }
        } else {
          // The account holder field should be whoever is currently logged in.
          if (!accountHolder.equals(context.currentUser)) {
            throw new InvalidTransactionException(
              "Expected account holder name to be "+
              "\"" + context.currentUser + "\", got \"" + accountHolder + "\""
            );
          }
        }
        // Account number should be 0 for logouts.
        if (accountNumber != 0) {
          throw new InvalidTransactionException(
            "Expected account ID to be 0, got " + accountNumber
          );
        }
        // Amount should be 0 for logouts.
        if (amount != 0) {
          throw new InvalidTransactionException(
            "Expected amount to be 0, got " + amount
          );
        }
        // Misc field should be empty for logouts.
        if (!misc.equals("")) {
          throw new InvalidTransactionException(
            "Expected misc to be empty, got \"" + misc + "\""
          );
        }
        // Update the context.
        context.currentUser = null;
        context.currentUserIsAdministrator = false;
        // Logout does nothing when applied, so just return null.
        return null;
      }

      case WITHDRAWAL: {
        return new WithdrawalTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case TRANSFER: {
        if (!misc.equals("")) {
          throw new InvalidTransactionException(
            "Expected misc to be empty, got \"" + misc + "\""
          );
        }
        if (accountNumber == 0) {
          throw new InvalidTransactionException(
            "Expected account ID to not be 0"
          );
        }

        if (context.transferContext == null) {
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
          context.transferContext =
            new TransferContext(accountNumber, accountHolder, amount);

          return null;

        } else {
          return new TransferTransaction(
            accountNumber, accountHolder, amount, misc, context
          );
        }
      }

      case PAYBILL: {
        return new PayBillTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case DEPOSIT: {
        return new DepositTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case CREATE: {
        return new CreateTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case DELETE: {
        return new DeleteTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case DISABLE: {
        return new DisableTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case CHANGEPLAN: {
        return new ChangePlanTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case ENABLE: {
        return new EnableTransaction(
          accountNumber, accountHolder, amount, misc, context
        );
      }

      case LOGIN: {
        if (context.currentUserIsAdministrator || context.currentUser != null) {
          throw new InvalidTransactionException(
            "Tried to login while logged in"
          );
        }
        if (accountNumber != 0) {
          throw new InvalidTransactionException(
            "Expected account number to be 0, got " + accountNumber
          );
        }
        if (amount != 0) {
          throw new InvalidTransactionException(
            "Expected amount to be 0, got " + amount
          );
        }
        switch (misc) {
          case "A": {
            if (!accountHolder.equals("")) {
              throw new InvalidTransactionException(
                "Expected account holder name to be empty, "+
                "got \"" + accountHolder + "\""
              );
            }
            context.currentUserIsAdministrator = true;
            break;
          }
          case "S": {
            if (accountHolder.equals("")) {
              throw new InvalidTransactionException(
                "Expected account holder name, got empty string"
              );
            }
            context.currentUser = accountHolder;
            break;
          }
          default: {
            throw new InvalidTransactionException(
              "Unknown login type: " + misc
            );
          }
        }
        return null;
      }

      default: {
        throw new InvalidTransactionException(
          "Invalid transaction code: " + code
        );
      }
    }
  }
}
