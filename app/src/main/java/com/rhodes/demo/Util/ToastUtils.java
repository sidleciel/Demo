package com.rhodes.demo.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by pengsk on 2015/3/19.
 */
public class ToastUtils {
    Toast toastStart;
    private static ToastUtils ourInstance ;

    public static ToastUtils getInstance(Context context) {
        if (ourInstance==null){
            ourInstance = new ToastUtils(context);

        }
        return ourInstance;
    }

    private ToastUtils(Context context) {
         toastStart = new Toast(context);
        toastStart= Toast.makeText(context, "", Toast.LENGTH_LONG);
    }
//    public  void  showToast(Activity context,String info){
//////        View toastRoot = context.getLayoutInflater().inflate(R.layout.toast, null);
////        View toastRoot = context.getLayoutInflater().inflate(R.layout.register_alert_dialog, null);
////        TextView message = (TextView) toastRoot.findViewById(R.id.message);
////        message.setText(info);
////
////
////        toastStart.setGravity(Gravity.BOTTOM, 0, 10);
////        toastStart.setDuration(Toast.LENGTH_LONG);
////        toastStart.setView(toastRoot);
////        toastStart.show();\
//    }

    public  void  showToastSystem(String info){
        toastStart.setText(info);
        toastStart.show();
    }
//    public  void  showToastSystemInThread(String info){
//        Looper.prepare();
//        toastStart.setText(info);
//        toastStart.show();
//        Looper.loop();
//    }
}
