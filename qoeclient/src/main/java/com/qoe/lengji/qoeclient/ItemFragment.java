package com.qoe.lengji.qoeclient;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.ArrayList;

public class ItemFragment extends Fragment {

    private ArrayList<Video> videos = null;
    private ListView listview = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        int videoType = bundle.getInt("type");
        videos = VideoManager.getVideoList(videoType);
        if(videos.isEmpty()){
            Toast.makeText(container.getContext(),"服务器忙或未添加视频",Toast.LENGTH_SHORT).show();
        }

        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        listview = (ListView) view.findViewById(R.id.videolist);
        listview.setAdapter(new VideolistAdapter());
        listview.setOnItemClickListener(new VideoItemOnclickListener());
        return view;
    }

    private class VideoItemOnclickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            Video video = (Video) listview.getItemAtPosition(position);
            intent.putExtra("video",video);
            startActivity(intent);
        }
    }

    private class VideolistAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return videos.size();
        }

        @Override
        public Object getItem(int position) {
            return videos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v;
            Video video = videos.get(position);
            if (convertView == null) {
                v = View.inflate(getActivity(), R.layout.fragment_item, null);
                TextView titleView = (TextView) v.findViewById(R.id.video_title);
                titleView.setText(video.getTitle());
                TextView descriptionView = (TextView) v.findViewById(R.id.video_description);
                descriptionView.setText(video.getDescription());
                SmartImageView coverView = (SmartImageView) v.findViewById(R.id.video_cover);
                coverView.setImageUrl(video.getUri_cover(),R.drawable.playvideo);
            } else {
                v = convertView;
            }

            return v;
        }

    }

}
