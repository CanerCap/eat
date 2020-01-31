/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code.model;

import java.sql.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 *
 * @author bastinl
 */
public class LessonSelection  {
    
    private HashMap<String, Lesson> chosenLessons;
    private int ownerID;
    
    private DataSource ds = null;
    
    private ResultSet rs = null;
    private Statement st = null;

    //As this class is called by useBean a non-argument constructor has been created
    public LessonSelection(){
        super();
        chosenLessons = new HashMap<String, Lesson>();
    }

    public LessonSelection(int owner) {
        
        chosenLessons = new HashMap<String, Lesson>();
        this.ownerID = owner;

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
        
        // Connect to the database - this is a pooled connection, so you don't need to close it afterwards
        try {

            Connection connection = ds.getConnection();

            if (connection != null) {

                // TODO get the details of any lessons currently selected by this user
                String query = "select description, level, startDateTime, endDateTime, A.lessonid from lessons A left join lessons_booked B ON A.lessonid = B.lessonid where B.clientid = '" + ownerID + "';";

                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {

                        Lesson i = new Lesson(rs.getString("description"), rs.getTimestamp("startDateTime"), rs.getTimestamp("endDateTime"), rs.getInt("level"), rs.getString("lessonid"));
                        //System.out.println(i.description);
                        addLesson(i);
                    }

                } catch (SQLException e) {

                    System.out.println("Exception is ;" + e + ": message is " + e.getMessage());

                }


            }


        }catch(Exception e){

                System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
            }
        
    }

    /**
     * @return the items
     */
    public Set <Entry <String, Lesson>> getItems() {
        return chosenLessons.entrySet();
    }

    public void addLesson(Lesson l) {
       
        Lesson i = new Lesson(l);
        this.chosenLessons.put(l.getId(), i);
       
    }

    public Lesson getLesson(String id){
        return this.chosenLessons.get(id);
    }
    
    public int getNumChosen(){
        return this.chosenLessons.size();
    }

    public int getOwner() {
        return this.ownerID;
    }
    
    public void updateBooking() {
        
        // A tip: here is how you can get the ids of any lessons that are currently selected


        try {


            Connection connection = ds.getConnection();

            if (connection != null) {
                String query = "delete from lessons_booked where clientid = '" + ownerID + "';";

                try {
                    Statement stmt = connection.createStatement();
                    stmt.executeUpdate(query);
                    Object[] lessonKeys = chosenLessons.keySet().toArray();


                    for (int i=0; i<lessonKeys.length; i++) {
                        String insertQuery ="INSERT INTO lessons_booked (clientid, lessonid) VALUES ('" + ownerID +"','" + lessonKeys[i]  + "');";
                        stmt.executeUpdate(insertQuery);

                        // Temporary check to see what the current lesson ID is....
                    }



                } catch (SQLException e) {

                    System.out.println("Exception is ;" + e + ": message is " + e.getMessage());

                }


            }
        }catch(Exception e){

            System.out.println("Exception is ;"+e + ": message is " + e.getMessage());
        }

    }

}
