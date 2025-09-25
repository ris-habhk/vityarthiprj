// src/edu/ccrm/domain/Course.java
package edu.ccrm.domain;

import edu.ccrm.util.CourseCode;

public class Course {
    private CourseCode code;
    private String title;
    private int credits;
    private Instructor teacher;
    private Semester sem;
    private String dept;
    private boolean active;
    
    // Builder pattern but simpler
    private Course(Builder b) {
        this.code = b.code;
        this.title = b.title;
        this.credits = b.credits;
        this.teacher = b.teacher;
        this.sem = b.sem;
        this.dept = b.dept;
        this.active = true;
    }
    
    public static class Builder {
        private CourseCode code;
        private String title;
        private int credits;
        private Instructor teacher;
        private Semester sem;
        private String dept;
        
        public Builder setCode(CourseCode c) {
            this.code = c;
            return this;
        }
        
        public Builder setTitle(String t) {
            this.title = t;
            return this;
        }
        
        public Builder setCredits(int cr) {
            this.credits = cr;
            return this;
        }
        
        public Builder setInstructor(Instructor i) {
            this.teacher = i;
            return this;
        }
        
        public Builder setSemester(Semester s) {
            this.sem = s;
            return this;
        }
        
        public Builder setDepartment(String d) {
            this.dept = d;
            return this;
        }
        
        public Course build() {
            // Quick validation
            if (code == null || title == null) {
                throw new RuntimeException("Need code and title");
            }
            return new Course(this);
        }
    }
    
    // Getters
    public CourseCode getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public Instructor getInstructor() { return teacher; }
    public Semester getSemester() { return sem; }
    public String getDepartment() { return dept; }
    public boolean isActive() { return active; }
    
    public void setActive(boolean a) { this.active = a; }
    public void setInstructor(Instructor i) { this.teacher = i; }
    
    @Override
    public String toString() {
        return code + " - " + title + " (" + credits + " cr) - " + 
               (teacher != null ? teacher.getName() : "No teacher") + " - " + sem;
    }
    
    // Quick info for display
    public String getShortInfo() {
        return code + ": " + title;
    }
}