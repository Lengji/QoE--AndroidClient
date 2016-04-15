package com.qoe.lengji.qoeclient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

public class CustomMediaController extends PopupWindow {

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int defaultTimeout = 3000;
    private static final int stuckCheckTime = 500;
    private static final int controllerHeight = 40;  // 控制器的高度，使用时要乘以densityRatio
    private static final float densityRatio = 2.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）

    private boolean isDragging = false;
    private boolean checkingStuck = false;
    private boolean beingStuck = false;
    private int lastPosition = 0;
    private long stuckTime = 0;

    private ImageButton action_pause = null;
    private ImageButton action_fullscreen = null;
    private SeekBar seekbar = null;
    private TextView textView_playTime = null;
    private TextView textView_duration = null;
    private TextView action_change_resolution = null;

    private CustomMediaPlayerControl mediaControl;

    private final Handler showHideHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    int pos = setProgress();
                    if (!isDragging && isShowing() && mediaControl.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    };

    private final Handler stuckHandler = new Handler();
    Runnable onceCheckStuck = new Runnable() {
        @Override
        public void run() {
            if (checkingStuck && mediaControl.isPlaying()) {
                if (lastPosition == mediaControl.getCurrentPosition()) {
                    stuckTime += stuckCheckTime;
                    beingStuck = true;
                } else if (beingStuck) {
                    beingStuck = false;
                    mediaControl.onStuck(stuckTime);
                    stuckTime = 0;
                }
                lastPosition = mediaControl.getCurrentPosition();
                stuckHandler.postDelayed(onceCheckStuck, stuckCheckTime);
            }
        }
    };

    public CustomMediaController(Context context) {
        super(context);
        initStatus();
        initViews(context);
        setActions();
    }

    private void initStatus() {
        beingStuck = false;
        checkingStuck = false;
        isDragging = false;
    }

    private void initViews(Context context) {
        View controllerView = View.inflate(context, R.layout.media_controller, null);
        setContentView(controllerView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(getHeight());
        setFocusable(true);

        action_pause = (ImageButton) controllerView.findViewById(R.id.control_pause);
        action_fullscreen = (ImageButton) controllerView.findViewById(R.id.control_fullscreen);
        seekbar = (SeekBar) controllerView.findViewById(R.id.control_seekbar);
        seekbar.setMax(1000);
        textView_playTime = (TextView) controllerView.findViewById(R.id.current_time);
        textView_duration = (TextView) controllerView.findViewById(R.id.end_time);
        action_change_resolution = (TextView) controllerView.findViewById(R.id.control_resolution);
    }

    private void setActions() {
        action_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaControl == null) {
                    return;
                }
                if (mediaControl.isPlaying()) {
                    mediaControl.pause();
                    stopCheckStuck();
                } else {
                    mediaControl.start();
                    startCheckStuck();
                }
                updatePlayPause();
                show();
            }
        });

        action_fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
                if (mediaControl == null) {
                    return;
                }
                if (mediaControl.isFullScreen() && mediaControl.exitFullScreen()) {
                    action_fullscreen.setImageResource(R.drawable.media_fullscreen);
                } else if (!mediaControl.isFullScreen() && mediaControl.fullScreen()) {
                    action_fullscreen.setImageResource(R.drawable.media_fullscreen_exit);
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long newPosition = mediaControl.getDuration() * progress / 1000;
                    mediaControl.seekTo((int) newPosition);
                    textView_playTime.setText(stringForTime((int) newPosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                show(60000);
                isDragging = true;
                showHideHandler.removeMessages(SHOW_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragging = false;
                setProgress();
                show();
            }
        });

        action_change_resolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaControl != null) {
                    //    mediaControl.changeResolution();
                }
            }
        });

    }

    private int setProgress() {
        if (mediaControl == null || isDragging) {
            return 0;
        }
        int position = mediaControl.getCurrentPosition();
        int duration = mediaControl.getDuration();
        if (duration > 0) {
            long pos = 1000L * position / duration;
            seekbar.setProgress((int) pos);
        }
        int percent = mediaControl.getBufferPercentage();
        seekbar.setSecondaryProgress(percent * 10);
        textView_duration.setText(stringForTime(duration));
        textView_playTime.setText(stringForTime(position));
        return position;
    }

    private String stringForTime(int time) {
        int totalSeconds = time / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    public void updatePlayPause() {
        if (mediaControl == null) {
            return;
        }
        if (mediaControl.isPlaying()) {
            action_pause.setImageResource(R.drawable.media_pause);
        } else {
            action_pause.setImageResource(R.drawable.media_play);
        }
    }

    public void hide() {
        if (isShowing()) {
            dismiss();
        }
        showHideHandler.removeMessages(SHOW_PROGRESS);
    }

    public void show() {
        show(defaultTimeout);
    }

    public void show(int timeout) {
        showAsDropDown(((CustomVideoView) mediaControl).getParentView(), 0, -getHeight());
        updatePlayPause();
        showHideHandler.sendEmptyMessage(SHOW_PROGRESS);
        showHideHandler.removeMessages(FADE_OUT);
        if (mediaControl.isPlaying()) {
            showHideHandler.sendEmptyMessageDelayed(FADE_OUT, timeout);
        }
    }

    public int getHeight() {
        return (int) (controllerHeight * densityRatio);
    }

    public void setPlayer(CustomMediaPlayerControl control) {
        mediaControl = control;
    }

    public void startCheckStuck() {
        checkingStuck = true;
        if (!beingStuck) {
            stuckTime = 0;
        }
        lastPosition = mediaControl.getCurrentPosition();
        stuckHandler.postDelayed(onceCheckStuck, stuckCheckTime);
    }

    public void stopCheckStuck() {
        checkingStuck = false;
    }

    public interface CustomMediaPlayerControl {
        boolean isPlaying();

        void pause();

        void start();

        boolean isFullScreen();

        boolean fullScreen();

        boolean exitFullScreen();

        int getDuration();

        int getCurrentPosition();

        int getBufferPercentage();

        void seekTo(int newPosition);

        void changeResolution(int resolution);

        void onStuck(long stuckTime);

    }

}
