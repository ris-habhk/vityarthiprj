// src/edu/ccrm/util/CourseCode.java
package edu.ccrm.util;

public final class CourseCode {
    private final String code;
    
    public CourseCode(String code) {
        if (code == null || !code.matches("[A-Z]{3}-\\d{3}")) {
            throw new IllegalArgumentException("Invalid course code format. Expected format: ABC-123");
        }
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CourseCode that = (CourseCode) obj;
        return code.equals(that.code);
    }
    
    @Override
    public int hashCode() {
        return code.hashCode();
    }
}