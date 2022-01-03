package com.shree;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

@WebServlet("/auth/ChartDate")
public class Chart extends HttpServlet {
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String op = request.getParameter("option");
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        String query;
        ArrayList<String> dates = new ArrayList<String>();
        ArrayList<LocalTime> times = new ArrayList<LocalTime>();
        LocalDate date = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            dates.add(date.minusDays(i).toString());
        }
        System.out.println(dates.get(5));
        LocalTime time = LocalTime.now();
        for (int j = 0; j < 12; j++) {
            times.add(time.minusHours(j));
        }
        System.out.println(times.get(5));
        ArrayList<Integer> data = new ArrayList<Integer>();
        for (int k = 0; k < 7; k++) {
            System.out.println(k);
            data.add(k, 0);
        }
        for (int k : data) {
            System.out.print(k);

        }
        System.out.println();
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, userName, password);
            if (op.equals("search")) {

                query = "Select date from search_logs;";

            } else {
                query = "Select date from delete_logs;";
            }
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int index = dates.indexOf(resultSet.getString(1).toString());
                System.out.println(resultSet.getString(1).toString());
                if (index != -1) {
                    System.out.println(index);
                    int temp = data.get(index);
                    temp = temp + 1;
                    data.set(index, temp);
                    System.out.println(data.get(index));
                }
            }
            for (int i : data)
                System.out.print(i);
            System.out.println();
            JSONArray jsonArray = new JSONArray();
            jsonArray.add(dates);
            jsonArray.add(data);
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
