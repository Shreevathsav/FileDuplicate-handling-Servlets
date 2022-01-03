package com.shree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

class APIcall {

    public static void main(String args[]) throws IOException {
        HttpURLConnection connection;
        String json = "{ \"query\": \"get_iocs\", \"days\": 7 }";
        URL url = new URL("https://mb-api.abuse.ch/api/v1?query=get_recent&selector=100");
        connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        OutputStream os = connection.getOutputStream();
        os.write(json.getBytes("UTF-8"));
        os.close();

        BufferedReader reader;
        String line;
        StringBuffer res = new StringBuffer();

        int status = connection.getResponseCode();
        if (status > 299) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            reader.close();
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            reader.close();
        }
        JSONObject jsonObject = new JSONObject(res.toString());
        System.out.println(jsonObject);
        // System.out.println(res);

    }
}
