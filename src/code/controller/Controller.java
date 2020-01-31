/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package code.controller;

import code.model.Lesson;
import code.model.LessonSelection;
import code.model.LessonTimetable;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import code.model.Users;
import java.sql.SQLException;

/**
 *
 * @author bastinl
 */

@WebServlet("/Controller")
public class Controller extends HttpServlet {

   private Users users;
   private LessonTimetable availableLessons;


    private String login = "/login.jsp";
    public void init() {
         users = new Users();
         availableLessons = new LessonTimetable();
         // TODO Attach the lesson timetable to an appropriate scope
        this.getServletContext().setAttribute("availableLessons", availableLessons);
        
    }
    
    public void destroy() {
        
    }


    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        // Check what action the user has selected...
        String action = request.getPathInfo();
        // Default dispatcher - return to log in page.
        RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher(login);
        // Get the session variable, if one exists...
        HttpSession session = request.getSession(false);

        // User has attempted to log in...
        // Returns -1 if they aren't a valid user
        if(action.equals("/login") && (users.isValid(request.getParameter("username"), request.getParameter("password"))!=-1)) {

            // Create a session
            session = request.getSession();
            // Creates a selection for the user
            LessonSelection selection = new LessonSelection(users.isValid(request.getParameter("username"), request.getParameter("password")));
            // Assigns the selection to the session
            session.setAttribute("selection", selection);
            // Requests the timetableview upon completion
            dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
        }
        else if(action.equals("/addUser")){
            //Call the addUser functions if a new user signs up
            users.addUser(request.getParameter("newUsername"), request.getParameter("newPassword"));
            // Navigates to the login screen
            dispatcher = this.getServletContext().getRequestDispatcher("/login.jsp");

        }

        // If the user has already successfully logged in..
        else if(session.getAttribute("selection") != null) {

            // If the users chooses a particular lesson
            if(action.equals("/chooseLesson")){
                // The lesson selected and current selection are retrieved
                Lesson lesson = this.availableLessons.getLesson(request.getParameter("lessonid"));
                LessonSelection selection = (LessonSelection) session.getAttribute("selection");
                Lesson previousHistoryOfLesson = selection.getLesson(lesson.getId());

                // If the lesson already appears in the list of selected lesson, it shall not be added again.
                if(previousHistoryOfLesson != null){
                    selection.addLesson(lesson);
                    session.setAttribute("selection", selection);
                }

                // The user is then taken to the selection view.
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");

            }
            // Load timetableView from Navigation bar
            else if(action.equals("/lessons")){
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonTimetableView.jspx");
            }
            // View the current selection
            else if(action.equals("/viewSelection")){
                dispatcher = this.getServletContext().getRequestDispatcher("/LessonSelectionView.jspx");
            }
            // Finalise the booking of the selected lessons
            else if(action.equals("/finaliseBooking")){
                LessonSelection selection = (LessonSelection) session.getAttribute("selection");
                selection.updateBooking();
            }
            // Log out of the current session
            else if(action.equals("/logOut")){
                session.invalidate();
            }

        }

        dispatcher.forward(request, response);

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
