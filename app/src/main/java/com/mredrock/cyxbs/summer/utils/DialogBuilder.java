package com.mredrock.cyxbs.summer.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mredrock.cyxbs.summer.R;

import uk.co.senab.photoview.PhotoView;

public class DialogBuilder {
    private Context context;
    private int themeResId;
    private View layout;
    private boolean cancelable = true;
    private CharSequence title, message, cancelText, sureText;//除了message的所有文本，不写则Gone。
    private View.OnClickListener sureClickListener, cancelClickListener;

    public DialogBuilder(Context context) {
        this(context, R.style.summer_custom_dialog);
    }

    public DialogBuilder(Context context, int themeResId) {
        this(context, themeResId, ((LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.summer_default_dialog_layout, null));
    }
    //自定义layout用这个
    public DialogBuilder(Context context, int themeResId, View layout) {
        this.context = context;
        this.themeResId = themeResId;
        this.layout = layout;
    }

    //能否返回键取消
    public DialogBuilder setCancelable(Boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public DialogBuilder title(CharSequence title) {
        this.title = title;
        return this;
    }

    public DialogBuilder message(CharSequence message) {
        this.message = message;
        return this;
    }

    public DialogBuilder cancelText(CharSequence str) {
        this.cancelText = str;
        return this;
    }

    //确定按钮文本
    public DialogBuilder sureText(CharSequence str) {
        this.sureText = str;
        return this;
    }

    public DialogBuilder setSureOnClickListener(View.OnClickListener listener) {
        this.sureClickListener = listener;
        return this;
    }

    public DialogBuilder setCancelOnClickListener(View.OnClickListener listener) {
        this.cancelClickListener = listener;
        return this;
    }

    public Dialog build() {
        final Dialog dialog = new Dialog(context, themeResId);
        dialog.setCancelable(cancelable);
        dialog.addContentView(layout, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
        setText(title, R.id.title);
        setText(message, R.id.message);
        setText(cancelText, R.id.cancel);
        setText(sureText, R.id.sure);
        if (isValid(cancelText) || isValid(sureText)) {
            layout.findViewById(R.id.line2).setVisibility(View.VISIBLE);
        }
        if (isValid(cancelText) && isValid(sureText)) {
            layout.findViewById(R.id.line).setVisibility(View.VISIBLE);
        }
        if(!isValid(title)){
            ((TextView)layout.findViewById(R.id.message)).setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
        }
        final TextView textView = layout.findViewById(R.id.message);
        textView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if(textView.getLineCount() ==1){
                    textView.setGravity(Gravity.CENTER);
                }
                return true;
            }
        });
        if (sureClickListener != null) {
            layout.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sureClickListener.onClick(view);
                    dialog.dismiss();
                }
            });
        }
        if (cancelClickListener != null) {
            layout.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelClickListener.onClick(view);
                    dialog.dismiss();
                }
            });
        }
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.80);
        dialog.getWindow().setAttributes(params);
        return dialog;
    }

    private void setText(CharSequence text, int id) {
        if (isValid(text)) {
            TextView textView = layout.findViewById(id);
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isValid(CharSequence text) {
        return text != null && !"".equals(text.toString().trim());
    }

    public static Dialog buildImgDialog(Context context,String url){
        Dialog dialog = new Dialog(context,R.style.edit_AlertDialog_style);
        dialog.setContentView(R.layout.show_img_dialog);
        PhotoView imageView = dialog.findViewById(R.id.show_img);
        ProgressBar progressBar = dialog.findViewById(R.id.show_progress);
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        progressBar.setVisibility(View.GONE);
                       imageView.setImageBitmap(resource);
                    }
                });
        dialog.setCanceledOnTouchOutside(true);
//        Window dialogWindow = dialog.getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.height = (int)(DensityUtils.getScreenHeight(context)*0.7);
//        lp.width = (int)(DensityUtils.getScreenWidth(context)*0.9);
//        dialogWindow.setAttributes(lp);
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        lp.height = (int)(DensityUtils.getScreenHeight(context)*0.7);
        lp.width = (int)(DensityUtils.getScreenWidth(context)*0.9);

        dialog.getWindow().findViewById(R.id.show_img).setOnClickListener(v1->{
            Log.d("photo", "buildImgDialog: ");
            dialog.cancel();
        });
        return dialog;
    }
}
