package com.dojo.passport;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CancelledBottomSheetDialog extends BottomSheetDialogFragment {

    TextView activityName, assetName, address, time, bookingId;
    String assetNo;
    ImageView close, img;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cancelled_bottom_sheet, container, false);

        activityName = view.findViewById(R.id.text1);
        assetName = view.findViewById(R.id.text2);
        address = view.findViewById(R.id.text3);
        time = view.findViewById(R.id.text4);
        bookingId = view.findViewById(R.id.bookingId);
        close = view.findViewById(R.id.close);
        img = view.findViewById(R.id.img);

        String activity = this.getArguments().getString("activityType");
        activityName.setText(activity);
        time.setText(this.getArguments().getString("time"));
        bookingId.setText("Booking ID: " + this.getArguments().getString("bookingId"));
        assetNo = getArguments().getString("assetNo");

        switch (activity) {
            case "Gym":
                img.setImageResource(R.drawable.gym_small);
                break;
            case "Badminton":
                img.setImageResource(R.drawable.badminton_small);
                break;
            case "Boxing":
                img.setImageResource(R.drawable.boxing_small);
                break;
            case "Cricket":
                img.setImageResource(R.drawable.cricket_small);
                break;
            case "Football":
                img.setImageResource(R.drawable.football_small);
                break;
            case "Swimming":
                img.setImageResource(R.drawable.swimming_small);
                break;
            case "Tennis":
                img.setImageResource(R.drawable.tennis_small);
                break;
            case "Yoga":
                img.setImageResource(R.drawable.yoga_small);
                break;
            case "Aerobics":
                img.setImageResource(R.drawable.aerobics_small);
                break;
            case "Cardio":
                img.setImageResource(R.drawable.cardio_small);
                break;
            case "CrossFit":
                img.setImageResource(R.drawable.crossfit_small);
                break;
            case "Dance":
                img.setImageResource(R.drawable.dance_small);
                break;
            case "Karate":
                img.setImageResource(R.drawable.karate_small);
                break;
            case "Taekwondo":
                img.setImageResource(R.drawable.taekwondo_small);
                break;
            default:
                img.setImageResource(R.color.white);
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DOJO").child("Assets").child(assetNo);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Profile").child("Address").exists()) {
                    address.setText(snapshot.child("Profile").child("Address").getValue().toString());
                }
                if (snapshot.child("Name").exists()) {
                    assetName.setText(assetNo + " " + snapshot.child("Name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        close.setOnClickListener(view1 -> dismiss());

        return view;
    }
}
