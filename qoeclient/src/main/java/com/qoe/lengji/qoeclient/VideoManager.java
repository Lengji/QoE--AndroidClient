package com.qoe.lengji.qoeclient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VideoManager {
    public static ArrayList<Video> getVideoList(int type) {
        final ArrayList<Video> list = new ArrayList<>();
        String ip = "http://10.105.40.212";
        String port = "8080";
        final String requestPath = ip + ":" + port + "/QoEServer/VideoServlet?type=" + String.valueOf(type);
        final String resourcePath = ip + ":" + port + "/QoEResource";

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(requestPath);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(3000);
                    connection.setRequestMethod("GET");
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        InputStream inputStream = connection.getInputStream();
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] data = new byte[1024];
                        int length;
                        while (-1 != (length = inputStream.read(data))) {
                            outputStream.write(data, 0, length);
                        }
                        String jsonString = new String(outputStream.toByteArray());
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                            String id = jsonObject.getString("id");
                            String title = jsonObject.getString("title");
                            String detail = jsonObject.getString("description");
                            int type = jsonObject.getInt("type");
                            String uri_cover, uri_uhd = null, uri_hd = null, uri_sd = null;
                            uri_cover = resourcePath + "/" + Video.getTypeString(type)
                                    + "/" + id + "/" + "cover.jpg";
                            if (jsonObject.getBoolean("uhd")) {
                                uri_uhd = resourcePath + "/" + Video.getTypeString(type)
                                        + "/" + id + "/" + "uhd.mp4";
                            }
                            if (jsonObject.getBoolean("hd")) {
                                uri_hd = resourcePath + "/" + Video.getTypeString(type)
                                        + "/" + id + "/" + "hd.mp4";
                            }
                            if (jsonObject.getBoolean("sd")) {
                                uri_sd = resourcePath + "/" + Video.getTypeString(type)
                                        + "/" + id + "/" + "sd.mp4";
                            }
                            Video v = new Video(id,title, detail, uri_cover, uri_uhd, uri_hd, uri_sd);
                            list.add(v);
                        }
                    }
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
        return list;
    }

    private VideoManager() {
    }
}
