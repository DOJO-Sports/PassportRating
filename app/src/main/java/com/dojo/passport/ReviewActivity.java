package com.dojo.passport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {
    RatingBar ratingbar;
    Button button;
    String docid;
    TextView assetname,address,time;
    EditText comment;
    ImageView backBtn,img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        docid=getIntent().getExtras().getString("docid");
        assetname=findViewById(R.id.assetName);
        address=findViewById(R.id.address);
        time=findViewById(R.id.time);
        backBtn=findViewById(R.id.backBtn);
        img=findViewById(R.id.img);
        backBtn.setOnClickListener(view -> onBackPressed());
        FirebaseFirestore.getInstance().collection("Bookings").document(docid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document!=null){
                        assetname.setText(document.getString("ActivityType"));
                        switch (assetname.getText().toString()) {
                            case "Gym":
                                img.setImageResource(R.drawable.gym_rect);
                                break;
                            case "Badminton":
                                img.setImageResource(R.drawable.badminton_rect);
                                break;
                            case "Boxing":
                                img.setImageResource(R.drawable.boxing_rect);
                                break;
                            case "Cricket":
                                img.setImageResource(R.drawable.cricket_rect);
                                break;
                            case "Football":
                                img.setImageResource(R.drawable.football_rect);
                                break;
                            case "Swimming":
                                img.setImageResource(R.drawable.swimming_rect);
                                break;
                            case "Tennis":
                                img.setImageResource(R.drawable.tennis_rect);
                                break;
                            case "Yoga":
                                img.setImageResource(R.drawable.yoga_rect);
                                break;
                            case "Aerobics":
                                img.setImageResource(R.drawable.aerobics_rect);
                                break;
                            case "Cardio":
                                img.setImageResource(R.drawable.cardio_rect);
                                break;
                            case "CrossFit":
                                img.setImageResource(R.drawable.crossfit_rect);
                                break;
                            case "Dance":
                                img.setImageResource(R.drawable.dance_rect);
                                break;
                            case "Karate":
                                img.setImageResource(R.drawable.karate_rect);
                                break;
                            case "Taekwondo":
                                img.setImageResource(R.drawable.taekwondo_rect);
                                break;
                        }
                        address.setText(document.getString("AssetNo"));
                        Timestamp ts=document.getTimestamp("TimeStamp");
                        Date date=ts.toDate();
                        time.setText("Booked on "+date.toString().substring(0,10));
                    }
                }
            }
        });
        addListenerOnButtonClick();

    }
    public void addListenerOnButtonClick(){
        ratingbar=(RatingBar)findViewById(R.id.ratingBar);
        button=(Button)findViewById(R.id.button);
        comment=findViewById(R.id.comment);
        //Performing action on Button Click
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                //Getting the rating and displaying it on the toast
                String rating=String.valueOf(ratingbar.getRating());
                String usercomment=comment.getText().toString();
                Map rate=new HashMap<>();
                Map com=new HashMap<>();
                rate.put("Rating",rating);
                com.put("Review",usercomment);
                FirebaseFirestore.getInstance().collection("Bookings").document(docid).set(rate, SetOptions.merge());
                FirebaseFirestore.getInstance().collection("Bookings").document(docid).set(com, SetOptions.merge());
                Toast.makeText(getApplicationContext(), "Review Added", Toast.LENGTH_LONG).show();
            }

        });
    }
}