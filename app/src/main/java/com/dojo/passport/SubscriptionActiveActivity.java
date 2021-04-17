package com.dojo.passport;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubscriptionActiveActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser user;
    String Phone;
    TextView TotalCredits, expiryDate;
    CardView tpCard1, tpCard2, tpCard3, tpCard4, tpCard5;
    ImageView backBtn;
    TextView priceP1, creditC1, priceP2, creditC2, priceP3, creditC3, priceP4, creditC4, priceP5, creditC5;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_active);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user = mAuth.getCurrentUser();
            Phone = user.getPhoneNumber();
        }

        backBtn = findViewById(R.id.backBtn);
        tpCard1 = findViewById(R.id.tp_card1);
        tpCard1.setVisibility(View.GONE);
        tpCard2 = findViewById(R.id.tp_card2);
        tpCard2.setVisibility(View.GONE);
        tpCard3 = findViewById(R.id.tp_card3);
        tpCard3.setVisibility(View.GONE);
        tpCard4 = findViewById(R.id.tp_card4);
        tpCard4.setVisibility(View.GONE);
        tpCard5 = findViewById(R.id.tp_card5);
        tpCard5.setVisibility(View.GONE);
        priceP1 = findViewById(R.id.price_p1);
        creditC1 = findViewById(R.id.credit_c1);
        priceP2 = findViewById(R.id.price_p2);
        creditC2 = findViewById(R.id.credit_c2);
        priceP3 = findViewById(R.id.price_p3);
        creditC3 = findViewById(R.id.credit_c3);
        priceP4 = findViewById(R.id.price_p4);
        creditC4 = findViewById(R.id.credit_c4);
        priceP5 = findViewById(R.id.price_p5);
        creditC5 = findViewById(R.id.credit_c5);
        TotalCredits = findViewById(R.id.total_credits);
        expiryDate = findViewById(R.id.expiry_date);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        databaseReference.child("Customer's").child(Phone).child("Membership").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String setCredit = String.valueOf(snapshot.child("Credits").getValue());
                String exDate = String.valueOf(snapshot.child("ExpiryDate").getValue());
                TotalCredits.setText(setCredit);
                expiryDate.setText("Expires on "+exDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference.child("Passport").child("Membership").child("TopUp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("TP1").exists()){
                    tpCard1.setVisibility(View.VISIBLE);
                    String price1 = String.valueOf(snapshot.child("TP1").child("Amount").getValue());
                    String credit1 = String.valueOf(snapshot.child("TP1").child("Credit").getValue());
                    priceP1.setText("₹"+price1);
                    creditC1.setText(credit1+" Credit");
                }

                if (snapshot.child("TP2").exists()){
                    tpCard2.setVisibility(View.VISIBLE);
                    String price2 = String.valueOf(snapshot.child("TP2").child("Amount").getValue());
                    String credit2 = String.valueOf(snapshot.child("TP2").child("Credit").getValue());
                    priceP2.setText("₹"+price2);
                    creditC2.setText(credit2+" Credit");
                }

                if (snapshot.child("TP3").exists()){
                    tpCard3.setVisibility(View.VISIBLE);
                    String price3 = String.valueOf(snapshot.child("TP3").child("Amount").getValue());
                    String credit3 = String.valueOf(snapshot.child("TP3").child("Credit").getValue());
                    priceP3.setText("₹"+price3);
                    creditC3.setText(credit3+" Credit");
                }

                if (snapshot.child("TP4").exists()){
                    tpCard4.setVisibility(View.VISIBLE);
                    String price4 = String.valueOf(snapshot.child("TP4").child("Amount").getValue());
                    String credit4 = String.valueOf(snapshot.child("TP4").child("Credit").getValue());
                    priceP4.setText("₹"+price4);
                    creditC4.setText(credit4+" Credit");
                }

                if (snapshot.child("TP5").exists()){
                    tpCard5.setVisibility(View.VISIBLE);
                    String price5 = String.valueOf(snapshot.child("TP5").child("Amount").getValue());
                    String credit5 = String.valueOf(snapshot.child("TP5").child("Credit").getValue());
                    priceP5.setText("₹"+price5);
                    creditC5.setText(credit5+" Credit");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}