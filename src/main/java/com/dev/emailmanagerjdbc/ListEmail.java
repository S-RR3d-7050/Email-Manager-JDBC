package com.dev.emailmanagerjdbc;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "ListEmail", value = "/ListEmail")
public class ListEmail extends HttpServlet {

    //private Number id=0;
    private final ArrayList<String> emails = new ArrayList<String>();

    public Connection connect(){
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\macga\\OneDrive\\Bureau\\LCS3\\JEE\\Email-Manager-JDBC\\src\\main\\resources\\EmailsDB.db");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    public ListEmail(){
        super();
    }



    public void subscribe(String email) throws ClassNotFoundException, SQLException {

        //int id = getId();
        Connection con = connect();
        PreparedStatement statement = null;
        try {
            String sql = "INSERT INTO Emails VALUES (?);";

            statement = con.prepareStatement(sql);

            //statement.setInt(1,id);
            statement.setString(1,email);

            statement.executeUpdate();
            emails.add(emails.size(), email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unsubscribe(String email){
        //int id = getId();
        Connection con= connect();
        PreparedStatement statement = null;
        try {
            String sql = "DELETE FROM Emails WHERE email = ?;";

            statement = con.prepareStatement(sql);

            //statement.setInt(1,id);
            statement.setString(1,email);

            statement.executeUpdate();
            emails.remove(email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void displayAllEmails(ArrayList<String> emails) {

        String sql = "SELECT * FROM Emails;";
        try {
            Connection con= connect();
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            String s;
            while (resultSet.next()){
                s= resultSet.getString("email");
                emails.add(s);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // CHECK IF MAIL IS VALID
    public static boolean isValidEmail(String email){
        Pattern special = Pattern.compile("^.+@.+\\..+$");
        Matcher hasSpecial = special.matcher(email);
        return hasSpecial.find();
    }



    public void init() {
        //connect();
        displayAllEmails(emails);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("text/html");
        String email = request.getParameter("email");
        PrintWriter out = response.getWriter();

        out.println();
        out.println("<html>");
        out.println("<head><title>EmailManger</title></head>");
        out.println("<body>");
        out.println("<form method=\"post\" action=ListEmail>");
        out.println("<h1>Members: </h1><br><hr>");
        out.println("<ul>");
        //displayAllEmails(emails);
        for (String s : emails) {
            if (s != null) {
                out.println("<li><p>" + s + "</p></li>");
            }
        }
        out.println("</ul>");
        out.print("<p>Entrer votre addresse email : <input type=text name=email></p>");
        out.print("<input type=\"submit\"  name=\"action\"value=\"subscribe\">");
        out.print("<input type=\"submit\" name=\"action\" value=\"unsubscribe\">");
        out.println("</form></body></html>");
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        if (action.contentEquals("subscribe")) {
            if (Objects.equals(req.getParameter("email"), "")) {
                //response.sendRedirect("Erreur.jsp");
                resp.sendError(404, "Addresse mail vide!");
            }
            else if (!isValidEmail(req.getParameter("email"))){
                resp.sendError(500, "Addresse mail invalide");
            }
            else {

                PrintWriter out = resp.getWriter();
                resp.setContentType("text/html");
                try {
                    subscribe(req.getParameter("email"));
                } catch (ClassNotFoundException | SQLException e) {
                    throw new RuntimeException(e);
                }
                out.println("<html>");
                out.println("<head><title>EmailAjoute</title></head>");
                out.println("<body>");
                out.println("<h4>Addresse "+req.getParameter("email")+" inscrite.</h4>");
                out.println("<hr>");
                out.println("<a href=\"ListEmail\"><h4>Afficher la liste</h4></a>");
                out.println("</form></body></html>");
            }
        }
        else if (action.contentEquals("unsubscribe")){
            if (Objects.equals(req.getParameter("email"), "")) {
                resp.sendError(404, "Addresse mail vide!");
            }
            else {

                PrintWriter out = resp.getWriter();
                resp.setContentType("text/html");
                unsubscribe(req.getParameter("email"));
                out.println("<html>");
                out.println("<head><title>EmailAjoute</title></head>");
                out.println("<body>");
                out.println("<h4>Addresse "+req.getParameter("email")+" supprimeé.</h4>");
                out.println("<hr>");
                out.println("<a href=\"ListEmail\"><h4>Afficher la liste</h4></a>");
                out.println("</form></body></html>");
            }

        }


    }

    public void destroy() {
    }
}