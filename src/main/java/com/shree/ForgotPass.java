package com.shree;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

@WebServlet("/forgetPass")
public class ForgotPass extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uname = request.getParameter("uname");
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        String ques = null;
        String answer = null;
        HashMap<String, String> map = new HashMap<String, String>();
        Connection connection;
        System.out.println("inside");

        try {
            connection = DriverManager.getConnection(url, userName, password);
            String query = "select username,question,answer from users;";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            int flag = 0;
            if (uname != null) {
                while (rs.next()) {
                    if (uname.equals(rs.getString(1))) {
                        flag = 1;
                        ques = rs.getString(2);
                        System.out.println(ques);
                        answer = rs.getString(3);
                        break;
                    }
                }
            }
            if (flag == 1) {
                map.put("status", "200");
                map.put("question", ques.toString());
                map.put("answer", answer.toString());

            } else {
                map.put("status", "400");
            }
            System.out.println(map);
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
