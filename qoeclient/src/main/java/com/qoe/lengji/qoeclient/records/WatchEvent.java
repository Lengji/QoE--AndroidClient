package com.qoe.lengji.qoeclient.records;

import org.json.JSONException;
import org.json.JSONObject;

public class WatchEvent {
    public static final int PREPARED = 1;
    public static final int PAUSE_NORMAL = 2;
    public static final int PAUSE_STUCK = 3;
    public static final int PLAY = 4;
    public static final int FULLSCREEN = 5;
    public static final int FULLSCREEN_EXIT = 6;
    public static final int SEEK_LEFT = 7;
    public static final int SEEK_RIGHR = 8;
    public static final int STUCK = 9;
    public static final int RESOLUTION_SD = 10;
    public static final int RESOLUTION_HD = 11;
    public static final int RESOLUTION_UHD = 12;
    public static final int FINISH = 13;
    public static final int QUIT = 14;

    private int event = 0;
    private long position = 0;
    private long duration = 0;

    public WatchEvent(int event, long position, long duration) {
        this.event = event;
        this.position = position;
        this.duration = duration;
    }

    public WatchEvent(int event, long position) {
        this.event = event;
        this.position = position;
    }

    public WatchEvent(JSONObject jsonObject){
        try {
            this.event = getEventInt(jsonObject.getString("event"));
            this.position = jsonObject.getLong("position");
            this.duration = jsonObject.getLong("duration");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        if (duration > 0) {
            return "WatchEvent{" +
                    "event=" + getEventString(event) +
                    ", position=" + position +
                    ", duration=" + duration +
                    '}';
        } else {
            return "WatchEvent{" +
                    "event=" + getEventString(event) +
                    ", position=" + position +
                    '}';
        }
    }

    public int getEvent() {
        return event;
    }

    public long getPosition() {
        return position;
    }

    public long getDuration(){
        return duration;
    }

    public static String getEventString(int event) {
        switch (event) {
            case PREPARED:
                return "PREPARED";
            case PAUSE_NORMAL:
                return "PAUSE_NORMAL";
            case PAUSE_STUCK:
                return "PAUSE_STUCK";
            case PLAY:
                return "PLAY";
            case FULLSCREEN:
                return "FULLSCREEN";
            case FULLSCREEN_EXIT:
                return "FULLSCREEN_EXIT";
            case SEEK_LEFT:
                return "SEEK_LEFT";
            case SEEK_RIGHR:
                return "SEEK_RIGHR";
            case STUCK:
                return "STUCK";
            case RESOLUTION_SD:
                return "RESOLUTION_SD";
            case RESOLUTION_HD:
                return "RESOLUTION_HD";
            case RESOLUTION_UHD:
                return "RESOLUTION_UHD";
            case FINISH:
                return "FINISH";
            case QUIT:
                return "QUIT";
            default:
                return null;
        }
    }

    public static int getEventInt(String eventString){
        switch(eventString){
            case "PREPARED":
                return PREPARED;
            case "PAUSE_NORMAL":
                return PAUSE_NORMAL;
            case "PAUSE_STUCK":
                return PAUSE_STUCK;
            case "PLAY":
                return PLAY;
            case "FULLSCREEN":
                return FULLSCREEN;
            case "FULLSCREEN_EXIT":
                return FULLSCREEN_EXIT;
            case "SEEK_LEFT":
                return SEEK_LEFT;
            case "SEEK_RIGHR":
                return SEEK_RIGHR;
            case "STUCK":
                return STUCK;
            case "RESOLUTION_SD":
                return RESOLUTION_SD;
            case "RESOLUTION_HD":
                return RESOLUTION_HD;
            case "RESOLUTION_UHD":
                return RESOLUTION_UHD;
            case "FINISH":
                return FINISH;
            case "QUIT":
                return QUIT;
            default:
                return 0;
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", getEventString(event));
            jsonObject.put("position", position);
            jsonObject.put("duration", duration);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
