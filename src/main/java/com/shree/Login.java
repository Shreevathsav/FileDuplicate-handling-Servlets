package com.shree;

import java.sql.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Login")
public class Login extends HttpServlet {

    private static boolean validatePassword(String originalPassword, String storedPassword)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

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
            String query = "select id,username,password from users;";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            int flag = 0;
            String id = null;
            if (user != null) {
                while (rs.next()) {

                    if (user.equals(rs.getString(2)) && validatePassword(pass, rs.getString(3))) {
                        flag = 1;
                        id = rs.getString(2);
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
                    session.setAttribute("id", id);
                    arg1.sendRedirect("auth/index.html");

                } else {
                    RequestDispatcher rd = getServletContext().getRequestDispatcher("/login.html");
                    PrintWriter out = arg1.getWriter();
                    out.println("<font color=red>Either username or password is wrong.</font>");
                    rd.include(arg0, arg1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

    }

    // @Override
    // public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain
    // arg2)
    // throws IOException, ServletException {
    // // TODO Auto-generated method stub

    // }
}
