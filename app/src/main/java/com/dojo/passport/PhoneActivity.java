package com.dojo.passport;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PhoneActivity extends AppCompatActivity {

    Button getStartedBtn;
    LinearLayout getStarted, phoneNo;
    ImageButton Next;
    Geocoder geocoder;
    String city;
    EditText PhoneNum;
    Dialog mDialog;
    String androidId = "";
    String Number, PhoneNumber, PhoneCoutry = "+91";
    HashMap<String, String> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);

        getStartedBtn = findViewById(R.id.get_started_btn);
        getStarted = findViewById(R.id.get_started);
        phoneNo = findViewById(R.id.linearLayoutContent);
        Next = findViewById(R.id.next);
        PhoneNum = findViewById(R.id.phone);
        mDialog = new Dialog(this);

        androidId = Settings.Secure.getString(PhoneActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        newlocation();

       // locationCheck();

        phoneNo.setVisibility(View.GONE);
        getStarted.setVisibility(View.VISIBLE);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNo.setVisibility(View.VISIBLE);
                getStarted.setVisibility(View.GONE);
                //locationCheck();
            }
        });

        Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Number = PhoneNum.getText().toString().trim();
                if(Number.isEmpty()){
                    PhoneNum.setError("Enter a valid mobile");
                    PhoneNum.requestFocus();
                    return;
                }
                if (Number.length()<10){
                    Toast.makeText(PhoneActivity.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                PhoneNumber = PhoneCoutry + Number;

                Intent intent = new Intent(PhoneActivity.this, LoginActivity.class);
                Bundle data = new Bundle();
                data.putString("PhoneNumber",PhoneNumber);
                intent.putExtras(data);
                startActivity(intent);
                finish();

            }
        });

    }
    public void newlocation() {
        //check for sdk version
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);

            newlocation();

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            try {
                values = new HashMap<>();
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                geocoder =new Geocoder(PhoneActivity.this, Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(lat,lng,1);
                    if(addresses.get(0).getLocality()!= null) {
                        city = addresses.get(0).getLocality();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                values.put("Latitude", String.valueOf(lat));
                values.put("Longitude", String.valueOf(lng));
                values.put("City",city);

                Log.d("check",values.toString());
                Log.d("check",city);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void locationCheck(){
        if (!city.equals("Mumbai")){
            mDialog.show();
            mDialog.setContentView(R.layout.dialog_waitlist);
            mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            mDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mDialog.setCancelable(true);

            Button Join = mDialog.findViewById(R.id.join_waitlist);
            ImageView Close = mDialog.findViewById(R.id.close);

            Join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("WaitList").child(androidId).setValue(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(PhoneActivity.this, "Thanks for Joining", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    Toast.makeText(PhoneActivity.this, "Joined", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            });

            Close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(PhoneActivity.this,"Close", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            });


        }
    }

}