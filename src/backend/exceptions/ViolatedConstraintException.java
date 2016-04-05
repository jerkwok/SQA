package backend.exceptions;

/**
 * Thrown during application of a transaction, when the transaction cannot be
 * applied due to a constraint.
 */
public class ViolatedConstraintException extends RuntimeException {
  public ViolatedConstraintException(String message) {
    super(message);
  }
}
