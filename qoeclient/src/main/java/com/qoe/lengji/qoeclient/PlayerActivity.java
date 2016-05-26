package com.qoe.lengji.qoeclient;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;



public class PlayerActivity extends Activity implements CustomVideoView.canFullScreen {

    private CustomVideoView mVideoView;
    private TextView textForScore;
    private RatingBar mRatingBar;
    private TextView titleView;
    private TextView descrView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initVideoView();
        playVideo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        mVideoView.quit((int)mRatingBar.getRating());
        super.onDestroy();
    }

    private void initVideoView() {
        titleView = (TextView) findViewById(R.id.video_title);
        descrView = (TextView) findViewById(R.id.video_description);
        textForScore = (TextView) findViewById(R.id.text_score);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);

        mVideoView = (CustomVideoView) findViewById(R.id.videoview);
        mVideoView.setParentView(findViewById(R.id.videoviewContainer));
        mVideoView.setMediaController(new CustomMediaController(this));
    }

    private void playVideo() {
        Intent intent = getIntent();
        Video video = (Video) intent.getSerializableExtra("video");
        titleView.setText(video.getTitle());
        descrView.setText(video.getDescription());
        mVideoView.setVideo(video);
        mVideoView.requestFocus();
    }

    @Override
    public boolean makeFullScreen() {
        titleView.setVisibility(View.GONE);
        descrView.setVisibility(View.GONE);
        textForScore.setVisibility(View.GONE);
        mRatingBar.setVisibility(View.GONE);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setAttributes(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return true;
    }

    @Override
    public boolean exitFullScreen() {
        titleView.setVisibility(View.VISIBLE);
        descrView.setVisibility(View.VISIBLE);
        mRatingBar.setVisibility(View.VISIBLE);
        textForScore.setVisibility(View.VISIBLE);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setAttributes(params);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVideoView.reSize();
    }

    @Override
    public void onBackPressed() {
        if (mVideoView != null && mVideoView.isFullScreen()) {
            mVideoView.exitFullScreen();
            mVideoView.getMediaController().updateFullScreen();
            return;
        }
        super.onBackPressed();
    }

}
