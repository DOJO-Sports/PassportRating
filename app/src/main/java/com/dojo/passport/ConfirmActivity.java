package com.dojo.passport;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dojo.passport.Notifications.APIService;
import com.dojo.passport.Notifications.Client;
import com.dojo.passport.Notifications.Data;
import com.dojo.passport.Notifications.MyResponse;
import com.dojo.passport.Notifications.Sender;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmActivity extends AppCompatActivity {

    String Id, name, activityType, credit,phone;
    Map<String, Object> addBooking = new HashMap<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    double amount;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_activity);

        ConfirmedPopup popup = new ConfirmedPopup();
        createNotificatonChannel();
        RelativeLayout confirmBtn = findViewById(R.id.confirmBtn);
        ImageView close = findViewById(R.id.close);
        ImageView imageView = findViewById(R.id.imageView);
        TextView activityName = findViewById(R.id.activityName);
        TextView assetName = findViewById(R.id.assetName);
        TextView time = findViewById(R.id.time);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        Bundle bundle = getIntent().getExtras();
        Id = bundle.getString("Id");
        name = bundle.getString("name");
        activityType = bundle.getString("activityType");
        credit = bundle.getString("credit");
        amount = bundle.getDouble("amount");
        phone=bundle.getString("phone");

        activityName.setText(activityType);
        assetName.setText(name);

        switch (activityType) {
            case "Gym":
                imageView.setImageResource(R.drawable.gym_small);
                break;
            case "Badminton":
                imageView.setImageResource(R.drawable.badminton_small);
                break;
            case "Boxing":
                imageView.setImageResource(R.drawable.boxing_small);
                break;
            case "Cricket":
                imageView.setImageResource(R.drawable.cricket_small);
                break;
            case "Football":
                imageView.setImageResource(R.drawable.football_small);
                break;
            case "Swimming":
                imageView.setImageResource(R.drawable.swimming_small);
                break;
            case "Tennis":
                imageView.setImageResource(R.drawable.tennis_small);
                break;
            case "Yoga":
                imageView.setImageResource(R.drawable.yoga_small);
                break;
            case "Aerobics":
                imageView.setImageResource(R.drawable.aerobics_small);
                break;
            case "Cardio":
                imageView.setImageResource(R.drawable.cardio_small);
                break;
            case "CrossFit":
                imageView.setImageResource(R.drawable.crossfit_small);
                break;
            case "Dance":
                imageView.setImageResource(R.drawable.dance_small);
                break;
            case "Karate":
                imageView.setImageResource(R.drawable.karate_small);
                break;
            case "Taekwondo":
                imageView.setImageResource(R.drawable.taekwondo_small);
                break;
        }

        close.setOnClickListener(v -> onBackPressed());

        confirmBtn.setOnClickListener(v -> {

            addBooking.put("ActivityType", activityType);
            addBooking.put("Amount", amount);
            addBooking.put("AssetNo", Id);
            addBooking.put("BookingID", genID());
            addBooking.put("Credit", Double.parseDouble(credit));
            addBooking.put("TimeStamp", Timestamp.now());
            addBooking.put("Phone",phone);
            //addBooking.put("CustName", "");
            //addBooking.put("CustNo", "");
            //addBooking.put("PaymentMode", "");

            db.collection("Bookings")
                    .add(addBooking)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            String docid=documentReference.getId();
                            popup.showDialog(ConfirmActivity.this);
                            String d=time.getText().toString().substring(0,16)+" "+time.getText().toString().substring(29,36);
                            Date date = null;
                            long millis = 0;
                            try {
                                date = new SimpleDateFormat("EEE, MMM dd yyyy hh.mmaa").parse(d);
                                millis=date.getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Intent intent=new Intent(ConfirmActivity.this,ReviewBroadcast.class);
                            intent.putExtra("Activity",activityName.getText() + " , " + assetName.getText());
                            intent.putExtra("docid",docid);
                            //sendBroadcast(intent);
                            PendingIntent pintent=PendingIntent.getBroadcast(ConfirmActivity.this,0,intent,0);
                            AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
                            long timemilli=System.currentTimeMillis();
                            long tensec=1000*60;
                            alarmManager.set(AlarmManager.RTC_WAKEUP,millis,pintent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfirmActivity.this, "Booking Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

        });
    }

    public String genID(){
        long tsLong = System.currentTimeMillis()/1000;
        return tsLong + "0";
    }
    public void createNotificatonChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name="ReviewChannel";
            String description="Dojo Activity";
            int imp= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel=new NotificationChannel("reviewchannel",name,imp);
            channel.setDescription(description);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}