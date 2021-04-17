package com.dojo.passport;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ReviewBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String activity=intent.getExtras().getString("Activity");
        String docid=intent.getExtras().getString("docid");
        Intent i=new Intent(context,ReviewActivity.class);
        i.putExtra("docid",docid);
        Map act=new HashMap<>();
        act.put("Activity","Ended");
        FirebaseFirestore.getInstance().collection("Bookings").document(docid).set(act, SetOptions.merge());
        PendingIntent pendingIntent=PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,"reviewchannel")
                .setSmallIcon(R.drawable.ic_baseline_notifications_none_24)
                .setContentTitle("Review Your Experience")
                .setContentText(activity)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManage=NotificationManagerCompat.from(context);
        notificationManage.notify(200,builder.build());
    }
}
