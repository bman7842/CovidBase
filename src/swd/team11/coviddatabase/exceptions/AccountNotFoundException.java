package swd.team11.coviddatabase.exceptions;

/**
 * Exception created when account is not found
 */
public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(String msg) {
        super("ACCOUNT NOT FOUND: " +msg);
    }

}
