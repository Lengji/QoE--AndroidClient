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

import java.util.Date;

public class CustomMediaController extends PopupWindow {

    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int defaultTimeout = 5000;
    private static final int controllerHeight = 40;  // 控制器的高度，使用时要乘以densityRatio
    private static final float densityRatio = 2.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）
    private static final int selectorWidth = 60;
    private static final int selectorItemHeight = 40;
    private static final int selectorItemCount = 3;

    private boolean isDragging = false;
    private boolean beingStuck = false;
    private long stuckStartTime = 0;

    private ImageButton action_pause = null;
    private ImageButton action_fullscreen = null;
    private SeekBar seekbar = null;
    private TextView textView_playTime = null;
    private TextView textView_duration = null;
    private TextView action_change_resolution = null;

    private PopupWindow resolutionSelector = null;
    private TextView resolution_sd = null;
    private TextView resolution_hd = null;
    private TextView resolution_uhd = null;

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

    private CustomMediaPlayerControl mediaControl = null;

    public CustomMediaController(Context context) {
        super(context);
        initStatus();
        initViews(context);
        setActions();
    }

    private void initStatus() {
        beingStuck = false;
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

        View selectorView = View.inflate(context, R.layout.resolution_selector, null);
        resolutionSelector = new PopupWindow(selectorView, getSelectorWidth(), getSelectorHeight(), false);
        resolution_sd = (TextView) selectorView.findViewById(R.id.resolution_sd);
        resolution_hd = (TextView) selectorView.findViewById(R.id.resolution_hd);
        resolution_uhd = (TextView) selectorView.findViewById(R.id.resolution_uhd);
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
                } else {
                    mediaControl.start();
                }
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
                    updateFullScreen();
                } else if (!mediaControl.isFullScreen() && mediaControl.fullScreen()) {
                    updateFullScreen();
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    long newPosition = mediaControl.getDuration() * progress / 1000;
                    mediaControl.seekToPosition((int) newPosition);
                    textView_playTime.setText(stringForTime((int) newPosition));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragging = true;
                show();
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
                    if (resolutionSelector.isShowing()) {
                        hideResolutionSelector();
                    } else {
                        showResolutionSelector();
                    }
                }
            }
        });

        resolution_sd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideResolutionSelector();
                mediaControl.changeResolution(1);
                action_change_resolution.setText("流畅");
            }
        });
        resolution_hd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideResolutionSelector();
                mediaControl.changeResolution(2);
                action_change_resolution.setText("清晰");
            }
        });
        resolution_uhd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideResolutionSelector();
                mediaControl.changeResolution(3);
                action_change_resolution.setText("高清");
            }
        });

    }

    public void updateFullScreen() {
        if (mediaControl.isFullScreen()) {
            action_fullscreen.setImageResource(R.drawable.media_fullscreen);
        } else {
            action_fullscreen.setImageResource(R.drawable.media_fullscreen_exit);
        }
    }

    private int setProgress() {
        if (mediaControl == null || isDragging) {
            return 0;
        }
        int position = mediaControl.getCurrentPosition();
        int duration = mediaControl.getDuration();
        if (duration > 0) {
            long pct = 1000L * position / duration;
            seekbar.setProgress((int) pct);
        }
        int bufferPct = mediaControl.getBufferPercentage();
        seekbar.setSecondaryProgress(bufferPct * 10);
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
            return String.format("%d:%02d:%02d".toLowerCase(), hours, minutes, seconds);
        }
        return String.format("%02d:%02d".toLowerCase(), minutes, seconds);
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

    @Override
    public void dismiss() {
        super.dismiss();
        resolutionSelector.dismiss();
    }

    public void hide() {
        dismiss();
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
        if (!isDragging && !resolutionSelector.isShowing() && mediaControl.isPlaying()) {
            showHideHandler.sendEmptyMessageDelayed(FADE_OUT, timeout);
        }
    }

    public void hideResolutionSelector() {
        resolutionSelector.dismiss();
        show();
    }

    public void showResolutionSelector() {
        int[] location = new int[2];
        action_change_resolution.getLocationInWindow(location);
        int widthMove = (getSelectorWidth() - action_change_resolution.getWidth()) / 2;
        int heightMove = getHeight() + getSelectorHeight();
        resolutionSelector.showAsDropDown(((CustomVideoView) mediaControl).getParentView(),
                location[0] - widthMove, location[1] - heightMove);
        show();
    }

    public void startStuck() {
        beingStuck = true;
        stuckStartTime = new Date().getTime();
    }

    public void stopStuck() {
        beingStuck = false;
        long stuckEndTime = new Date().getTime();
        mediaControl.onStuck(stuckEndTime - stuckStartTime);
    }

    public boolean bingStuck() {
        return beingStuck;
    }

    public int getHeight() {
        return (int) (controllerHeight * densityRatio);
    }

    private int getSelectorWidth() {
        return selectorWidth * (int) densityRatio;
    }

    private int getSelectorHeight() {
        return selectorItemCount * selectorItemHeight * (int) densityRatio;
    }

    public void setPlayer(CustomMediaPlayerControl control) {
        mediaControl = control;
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

        void seekToPosition(int newPosition);

        void changeResolution(int resolution);

        void onStuck(long stuckTime);

    }

}
