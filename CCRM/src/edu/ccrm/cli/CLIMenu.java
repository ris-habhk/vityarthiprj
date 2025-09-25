
package edu.ccrm.cli;

import edu.ccrm.domain.*;
import edu.ccrm.service.StudentService;
import edu.ccrm.service.CourseService;
import edu.ccrm.io.FileService;
import edu.ccrm.exception.DuplicateEnrollmentException;
import edu.ccrm.exception.MaxCreditLimitExceededException;
import edu.ccrm.exception.FileImportException;
import edu.ccrm.util.CourseCode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CLIMenu {
    private final Scanner sc;
    private final StudentService stuService;
    private final CourseService crsService;
    private final FileService fileSvc;
    private List<Instructor> teachers;
    
    public CLIMenu() {
        this.sc = new Scanner(System.in);
        this.stuService = new StudentService();
        this.crsService = new CourseService();
        this.fileSvc = new FileService();
        setupSampleData();
    }
    
    private void setupSampleData() {
        // Create some sample instructors
        teachers = List.of(
            new Instructor("I001", "Dr. Smith", "smith@university.edu", "Computer Science"),
            new Instructor("I002", "Prof. Johnson", "johnson@university.edu", "Mathematics"),
            new Instructor("I003", "Dr. Williams", "williams@university.edu", "Physics")
        );
        
        // Add some sample courses
        try {
            Course cs1 = new Course.Builder()
                    .setCode(new CourseCode("CS-101"))
                    .setTitle("Intro to Programming")
                    .setCredits(3)
                    .setInstructor(teachers.get(0))
                    .setSemester(Semester.FALL)
                    .setDepartment("Computer Science")
                    .build();
            
            Course math1 = new Course.Builder()
                    .setCode(new CourseCode("MTH-201"))
                    .setTitle("Calculus I")
                    .setCredits(4)
                    .setInstructor(teachers.get(1))
                    .setSemester(Semester.FALL)
                    .setDepartment("Mathematics")
                    .build();
            
            Course phys1 = new Course.Builder()
                    .setCode(new CourseCode("PHY-301"))
                    .setTitle("Classical Mechanics")
                    .setCredits(3)
                    .setInstructor(teachers.get(2))
                    .setSemester(Semester.SPRING)
                    .setDepartment("Physics")
                    .build();
            
            crsService.addCourse(cs1);
            crsService.addCourse(math1);
            crsService.addCourse(phys1);
            
        } catch (Exception e) {
            System.err.println("Problem creating sample courses: " + e.getMessage());
        }
    }
    
    public void start() {
        int opt;
        while (true) {
            showMainMenu();
            opt = getIntInput("Pick an option: ");
            
            switch (opt) {
                case 1:
                    handleStudents();
                    break;
                case 2:
                    handleCourses();
                    break;
                case 3:
                    handleEnrollments();
                    break;
                case 4:
                    handleGrades();
                    break;
                case 5:
                    handleImportExport();
                    break;
                case 6:
                    handleBackups();
                    break;
                case 7:
                    showReports();
                    break;
                case 8:
                    showJavaInfo();
                    break;
                case 9:
                    System.out.println("Bye!");
                    return;
                default:
                    System.out.println("Not a valid option. Try again.");
            }
            
            if (opt != 9) {
                System.out.println("\nPress Enter to continue...");
                sc.nextLine();
            }
        }
    }
    
    private void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Students");
        System.out.println("2. Courses");
        System.out.println("3. Enrollments");
        System.out.println("4. Grades");
        System.out.println("5. Import/Export");
        System.out.println("6. Backup");
        System.out.println("7. Reports");
        System.out.println("8. Java Info");
        System.out.println("9. Exit");
    }
    
    private void handleStudents() {
        int opt;
        do {
            System.out.println("\n=== STUDENTS ===");
            System.out.println("1. Add Student");
            System.out.println("2. List Students");
            System.out.println("3. Find Students");
            System.out.println("4. Edit Student");
            System.out.println("5. Remove Student");
            System.out.println("6. View Transcript");
            System.out.println("7. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    addStu();
                    break;
                case 2:
                    listStu();
                    break;
                case 3:
                    findStu();
                    break;
                case 4:
                    editStu();
                    break;
                case 5:
                    removeStu();
                    break;
                case 6:
                    showTranscript();
                    break;
                case 7:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Nope, try again.");
            }
        } while (opt != 7);
    }
    
    private void addStu() {
        System.out.println("\n--- Add Student ---");
        String id = getStringInput("ID: ");
        String reg = getStringInput("Reg Number: ");
        String name = getStringInput("Name: ");
        String email = getStringInput("Email: ");
        
        Student s = new Student(id, reg, name, email);
        stuService.addStudent(s);
        System.out.println("Student added!");
    }
    
    private void listStu() {
        System.out.println("\n--- All Students ---");
        List<Student> students = stuService.getAllStudents();
        if (students.isEmpty()) {
            System.out.println("No students yet.");
        } else {
            for (Student s : students) {
                System.out.println(s.getDisplayInfo());
            }
        }
    }
    
    private void findStu() {
        System.out.println("\n--- Find Students ---");
        String term = getStringInput("Search for: ");
        List<Student> found = stuService.search(term);
        
        if (found.isEmpty()) {
            System.out.println("Nothing found.");
        } else {
            System.out.println("Found " + found.size() + " students:");
            for (Student s : found) {
                System.out.println(s.getDisplayInfo());
            }
        }
    }
    
    private void editStu() {
        System.out.println("\n--- Edit Student ---");
        String reg = getStringInput("Enter reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        
        System.out.println("Current: " + s.getDisplayInfo());
        String newName = getStringInput("New name (enter to skip): ");
        String newEmail = getStringInput("New email (enter to skip): ");
        
        if (!newName.isEmpty()) {
            s.setFullName(newName);
        }
        if (!newEmail.isEmpty()) {
            s.setEmail(newEmail);
        }
        
        stuService.updateStudent(s);
        System.out.println("Updated!");
    }
    
    private void removeStu() {
        System.out.println("\n--- Remove Student ---");
        String reg = getStringInput("Reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Not found.");
            return;
        }
        
        stuService.deactivateStudent(s.getId());
        System.out.println("Student removed.");
    }
    
    private void showTranscript() {
        System.out.println("\n--- Transcript ---");
        String reg = getStringInput("Reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("No student found.");
            return;
        }
        
        System.out.println("\n=== TRANSCRIPT ===");
        System.out.println("Name: " + s.getFullName());
        System.out.println("Reg: " + s.getRegNo());
        System.out.println("GPA: " + String.format("%.2f", s.calculateGPA()));
        System.out.println("\nCourses:");
        
        if (s.getEnrollments().isEmpty()) {
            System.out.println("No courses yet.");
        } else {
            for (Enrollment e : s.getEnrollments()) {
                Course c = e.getCourse();
                String grade = e.getGrade() != null ? 
                    e.getGrade().getLetter() + " (" + e.getGrade().getPoints() + ")" : "No grade";
                System.out.println(c.getCode() + " - " + c.getTitle() + " - " + grade);
            }
        }
    }
    
    private void handleCourses() {
        int opt;
        do {
            System.out.println("\n=== COURSES ===");
            System.out.println("1. Add Course");
            System.out.println("2. List Courses");
            System.out.println("3. Find Courses");
            System.out.println("4. Edit Course");
            System.out.println("5. Remove Course");
            System.out.println("6. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    addCrs();
                    break;
                case 2:
                    listCrs();
                    break;
                case 3:
                    findCrs();
                    break;
                case 4:
                    editCrs();
                    break;
                case 5:
                    removeCrs();
                    break;
                case 6:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 6);
    }
    
    private void addCrs() {
        System.out.println("\n--- Add Course ---");
        
        try {
            String codeStr = getStringInput("Code (like CS-101): ");
            CourseCode code = new CourseCode(codeStr);
            String title = getStringInput("Title: ");
            int credits = getIntInput("Credits: ");
            
            System.out.println("Teachers:");
            for (int i = 0; i < teachers.size(); i++) {
                System.out.println((i + 1) + ". " + teachers.get(i).getName());
            }
            
            int teacherOpt = getIntInput("Pick teacher (1-" + teachers.size() + "): ");
            if (teacherOpt < 1 || teacherOpt > teachers.size()) {
                System.out.println("Bad choice.");
                return;
            }
            Instructor teacher = teachers.get(teacherOpt - 1);
            
            System.out.println("Semesters:");
            Semester[] sems = Semester.values();
            for (int i = 0; i < sems.length; i++) {
                System.out.println((i + 1) + ". " + sems[i].getDisplayName());
            }
            
            int semOpt = getIntInput("Pick semester (1-" + sems.length + "): ");
            if (semOpt < 1 || semOpt > sems.length) {
                System.out.println("Bad choice.");
                return;
            }
            Semester sem = sems[semOpt - 1];
            
            String dept = getStringInput("Department: ");
            
            Course c = new Course.Builder()
                    .setCode(code)
                    .setTitle(title)
                    .setCredits(credits)
                    .setInstructor(teacher)
                    .setSemester(sem)
                    .setDepartment(dept)
                    .build();
            
            crsService.addCourse(c);
            System.out.println("Course added!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void listCrs() {
        System.out.println("\n--- All Courses ---");
        List<Course> courses = crsService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses.");
        } else {
            for (Course c : courses) {
                System.out.println(c.toString());
            }
        }
    }
    
    private void findCrs() {
        System.out.println("\n--- Find Courses ---");
        String term = getStringInput("Search: ");
        List<Course> found = crsService.search(term);
        
        if (found.isEmpty()) {
            System.out.println("No matches.");
        } else {
            System.out.println("Found " + found.size() + " courses:");
            for (Course c : found) {
                System.out.println(c.toString());
            }
        }
    }
    
    private void editCrs() {
        System.out.println("\n--- Edit Course ---");
        String codeStr = getStringInput("Course code: ");
        
        try {
            CourseCode code = new CourseCode(codeStr);
            Course c = crsService.getCourseByCode(code);
            
            if (c == null) {
                System.out.println("Not found.");
                return;
            }
            
            System.out.println("Current: " + c.toString());
            
            System.out.println("Teachers:");
            for (int i = 0; i < teachers.size(); i++) {
                System.out.println((i + 1) + ". " + teachers.get(i).getName());
            }
            
            int teacherOpt = getIntInput("New teacher (1-" + teachers.size() + ", 0 to keep): ");
            if (teacherOpt > 0 && teacherOpt <= teachers.size()) {
                Instructor newTeacher = teachers.get(teacherOpt - 1);
                c.setInstructor(newTeacher);
            }
            
            crsService.updateCourse(c);
            System.out.println("Updated!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void removeCrs() {
        System.out.println("\n--- Remove Course ---");
        String codeStr = getStringInput("Course code: ");
        
        try {
            CourseCode code = new CourseCode(codeStr);
            crsService.deactivateCourse(code);
            System.out.println("Course removed.");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void handleEnrollments() {
        int opt;
        do {
            System.out.println("\n=== ENROLLMENTS ===");
            System.out.println("1. Enroll Student");
            System.out.println("2. Unenroll Student");
            System.out.println("3. View Enrollments");
            System.out.println("4. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    enrollStu();
                    break;
                case 2:
                    unenrollStu();
                    break;
                case 3:
                    viewEnrollments();
                    break;
                case 4:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 4);
    }
    
    private void enrollStu() {
        System.out.println("\n--- Enroll Student ---");
        String reg = getStringInput("Student reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        
        String codeStr = getStringInput("Course code: ");
        
        try {
            CourseCode code = new CourseCode(codeStr);
            Course c = crsService.getCourseByCode(code);
            
            if (c == null) {
                System.out.println("Course not found.");
                return;
            }
            
            stuService.enrollStudentInCourse(s, c);
            System.out.println("Enrolled!");
            
        } catch (Exception e) {
            System.out.println("Problem: " + e.getMessage());
        }
    }
    
    private void unenrollStu() {
        System.out.println("\n--- Unenroll Student ---");
        String reg = getStringInput("Student reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Not found.");
            return;
        }
        
        String codeStr = getStringInput("Course code: ");
        
        try {
            CourseCode code = new CourseCode(codeStr);
            Course c = crsService.getCourseByCode(code);
            
            if (c == null) {
                System.out.println("Course not found.");
                return;
            }
            
            stuService.unenrollStudentFromCourse(s, c);
            System.out.println("Unenrolled!");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void viewEnrollments() {
        System.out.println("\n--- Student Enrollments ---");
        String reg = getStringInput("Reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Not found.");
            return;
        }
        
        System.out.println("Courses for " + s.getFullName() + ":");
        if (s.getEnrollments().isEmpty()) {
            System.out.println("None.");
        } else {
            for (Enrollment e : s.getEnrollments()) {
                Course c = e.getCourse();
                System.out.println(c.getCode() + " - " + c.getTitle() + " - " + e.getEnrollmentDate());
            }
        }
    }
    
    private void handleGrades() {
        int opt;
        do {
            System.out.println("\n=== GRADES ===");
            System.out.println("1. Add Grade");
            System.out.println("2. View Grades");
            System.out.println("3. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    addGrade();
                    break;
                case 2:
                    viewGrades();
                    break;
                case 3:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 3);
    }
    
    private void addGrade() {
        System.out.println("\n--- Add Grade ---");
        String reg = getStringInput("Student reg number: ");
        Student s = stuService.getStudentByRegNo(reg);
        
        if (s == null) {
            System.out.println("Not found.");
            return;
        }
        
        String codeStr = getStringInput("Course code: ");
        
        try {
            CourseCode code = new CourseCode(codeStr);
            
           
            Enrollment e = null;
            for (Enrollment en : s.getEnrollments()) {
                if (en.getCourse().getCode().equals(code)) {
                    e = en;
                    break;
                }
            }
            
            if (e == null) {
                System.out.println("Not enrolled.");
                return;
            }
            
            System.out.println("Grades:");
            Grade[] grades = Grade.values();
            for (int i = 0; i < grades.length; i++) {
                System.out.println((i + 1) + ". " + grades[i].getLetter() + " (" + grades[i].getPoints() + ")");
            }
            
            int gradeOpt = getIntInput("Pick grade (1-" + grades.length + "): ");
            if (gradeOpt < 1 || gradeOpt > grades.length) {
                System.out.println("Bad choice.");
                return;
            }
            
            Grade g = grades[gradeOpt - 1];
            e.setGrade(g);
            
            System.out.println("Grade added!");
            
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
    private void viewGrades() {
        showTranscript(); 
    }
    
    private void handleImportExport() {
        int opt;
        do {
            System.out.println("\n=== IMPORT/EXPORT ===");
            System.out.println("1. Export Students");
            System.out.println("2. Import Students");
            System.out.println("3. Export Courses");
            System.out.println("4. Import Courses");
            System.out.println("5. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    exportStu();
                    break;
                case 2:
                    importStu();
                    break;
                case 3:
                    exportCrs();
                    break;
                case 4:
                    importCrs();
                    break;
                case 5:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 5);
    }
    
    private void exportStu() {
        try {
            String file = getStringInput("Filename (students.csv): ");
            fileSvc.exportStudents(stuService.getAllStudents(), file);
            System.out.println("Exported to " + file);
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
    
    private void importStu() {
        try {
            String file = getStringInput("Filename: ");
            List<Student> imported = fileSvc.importStudents(file);
            
            for (Student s : imported) {
                stuService.addStudent(s);
            }
            
            System.out.println("Imported " + imported.size() + " students.");
        } catch (Exception e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }
    
    private void exportCrs() {
        try {
            String file = getStringInput("Filename (courses.csv): ");
            fileSvc.exportCourses(crsService.getAllCourses(), file);
            System.out.println("Exported to " + file);
        } catch (IOException e) {
            System.out.println("Export failed: " + e.getMessage());
        }
    }
    
    private void importCrs() {
        try {
            String file = getStringInput("Filename: ");
            List<Course> imported = fileSvc.importCourses(file, teachers);
            
            for (Course c : imported) {
                crsService.addCourse(c);
            }
            
            System.out.println("Imported " + imported.size() + " courses.");
        } catch (Exception e) {
            System.out.println("Import failed: " + e.getMessage());
        }
    }
    
    private void handleBackups() {
        int opt;
        do {
            System.out.println("\n=== BACKUP ===");
            System.out.println("1. Create Backup");
            System.out.println("2. Check Size");
            System.out.println("3. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    makeBackup();
                    break;
                case 2:
                    checkBackupSize();
                    break;
                case 3:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 3);
    }
    
    private void makeBackup() {
        try {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = "backup_" + time;
            
            fileSvc.backupData(backupName);
            System.out.println("Backup created: " + backupName);
            
            long size = fileSvc.getBackupSize(backupName);
            System.out.println("Size: " + size + " bytes");
            
        } catch (IOException e) {
            System.out.println("Backup failed: " + e.getMessage());
        }
    }
    
    private void checkBackupSize() {
        String name = getStringInput("Backup folder name: ");
        
        try {
            long size = fileSvc.getBackupSize(name);
            System.out.println("Size: " + size + " bytes");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private void showReports() {
        int opt;
        do {
            System.out.println("\n=== REPORTS ===");
            System.out.println("1. GPA Report");
            System.out.println("2. Top Students");
            System.out.println("3. Back");
            
            opt = getIntInput("Choose: ");
            
            switch (opt) {
                case 1:
                    gpaReport();
                    break;
                case 2:
                    topStudents();
                    break;
                case 3:
                    System.out.println("Going back...");
                    break;
                default:
                    System.out.println("Invalid.");
            }
        } while (opt != 3);
    }
    
    private void gpaReport() {
        System.out.println("\n--- GPA Report ---");
        
        List<Student> students = stuService.getAllStudents();
        
      
        long excellent = students.stream().filter(s -> s.calculateGPA() >= 9.0).count();
        long good = students.stream().filter(s -> s.calculateGPA() >= 7.5 && s.calculateGPA() < 9.0).count();
        long average = students.stream().filter(s -> s.calculateGPA() >= 6.0 && s.calculateGPA() < 7.5).count();
        long poor = students.stream().filter(s -> s.calculateGPA() > 0 && s.calculateGPA() < 6.0).count();
        long none = students.stream().filter(s -> s.calculateGPA() == 0).count();
        
        System.out.println("GPA Breakdown:");
        System.out.println("9.0+: " + excellent);
        System.out.println("7.5-8.9: " + good);
        System.out.println("6.0-7.4: " + average);
        System.out.println("<6.0: " + poor);
        System.out.println("No GPA: " + none);
    }
    
    private void topStudents() {
        System.out.println("\n--- Top Students ---");
        
        System.out.println("Coming soon...");
    }
    
    private void showJavaInfo() {
        System.out.println("\n--- Java Info ---");
        System.out.println("Version: " + System.getProperty("java.version"));
        System.out.println("Vendor: " + System.getProperty("java.vendor"));
        System.out.println("OS: " + System.getProperty("os.name"));
    }
    
  
    private int getIntInput(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }
}