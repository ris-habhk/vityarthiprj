// src/edu/ccrm/Main.java
package edu.ccrm;

import edu.ccrm.cli.CLIMenu;
import edu.ccrm.config.AppConfig;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Campus Course & Records Manager (CCRM) ===");
        System.out.println("Loading configuration...");
        
        // Initialize singleton configuration
        AppConfig config = AppConfig.getInstance();
        
        // Start the CLI menu
        CLIMenu menu = new CLIMenu();
        menu.start();
        
        System.out.println("Thank you for using CCRM!");
    }
}