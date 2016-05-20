package com.qoe.lengji.qoeclient;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataSender {
    public static boolean send(final JSONObject jsonObject) {
        String ip = "http://10.105.40.212";
        String port = "8080";
        final String urlString = ip + ":" + port + "/QoEServer/DataServlet";
        final boolean[] result = {false};
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    urlConnection.setChunkedStreamingMode(0);

                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                    String jsonString = jsonObject.toString();
                    wr.writeBytes(jsonString);
                    wr.flush();
                    wr.close();

                    int statusCode = urlConnection.getResponseCode();
                    if (statusCode == 200) {
                        InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] data = new byte[1024];
                        int length;
                        while ((length = inputStream.read(data)) != -1) {
                            outputStream.write(data, 0, length);
                        }
                        String response = new String(outputStream.toByteArray());
                        if(response.equals("successful")){
                            result[0] = true;
                        }
                        inputStream.close();
                    }
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private DataSender() {
    }
}
