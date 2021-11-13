package com.distributed_systems.video_streaming.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.distributed_systems.video_streaming.R;
import com.distributed_systems.video_streaming.adapter.ChannelListAdapter;
import com.distributed_systems.video_streaming.databinding.FragmentChannelListBinding;
import com.distributed_systems.video_streaming.model.PublishersInfo;
import com.distributed_systems.video_streaming.viewmodel.ChannelListViewModel;


import java.util.List;


public class ChannelListFragment extends Fragment {

    private FragmentChannelListBinding binding;
    private ChannelListViewModel viewModel;
    private ChannelListAdapter adapter;

    private LiveData<List<PublishersInfo>> publisherList;


    public ChannelListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel_list, container, false);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ChannelListViewModel.class);
        viewModel.context = getActivity();
        publisherList = viewModel.getChannelsList();

        binding.channelListRv.setLayoutManager(new LinearLayoutManager(getActivity()));

        publisherList.observe(getViewLifecycleOwner(), new Observer<List<PublishersInfo>>() {
            @Override
            public void onChanged(List<PublishersInfo> publishersInfo) {
                if (publishersInfo == null) return;

                binding.loader.setVisibility(View.GONE);

                adapter = new ChannelListAdapter(publishersInfo, getActivity());
                binding.channelListRv.setAdapter(adapter);

            }
        });

        viewModel.fetchChannelNames();
    }
}
