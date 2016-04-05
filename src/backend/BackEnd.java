package backend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Writer;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Map;
import java.util.Queue;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Collection;

import backend.exceptions.InvalidTransactionException;
import backend.exceptions.ViolatedConstraintException;

/**
 * The "main" class of the back end program.
 */
public class BackEnd {
  /**
   * The program entry point.
   *
   * Paths to all of the input and output files must be passed in as
   * command-line arguments. See README.md in project root for more info.
   */
  public static void main(String[] args) {
    if (args.length < 4) {
      System.out.println(
        "Usage: java backend.BackEnd "+
        "<old_master_accounts_file> "+
        "<transactions_file> "+
        "<new_master_accounts_file> "+
        "<current_accounts_file>"
      );
      return;
    }

    Map<Integer, Account> accounts;
    Queue<Transaction> transactions;
    int highestAccountId = 0;

    // Load the accounts from the old master accounts file.
    try (
      BufferedReader accountsReader =
        new BufferedReader(new FileReader(args[0]))
    ) {
      accounts = readAccounts(accountsReader);
    } catch (IOException e) {
      System.err.println(e);
      return;
    }

    // Figure out what the highest account ID is.
    for (int accountNumber : accounts.keySet()) {
      if (accountNumber > highestAccountId) {
        highestAccountId = accountNumber;
      }
    }

    // Load the transactions from transactions file.
    try (
      BufferedReader transactionsReader =
        new BufferedReader(new FileReader(args[1]))
    ) {
      transactions = readTransactions(transactionsReader, highestAccountId);
    } catch (IOException e) {
      System.err.println(e);
      return;
    }

    // Apply all of the transactions to the accounts.
    while (!transactions.isEmpty()) {
      Transaction transaction = transactions.remove();

      try {
        transaction.apply(accounts);
      } catch (ViolatedConstraintException e) {
        System.err.println(e);
      }
    }

    // Write the final accounts to the new master accounts file and the
    //   current accounts file.
    try (
      Writer masterWriter = new FileWriter(args[2]);
      Writer currentWriter = new FileWriter(args[3])
    ) {
      writeAccounts(accounts.values(), masterWriter, currentWriter);
    } catch (IOException e) {
      System.err.println(e);
      return;
    }
  }

  /**
   * Reads lines from the master accounts file via reader, parses them into
   * Accounts according to the master accounts file format, and then returns a
   * Map from account IDs to Accounts.
   */
  private static Map<Integer, Account> readAccounts(
    BufferedReader reader
  ) throws IOException {
    Map<Integer, Account> accounts = new HashMap<>();

    String line = reader.readLine();
    while (line != null) {
      Account account = Account.fromMasterAccountsLine(line);
      accounts.put(account.getAccountNumber(), account);

      line = reader.readLine();
    }

    return accounts;
  }

  /**
   * Reads lines from the transactions file via reader, parses them into
   * Transactions, and then returns them in a Queue in the order they were read
   * from reader.
   *
   * highestAccoundId is used to generate new account IDs for
   * create transactions.
   */
  private static Queue<Transaction> readTransactions(
    BufferedReader reader, int highestAccountId
  ) throws IOException {
    Queue<Transaction> transactions = new LinkedList<>();
    TransactionContext context = new TransactionContext(highestAccountId);

    String line = reader.readLine();
    while (line != null) {
      try {
        Transaction transaction = Transaction.fromLine(line, context);
        // Sometimes the transaction will be null because not every line
        //   becomes a single transaction, e.g. login/logout only manipulate the
        //   context, 2 transfer transactions get combined into
        //   one TransferTransaction, etc. so just continue if it's null.
        if (transaction != null) {
          transactions.add(transaction);
        }
      } catch (InvalidTransactionException e) {
        System.err.println("Error while parsing transaction \"" + line + "\":");
        System.err.println(e);
      }

      line = reader.readLine();
    }

    return transactions;
  }

  /**
   * Writes a Collection of Accounts to the master accounts file via
   * masterWriter, and the current accounts file via currentWriter.
   */
  private static void writeAccounts(
    Collection<Account> accounts, Writer masterWriter, Writer currentWriter
  ) throws IOException {
    for (Account account : accounts) {
      masterWriter.write(account.toMasterAccountsLine());
      currentWriter.write(account.toCurrentAccountsLine());
    }
  }
}
