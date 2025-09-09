package Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import Config.Db;

public class Logger {
 
    public Logger(Number token_id, String status){
        
        Timestamp current_timestamp = new Timestamp(new Date().getTime());

        // String insert_log = "INSERT INTO TokenLog  SET token_id ='"+ token_id +"', status = '"+ status +"' time ='"+current_timestamp+ "';";
        String insert_log = "INSERT INTO TokenLog SET token_id = " + token_id + ", status = '" + status + "'"+ ", time = '" + current_timestamp + "';";

        try{
            Db.executeInsert(insert_log); 
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
