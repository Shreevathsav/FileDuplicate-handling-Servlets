package com.shree;

import java.sql.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminLogin")
public class AdminLogin extends HttpServlet {
    public void service(HttpServletRequest arg0, HttpServletResponse arg1) throws IOException, ServletException {
        System.out.println("inside login");
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        String user = arg0.getParameter("username");
        String pass = arg0.getParameter("password");
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, userName, password);
            String query = "select convert_from(decrypt(password, 'admin', 'aes'), 'SQL_ASCII') from admin_credentials";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            int flag = 0;
            if (user != null) {
                while (rs.next()) {
                    if (user.equals("admin") && pass.equals(rs.getString(1))) {
                        flag = 1;

                        break;
                    }
                }
                connection.close();
                if (flag == 1) {
                    HttpSession oldSession = arg0.getSession(false);
                    if (oldSession != null) {
                        oldSession.invalidate();
                    }
                    HttpSession session = arg0.getSession();
                    session.setAttribute("user", "admin");
                    arg1.sendRedirect("auth/admin.html");

                } else {
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
                    PrintWriter out = arg1.getWriter();
                    out.println("<font color=red>Either username or password is wrong.</font>");
                    rd.include(arg0, arg1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
