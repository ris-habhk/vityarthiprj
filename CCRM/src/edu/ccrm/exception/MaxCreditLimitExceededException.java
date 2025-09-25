// src/edu/ccrm/exception/MaxCreditLimitExceededException.java
package edu.ccrm.exception;

public class MaxCreditLimitExceededException extends Exception {
    public MaxCreditLimitExceededException(String msg) {
        super(msg);
    }
    
    public MaxCreditLimitExceededException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    // Constructor with details
    public MaxCreditLimitExceededException(int current, int adding, int max) {
        super("Credit limit: " + current + " + " + adding + " > " + max);
    }
}