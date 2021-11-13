package com.distributed_systems.video_streaming.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.distributed_systems.video_streaming.R;
import com.distributed_systems.video_streaming.activity.VideoListActivity;
import com.distributed_systems.video_streaming.databinding.LayoutViewholderChannelListBinding;
import com.distributed_systems.video_streaming.model.PublishersInfo;


import java.util.List;

public class ChannelListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<PublishersInfo> data;
    Context context;

    public ChannelListAdapter(List<PublishersInfo> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_viewholder_channel_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        final PublishersInfo publishersInfo = data.get(position);
        ViewHolder rowCell = (ViewHolder) holder;

        rowCell.binding.channelName.setText(publishersInfo.channelName);

        rowCell.binding.rootLayoutCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, VideoListActivity.class);
                i.putExtra("channel_name", publishersInfo.channelName);

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LayoutViewholderChannelListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
