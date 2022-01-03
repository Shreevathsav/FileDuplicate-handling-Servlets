package com.shree;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

@WebServlet("/UpdatePass")
public class UpdatePassword extends HttpServlet {
    private static String generateStorngPasswordHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uname = request.getParameter("uname");
        String pass = request.getParameter("pass");

        String encodedPass = null;
        try {
            encodedPass = generateStorngPasswordHash(pass);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        HashMap<String, String> map = new HashMap<String, String>();
        Connection connection = null;
        System.out.println("inside");

        try {
            connection = DriverManager.getConnection(url, userName, password);
            String query = "UPDATE users SET password = (?) WHERE username = (?);";
            PreparedStatement pStatement = connection.prepareStatement(query);
            pStatement.setString(1, encodedPass);
            pStatement.setString(2, uname);
            int rows = pStatement.executeUpdate();
            if (rows != 0) {
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
