package com.shree;

import java.sql.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/auth/AdminOperations")
public class AdminOperations extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("inside admin op");
        String op = request.getParameter("operation");
        String query;
        if (op.equals("delete")) {
            query = "Select * from delete_logs";
        } else {
            query = "Select * from search_logs";
        }
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";

        Connection connection;
        try {
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println(query);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData re = resultSet.getMetaData();
            JSONArray jsonArray = new JSONArray();
            while (resultSet.next()) {
                int n = re.getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= n; i++) {
                    String col = re.getColumnName(i);
                    jsonObject.put(col, resultSet.getObject(col).toString());
                }
                jsonArray.add(jsonObject);
            }
            System.out.println(jsonArray);
            String json = jsonArray.toJSONString();
            System.out.println(json);
            PrintWriter printWriter = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            printWriter.write(json);
            printWriter.flush();
            printWriter.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
