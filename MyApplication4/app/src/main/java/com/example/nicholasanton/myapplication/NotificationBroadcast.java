//package com.example.nicholasanton.myapplication;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.widget.Toast;
//
//import java.util.Objects;
//
//public class NotificationBroadcast extends BroadcastReceiver {
//
//
//    @Override
//    public  void onReceive(Context context, Intent intent){
//
//        switch (Objects.requireNonNull(intent.getAction())) {
//            case NotificationGenerator.NOTIFY_PLAY:
//                Toast.makeText(context, "NOTIFY_PLAY", Toast.LENGTH_LONG).show();
//                break;
//            case NotificationGenerator.NOTIFY_PAUSE:
//                Toast.makeText(context, "NOTIFY_PAUSE", Toast.LENGTH_LONG).show();
//                break;
//            case NotificationGenerator.NOTIFY_NEXT:
//                Toast.makeText(context, "NOTIFY_NEXT", Toast.LENGTH_LONG).show();
//                break;
//            case NotificationGenerator.NOTIFY_DELETE:
//                Toast.makeText(context, "NOTIFY_DELETE", Toast.LENGTH_LONG).show();
//                break;
//            case NotificationGenerator.NOTIFY_PREVIOUS:
//                Toast.makeText(context, "NOTIFY_PREVIOUS", Toast.LENGTH_LONG).show();
//                break;
//        }
//    }
//}
//
