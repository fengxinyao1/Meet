package com.mredrock.cyxbs.summer.ui.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.mredrock.cyxbs.summer.R;
import com.mredrock.cyxbs.summer.base.BaseMvpActivity;
import com.mredrock.cyxbs.summer.bean.AskBean;
import com.mredrock.cyxbs.summer.databinding.ActivityAskFriendsBinding;
import com.mredrock.cyxbs.summer.ui.mvp.contract.AskFriednsContract;
import com.mredrock.cyxbs.summer.ui.mvp.model.AskFriendsModel;
import com.mredrock.cyxbs.summer.ui.mvp.presenter.AskFriendsPresenter;
import com.mredrock.cyxbs.summer.utils.DensityUtils;
import com.mredrock.cyxbs.summer.utils.DialogBuilder;
import com.mredrock.cyxbs.summer.utils.Glide4Engine;
import com.mredrock.cyxbs.summer.utils.RecorderUtil;
import com.mredrock.cyxbs.summer.utils.Toasts;
import com.mredrock.cyxbs.summer.utils.UriUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AskFriendsActivity extends BaseMvpActivity implements AskFriednsContract.IAskFriendsView {
    private ActivityAskFriendsBinding binding;
    private AskFriendsPresenter presenter;
    private RxPermissions rxPermissions;
    private RecorderUtil recorderUtil;
    private boolean isAskPer = false;
    private AVUser user;
    private String fileName = "";
    private String filePath = "";
    private String name="";
    private String path="";
    private Dialog dialog;

    private int REQUEST_CODE_CHOOSE = 10086;
    private List<Uri> selects;
    private AVFile photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ask_friends);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        rxPermissions = new RxPermissions(this);
        recorderUtil = new RecorderUtil();
        selects = new ArrayList<>();
        user = AVUser.getCurrentUser();
        askPermissions();

        DensityUtils.setTransparent(binding.summerAskTl,this);
        DensityUtils.setSystemUi(getWindow());

        //返回按钮
        binding.summerAskBack.setOnClickListener(v-> finish());

        //麦克风按钮
            binding.summerAskVoice.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    binding.summerAskVoice.setBackgroundResource(R.drawable.summer_icon_voice_light);
                    recorderUtil.recordStart();
                }
                try{
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        binding.summerAskVoice.setBackgroundResource(R.drawable.summer_icon_voice);
                        recorderUtil.recordStop((fileName,filePath) -> {
                            Toasts.show(fileName);
                            this.fileName = fileName;
                            this.filePath = filePath;
                        });
                    }
                }catch (Exception e){
                    Toasts.show("1");
                }
                return false;
            });


        //图片选择按钮
        binding.summerAskPhoto.setOnClickListener(v -> {
            if(isAskPer){
                Matisse.from(this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .capture(true)  // 开启相机，和 captureStrategy 一并使用否则报错
                        .captureStrategy(new CaptureStrategy(true,"com.mredrock.cyxbs.summer.fileprovider"))
                        .maxSelectable(1)
                        .gridExpectedSize(DensityUtils.getScreenWidth(this)/3)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .imageEngine(new Glide4Engine())
                        .theme(R.style.Matisse_Dracula)
                        .forResult(REQUEST_CODE_CHOOSE);
            }
        });

        //提交按钮
        binding.summerAskCommit.setOnClickListener(v -> {
            if(isAskPer&&binding.summerAskName.getText().length()>0
                    &&binding.summerAskContent.getText().length()>0){
                AskBean bean = new AskBean();
                bean.setAskContent(binding.summerAskContent.getText().toString());
                bean.setAskName(binding.summerAskName.getText().toString());
                try {
                    if(filePath.length()>0){
                        AVFile voice = AVFile.withAbsoluteLocalPath(fileName,filePath);
                        bean.setVoice(voice);
                    }
                    if(path.length()>0){
                        bean.setPhoto(photo);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                presenter.ask(bean);
            }
        });



    }

    @SuppressLint({"CheckResult"})
    private void askPermissions(){
        rxPermissions
                .requestEach(Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.RECORD_AUDIO
                )
                .subscribe(permission -> {
                    if(permission.granted){
                        isAskPer = true;
                    }else{
                        Toasts.show("未获取权限");
                    }
                });
    }

    @Override
    public void attachPresenter() {
        presenter = new AskFriendsPresenter(new AskFriendsModel());
        presenter.attachView(this);
    }

    @Override
    public void detachPresenter() {
        presenter.detachView();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.out_to_right,R.anim.in_from_left);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void showLoad() {
        dialog = new DialogBuilder(this).title("").message("发布中...").setCancelable(false).build();
        dialog.show();
    }

    @Override
    public void hideLoad() {
        dialog.hide();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK){
            selects.clear();
            selects = Matisse.obtainResult(data);
            try{
                name = System.currentTimeMillis()/1000 + ".jpg";
                if(selects.get(0).toString().contains("provider")){
                    path = UriUtil.getFPUriToPath(this,selects.get(0));
                    photo = AVFile.withAbsoluteLocalPath(name,path);
                }
                else{
                    path = UriUtil.getRealPathFromUri(this,selects.get(0));
                    photo = AVFile.withAbsoluteLocalPath(name,path);
                }
                    binding.summerAskPhoto.setBackgroundResource(R.drawable.summer_icon_photo_light);
            }catch (IOException e){
                Toasts.show("文件写入失败");
            }
        }
    }
}
