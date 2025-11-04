package com.project.TeamG.TurfManagement;

import java.sql.*;
import java.util.Scanner;

public class Admin {
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";
    private Scanner sc = new Scanner(System.in);

    // ✅ Admin Login Check
    public boolean login(String username, String password) {
        return username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD);
    }

    // ✅ Admin Menu
    public void adminMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Turf Slots");
            System.out.println("2. Add Turf Slot");
            System.out.println("3. Remove Turf Slot");
            System.out.println("4. View All Bookings");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewSlots();
                    break;
                case 2:
                    addSlot();
                    break;
                case 3:
                    removeSlot();
                    break;
                case 4:
                    viewAllBookings();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ✅ View all turf slots
    private void viewSlots() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM turf_slots")) {

            System.out.println("\n--- All Turf Slots ---");
            System.out.printf("%-5s %-20s %-20s %-10s %-15s\n", "ID", "Name", "Timing", "Price", "Status");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s %-10.2f %-15s\n",
                        rs.getInt("slot_id"),
                        rs.getString("slot_name"),
                        rs.getString("timing"),
                        rs.getDouble("price"),
                        rs.getString("status"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Add new turf slot
    private void addSlot() {
        System.out.print("Enter Slot Name: ");
        String slotName = sc.nextLine();
        System.out.print("Enter Timing (e.g., 10AM - 11AM): ");
        String timing = sc.nextLine();
        System.out.print("Enter Price (₹): ");
        double price = sc.nextDouble();
        sc.nextLine();

        String query = "INSERT INTO turf_slots (slot_name, timing, price) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, slotName);
            ps.setString(2, timing);
            ps.setDouble(3, price);
            ps.executeUpdate();
            System.out.println("✅ Slot added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Remove turf slot
    private void removeSlot() {
        System.out.print("Enter Turf Slot ID to remove: ");
        int slotId = sc.nextInt();

        String query = "DELETE FROM turf_slots WHERE slot_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, slotId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Slot removed successfully!");
            } else {
                System.out.println("⚠️ Slot ID not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ View all user bookings
    private void viewAllBookings() {
        String query = "SELECT * FROM bookings ORDER BY booking_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- All User Bookings ---");
            System.out.printf("%-10s %-20s %-20s %-20s %-10s\n",
                    "Book_ID", "User_Name", "Slot_Name", "Timing", "Price(₹)");
            System.out.println("--------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s %-20s %-10.2f\n",
                        rs.getInt("booking_id"),
                        rs.getString("user_name"),
                        rs.getString("slot_name"),
                        rs.getString("timing"),
                        rs.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
