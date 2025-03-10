package com.mredrock.cyxbs.summer.ui.mvp.model;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.mredrock.cyxbs.summer.bean.AskBean;
import com.mredrock.cyxbs.summer.ui.mvp.contract.AskFriednsContract;

public class AskFriendsModel implements AskFriednsContract.IAskFriendsModel {


    @Override
    public void Ask(AskBean bean, LoadCallBack callBack) {
            AVObject info = new AVObject("askInfo");
            info.put("askName",bean.getAskName());
            info.put("askContent",bean.getAskContent());
            if(bean.getVoice()!=null){
                info.put("voice",bean.getVoice());
            }
            if(bean.getPhoto()!=null){
                info.put("photo",bean.getPhoto());
            }
            info.put("author", AVUser.getCurrentUser());
            info.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e==null){
                        callBack.succeed("");//成功就回调
                    }else{
                        callBack.failed(e.getMessage());
                    }
                }
            });
    }
}
