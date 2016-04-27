package com.qoe.lengji.qoeclient;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.net.URI;
import java.net.URL;


public class PlayerActivity extends Activity implements CustomVideoView.canFullScreen {

    private CustomVideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        initVideoView();
        playVideo();
    }

    private void initVideoView() {
        mVideoView = (CustomVideoView) findViewById(R.id.videoview);
        mVideoView.setParentView(findViewById(R.id.videoviewContainer));
        mVideoView.setMediaController(new CustomMediaController(this));
    }

    private void playVideo() {
        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(("Uri"));
        String titlr = intent.getStringExtra("Titlr");
        String detail = intent.getStringExtra("Detail");

        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();

    }

    @Override
    public boolean makeFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(params);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        return true;
    }

    @Override
    public boolean exitFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        return true;
    }

    @Override
    public void onConfigurationChanged (Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mVideoView.reSize();
    }

    @Override
    public void onBackPressed() {
        if(mVideoView!=null && mVideoView.isFullScreen()){
            mVideoView.exitFullScreen();
            mVideoView.getMediaController().updateFullScreen();
            return;
        }
        super.onBackPressed();
    }

}
