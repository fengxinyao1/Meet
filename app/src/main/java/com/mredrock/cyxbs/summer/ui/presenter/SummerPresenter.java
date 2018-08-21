package com.mredrock.cyxbs.summer.ui.presenter;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.mredrock.cyxbs.summer.base.BaseContract;
import com.mredrock.cyxbs.summer.base.BasePresenter;
import com.mredrock.cyxbs.summer.ui.contract.SummerContract;

import java.util.List;

public class SummerPresenter extends BasePresenter<SummerContract.ISummerView> {
    private SummerContract.ISummerModel model;
    private static int skip = 10;

    public SummerPresenter(SummerContract.ISummerModel model) {
        this.model = model;
    }

    public void start(){
        model.loadData(0,new BaseContract.ISomethingModel.LoadCallBack() {
            @Override
            public void succeed(Object o) {
                if(o != null){
                    List<AVObject> data = (List<AVObject>) o;
                    getView().setData(data);
                    skip=0;
                }
            }

            @Override
            public void failed(String msg) {

            }
        });
    }

    public void loadMore(){
        model.loadData(skip, new BaseContract.ISomethingModel.LoadCallBack() {
            @Override
            public void succeed(Object o) {
                if(o!=null){
                    skip+=10;
                    List<AVObject> data = (List<AVObject>) o;
                    getView().setMoreData(data);
                }
            }

            @Override
            public void failed(String msg) {

            }
        });
    }

}
