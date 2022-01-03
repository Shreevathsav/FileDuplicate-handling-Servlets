package com.shree;

import java.sql.Statement;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStream;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/auth/download")
public class DownloadPDF extends HttpServlet {
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    // private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
    // Font.NORMAL, BaseColor.RED);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void PDFDownloader(JSONArray jArray, String FILE, String option)
            throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(FILE));
        document.open();
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);
        preface.add(new Paragraph(option + " report", catFont));

        addEmptyLine(preface, 1);

        preface.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                smallBold));
        document.add(preface);

        document.newPage();

        PdfPTable table = new PdfPTable(4);

        PdfPCell c1 = new PdfPCell(new Phrase("User"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Logs"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Date"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        c1 = new PdfPCell(new Phrase("Time"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);
        table.setHeaderRows(1);

        for (int i = 0; i < jArray.size(); i++) {
            JSONObject jsonObject = (JSONObject) jArray.get(i);
            table.addCell(jsonObject.get("username").toString());
            if (option.equals("search"))
                table.addCell(jsonObject.get("serches").toString());
            else
                table.addCell(jsonObject.get("deletes").toString());
            table.addCell(jsonObject.get("time").toString());
            table.addCell(jsonObject.get("date").toString());
        }

        // table.addCell("2.1");
        // table.addCell("2.2");
        // table.addCell("2.3");
        // table.addCell("2.3");
        // table.addCell("2.4");

        document.add(table);
        document.close();
        System.out.println("file created");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String op = request.getParameter("option");
        String query;
        if (op.equals("search")) {
            query = "Select username, serches, date, time from search_logs;";
        } else {
            query = "Select username, deletes, date, time from delete_logs;";
        }
        String url = "jdbc:postgresql://localhost:5432/JSON";
        String userName = "postgres";
        String password = "admin";
        Connection connection;

        Statement statement;

        try {
            connection = DriverManager.getConnection(url, userName, password);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData rsm = rs.getMetaData();
            JSONArray jsonArray = new JSONArray();
            while (rs.next()) {
                int n = rsm.getColumnCount();
                JSONObject jsonObject = new JSONObject();
                for (int i = 1; i <= n; i++) {
                    String col = rsm.getColumnName(i);
                    jsonObject.put(col, rs.getObject(col).toString());
                }
                jsonArray.add(jsonObject);
            }
            System.out.println(jsonArray);
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            String FILE = "D:\\Directory\\demo\\src\\main\\webapp\\WEB-INF\\LOGS\\" + op + "-" + dateFormat.format(date)
                    + ".pdf";
            PDFDownloader(jsonArray, FILE, op);
            System.out.println("after file created");
            // String FILE_LOCATION = "C:\\Users\\Shreevathsav\\Downloads";
            String fileName = op + "-" + dateFormat.format(date) + ".pdf";

            FileInputStream fileInputStream = null;
            OutputStream responseOutputStream = null;
            try {
                File file = new File(FILE);

                String mimeType = request.getServletContext().getMimeType(FILE);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }
                response.setContentType(mimeType);
                response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setContentLength((int) file.length());

                fileInputStream = new FileInputStream(file);
                responseOutputStream = response.getOutputStream();
                int bytes;
                while ((bytes = fileInputStream.read()) != -1) {
                    responseOutputStream.write(bytes);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                fileInputStream.close();
                responseOutputStream.close();
            }

            // response.reset();
            // response.setContentType("application/pdf");
            // response.setHeader("Content-disposition", "attachment; filename=" +
            // fileName);
            // response.setContentLength((int) file.length());

            // ServletOutputStream servletOutputStream = response.getOutputStream();
            // BufferedInputStream bufferedInputStream = new BufferedInputStream(new
            // FileInputStream(file));
            // int bytesRead = bufferedInputStream.read();
            // while (bytesRead != -1) {
            // servletOutputStream.write(bytesRead);
            // bytesRead = bufferedInputStream.read();
            // }
            // if (servletOutputStream != null) {
            // servletOutputStream.close();
            // }
            // if (bufferedInputStream != null) {
            // bufferedInputStream.close();
            // }

            // try (InputStream in = request.getServletContext().getResourceAsStream(FILE);
            // OutputStream out = response.getOutputStream()) {

            // byte[] buffer = new byte[1080];

            // int numBytesRead;
            // while ((numBytesRead = in.read(buffer)) > 0) {
            // out.write(buffer, 0, numBytesRead);
            // }
            // }
            // map.put("status", "200");
            // Gson gson = new Gson();
            // String json = gson.toJson(map);
            // PrintWriter printWriter = response.getWriter();
            // response.setContentType("application/json");
            // response.setCharacterEncoding("UTF-8");
            // printWriter.write(json);
            // printWriter.flush();
            // printWriter.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
