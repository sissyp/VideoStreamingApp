package com.distributed_systems.video_streaming.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.distributed_systems.video_streaming.R;
import com.distributed_systems.video_streaming.databinding.ActivityLoginBinding;
import com.distributed_systems.video_streaming.viewmodel.LoginViewModel;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    LoginViewModel viewModel;

    LiveData<Boolean> loginSucceeded;
    LiveData<Boolean> loginFailed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        loginSucceeded = viewModel.getLoginSucceeded();
        loginFailed = viewModel.getLoginFailed();

        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.usernameEdittext.getText().toString();
                String password = binding.passwordEdittext.getText().toString();
                viewModel.onClickSignIn(username, password);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginSucceeded.observe(this , new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if(bool)
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();

            }
        });
        loginFailed.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean bool) {
                if(bool)
                    Toast.makeText(LoginActivity.this, "Wrong data given", Toast.LENGTH_LONG).show();
            }
        });
    }


}