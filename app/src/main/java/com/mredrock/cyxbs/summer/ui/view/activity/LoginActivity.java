package com.mredrock.cyxbs.summer.ui.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.mredrock.cyxbs.summer.R;
import com.mredrock.cyxbs.summer.base.BaseActivity;
import com.mredrock.cyxbs.summer.databinding.ActivityLoginBinding;
import com.mredrock.cyxbs.summer.utils.ActivityManager;
import com.mredrock.cyxbs.summer.utils.DensityUtils;
import com.mredrock.cyxbs.summer.utils.DialogBuilder;
import com.mredrock.cyxbs.summer.utils.SPHelper;
import com.mredrock.cyxbs.summer.utils.Toasts;

public class LoginActivity extends BaseActivity {

    private String tx_account;
    private String tx_password;
    private ActivityLoginBinding binding;
    private Dialog dialog;
    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        DensityUtils.setTransparent(binding.summerLoginTl,this);
        initAVCloud();
        initListener();
    }

    private void initAVCloud(){
        tx_password="";
        tx_account="";
        //如果记住了密码 直接登陆
        if((boolean)App.spHelper().get("isChecked",false)){
            binding.loginAccount.setText(App.spHelper().get("account","").toString().trim());
            binding.loginPassword.setText(App.spHelper().get("password","").toString().trim());
            tx_account = App.spHelper().get("account","").toString().trim();
            tx_password = App.spHelper().get("password","").toString().trim();
            binding.loginCheckbox.setChecked(true);
            Login();
            dialog = new DialogBuilder(this).title("").message("登陆中...").setCancelable(false).build();
            dialog.show();
        }
    }

    private void initListener(){
        //注册按钮
        binding.loginToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            overridePendingTransition(R.anim.in_from_right,R.anim.out_to_left);
        });
        //登陆账号
        binding.loginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tx_account = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //登陆密码
        binding.loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tx_password = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //登陆按钮
        binding.loginCommit.setOnClickListener(v -> {
            Login();
            dialog = new DialogBuilder(context).title("").message("登陆中...").setCancelable(false).build();
            dialog.show();
        });
        //记住密码单选框
        binding.loginCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                App.spHelper().put("account",tx_account);
                App.spHelper().put("password",tx_password);
                App.spHelper().put("isChecked",true);
            }else{
                App.spHelper().remove("account");
                App.spHelper().remove("password");
                App.spHelper().remove("isChecked");
            }
        });
    }

    /**
     * 登陆操作 利用LeanCloud后端支持
     */
    private void Login(){
        if(tx_account.length()>0&&tx_password.length()>0){
            AVUser.logInInBackground(tx_account, tx_password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if(e==null){
                        if(dialog!=null){
                            dialog.dismiss();
                        }
                        App.spHelper().put("username",tx_account);
                        LoginActivity.this.finish();
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        overridePendingTransition(R.anim.out_to_top,R.anim.in_from_bottm);
                    }else{
                        Toasts.show(e.getMessage());
                        if(binding.loginAccount.getText().length()==0){
                            binding.loginAccount.setError("用户名不能为空");
                        }
                        if(binding.loginPassword.getText().length()==0){
                            binding.loginPassword.setError("密码不能为空");
                        }
                    }
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
    }
}

