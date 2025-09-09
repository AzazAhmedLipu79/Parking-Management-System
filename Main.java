import Person.Person;
import Stats.Stats;

import java.util.Locale;
import java.util.Scanner;

class Main {

    private static final Scanner SC = new Scanner(System.in);

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        clear();
        banner();

        String name = readNonEmpty("Enter Name");
        String phone = readNonEmpty("Enter Phone");
        String regId = readNonEmpty("Enter Registration ID");
        String vehicleType = readVehicleType();
        double allocationTime = readPositiveDouble("Enter Allocation Time (hours)");

        clear();
        banner();
        section("Booking Summary");
        row("Name", name);
        row("Phone", phone);
        row("Registration ID", regId);
        row("Vehicle Type", vehicleType);
        row("Allocation (hours)", String.format("%.2f", allocationTime));
        line();

        Person p = new Person(name, phone, regId, vehicleType, allocationTime);
        Stats s = new Stats();

        while (true) {
            section("Manage");
            System.out.println("  1) Print Receipt");
            System.out.println("  2) See Distance");
            System.out.println("  3) Leave");
            System.out.println("  4) View Spot Availability");
            System.out.println("  5) View Penalty Insights");
            System.out.println("  6) View Revenue Insights");
            System.out.println("  0) Exit");
            line();
            int status = readMenuChoice(0, 6);

            switch (status) {
                case 1:
                    clear(); banner(); section("Receipt");
                    p.print();
                    pause();
                    break;

                case 2:
                    clear(); banner(); section("Distance");
                    double lat = readDouble("Enter Your Latitude (e.g. 23.8)");
                    double lon = readDouble("Enter Your Longitude (e.g. 90.4)");
                    double dist = p.distance(lat, lon);
                    System.out.printf("Distance: %.2f meters away%n", dist);
                    line();
                    pause();
                    break;

                case 3:
                    clear(); banner(); section("Leave Slot");
                    double leaveHours = readPositiveDouble("Enter Leave Time (hours)");
                    p.leave_slot(leaveHours);
                    goodbye();
                    SC.close();
                    return;

                case 4:
                    clear(); banner(); s.GetSpotAvailability();
                    pause();
                    break;

                case 5:
                    clear(); banner(); s.GetPenaltyInsight();
                    pause();
                    break;

                case 6:
                    clear(); banner(); s.RevenueInsight();
                    pause();
                    break;

                case 0:
                    goodbye();
                    SC.close();
                    return;

                default:
                    // unreachable due to validator
                    break;
            }
            clear();
            banner();
        }
    }
 

    private static void banner() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              Welcome To Parking Management System        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private static void section(String title) {
        System.out.println("► " + title);
        line();
    }

    private static void line() {
        System.out.println("──────────────────────────────────────────────────────────");
    }

    private static void row(String k, String v) {
        System.out.printf("  %-22s : %s%n", k, v);
    }

    private static void pause() {
        System.out.println();
        System.out.print("Press ENTER to continue...");
        SC.nextLine();
    }

    private static void goodbye() {
        System.out.println();
        System.out.println("✓ Thank you for using the Parking Management System.");
        System.out.println();
    }

    private static void clear() {
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception ignored) {
            for (int i = 0; i < 40; i++) System.out.println();
        }
    }

    /* ========== Input Helpers ========== */

    private static String readNonEmpty(String label) {
        while (true) {
            System.out.print(label + ": ");
            String val = SC.nextLine().trim();
            if (!val.isEmpty()) return val;
            System.out.println("Please enter a non-empty value.");
        }
    }

    private static String readVehicleType() {
        while (true) {
            System.out.println();
            System.out.println("Enter Vehicle Type >");
            System.out.println("  1) CAR");
            System.out.println("  2) BIKE");
            System.out.println("  3) TRUCK");
            System.out.print("Choice (1-3 or CAR/BIKE/TRUCK): ");
            String in = SC.nextLine().trim().toUpperCase();

            if (in.equals("1") || in.equals("CAR")) return "CAR";
            if (in.equals("2") || in.equals("BIKE")) return "BIKE";
            if (in.equals("3") || in.equals("TRUCK")) return "TRUCK";

            System.out.println("Invalid choice. Please enter 1, 2, 3 or CAR/BIKE/TRUCK.");
        }
    }

    private static int readMenuChoice(int min, int max) {
        while (true) {
            System.out.print("Select an option (" + min + "-" + max + "): ");
            String s = SC.nextLine().trim();
            try {
                int n = Integer.parseInt(s);
                if (n >= min && n <= max) return n;
            } catch (NumberFormatException ignored) {}
            System.out.println("Invalid input. Please enter a number between " + min + " and " + max + ".");
        }
    }

    private static double readDouble(String label) {
        while (true) {
            System.out.print(label + ": ");
            String s = SC.nextLine().trim();
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private static double readPositiveDouble(String label) {
        while (true) {
            double v = readDouble(label);
            if (v >= 0) return v;
            System.out.println("Value must be ≥ 0. Try again.");
        }
    }
}
