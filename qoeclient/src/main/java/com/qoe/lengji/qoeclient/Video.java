package com.qoe.lengji.qoeclient;


import java.io.Serializable;

public class Video implements Serializable{

    private String id;
    private String title;
    private String description;
    private String uri_cover = null;
    private String uri_uhd = null;
    private String uri_hd = null;
    private String uri_sd = null;

    public Video(String id, String title, String description, String uri_cover, String uri_uhd, String uri_hd, String uri_sd) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.uri_cover = uri_cover;
        this.uri_uhd = uri_uhd;
        this.uri_hd = uri_hd;
        this.uri_sd = uri_sd;
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", uri_cover='" + uri_cover + '\'' +
                ", uri_uhd='" + uri_uhd + '\'' +
                ", uri_hd='" + uri_hd + '\'' +
                ", uri_sd='" + uri_sd + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUri_cover() {
        return uri_cover;
    }

    public String getUri_uhd() {
        return uri_uhd;
    }

    public String getUri_hd() {
        return uri_hd;
    }

    public String getUri_sd() {
        return uri_sd;
    }

    public static String getTypeString(int type) {
        switch (type) {
            case 0:
                return "Movie";
            case 1:
                return "Episode";
            case 2:
                return "Music";
            case 3:
                return "Cartoon";
            case 4:
                return "Sport";
            case 5:
                return "Entertainment";
            default:
                return "Other";
        }
    }

}