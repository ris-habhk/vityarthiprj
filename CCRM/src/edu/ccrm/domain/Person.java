// src/edu/ccrm/domain/Person.java
package edu.ccrm.domain;

import java.time.LocalDate;

public abstract class Person {
    protected String id;
    protected String name;
    protected String email;
    protected LocalDate created;
    protected boolean active;
    
    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.created = LocalDate.now();
        this.active = true;
    }
    
    public abstract String getDisplayInfo();
    
    // Getters with shorter names
    public String getId() { return id; }
    public void setId(String i) { this.id = i; }
    
    public String getFullName() { return name; }
    public String getName() { return name; } // Short version
    public void setFullName(String n) { this.name = n; }
    
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    
    public LocalDate getDateCreated() { return created; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean a) { this.active = a; }
    
    public void deactivate() {
        this.active = false;
    }
    
    @Override
    public String toString() {
        return name + " (" + id + ")";
    }
}