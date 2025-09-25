// src/edu/ccrm/service/StudentService.java
package edu.ccrm.service;

import edu.ccrm.domain.Student;
import edu.ccrm.domain.Enrollment;
import edu.ccrm.domain.Course;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StudentService implements Searchable<Student> {
    private List<Student> students;
    private static final int MAX_CREDITS = 18;
    
    public StudentService() {
        this.students = new ArrayList<>();
    }
    
    public void addStudent(Student s) {
        students.add(s);
    }
    
    public Student getStudentById(String id) {
        return students.stream()
                .filter(s -> s.getId().equals(id) && s.isActive())
                .findFirst()
                .orElse(null);
    }
    
    public Student getStudentByRegNo(String reg) {
        return students.stream()
                .filter(s -> s.getRegNo().equals(reg) && s.isActive())
                .findFirst()
                .orElse(null);
    }
    
    public List<Student> getAllStudents() {
        return students.stream()
                .filter(Student::isActive)
                .collect(Collectors.toList());
    }
    
    public void updateStudent(Student s) {
        // Replace student in list
        students.removeIf(old -> old.getId().equals(s.getId()));
        students.add(s);
    }
    
    public void deactivateStudent(String id) {
        Student s = getStudentById(id);
        if (s != null) {
            s.setActive(false);
        }
    }
    
    public void enrollStudentInCourse(Student s, Course c) 
            throws DuplicateEnrollmentException, MaxCreditLimitExceededException {
        
        // Check if already enrolled
        boolean alreadyEnrolled = s.getEnrollments().stream()
                .anyMatch(e -> e.getCourse().getCode().equals(c.getCode()) 
                        && e.getCourse().getSemester() == c.getSemester());
        
        if (alreadyEnrolled) {
            throw new DuplicateEnrollmentException("Already enrolled in this course");
        }
        
        // Check credit limit
        int currentCredits = s.getEnrollments().stream()
                .filter(e -> e.getCourse().getSemester() == c.getSemester())
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
        
        if (currentCredits + c.getCredits() > MAX_CREDITS) {
            throw new MaxCreditLimitExceededException(
                    "Too many credits. Current: " + currentCredits + 
                    ", Adding: " + c.getCredits() + 
                    ", Max: " + MAX_CREDITS);
        }
        
        // Add enrollment
        Enrollment e = new Enrollment(s, c);
        s.addEnrollment(e);
    }
    
    public void unenrollStudentFromCourse(Student s, Course c) {
        s.getEnrollments().removeIf(e -> 
                e.getCourse().getCode().equals(c.getCode()) && 
                e.getCourse().getSemester() == c.getSemester());
    }
    
    @Override
    public List<Student> search(String q) {
        String term = q.toLowerCase();
        return students.stream()
                .filter(s -> s.isActive() && 
                        (s.getName().toLowerCase().contains(term) ||
                         s.getRegNo().toLowerCase().contains(term) ||
                         s.getEmail().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }
    
    // Helper methods
    public int getStudentCount() {
        return (int) students.stream().filter(Student::isActive).count();
    }
    
    public boolean studentExists(String regNo) {
        return getStudentByRegNo(regNo) != null;
    }
    
    // Get students with GPA above threshold
    public List<Student> getStudentsWithGPA(double minGPA) {
        return students.stream()
                .filter(s -> s.isActive() && s.calculateGPA() >= minGPA)
                .collect(Collectors.toList());
    }
}