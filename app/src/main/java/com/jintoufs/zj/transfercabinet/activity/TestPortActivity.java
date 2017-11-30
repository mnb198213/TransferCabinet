package com.jintoufs.zj.transfercabinet.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.basekit.util.ToastUtils;
import com.jintoufs.zj.transfercabinet.R;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zj on 2017/9/5.
 */

public class TestPortActivity extends SerialPortActivity {


    @BindView(R.id.tv_input)
    TextView tvInput;
    @BindView(R.id.iv_image)
    ImageView iv_image;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_test_port);
        ButterKnife.bind(this);
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("zoujiang", "收到的信息：" + new String(buffer).toString() + "    size:" + size);
//                tvInput.setText(new String(buffer));
//                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, size);
//                if (bitmap != null){
//                    Logger.i("bitmap 不为 null");
//                    iv_image.setImageBitmap(bitmap);
//                }else {
//                    Logger.i("bitmap 为 null");
//                }
//                String mSavePath = Environment.getExternalStorageDirectory() + "/" + "transfercabinet";
//                File file = new File(mSavePath);
//                if (!file.exists()) {
//                    file.mkdir();
//                }
//                File imgFile = new File(file, "ddd.jpg");
//                String path = imgFile.getAbsolutePath();
//                byte2image(buffer, path);
//                Logger.i("文件长度：" + imgFile.length());
////                Bitmap bitmap = BitmapFactory.decodeFile(path);
//                Drawable drawable = BitmapDrawable.createFromPath(path);
//                Logger.i("文件路径：" + path);
//                iv_image.setImageDrawable(drawable);
//                if (bitmap != null) {
//                    Logger.i("bitmap 不为 null");
//                    iv_image.setImageBitmap(bitmap);
//                } else {
//                    Logger.i("bitmap 为 null");
//                }
            }
        });
    }

    public void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            FileOutputStream imageOutput = new FileOutputStream(new File(path));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        } catch (Exception ex) {
            Logger.i("异常：" + ex.getClass().getName());
            System.out.println("Exception: " + ex);
            ex.printStackTrace();
        }
    }
}
