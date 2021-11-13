package com.distributed_systems.video_streaming.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.distributed_systems.video_streaming.R;

import com.distributed_systems.video_streaming.databinding.ActivityLoadingPageBinding;
import com.distributed_systems.video_streaming.viewmodel.LoadingPageViewModel;

import java.io.IOException;


public class LoadingPageActivity extends AppCompatActivity {

    ActivityLoadingPageBinding binding;
    LoadingPageViewModel viewModel;

    LiveData<Boolean> communicationWithServerFailed;
    LiveData<Boolean> wrongData;
    LiveData<Boolean> processCompletedSuccessfully;

    long userTime;
    public static final long WAIT_FOR_BROKER = 2300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading_page);

        viewModel = ViewModelProviders.of(this).get(LoadingPageViewModel.class);

        communicationWithServerFailed = viewModel.getCommunicationWithServerFailed();
        wrongData = viewModel.getWrongData();
        processCompletedSuccessfully = viewModel.getProcessCompletedSuccessfully();
        viewModel.context = getApplicationContext();

        Log.d("create","initialization");

        communicationWithServerFailed.observe(this , new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if(bool) {
                    Toast.makeText(LoadingPageActivity.this, "Communication with server failed", Toast.LENGTH_LONG).show();
                    Log.d("communication_error","error communicating with server");
                }
            }
        });
        wrongData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if(bool){
                    Toast.makeText(LoadingPageActivity.this, "wrong data given from the server", Toast.LENGTH_LONG).show();
                    Log.d("unknown response","unknown response");
                }
            }
        });
        processCompletedSuccessfully.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if(bool){
                    proceedOnLogin();
                    Log.d("login","success");
                }
            }
        });
    }


    public void proceedOnLogin(){

        long time_difference = System.currentTimeMillis() - userTime;

        if(time_difference >= WAIT_FOR_BROKER ){
            startActivity(new Intent(LoadingPageActivity.this, LoginActivity.class));
            finish();
        }
        else{
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(LoadingPageActivity.this, LoginActivity.class));
                    finish();
                }
            }, WAIT_FOR_BROKER - time_difference);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        userTime = System.currentTimeMillis();
        Log.d("connect","connect with broker");
        viewModel.connectBroker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wrongData.removeObservers(this);
        communicationWithServerFailed.removeObservers(this);
        processCompletedSuccessfully.removeObservers(this);
    }
}
