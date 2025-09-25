// src/edu/ccrm/io/FileService.java
package edu.ccrm.io;

import edu.ccrm.config.AppConfig;
import edu.ccrm.domain.Student;
import edu.ccrm.domain.Course;
import edu.ccrm.domain.Instructor;
import edu.ccrm.domain.Semester;
import edu.ccrm.domain.Grade;
import edu.ccrm.util.CourseCode;
import edu.ccrm.exception.FileImportException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileService {
    private final AppConfig cfg;
    
    public FileService() {
        this.cfg = AppConfig.getInstance();
    }
    
    public void exportStudents(List<Student> students, String filename) throws IOException {
        Path path = cfg.getDataDirectory().resolve(filename);
        List<String> lines = students.stream()
                .map(this::studentToCSV)
                .collect(Collectors.toList());
        
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    public List<Student> importStudents(String filename) throws IOException, FileImportException {
        Path path = cfg.getDataDirectory().resolve(filename);
        if (!Files.exists(path)) {
            throw new FileImportException("File not found: " + filename);
        }
        
        List<Student> result = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        
        for (int i = 0; i < lines.size(); i++) {
            try {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                
                Student s = studentFromCSV(line);
                result.add(s);
            } catch (Exception e) {
                throw new FileImportException("Bad line " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        return result;
    }
    
    public void exportCourses(List<Course> courses, String filename) throws IOException {
        Path path = cfg.getDataDirectory().resolve(filename);
        List<String> lines = courses.stream()
                .map(this::courseToCSV)
                .collect(Collectors.toList());
        
        Files.write(path, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
    
    public List<Course> importCourses(String filename, List<Instructor> teachers) throws IOException, FileImportException {
        Path path = cfg.getDataDirectory().resolve(filename);
        if (!Files.exists(path)) {
            throw new FileImportException("File not found: " + filename);
        }
        
        List<Course> result = new ArrayList<>();
        List<String> lines = Files.readAllLines(path);
        
        for (int i = 0; i < lines.size(); i++) {
            try {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                
                Course c = courseFromCSV(line, teachers);
                result.add(c);
            } catch (Exception e) {
                throw new FileImportException("Bad line " + (i + 1) + ": " + e.getMessage());
            }
        }
        
        return result;
    }
    
    private String studentToCSV(Student s) {
        return String.join(",",
                s.getId(),
                s.getRegNo(),
                s.getName(),
                s.getEmail(),
                String.valueOf(s.isActive()),
                s.getDateCreated().toString()
        );
    }
    
    private Student studentFromCSV(String line) {
        String[] parts = line.split(",");
        if (parts.length < 6) {
            throw new RuntimeException("Bad student CSV");
        }
        
        Student s = new Student(parts[0], parts[1], parts[2], parts[3]);
        s.setActive(Boolean.parseBoolean(parts[4]));
        return s;
    }
    
    private String courseToCSV(Course c) {
        return String.join(",",
                c.getCode().toString(),
                c.getTitle(),
                String.valueOf(c.getCredits()),
                c.getInstructor() != null ? c.getInstructor().getId() : "",
                c.getSemester().name(),
                c.getDepartment(),
                String.valueOf(c.isActive())
        );
    }
    
    private Course courseFromCSV(String line, List<Instructor> teachers) {
        String[] parts = line.split(",");
        if (parts.length < 7) {
            throw new RuntimeException("Bad course CSV");
        }
        
        CourseCode code = new CourseCode(parts[0]);
        String title = parts[1];
        int credits = Integer.parseInt(parts[2]);
        String teacherId = parts[3];
        Semester sem = Semester.valueOf(parts[4]);
        String dept = parts[5];
        boolean active = Boolean.parseBoolean(parts[6]);
        
        Instructor teacher = teachers.stream()
                .filter(t -> t.getId().equals(teacherId))
                .findFirst()
                .orElse(null);
        
        Course c = new Course.Builder()
                .setCode(code)
                .setTitle(title)
                .setCredits(credits)
                .setInstructor(teacher)
                .setSemester(sem)
                .setDepartment(dept)
                .build();
        
        c.setActive(active);
        return c;
    }
    
    public void backupData(String backupName) throws IOException {
        Path backupDir = cfg.getBackupDirectory().resolve(backupName);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }
        
        // Copy files from data dir to backup
        try (Stream<Path> paths = Files.walk(cfg.getDataDirectory())) {
            paths.filter(Files::isRegularFile)
                 .forEach(src -> {
                     try {
                         Path dest = backupDir.resolve(cfg.getDataDirectory().relativize(src));
                         Files.createDirectories(dest.getParent());
                         Files.copy(src, dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                     } catch (IOException e) {
                         System.err.println("Failed to copy: " + e.getMessage());
                     }
                 });
        }
    }
    
    public long getBackupSize(String backupName) throws IOException {
        Path backupDir = cfg.getBackupDirectory().resolve(backupName);
        if (!Files.exists(backupDir)) {
            return 0;
        }
        
        return calcDirSize(backupDir);
    }
    
    // Calculate folder size
    private long calcDirSize(Path path) throws IOException {
        if (!Files.exists(path)) {
            return 0;
        }
        
        if (Files.isRegularFile(path)) {
            return Files.size(path);
        }
        
        try (Stream<Path> paths = Files.list(path)) {
            return paths.mapToLong(p -> {
                try {
                    if (Files.isDirectory(p)) {
                        return calcDirSize(p);
                    } else {
                        return Files.size(p);
                    }
                } catch (IOException e) {
                    System.err.println("Size calc error: " + e.getMessage());
                    return 0;
                }
            }).sum();
        }
    }
    
    // Quick file existence check
    public boolean fileExists(String filename) {
        return Files.exists(cfg.getDataDirectory().resolve(filename));
    }
    
    // Get list of backup folders
    public List<String> getBackups() throws IOException {
        Path backupDir = cfg.getBackupDirectory();
        if (!Files.exists(backupDir)) {
            return new ArrayList<>();
        }
        
        try (Stream<Path> paths = Files.list(backupDir)) {
            return paths.filter(Files::isDirectory)
                       .map(p -> p.getFileName().toString())
                       .collect(Collectors.toList());
        }
    }
}