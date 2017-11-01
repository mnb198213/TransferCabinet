package com.jintoufs.zj.transfercabinet.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zj on 2017/9/4.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {     // boot
//            ToastUtils.showLongToast(context,"开机的广播接收器");
//            Intent intent2 = new Intent();
//            intent2.setAction("android.intent.action.MAIN");
//            intent2.addCategory("android.intent.category.LAUNCHER");
//            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent2);

//            Intent intent1 = new Intent(context, LoginActivity.class);
//            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
        }
    }
}
