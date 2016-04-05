package backend.exceptions;

/**
 * Thrown during transaction parsing when a line contains invalid data.
 */
public class InvalidTransactionException extends RuntimeException {
  public InvalidTransactionException(String message) {
    super(message);
  }
}
