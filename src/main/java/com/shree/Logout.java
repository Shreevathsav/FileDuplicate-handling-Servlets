package com.shree;

import java.io.IOException;
// import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// import com.google.gson.Gson;

@WebServlet("/auth/Logout")
public class Logout extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Logout");
        HttpSession session = request.getSession(false);
        if (session != null) {

            session.invalidate();

        }

        response.sendRedirect(request.getContextPath() + "/login.html");
    }
}
