package com.shree;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/auth/adminDelete")
public class DeleteLogs extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String op = request.getParameter("option");
        String id = request.getParameter("id");
        String query;
        if (op.equals("search")) {
            query = "Delete from search_logs where id =" + id + ";";
        } else {
            query = "Delete from delete_logs where id =" + id + ";";
        }
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        HashMap<String, String> map = new HashMap<String, String>();
        Connection connection;

        Statement statement;
        try {
            connection = DriverManager.getConnection(url, userName, password);
            statement = connection.createStatement();
            int row = statement.executeUpdate(query);
            if (row == 1) {
                map.put("status", "200");
            } else {
                map.put("status", "400");
            }
            Gson gson = new Gson();
            String json = gson.toJson(map);
            PrintWriter printWriter = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            printWriter.write(json);
            printWriter.flush();
            printWriter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
