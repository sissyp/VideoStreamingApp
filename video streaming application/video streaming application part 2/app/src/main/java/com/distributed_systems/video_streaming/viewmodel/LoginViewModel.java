package com.distributed_systems.video_streaming.viewmodel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.distributed_systems.video_streaming.network.Constants;


public class LoginViewModel extends ViewModel {

    private MutableLiveData<Boolean> loginSucceeded;
    private MutableLiveData<Boolean> loginFailed;

    public LoginViewModel(){
        loginSucceeded = new MutableLiveData<>();
        loginFailed = new MutableLiveData<>();
    }


    public void onClickSignIn(String username, String password){
        if(username.equals(Constants.USERNAME) && password.equals(Constants.PASSWORD)){
            loginSucceeded.setValue(true);
        }else{
            loginFailed.setValue(true);
        }
    }


    public LiveData<Boolean> getLoginSucceeded() {
        return loginSucceeded;
    }

    public LiveData<Boolean> getLoginFailed() {
        return loginFailed;
    }
}
