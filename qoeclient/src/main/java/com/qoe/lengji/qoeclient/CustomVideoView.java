package com.qoe.lengji.qoeclient;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.util.Calendar;

public class CustomVideoView extends VideoView  implements CustomMediaController.CustomMediaPlayerControl{

    private View parentView = null; //父视图，为了确保视频画面处于在屏幕的水平中央，并且控制条是依附在夫视图上的，以保证画面横向未填满屏幕时控制块仍能顶满屏幕。
    private CustomMediaController mController = null;
    private boolean isFullScreen = false;
    private canFullScreen mActivity = null;
    private static final float densityRatio = 2.0f; // 密度比值系数（密度比值：一英寸中像素点除以160）

    public CustomVideoView(Context context) {
        super(context);
        this.mActivity = (canFullScreen)context;
        init();
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()){
            this.mActivity = (canFullScreen)context;
        }
        init();
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mActivity = (canFullScreen)context;
        init();
    }

    private void init() {
        final long initTime = Calendar.getInstance().getTimeInMillis();
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
                long startTime = Calendar.getInstance().getTimeInMillis();
                hasPrepared(startTime - initTime);
                if (mController != null) {
                    mController.startCheckStuck();
                    mController.hide();
                }
                reSize();
            }
        });
    }

    public CustomMediaController getMediaController(){
        return mController;
    }

    public void setMediaController(CustomMediaController controller){
        if(mController!=null){
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
        if(mController.isShowing()){
            mController.hide();
        }else{
            mController.show();
        }
    }

    public void reSize(){
        if(isFullScreen){
            parentView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            parentView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }else{
            parentView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            parentView.getLayoutParams().height =(int) (270 * densityRatio);
            getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        mController.hide();
    }

    public void hasPrepared( long waitTime) {
        Log.i("prepared time",String.valueOf(waitTime));
    }

    @Override
    public boolean isPlaying(){
        return super.isPlaying();
    }

    @Override
    public void pause(){
        super.pause();
    }

    @Override
    public void start(){
        super.start();
    }

    @Override
    public boolean isFullScreen(){
        return isFullScreen;
    }

    @Override
    public boolean fullScreen(){
        isFullScreen = mActivity.makeFullScreen();
        reSize();
        return isFullScreen;
    }

    @Override
    public boolean exitFullScreen(){
        isFullScreen = !mActivity.exitFullScreen();
        reSize();
        return !isFullScreen;
    }

    @Override
    public int getDuration(){
        return super.getDuration();
    }

    @Override
    public int getCurrentPosition(){
        return super.getCurrentPosition();
    }

    @Override
    public int getBufferPercentage(){
        return super.getBufferPercentage();
    }

    @Override
    public void seekTo(int newPosition){
        super.seekTo(newPosition);
    }

    @Override
    public void changeResolution(int resolution){

    }

    @Override
    public void onStuck(long stuckTime){
        Log.i("Stuck Time:", String.valueOf(stuckTime));

    }

    public interface canFullScreen{
        boolean makeFullScreen();
        boolean exitFullScreen();
    }
}
