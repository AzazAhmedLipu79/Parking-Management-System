package Reservation;

import Config.Db; 
import Logger.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Reservation {
    private int spot_id; 
    private int token_id;
    private double allocation_time;
    private double amount;

    public Reservation(String reg_id, String vehicle_type, double allocation_time) {
        
        String avaialble_reservation_sql = "SELECT a.id AS spot_id, b.fees_per_hour FROM Spot a LEFT JOIN Pricing b ON a.spot_type = b.spot_type WHERE a.spot_type = '" + vehicle_type + "' AND a.status = 'AVAILABLE' LIMIT 1;";

        try (ResultSet rs = Db.executeQuery(avaialble_reservation_sql)) {
            if (rs.next()) {
                this.spot_id = rs.getInt("spot_id");   
                int fees_per_hour = rs.getInt("fees_per_hour");
                Timestamp current_timestamp = new Timestamp(new Date().getTime());

                if(allocation_time < 1){
                   this.allocation_time = 1; // threshold parking time
                }else{
                    this.allocation_time = allocation_time;
                }

                this.amount = this.allocation_time * fees_per_hour; 
                String book_reservation_sql = "INSERT INTO Token SET reg_id ='"+ reg_id+"', spot_id ='"+ this.spot_id +"', amount ='"+this.amount+"', arrival_time ='"+ current_timestamp+"';";
        
                this.token_id = Db.executeInsert(book_reservation_sql); 

                // UPDATE Spot SET status = 'OCCUPIED' WHERE spot_id = 1 LIMIT 1;
                String update_spot_sql =  "UPDATE Spot SET status = 'OCCUPIED' WHERE ID ='"+ this.spot_id +"' LIMIT 1";
                Db.executeUpdate(update_spot_sql);
                // Activity Log
                new Logger(this.token_id, "BOOKED");


            } else {
                System.out.println("No available spot found for " + vehicle_type);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        
    public Map<String, Number> get_info() {
        Map<String, Number> map = new HashMap<>();
        map.put("spot_id", (double) this.spot_id);  
        map.put("token_id", (double) this.token_id);
        map.put("amount", this.amount);
        return map;
    }
}
