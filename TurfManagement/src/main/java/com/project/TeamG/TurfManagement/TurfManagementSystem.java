package com.project.TeamG.TurfManagement;

import java.util.Scanner;

public class TurfManagementSystem {
    private Scanner sc = new Scanner(System.in);
    private Admin admin = new Admin();
    private User user = new User();

    public void start() {
        boolean isRunning = true;

        while (isRunning) {
            System.out.println("\n=== Turf Management System ===");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    adminLogin();
                    break;
                case 2:
                    userLogin();
                    break;
                case 3:
                    System.out.println("Thank you for using Turf Management System!");
                    isRunning = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void adminLogin() {
        System.out.print("Enter Admin Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = sc.nextLine();

        if (admin.login(username, password)) {
            admin.adminMenu();
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void userLogin() {
        System.out.print("Enter your Name: ");
        String name = sc.nextLine();
        user.userMenu(name);
    }
}
