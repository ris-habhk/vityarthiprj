// src/edu/ccrm/domain/Enrollment.java
package edu.ccrm.domain;

import java.time.LocalDate;

public class Enrollment {
    private Student stu;
    private Course crs;
    private LocalDate enrollDate;
    private Grade grade;
    
    public Enrollment(Student s, Course c) {
        this.stu = s;
        this.crs = c;
        this.enrollDate = LocalDate.now();
        this.grade = null;
    }
    
    public Student getStudent() { return stu; }
    public Course getCourse() { return crs; }
    public LocalDate getEnrollmentDate() { return enrollDate; }
    public Grade getGrade() { return grade; }
    
    public void setGrade(Grade g) { this.grade = g; }
    
    public boolean hasGrade() {
        return grade != null;
    }
    
    @Override
    public String toString() {
        return stu.getRegNo() + " in " + crs.getCode() + " on " + enrollDate + 
               (grade != null ? " - Grade: " + grade : "");
    }
}