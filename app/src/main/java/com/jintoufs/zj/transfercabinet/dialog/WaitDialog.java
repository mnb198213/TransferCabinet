package com.jintoufs.zj.transfercabinet.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

/**
 * Created by zj on 2017/12/1.
 */

public class WaitDialog {
    private Dialog dialog;
    private TextView tv_info;

    public WaitDialog(Context context) {
        getInstance(context);
    }

    public void getInstance(Context context) {
        dialog = new Dialog(context, R.style.TransparentDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_search_view, null);
        tv_info = (TextView) view.findViewById(R.id.tv_info);
        window.setContentView(view);
    }

    public void show(Context context, String strInfo) {
        if (dialog == null) {
            getInstance(context);
        }
        tv_info.setText(strInfo);
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null)
            dialog.dismiss();
    }


}
