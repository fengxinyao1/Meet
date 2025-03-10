package com.mredrock.cyxbs.summer.ui.mvp.presenter;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.mredrock.cyxbs.summer.base.BaseContract;
import com.mredrock.cyxbs.summer.base.BasePresenter;
import com.mredrock.cyxbs.summer.ui.mvp.contract.UserContract;
import com.mredrock.cyxbs.summer.utils.Toasts;

import java.util.List;

public class UserPresenter extends BasePresenter<UserContract.IUserView> {
    private UserContract.IUserModel model;

    public UserPresenter(UserContract.IUserModel model) {
        this.model = model;
    }

    public void loadUser(String objectId){
        model.LoadUserInfo(objectId, new UserContract.CallBack() {
            @Override
            public void succeed(Object o, boolean isFavorite) {
                if(o!=null){
                    AVUser avUser = (AVUser) o;
                    getView().setUser(avUser,isFavorite);
                }
            }

            @Override
            public void failed(String msg) {

            }
        });
    }

    public void loadData(AVUser avUser){
        model.LoadUserRecent(avUser, new BaseContract.ISomethingModel.LoadCallBack() {
            @Override
            public void succeed(Object o) {
                if(o!=null){
                    List<AVObject> datas = (List<AVObject>) o;
                    getView().setData(datas);
                }
            }

            @Override
            public void failed(String msg) {
                Toasts.show(msg);
            }
        });
    }
}
