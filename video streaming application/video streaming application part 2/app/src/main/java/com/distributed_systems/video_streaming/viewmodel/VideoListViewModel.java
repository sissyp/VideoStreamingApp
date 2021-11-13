package com.distributed_systems.video_streaming.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.distributed_systems.video_streaming.model.ChannelName;
import com.distributed_systems.video_streaming.model.Video;
import com.distributed_systems.video_streaming.network.Constants;
import com.distributed_systems.video_streaming.util.UserStorage;

import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoListViewModel extends ViewModel {

    private MutableLiveData<ChannelName> publisherInfoChanged;
    private MutableLiveData<List<Video>> videoListChanged;
    private MutableLiveData<Boolean> unknownPublisher;
    private MutableLiveData<Boolean> errorOccurred;

    public Context context;
    private UserStorage offlineStorage;
    private String channel_name;

    public VideoListViewModel() {
        this.publisherInfoChanged= new MutableLiveData<>();
        this.videoListChanged = new MutableLiveData<>();
        this.unknownPublisher = new MutableLiveData<>();
        this.errorOccurred = new MutableLiveData<>();
    }


    public void fetchVideos(){
        if(channel_name == null) return;

        new Thread( new Runnable() {
            @Override
            public void run() {
                // IN BACKGROUND THREAD to avoid blocking the UI

                if(offlineStorage == null) offlineStorage = new UserStorage(context);

                String broker_info = offlineStorage.getString(Constants.BROKER_RESPONSE);
                ObjectOutputStream output = null;
                ObjectInputStream input = null;

                try {

                    JSONObject jsonObject = new JSONObject(broker_info);
                    String channel_name1 = jsonObject.getString("channel_name1");
                    String channel_name2 = jsonObject.getString("channel_name2");
                    String ip;
                    int broker_port;

                    if (channel_name1.equals(channel_name)){
                        ip = jsonObject.getString("ip1");
                        broker_port = jsonObject.getInt("broker_port1");
                    }
                    else {
                        ip = jsonObject.getString("ip2");
                        broker_port = jsonObject.getInt("broker_port2");
                    }

                    Socket socket = new Socket(ip, broker_port);
                    output = new ObjectOutputStream(socket.getOutputStream());
                    input = new ObjectInputStream(socket.getInputStream());
                    List<Video> videoList = new ArrayList<Video>();
                    ArrayList<String> videos = (ArrayList<String>)input.readObject();
                    HashMap<String,ArrayList<String>> topicsForVideo = (HashMap<String, ArrayList<String>>) input.readObject();
                    for(String v: videos){
                        for (Map.Entry<String, ArrayList<String>> entry : topicsForVideo.entrySet()) {
                            if(entry.getKey().equals(v))
                                videoList.add(new Video(v,entry.getValue()));
                        }
                    }
                    videoListChanged.postValue(videoList);

                } catch (Exception e) {
                    errorOccurred.postValue(true);
                }

            }}).start();


    }

    public void setChannelName(String name){
        if(this.channel_name != null) return;
        this.channel_name = name;
    }

    public LiveData<List<Video>> getVideoListChanged() {
        return videoListChanged;
    }

    public LiveData<Boolean> getUnknownPublisher() {
        return unknownPublisher;
    }

    public LiveData<ChannelName> getPublisherInfoChanged() {
        return publisherInfoChanged;
    }

    public LiveData<Boolean> getErrorOccurred() {
        return errorOccurred;
    }
}
