package com.distributed_systems.video_streaming.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.distributed_systems.video_streaming.model.PublishersInfo;
import com.distributed_systems.video_streaming.network.Constants;
import com.distributed_systems.video_streaming.util.UserStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChannelListViewModel extends ViewModel {

    public Context context;
    private UserStorage storage;
    private MutableLiveData<List<PublishersInfo>> publisherList;


    public ChannelListViewModel(){
        publisherList = new MutableLiveData<>();
    }


    public void fetchChannelNames(){
        if(storage==null) storage = new UserStorage(context);
        try{
            new Thread( new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1500); // Simulate delay
                        String cached = storage.getString(Constants.BROKER_RESPONSE);
                        publisherList.postValue(parseResponse(cached));
                    }catch (Exception e){

                    }
                }} ).start();

        }catch (Exception e){

        }
    }

    List<PublishersInfo> parseResponse(String cached){
        if(cached.isEmpty()) return null;
        ArrayList<PublishersInfo> result = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(cached);
            result = PublishersInfo.parseIt(json);

        }
        catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return result;

    }


    public LiveData<List<PublishersInfo>> getChannelsList() {
        return publisherList;
    }
}
