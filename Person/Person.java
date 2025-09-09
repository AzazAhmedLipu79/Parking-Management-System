package Person; 
import Config.Db;
import Logger.Logger;
import Reservation.Reservation;
 
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map; 


/* 
 Person Create
 Person Reserve
 Person Can check Distance
 Person can release his vehicle by token_id and reg_id
 If time exceeds, penalty strikes
 */

public class Person {
     
    int person_id;
    String name;
    String phone;
    String reg_id;
    String vehicle_type;
    double allocation_time;
    Map<String, Number> token_info;
    double penalized_amount = 0;

    

    public Person(String name, String phone, String reg_id, String vehicle_type, double allocation_time){

        this.name = name;
        this.phone = phone;
        this.reg_id = reg_id; 
        this.vehicle_type = vehicle_type;
        this.allocation_time = allocation_time;
        Timestamp current_timestamp = new Timestamp(new Date().getTime());

        String sql = "INSERT INTO Person SET `name` ='"+this.name+"',`phone`='"+this.phone+"',`created_at`='"+current_timestamp.toString()+"',reg_id ='"+reg_id+"',type='"+this.vehicle_type+"'";

        
        System.out.println("Processing Reservation...");
        // Insert into database   
         try{
            this.person_id = Db.executeInsert(sql); 
            Reservation rsv = new Reservation(reg_id, vehicle_type, allocation_time);
            this.token_info = rsv.get_info();
            
            System.out.println("=====================================");
            System.out.println("Reservation Successful.");
 
                
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    public void leave_slot(double leaved_at){
        System.out.println("Processing Token Leave...");
        try{
            // If penalized
            if(leaved_at > allocation_time){
                 
                // get the penalized amount 
                // SELECT penalty_per_hour FROM `Pricing` WHERE spot_type = 'CAR' LIMIT 1
                String penalized_amount_sql = "SELECT penalty_per_hour FROM `Pricing` WHERE spot_type = '"+this.vehicle_type+"' LIMIT 1";
                    
                ResultSet rs = Db.executeQuery(penalized_amount_sql);
                    if (rs.next()) {
                                        
                        System.err.println("Penalty Occured!");
                        
                        this.penalized_amount = rs.getInt("penalty_per_hour") * (leaved_at - allocation_time);   
                        System.err.println("You have been charged "+ this.penalized_amount +" for being "+ (leaved_at - allocation_time)+" hours late.");
                    }
            }
            // Spot Status Update
            // UPDATE `Spot` SET `status`='AVAILABLE' WHERE ID = 1 LIMIT 1;
            String update_spot_sql = "UPDATE `Spot` SET `status`='AVAILABLE' WHERE ID ='"+ this.token_info.get("spot_id") +"'LIMIT 1;";
        
            Db.executeUpdate(update_spot_sql); 

            // Token Status Update
            // UPDATE `Token` SET `penalty_amount`='[value-5]',`leaved_at`='[value-7]',`status`='EXPIRED' WHERE token_id = 1 LIMIT 1
            String update_token_sql = "UPDATE `Token` SET `penalty_amount`='"+this.penalized_amount+"',`leaved_at`=arrival_time +'"+leaved_at+"',`status`='EXPIRED' WHERE token_id ='"+ this.token_info.get("token_id") +"'LIMIT 1";
            
            Db.executeUpdate(update_token_sql);
            
            // Log Append
            new Logger(this.token_info.get("token_id"), "DEPARTED");

            
            System.out.println("=====================================");
            System.out.println("Process Successful. Thank You!");
        }catch (Exception e) {
            System.err.println("SERVER CRASHED");
        }
    }

    public Double distance(Double latitude, Double longitude) {
        
        System.out.println("Calculating distance..."); 
        String haversine_distance_sql = "SELECT (6371 * ACOS(" +
                    "COS(RADIANS(" + latitude + ")) * COS(RADIANS(Lat)) * " +
                    "COS(RADIANS(Lon) - RADIANS(" + longitude + ")) + " +
                    "SIN(RADIANS(" + latitude + ")) * SIN(RADIANS(Lat))" +
                    ")) AS distance " +
                    "FROM Spot WHERE id = 1";

        try (ResultSet rs = Db.executeQuery(haversine_distance_sql)) {
            if (rs.next()) {
                
                System.out.println("=====================================");
                return rs.getDouble("distance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;  
    }



    public void print() {
        System.out.println("=====================================");
        System.out.printf("%-15s : %d%n", "Person ID", person_id);
        System.out.printf("%-15s : %s%n", "Name", name);
        System.out.printf("%-15s : %s%n", "Phone", phone);
        System.out.printf("%-15s : %s%n", "Reg ID", reg_id);
        System.out.printf("%-15s : %s%n", "Vehicle Type", vehicle_type);
        System.out.printf("%-15s : %.2f%n", "Allocation Time", allocation_time);

        

        System.out.println("Token Info:");
        if (token_info != null) { 
            System.out.printf("  %-13s : %d%n", "Token ID", token_info.get("token_id").intValue());
            System.out.printf("  %-13s : %d%n", "Spot ID", token_info.get("spot_id").intValue());
            System.out.printf("  %-13s : %.2f%n", "Amount", token_info.get("amount").doubleValue());

        } else {
            System.out.println("  (none)");
        }
        System.out.println("=====================================");
    }
 

}
