/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code.model;

import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author bastinl
 */
public class Users {
  
    private ResultSet rs = null;
    private PreparedStatement pstmt = null;
    DataSource ds = null;
   
    public Users() {
        
        // You don't need to make any changes to the try/catch code below
        try {
            // Obtain our environment naming context
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            // Look up our data source
            ds = (DataSource)envCtx.lookup("jdbc/LessonDatabase");
        }
            catch(Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }
        
    }

    public int isValid(String name, String pwd) throws SQLException {

        try {

            Connection connection = ds.getConnection();

            if (connection != null) {
                String query = "SELECT * FROM clients WHERE username = '" + name + "' AND password = '" + pwd + "';";
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                if (rs.next()) {  // If there is a row in the ResultSet

                    connection.close();
                    return rs.getInt("clientid");

                } else {

                    connection.close();
                    return -1;
                }

            }
            else {
                return -1;
            }
        } catch(SQLException e) {
                    
            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            return -1;
        }
    }


    public void addUser(String name, String pwd) {


         try {
            
            Connection connection = ds.getConnection();

            if (connection != null) {
                
                pstmt = connection.prepareStatement("INSERT INTO clients ( username, password) VALUES (?,?)");
                pstmt.setString(1, name);
                pstmt.setString(2, pwd);

                pstmt.executeUpdate();


            
            }
            
         }
            catch(SQLException e) {
                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
               
         }
        
    }

}
