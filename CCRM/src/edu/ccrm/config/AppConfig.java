
package edu.ccrm.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public class AppConfig {
    private static AppConfig instance;
    private Path dataDirectory;
    private Path backupDirectory;
    private DateTimeFormatter dateFormatter;
    
    private AppConfig() {
        
        dataDirectory = Paths.get("data");
        backupDirectory = Paths.get("backups");
        dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
       
        try {
            if (!java.nio.file.Files.exists(dataDirectory)) {
                java.nio.file.Files.createDirectories(dataDirectory);
            }
            if (!java.nio.file.Files.exists(backupDirectory)) {
                java.nio.file.Files.createDirectories(backupDirectory);
            }
        } catch (Exception e) {
            System.err.println("Error creating directories: " + e.getMessage());
        }
    }
    
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }
    
    // Getters
    public Path getDataDirectory() { return dataDirectory; }
    public Path getBackupDirectory() { return backupDirectory; }
    public DateTimeFormatter getDateFormatter() { return dateFormatter; }
}