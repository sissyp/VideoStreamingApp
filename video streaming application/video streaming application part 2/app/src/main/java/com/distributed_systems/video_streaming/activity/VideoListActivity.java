package com.distributed_systems.video_streaming.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.distributed_systems.video_streaming.R;
import com.distributed_systems.video_streaming.adapter.VideoListAdapter;
import com.distributed_systems.video_streaming.databinding.ActivityVideoListBinding;
import com.distributed_systems.video_streaming.model.ChannelName;
import com.distributed_systems.video_streaming.model.Video;
import com.distributed_systems.video_streaming.viewmodel.VideoListViewModel;


import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    ActivityVideoListBinding binding;
    VideoListViewModel viewModel;

    LiveData<List<Video>> videoListChanged;
    LiveData<ChannelName> publisherInfoChanged;
    LiveData<Boolean> unknownPublisher;
    LiveData<Boolean> errorOccurred;

    VideoListAdapter videoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_list);


        viewModel = ViewModelProviders.of(this).get(VideoListViewModel.class);
        viewModel.context = this;

        binding.videoRv.setLayoutManager(new LinearLayoutManager(this));


        videoListChanged = viewModel.getVideoListChanged();
        publisherInfoChanged = viewModel.getPublisherInfoChanged();
        errorOccurred = viewModel.getErrorOccurred();
        unknownPublisher = viewModel.getUnknownPublisher();


        videoListChanged.observe(this, new Observer<List<Video>>() {
            @Override
            public void onChanged(List<Video> videos) {
                if (videos == null) return;
                videoListAdapter = new VideoListAdapter(VideoListActivity.this, videos);
                binding.videoRv.setAdapter(videoListAdapter);
                binding.loader.setVisibility(View.GONE);
            }
        });
        publisherInfoChanged.observe(this, new Observer<ChannelName>() {
            @Override
            public void onChanged(ChannelName channel_name) {
                if(channel_name == null)return;
                binding.channelName.setText(channel_name.name);
                videoListAdapter.channelName = channel_name.name;
            }
        });
        errorOccurred.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (!b)return;
                Toast.makeText(VideoListActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
        unknownPublisher.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if(!b)return;
                Toast.makeText(VideoListActivity.this, "Unknown channel name", Toast.LENGTH_SHORT).show();
            }
        });


        binding.backArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        handleIntent();
        viewModel.fetchVideos();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        if(intent == null) return;
        String channel_name = intent.getStringExtra("channel_name");
        viewModel.setChannelName(channel_name);
    }


}
