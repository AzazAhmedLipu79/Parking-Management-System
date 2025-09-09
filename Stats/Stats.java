package Stats;

import java.sql.ResultSet;
import java.sql.SQLException;

import Config.Db;

public class Stats {

    public void GetSpotAvailability() {
        String sql = "SELECT a.spot_type, COUNT(a.spot_type) AS spot_capacity, " +
                     "(SELECT COUNT(b.ID) FROM Spot b WHERE b.spot_type = a.spot_type AND b.status = 'OCCUPIED') AS occupied_count " +
                     "FROM Spot a GROUP BY a.spot_type;";
        try (ResultSet rs = Db.executeQuery(sql)) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║            Spot Availability Summary         ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.printf("%-12s │ %-12s │ %-12s │ %-12s%n", "Spot Type", "Capacity", "Occupied", "Available");
            System.out.println("────────────────────────────────────────────────────────────");
            while (rs.next()) {
                String type = rs.getString("spot_type");
                int capacity = rs.getInt("spot_capacity");
                int occupied = rs.getInt("occupied_count");
                int available = capacity - occupied;
                System.out.printf("%-12s │ %-12d │ %-12d │ %-12d%n", type, capacity, occupied, available);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void GetPenaltyInsight() {
        String sql = "SELECT COUNT(token_id) AS penalty_count, " +
                     "COALESCE(SUM(penalty_amount),0) AS total_penalty_amount " +
                     "FROM Token WHERE penalty_amount > 0;";
        try (ResultSet rs = Db.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("\n╔══════════════════════════════════════════════╗");
                System.out.println("║               Penalty Insights               ║");
                System.out.println("╚══════════════════════════════════════════════╝");
                System.out.printf("%-20s : %d%n", "Penalty Count", rs.getInt("penalty_count"));
                System.out.printf("%-20s : %.2f%n", "Total Penalty Amount", rs.getDouble("total_penalty_amount"));
                System.out.println();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void RevenueInsight() {
        String sql = "SELECT b.Type, COUNT(a.token_id) AS token_count, " +
                     "COALESCE(SUM(a.amount),0) + COALESCE(SUM(a.penalty_amount),0) AS total_revenue " +
                     "FROM Token a JOIN Person b ON a.reg_id = b.reg_id GROUP BY b.Type;";
        try (ResultSet rs = Db.executeQuery(sql)) {
            System.out.println("\n╔══════════════════════════════════════════════╗");
            System.out.println("║                 Revenue Insights             ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.printf("%-12s │ %-12s │ %-15s%n", "Type", "Tokens", "Total Revenue");
            System.out.println("──────────────────────────────────────────────────────");
            while (rs.next()) {
                String type = rs.getString("Type");
                int tokens = rs.getInt("token_count");
                double revenue = rs.getDouble("total_revenue");
                System.out.printf("%-12s │ %-12d │ %-15.2f%n", type, tokens, revenue);
            }
            System.out.println();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
