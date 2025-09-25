// src/edu/ccrm/exception/FileImportException.java
package edu.ccrm.exception;

public class FileImportException extends Exception {
    public FileImportException(String msg) {
        super(msg);
    }
    
    public FileImportException(String msg, Throwable cause) {
        super(msg, cause);
    }
    
    // Common constructors
    public FileImportException(String filename, String problem) {
        super("Problem with " + filename + ": " + problem);
    }
}