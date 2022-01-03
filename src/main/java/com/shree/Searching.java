package com.shree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

@WebServlet("/auth/serachduplicates")
public class Searching extends HttpServlet {
    static final Runtime runtime = Runtime.getRuntime();

    public ArrayList<ArrayList<String>> serachDuplicates(String strPath) throws IOException {

        ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();

        search(strPath, arr);
        return arr;

    }

    public ArrayList<File> getAllFiles(File dir, ArrayList<File> f) throws IOException {
        if (f == null)
            f = new ArrayList<File>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                Path p = Paths.get(file.getCanonicalPath());
                System.out.println(p);
                Path p1 = p.toRealPath();
                System.out.println(p1);
                if (!(p.equals(p1))) {
                    int flag = 0;
                    for (File fi : f) {
                        if (p1 == Paths.get(fi.getAbsolutePath().toString())) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        ArrayList<File> temp = new ArrayList<File>();

                        f.addAll(getSymbilicFiles(file, temp));
                    }

                } else {
                    if (file.isFile()) {
                        f.add(file);

                    }
                    getAllFiles(file, f);
                }

            }
        }

        return f;
    }

    public ArrayList<File> getSymbilicFiles(File dir, ArrayList<File> f) throws IOException {
        System.out.println("hie");
        if (f == null) {
            f = new ArrayList<File>();
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                Path p = Paths.get(file.getAbsolutePath());
                Path p1 = p.toRealPath();
                if (!(p.equals(p1))) {

                } else {
                    if (file.isFile()) {
                        f.add(file);

                    }
                    getSymbilicFiles(file, f);
                }

            }
        }
        return f;
    }

    public void search(String path, ArrayList<ArrayList<String>> arr) throws IOException {
        long start = System.nanoTime();

        ArrayList<File> tempFile1 = new ArrayList<File>();
        getAllFiles(new File(path), tempFile1);
        for (int i = 0; i < tempFile1.size() - 1; i++) {
            int flag = 1;
            RandomAccessFile randomAccessFile1 = new RandomAccessFile(tempFile1.get(i), "r");
            FileChannel ch1 = randomAccessFile1.getChannel();
            ArrayList<String> temp = new ArrayList<String>();
            if (arr.size() != 0) {
                for (int a = 0; a < arr.size(); a++) {
                    for (int b = 0; b < arr.get(a).size(); b++) {
                        if (arr.get(a).get(b) == tempFile1.get(i).getAbsolutePath()) {
                            flag = 0;
                        }
                    }
                }
            }
            if (flag == 1) {
                for (int j = i + 1; j < tempFile1.size(); j++) {

                    RandomAccessFile randomAccessFile2 = new RandomAccessFile(tempFile1.get(j), "r");

                    FileChannel ch2 = randomAccessFile2.getChannel();
                    if (ch1.size() != ch2.size()) {

                    } else {
                        // long size = ch1.size();
                        ByteBuffer m1 = ByteBuffer.allocate(104857);
                        ByteBuffer m2 = ByteBuffer.allocate(104857);
                        ch1.read(m1);
                        ch2.read(m2);
                        if (m1.equals(m2)) {
                            if (temp.indexOf(tempFile1.get(i).getAbsolutePath()) == -1)
                                temp.add(tempFile1.get(i).getAbsolutePath());
                            if (temp.indexOf(tempFile1.get(j).getAbsolutePath()) == -1)
                                temp.add(tempFile1.get(j).getPath());

                            m1.clear();
                            m2.clear();
                        }

                    }
                    randomAccessFile2.close();
                    ch2.close();

                }

            }
            if (!(temp.isEmpty()))
                arr.add(temp);
            randomAccessFile1.close();
            ch1.close();

        }
        // JSONArray jsonArray = (JSONArray) jObject.get("dir");
        ArrayList<File> jsonArray = new ArrayList<File>();
        File f = new File(path);
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                jsonArray.add(file);
            }
        }
        for (int i = 0; i < jsonArray.size() - 1; i++) {
            ArrayList<File> tempFile2 = new ArrayList<File>();
            ArrayList<String> temp2 = new ArrayList<String>();
            getAllFiles(jsonArray.get(i), tempFile2);
            for (int j = i + 1; j < jsonArray.size(); j++) {
                ArrayList<File> tempFile3 = new ArrayList<File>();
                ArrayList<String> temp1 = new ArrayList<String>();

                getAllFiles(jsonArray.get(j), tempFile3);
                if (tempFile2.size() == tempFile3.size()) {
                    for (int k = 0; k < tempFile2.size(); k++) {
                        RandomAccessFile randomAccessFile1 = new RandomAccessFile(tempFile2.get(k), "r");
                        FileChannel ch1 = randomAccessFile1.getChannel();
                        for (int l = 0; l < tempFile3.size(); l++) {
                            RandomAccessFile randomAccessFile2 = new RandomAccessFile(tempFile3.get(l), "r");

                            FileChannel ch2 = randomAccessFile2.getChannel();
                            if (ch1.size() != ch2.size()) {

                            } else {
                                // long size = ch1.size();
                                ByteBuffer m1 = ByteBuffer.allocate(104857);
                                ByteBuffer m2 = ByteBuffer.allocate(104857);
                                ch1.read(m1);
                                ch2.read(m2);
                                if (m1.equals(m2)) {
                                    if (temp1.indexOf(tempFile2.get(k).getAbsolutePath().toString()) == -1) {
                                        temp1.add(tempFile2.get(k).getAbsolutePath().toString());
                                    }

                                    // if (temp1.indexOf(tempFile3.get(l).getAbsolutePath().toString()) == -1) {
                                    // System.out.println(tempFile3.get(l).getAbsolutePath().toString());
                                    // temp1.add(tempFile3.get(l).getAbsolutePath().toString());
                                    // }

                                }
                                m1.clear();
                                m2.clear();
                            }
                            ch2.close();
                            randomAccessFile2.close();
                        }
                        ch1.close();
                        randomAccessFile1.close();

                    }
                }

                if (temp1.size() == tempFile3.size()) {
                    if (temp2.indexOf(jsonArray.get(i).getAbsolutePath().toString()) == -1) {
                        temp2.add(jsonArray.get(i).getAbsolutePath().toString());
                    }
                    if (temp2.indexOf(jsonArray.get(j).getAbsolutePath().toString()) == -1) {
                        temp2.add(jsonArray.get(j).getAbsolutePath().toString());
                    }
                    temp1.clear();

                }
            }
            if (!(temp2.isEmpty()))
                arr.add(temp2);
        }
        long end = System.nanoTime();
        long time = end - start;
        // long elapsed = Duration.between(start, end).toNanos();
        System.out.print("time in ns: ");
        System.out.println(time);
        System.out.print("Memory used: ");
        System.out.println(runtime.totalMemory() - runtime.freeMemory());

    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = request.getParameter("path");
        String temp = "D:\\Directory\\demo\\server\\test\\subfolder_1";
        Path p = Paths.get(temp);
        Path p1 = p.toRealPath();
        System.out.println(p1);
        if (Files.isSymbolicLink(p1)) {
            System.out.println("sym");
        }
        System.out.println(Files.isSymbolicLink(p1));
        File file = new File(path);
        ArrayList<ArrayList<String>> sizeOfFile = new ArrayList<ArrayList<String>>();
        ArrayList<String> temp1 = new ArrayList<String>();
        ArrayList<ArrayList<String>> arr = new ArrayList<ArrayList<String>>();
        HashMap<String, ArrayList<ArrayList<String>>> map = new HashMap<String, ArrayList<ArrayList<String>>>();
        if (file.exists()) {
            arr = serachDuplicates(path);
            for (ArrayList<String> te : arr) {
                ArrayList<String> temp2 = new ArrayList<String>();
                if (!(te.isEmpty())) {

                    String cnt_size;
                    long size = Files.size(Paths.get(te.get(0)));
                    double size_kb = size / 1024;
                    size_kb = Math.round(size_kb * 100.0) / 100.0;
                    double size_mb = size_kb / 1024;
                    size_mb = Math.round(size_mb * 100.0) / 100.0;
                    double size_gb = size_mb / 1024;
                    size_gb = Math.round(size_gb * 100.0) / 100.0;

                    if (size_gb > 0) {
                        cnt_size = size_gb + " GB";
                    } else if (size_mb > 0) {
                        cnt_size = size_mb + " MB";
                    } else if (size_kb > 0) {
                        cnt_size = size_kb + " KB";
                    } else {
                        cnt_size = size + " Bytes";
                    }
                    System.out.println(size_kb);
                    System.out.println(cnt_size);
                    temp2.add(cnt_size);
                }

                sizeOfFile.add(temp2);
            }
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
                String query = "Insert into search_logs(serches,date,time,username) values(?,?,?,?);";
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            System.out.println("2 else");
            temp1.add("Invalid Path");
            arr.add(temp1);
        }
        if (arr.size() == 1) {
            System.out.println("3 else");
            if (arr.isEmpty()) {
                temp1.add("No duplicates");
                arr.add(temp1);
                temp1.clear();
            }

        }
        map.put("duplicates", arr);
        map.put("size", sizeOfFile);
        System.out.println(map);
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
