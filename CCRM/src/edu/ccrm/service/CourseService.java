// src/edu/ccrm/service/CourseService.java
package edu.ccrm.service;

import edu.ccrm.domain.Course;
import edu.ccrm.domain.Instructor;
import edu.ccrm.domain.Semester;
import edu.ccrm.util.CourseCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseService implements Searchable<Course> {
    private List<Course> courses;
    
    public CourseService() {
        this.courses = new ArrayList<>();
    }
    
    public void addCourse(Course c) {
        courses.add(c);
    }
    
    public Course getCourseByCode(CourseCode code) {
        return courses.stream()
                .filter(c -> c.getCode().equals(code) && c.isActive())
                .findFirst()
                .orElse(null);
    }
    
    public List<Course> getAllCourses() {
        return courses.stream()
                .filter(Course::isActive)
                .collect(Collectors.toList());
    }
    
    public void updateCourse(Course c) {
        // Remove old, add new
        courses.removeIf(old -> old.getCode().equals(c.getCode()));
        courses.add(c);
    }
    
    public void deactivateCourse(CourseCode code) {
        Course c = getCourseByCode(code);
        if (c != null) {
            c.setActive(false);
        }
    }
    
    public List<Course> getCoursesByInstructor(Instructor inst) {
        return courses.stream()
                .filter(c -> c.isActive() && c.getInstructor().equals(inst))
                .collect(Collectors.toList());
    }
    
    public List<Course> getCoursesBySemester(Semester sem) {
        return courses.stream()
                .filter(c -> c.isActive() && c.getSemester() == sem)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Course> search(String query) {
        String term = query.toLowerCase();
        return courses.stream()
                .filter(c -> c.isActive() && 
                       (c.getTitle().toLowerCase().contains(term) ||
                        c.getCode().toString().toLowerCase().contains(term) ||
                        c.getDepartment().toLowerCase().contains(term)))
                .collect(Collectors.toList());
    }
    
    // Helper to check if course exists
    public boolean courseExists(CourseCode code) {
        return getCourseByCode(code) != null;
    }
    
    // Get active course count
    public int getActiveCount() {
        return (int) courses.stream().filter(Course::isActive).count();
    }
}