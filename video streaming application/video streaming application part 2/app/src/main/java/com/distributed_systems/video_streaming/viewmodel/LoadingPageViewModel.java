package com.distributed_systems.video_streaming.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.distributed_systems.video_streaming.network.Constants;
import com.distributed_systems.video_streaming.util.InfoHandler;
import com.distributed_systems.video_streaming.util.UserStorage;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class LoadingPageViewModel extends ViewModel {

    public Context context;
    private MutableLiveData<Boolean> communicationWithServerFailed;
    private MutableLiveData<Boolean> wrongData;
    private MutableLiveData<Boolean> processCompletedSuccessfully;
    private UserStorage userStorage;

    public LoadingPageViewModel() {
        wrongData = new MutableLiveData<>();
        communicationWithServerFailed = new MutableLiveData<>();
        processCompletedSuccessfully = new MutableLiveData<>();
    }

    public void connectBroker(){
        try{
            new Thread( new Runnable() {
                @Override
                public void run() {
                    // IN BACKGROUND THREAD to avoid blocking the UI
                    try{
                        // COMMUNICATION with broker
                        InfoHandler infoHandler = InfoHandler.getInstance(context);
                        JSONObject response = infoHandler.getInfo();
                        userStorage = new UserStorage(context);
                        userStorage.setString(Constants.BROKER_RESPONSE, response.toString());
                        processCompletedSuccessfully.postValue(true);
                    }
                    catch (Exception e){
                        communicationWithServerFailed.postValue(true);
                    }
                }} ).start();

        }
        catch (Exception e){
            communicationWithServerFailed.setValue(true);
        }
    }

    public LiveData<Boolean> getCommunicationWithServerFailed() {
        return communicationWithServerFailed;
    }

    public LiveData<Boolean> getWrongData() {
        return wrongData;
    }

    public LiveData<Boolean> getProcessCompletedSuccessfully() {
        return processCompletedSuccessfully;
    }
}
