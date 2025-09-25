// src/edu/ccrm/domain/Student.java
package edu.ccrm.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Student extends Person {
    private String regNo;
    private List<Enrollment> enrolls;
    
    public Student(String id, String reg, String name, String email) {
        super(id, name, email);
        this.regNo = reg;
        this.enrolls = new ArrayList<>();
    }
    
    @Override
    public String getDisplayInfo() {
        return regNo + " - " + getName() + " - " + getEmail();
    }
    
    public String getRegNo() { return regNo; }
    public void setRegNo(String r) { this.regNo = r; }
    
    public List<Enrollment> getEnrollments() { return enrolls; }
    
    public void addEnrollment(Enrollment e) {
        enrolls.add(e);
    }
    
    public void removeEnrollment(Enrollment e) {
        enrolls.remove(e);
    }
    
    public boolean isEnrolledIn(Course c) {
        for (Enrollment e : enrolls) {
            if (e.getCourse().equals(c)) {
                return true;
            }
        }
        return false;
    }
    
    public double calculateGPA() {
        if (enrolls.isEmpty()) return 0.0;
        
        double points = 0.0;
        int credits = 0;
        
        for (Enrollment e : enrolls) {
            if (e.hasGrade()) {
                Course c = e.getCourse();
                points += e.getGrade().getPoints() * c.getCredits();
                credits += c.getCredits();
            }
        }
        
        return credits > 0 ? points / credits : 0.0;
    }
    
    public int getTotalCredits() {
        return enrolls.stream()
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }
    
    @Override
    public String toString() {
        return getName() + " (" + regNo + ") - " + enrolls.size() + " courses";
    }
}