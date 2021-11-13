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
import com.distributed_systems.video_streaming.activity.MainActivity;
import com.distributed_systems.video_streaming.databinding.LayoutViewholderVideoListBinding;
import com.distributed_systems.video_streaming.model.Video;

import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<Video> data;
    public String channelName;


    public VideoListAdapter(Context context, List<Video> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_viewholder_video_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Video video = data.get(position);
        ViewHolder rowCell = (ViewHolder) holder;


        rowCell.binding.videoName.setText(video.name);
        rowCell.binding.hashtags.setText(video.hashtagsForVideo(video.name));

        rowCell.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MainActivity.class);
                i.putExtra("channel_name", channelName);
                i.putExtra("video_title", video.name.replace(" ", "_"));
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LayoutViewholderVideoListBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
