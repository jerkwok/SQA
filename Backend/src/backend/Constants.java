package backend;

/**
 * Various monetary constants used in the program.
 * <p>
 * All constants are in whole cents.
 */
public class Constants {
  /// The maximum balance an account can have.
  public static final int MAX_BALANCE = 9999999;

  /// The fee charged to a normal account per transaction.
  public static final int NORMAL_FEE = 10;
  /// The fee charged to a student account per transaction.
  public static final int STUDENT_FEE = 5;

  /// The limit on the amount of money that can be withdrawn per day.
  public static final int WITHDRAWAL_LIMIT = 50000;
  /// The limit on the amount of money that can be transferred per day.
  public static final int TRANSFER_LIMIT = 100000;
  /// The limit on the amount of money that can be paid to a single company per day.
  public static final int PAYBILL_LIMIT = 200000;
}
