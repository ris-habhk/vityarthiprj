// src/edu/ccrm/domain/Semester.java
package edu.ccrm.domain;

public enum Semester {
    SPRING("Spring"), 
    SUMMER("Summer"), 
    FALL("Fall");
    
    private final String name;
    
    Semester(String n) {
        this.name = n;
    }
    
    public String getDisplayName() {
        return name;
    }
    
    // Short version
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}