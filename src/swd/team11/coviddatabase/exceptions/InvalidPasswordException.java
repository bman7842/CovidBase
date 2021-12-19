package swd.team11.coviddatabase.exceptions;

/**
 * Exception for representing when a users enters an incorrect password.
 */
public class InvalidPasswordException extends Exception {

    public InvalidPasswordException(String msg) {
        super("INVALID PASSWORD: " +msg);
    }

}
