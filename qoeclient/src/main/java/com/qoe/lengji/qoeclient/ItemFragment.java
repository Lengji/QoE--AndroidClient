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

import java.util.ArrayList;


public class ItemFragment extends Fragment {

    private int videoType = 0;
    private ArrayList<Video> videos = new ArrayList<Video>();
    private ListView listview = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Video v1 = new Video("Test", "http://devimages.apple.com/iphone/samples/bipbop/gear1/prog_index.m3u8", "Details");
        videos.add(v1);
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
            intent.putExtra("Title", video.getTitle());
            intent.putExtra("Url", video.getUrl());
            intent.putExtra("Detail", video.getDetail());
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
                TextView detailView = (TextView) v.findViewById(R.id.video_details);
                detailView.setText(video.getDetail());
            } else {
                v = convertView;
            }

            return v;
        }

    }

}
