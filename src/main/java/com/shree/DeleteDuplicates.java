package com.shree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

@WebServlet("/auth/duplicatedelete")
public class DeleteDuplicates extends HttpServlet {
    public boolean del(File file) throws IOException {
        System.out.println("insside delete servlet: inside delete function");
        boolean status = false;
        if (file.isDirectory()) {
            File[] f = file.listFiles();
            for (final File fi : f) {
                if (fi.isDirectory()) {
                    del(fi);
                    status = fi.delete();
                } else {
                    status = fi.delete();
                }
            }
            status = file.delete();
        } else {
            System.out.println("insside delete servlet:file delete");
            status = file.delete();
            System.out.println(status);
        }
        return status;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("insside delete servlet");
        String path = request.getParameter("delete");
        System.out.println("insside delete servlet: " + path);
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        boolean status = false;
        File file = new File(path);
        if (file.exists()) {
            System.out.println("insside delete servlet: file exists");
            status = del(file);
        }
        if (status) {
            System.out.println("insside delete servlet:all ok");
            HttpSession httpSession = request.getSession();
            String uid = httpSession.getAttribute("id").toString();
            LocalTime time = LocalTime.now();
            String date = java.time.LocalDate.now().toString();
            System.out.println("if");
            String url = "jdbc:postgresql://localhost:5432/JSON";
            String userName = "postgres";
            String password = "admin";

            Connection connection;
            try {
                connection = DriverManager.getConnection(url, userName, password);
                String query = "Insert into delete_logs(deletes,date,time,username) values(?,?,?,?);";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, path);

                statement.setString(2, date);
                statement.setString(3, time.toString());
                statement.setString(4, uid);
                int rows = statement.executeUpdate();
                if (rows != 0) {
                    System.out.println("rows added");
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            map.put("status", 200);

        } else {
            map.put("status", 400);
        }
        Gson gson = new Gson();
        String json = gson.toJson(map);
        PrintWriter printWriter = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        printWriter.write(json);
        printWriter.flush();
        printWriter.close();
    }
}
