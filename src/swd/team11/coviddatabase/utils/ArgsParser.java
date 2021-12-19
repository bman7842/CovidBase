package swd.team11.coviddatabase.utils;

import java.util.HashMap;
import java.util.Set;

/**
 * Tool for managing arguments sent over socket between server and client.
 * A socket message follows format: "identifier=arg1:value1_arg2:value2_..."
 */
public class ArgsParser {

    /**
     * Identifier string, first part of message. Signifies the type of data sent
     */
    private final String identifier;
    /**
     * Args stored in message, anything proceeding '='
     * Represented as key=arg, value=value.
     */
    private final HashMap<String, String> args;

    /**
     * Creates an ArgParser object from a raw string message
     * @param indentifierArgString  properly formatted arguments message
     */
    public ArgsParser(String indentifierArgString) {
        this(indentifierArgString.split("=")[0], indentifierArgString.split("=")[1]);
    }

    /**
     * Creates an ArgParser object from already split identifier and arg string.
     * @param identifier    Identifier string
     * @param argString     Argument string
     */
    public ArgsParser(String identifier, String argString) {
        args = new HashMap<>();
        this.identifier = identifier;

        for (String arg : argString.split(";")) { //_ divides args
            if (arg.split(":").length == 2) {
                add(arg.split(":")[0], arg.split(":")[1]);
            } else {
                add(arg.split(":")[0], "");
            }
        }
    }

    /**
     * Checks whether an argument exists
     * @param arg   Argument to check for
     * @return      true if argument is found, false if not
     */
    public boolean has(String arg) {
        return args.containsKey(arg);
    }

    /**
     * Adds an argument or update an existing argument
     * @param arg   argument to add/update
     * @param value value of argument
     */
    public void add(String arg, String value) {
        if (has(arg)) {
            System.out.println(arg + " is already in argsParser, updating value.");
        }
        args.put(arg, value);
    }

    /**
     * Get the value of an argument
     * @param arg   Argument in reference
     * @return      Value of argument
     */
    public String get(String arg) {
        return args.getOrDefault(arg, null);
    }

    /**
     * Get the identifier
     * @return  identifier string
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Get all args present
     * @return  set of collected arguments
     */
    public Set<String> getArgs() {
        return args.keySet();
    }

}
