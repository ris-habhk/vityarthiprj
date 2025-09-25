// src/edu/ccrm/domain/Instructor.java
package edu.ccrm.domain;

public class Instructor extends Person {
    private String dept;
    
    public Instructor(String id, String name, String email, String dept) {
        super(id, name, email);
        this.dept = dept;
    }
    
    @Override
    public String getDisplayInfo() {
        return "Prof. " + getName() + " - " + dept;
    }
    
    public String getName() {
        return getFullName(); // Shorter alias
    }
    
    public String getDepartment() { return dept; }
    public void setDepartment(String d) { this.dept = d; }
    
    @Override
    public String toString() {
        return getName() + " (" + dept + ")";
    }
}