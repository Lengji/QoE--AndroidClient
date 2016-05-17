package com.qoe.lengji.qoeclient;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.qoe.lengji.qoeclient.records.WatchEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CustomVideoView extends VideoView implements CustomMediaController.CustomMediaPlayerControl {

    private static final int RES_SD = 1;
    private static final int RES_HD = 2;
    private static final int RES_UHD = 3;

    private View parentView = null; //父视图，为了确保视频画面处于在屏幕的水平中央，并且控制条是依附在夫视图上的，以保证画面横向未填满屏幕时控制块仍能顶满屏幕。
    private CustomMediaController mController = null;
    private boolean isFullScreen = false;
    private canFullScreen mActivity = null;
    private static final int defaultHeight = 270;
    private static final float densityRatio = 2.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）
    private Video video;
    private int currentResolution;
    private long createTime = 0;
    private long initTime;

    private long pauseStartTime = 0;
    private ArrayList<WatchEvent> eventList = new ArrayList<>();

    public CustomVideoView(Context context) {
        super(context);
        this.mActivity = (canFullScreen) context;
        init();
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            this.mActivity = (canFullScreen) context;
        }
        init();
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mActivity = (canFullScreen) context;
        init();
    }

    private void init() {
        createTime = new Date().getTime();
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mController != null) {
                    showHideController();
                }
                return false;
            }
        });
        setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                long startTime = new Date().getTime();
                hasPrepared(startTime - initTime);
                if (mController != null) {
                    mController.startCheckStuck();
                    mController.hide();
                }
                reSize();
            }
        });
        setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                eventList.add(new WatchEvent(WatchEvent.FINISH, getCurrentPosition()));
            }
        });
    }

    public CustomMediaController getMediaController() {
        return mController;
    }

    public void setMediaController(CustomMediaController controller) {
        if (mController != null) {
            mController.hide();
        }
        mController = controller;
        mController.setPlayer(this);
    }

    public View getParentView() {
        return parentView;
    }

    public void setParentView(View parentView) {
        this.parentView = parentView;
    }

    private void showHideController() {
        if (mController.isShowing()) {
            mController.hide();
        } else {
            mController.show();
        }
    }

    public void reSize() {
        if (isFullScreen) {
            parentView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            parentView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            parentView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            parentView.getLayoutParams().height = (int) (defaultHeight * densityRatio);
            getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mController.hide();
    }

    public void setVideo(Video video) {
        this.video = video;
        currentResolution = RES_HD;
        setVideoPath(video.getUri_hd());
        eventList.add(new WatchEvent(WatchEvent.RESOLUTION_HD, getCurrentPosition()));
    }

    @Override
    public void setVideoPath(String path) {
        initTime = Calendar.getInstance().getTimeInMillis();
        super.setVideoPath(path);
    }

    @Override
    public boolean isPlaying() {
        return super.isPlaying();
    }

    @Override
    public boolean isFullScreen() {
        return isFullScreen;
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return super.getCurrentPosition();
    }

    @Override
    public int getBufferPercentage() {
        return super.getBufferPercentage();
    }

    public void quit(int rating) {
        eventList.add(new WatchEvent(WatchEvent.QUIT, getCurrentPosition()));
        submit(rating);
    }

    /*提交数据*/
    public void submit(int rating) {
        try {
            JSONObject submitData = new JSONObject();
            //设备的IMEI作为用户ID
            TelephonyManager tm = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            String IMEI = tm.getDeviceId();
            submitData.put("user", IMEI);
            //评分
            submitData.put("rating", rating);
            //播放页面持续时间
            submitData.put("totalTime", new Date().getTime() - createTime);
            //获取电量信息和充电状态
            IntentFilter inFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = getContext().registerReceiver(null, inFilter);
            int batteryPct = 0;
            boolean isCharging = false;
            if (batteryStatus != null) {
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = (status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL);

                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                batteryPct = level * 100 / scale;
            }
            submitData.put("isCharging", isCharging);
            submitData.put("batteryPct", batteryPct);
            //事件和操作纪录
            JSONArray ja = new JSONArray();
            for (WatchEvent we : eventList) {
                ja.put(we);
            }
            submitData.put("events", ja.toString());

            /*------------------发送到服务器---------------------*/
            if(DataSender.send(submitData)){
                Toast.makeText(getContext(),"发送成功",Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getContext(),"发送失败",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hasPrepared(long waitTime) {
        eventList.add(new WatchEvent(WatchEvent.PREPARED, getCurrentPosition(), waitTime));
    }

    @Override
    public void pause() {
        pauseStartTime = new Date().getTime();
        if (getMediaController().bingStuck()) {
            eventList.add(new WatchEvent(WatchEvent.PAUSE_STUCK, getCurrentPosition()));
        } else {
            eventList.add(new WatchEvent(WatchEvent.PAUSE_NORMAL, getCurrentPosition()));
        }
        super.pause();
    }

    @Override
    public void start() {
        long duration = new Date().getTime() - pauseStartTime;
        eventList.add(new WatchEvent(WatchEvent.PLAY, getCurrentPosition(), duration));
        super.start();
    }

    @Override
    public boolean fullScreen() {
        isFullScreen = mActivity.makeFullScreen();
        reSize();
        eventList.add(new WatchEvent(WatchEvent.FULLSCREEN, getCurrentPosition()));
        return isFullScreen;
    }

    @Override
    public boolean exitFullScreen() {
        isFullScreen = !mActivity.exitFullScreen();
        reSize();
        eventList.add(new WatchEvent(WatchEvent.FULLSCREEN_EXIT, getCurrentPosition()));
        return !isFullScreen;
    }

    @Override
    public void seekTo(int newPosition) {
        long currentPosition = getCurrentPosition();
        if (currentPosition > newPosition) {
            eventList.add(new WatchEvent(WatchEvent.SEEK_LEFT, getCurrentPosition(), currentPosition - newPosition));
        } else if (currentPosition < newPosition) {
            eventList.add(new WatchEvent(WatchEvent.SEEK_RIGHR, getCurrentPosition(), newPosition - currentPosition));
        }
        super.seekTo(newPosition);
    }

    @Override
    public void onStuck(long stuckTime) {
        eventList.add(new WatchEvent(WatchEvent.STUCK, getCurrentPosition(), stuckTime));
    }

    @Override
    public void changeResolution(int resolution) {
        if (currentResolution == resolution) {
            return;
        }
        int now = getCurrentPosition();
        switch (resolution) {
            case RES_SD:
                setVideoPath(video.getUri_sd());
                eventList.add(new WatchEvent(WatchEvent.RESOLUTION_SD, now));
                break;
            case RES_HD:
                setVideoPath(video.getUri_hd());
                eventList.add(new WatchEvent(WatchEvent.RESOLUTION_HD, now));
                break;
            case RES_UHD:
                setVideoPath(video.getUri_uhd());
                eventList.add(new WatchEvent(WatchEvent.RESOLUTION_UHD, now));
                break;
            default:
                break;
        }
        currentResolution = resolution;
        super.seekTo(now);
    }

    public interface canFullScreen {
        boolean makeFullScreen();

        boolean exitFullScreen();
    }

}
