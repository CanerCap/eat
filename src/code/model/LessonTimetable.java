/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package code.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


/**
 * @author bastinl
 */
public class LessonTimetable {

    private Connection connection = null;
    private ResultSet rs = null;
    private Statement st = null;
    private DataSource ds = null;
    private Map lessons = null;

    public LessonTimetable() {

        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            ds = (DataSource) envCtx.lookup("jdbc/LessonDatabase");
        } catch (Exception e) {
            System.out.println("Exception message is " + e.getMessage());
        }

        try {
            connection = ds.getConnection();

            if (connection != null) {


                lessons = new HashMap<String, Lesson>();
                String query = "SELECT * FROM lessons";

                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()) {

                        Lesson i = new Lesson(rs.getString("description"), rs.getTimestamp("startDateTime"), rs.getTimestamp("endDateTime"), rs.getInt("level"), rs.getString("lessonid"));

                        lessons.put(rs.getString(5), i);
                    }

                } catch (SQLException e) {

                    System.out.println("Exception is ;" + e + ": message is " + e.getMessage());
                }

            }


        } catch (Exception e) {

            System.out.println("Unable to make LessonTimetable Connection");
        }

    }


    /**
     * @return the items
     */
    public Lesson getLesson(String itemID) {

        return (Lesson) this.lessons.get(itemID);
    }

    public Map getLessons() {

        return this.lessons;
    }

    public void cleanUp() {
        try {
            connection.close();
            connection = null;
        } catch (Exception e) {
            System.out.println("Exception is ;" + e + ": message is " + e.getMessage());
        }
    }


}
