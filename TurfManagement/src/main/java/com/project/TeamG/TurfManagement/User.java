package com.project.TeamG.TurfManagement;

import java.sql.*;
import java.util.Scanner;

public class User {
    private Scanner sc = new Scanner(System.in);

    public void userMenu(String userName) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View Available Slots");
            System.out.println("2. Book Slot");
            System.out.println("3. View My Bookings");
            System.out.println("4. Cancel Booking");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    viewAvailableSlots();
                    break;
                case 2:
                    bookSlot(userName);
                    break;
                case 3:
                    viewBookings(userName);
                    break;
                case 4:
                    cancelBooking(userName);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private void viewAvailableSlots() {
        String query = "SELECT * FROM turf_slots WHERE status='Available' ORDER BY slot_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\n--- Available Slots ---");
            System.out.printf("%-5s %-20s %-20s %-10s\n", "ID", "Name", "Timing", "Price(₹)");
            System.out.println("---------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-20s %-10.2f\n",
                        rs.getInt("slot_id"),
                        rs.getString("slot_name"),
                        rs.getString("timing"),
                        rs.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bookSlot(String userName) {
        System.out.print("Enter Slot ID to Book: ");
        int slotId = sc.nextInt();
        sc.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            // Check availability
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM turf_slots WHERE slot_id=? AND status='Available'");
            ps.setInt(1, slotId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String slotName = rs.getString("slot_name");
                String timing = rs.getString("timing");
                double price = rs.getDouble("price");

                // Insert booking
                PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO bookings (user_name, slot_name, timing, price) VALUES (?, ?, ?, ?)");
                insert.setString(1, userName);
                insert.setString(2, slotName);
                insert.setString(3, timing);
                insert.setDouble(4, price);
                insert.executeUpdate();

                // Update slot status
                PreparedStatement update = conn.prepareStatement("UPDATE turf_slots SET status='Booked' WHERE slot_id=?");
                update.setInt(1, slotId);
                update.executeUpdate();

                System.out.println("✅ Slot booked successfully: " + slotName + " | " + timing + " | ₹" + price);
            } else {
                System.out.println("Slot not available or invalid ID!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewBookings(String userName) {
        String query = "SELECT * FROM bookings WHERE user_name=? ORDER BY booking_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n--- My Bookings ---");
            System.out.printf("%-10s %-20s %-20s %-10s\n", "Book_ID", "Slot_Name", "Timing", "Price(₹)");
            System.out.println("---------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-20s %-10.2f\n",
                        rs.getInt("booking_id"),
                        rs.getString("slot_name"),
                        rs.getString("timing"),
                        rs.getDouble("price"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelBooking(String userName) {
        System.out.print("Enter Booking ID to Cancel: ");
        int bookingId = sc.nextInt();
        sc.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            // Step 1: find booking and slot
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT slot_name FROM bookings WHERE booking_id=? AND user_name=?");
            ps.setInt(1, bookingId);
            ps.setString(2, userName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String slotName = rs.getString("slot_name");

                // Step 2: delete booking
                PreparedStatement delete = conn.prepareStatement("DELETE FROM bookings WHERE booking_id=?");
                delete.setInt(1, bookingId);
                delete.executeUpdate();

                // Step 3: set slot status back to Available
                // Use slot_name to update relevant slot(s)
                PreparedStatement update = conn.prepareStatement("UPDATE turf_slots SET status='Available' WHERE slot_name=?");
                update.setString(1, slotName);
                update.executeUpdate();

                System.out.println("✅ Booking for slot \"" + slotName + "\" canceled successfully!");
            } else {
                System.out.println("Invalid Booking ID or no booking found under your name!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
