package com.distributed_systems.video_streaming.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;


import com.distributed_systems.video_streaming.R;
import com.distributed_systems.video_streaming.databinding.ActivityMainBinding;
import com.distributed_systems.video_streaming.fragment.ChannelListFragment;
import com.distributed_systems.video_streaming.fragment.SearchFragment;
import com.distributed_systems.video_streaming.fragment.UploadVideoFragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.tabs.selectTab(binding.tabs.getTabs().get(0), false);

        ChannelListFragment fragment = new ChannelListFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragments, fragment, "channels").commit();

        binding.tabs.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int tab_number, @NotNull AnimatedBottomBar.Tab tab1) {
                if (tab_number == 0) {
                    ChannelListFragment channel_fragment = new ChannelListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, channel_fragment, "channels").commit();
                }
                else if (tab_number == 1) {
                    SearchFragment search_fragment = new SearchFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, search_fragment, "channels").commit();
                }
                else {
                    UploadVideoFragment upload_video_fragment = new UploadVideoFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragments, upload_video_fragment, "channels").commit();
                }
            }

            @Override
            public void onTabReselected(int i, @NotNull AnimatedBottomBar.Tab tab) {

            }
        });

    }
}