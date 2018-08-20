package com.mredrock.cyxbs.summer.ui.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;
import com.mredrock.cyxbs.summer.R;
import com.mredrock.cyxbs.summer.base.BaseActivity;
import com.mredrock.cyxbs.summer.databinding.ActivityRegisterBinding;
import com.mredrock.cyxbs.summer.utils.ActivityManager;
import com.mredrock.cyxbs.summer.utils.DensityUtils;
import com.mredrock.cyxbs.summer.utils.Toasts;

public class RegisterActivity extends BaseActivity {
    public static final String TAG = "Register";

    private String tx_account;
    private String tx_password;
    private String tx_email;
    private ActivityRegisterBinding binding;

    private AVUser user = new AVUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_register);
        DensityUtils.setTransparent(binding.summerRegisterTl,this);
        initListener();
    }


    private void initListener(){
        binding.registerToLogin.setOnClickListener(v -> RegisterActivity.this.finish());
        binding.registerAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tx_account = s.toString();
                user.setUsername(tx_account);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.registerPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tx_password = s.toString();
                user.setPassword(tx_password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.registerEma.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tx_email = s.toString();
                user.setEmail(tx_email);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.registerCommit.setOnClickListener(v -> Register());
    }

    private void Register(){
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                    ActivityManager.getInstance().finishAllActivity();
                    overridePendingTransition(R.anim.out_to_top,R.anim.in_from_bottm);
                }else{
                    Toasts.show(e.getMessage());
                    if(binding.registerAccount.getText().length()==0){
                        binding.registerAccount.setError("用户名不能为空");
                    }
                    if(binding.registerPassword.getText().length()==0){
                        binding.registerPassword.setError("密码不能为空");
                    }
                    if(binding.registerEma.getText().length()==0){
                        binding.registerEma.setError("邮箱不能为空");
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.out_to_right,R.anim.in_from_left);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
    }
}
