package com.shree;

import java.sql.Statement;
import java.util.HashMap;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

@WebServlet("/auth/clearlogs")
public class ClearLogs extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String uid = session.getAttribute("id").toString();
        String query = "Delete from search_logs where username = \'" + uid + "\';";
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        Connection connection;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try {
            connection = DriverManager.getConnection(url, userName, password);
            Statement statement = connection.createStatement();
            int row = statement.executeUpdate(query);
            if (row != 0) {
                System.out.println(row);
                map.put("status", 200);
                Gson gson = new Gson();
                String json = gson.toJson(map);
                PrintWriter printWriter = response.getWriter();
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                printWriter.write(json);
                printWriter.flush();
                printWriter.close();

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
