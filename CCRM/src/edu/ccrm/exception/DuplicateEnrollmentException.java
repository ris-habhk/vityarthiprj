// src/edu/ccrm/exception/DuplicateEnrollmentException.java
package edu.ccrm.exception;

public class DuplicateEnrollmentException extends Exception {
    public DuplicateEnrollmentException(String msg) {
        super(msg);
    }
    
    public DuplicateEnrollmentException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    // Quick constructor for common case
    public DuplicateEnrollmentException() {
        super("Student already enrolled in this course");
    }
}