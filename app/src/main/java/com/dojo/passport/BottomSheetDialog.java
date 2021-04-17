package com.dojo.passport;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    TextView activityName, assetName, time, bookingId;
    RadioGroup radioGroup;
    RadioButton radioBtn1, radioBtn2, radioBtn3;
    Button cancelBookingBtn;
    Map<String, Object> addData = new HashMap<>();
    ImageView close, img;
    String assetNo, BookingID;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        activityName = view.findViewById(R.id.text1);
        assetName = view.findViewById(R.id.text2);
        time = view.findViewById(R.id.text3);
        bookingId = view.findViewById(R.id.bookingId);
        cancelBookingBtn = view.findViewById(R.id.cancelBookingBtn);
        close = view.findViewById(R.id.close);
        img = view.findViewById(R.id.img);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioBtn1 = view.findViewById(R.id.radioBtn1);
        radioBtn2 = view.findViewById(R.id.radioBtn2);
        radioBtn3 = view.findViewById(R.id.radioBtn3);

        String activity = this.getArguments().getString("activityType");
        activityName.setText(activity);
        assetNo = getArguments().getString("assetNo");
        time.setText(this.getArguments().getString("time"));
        BookingID = this.getArguments().getString("bookingId");
        bookingId.setText("Booking ID: " + BookingID);

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
                if (snapshot.child("Name").exists()) {
                    assetName.setText(assetNo + " " + snapshot.child("Name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        cancelBookingBtn.setOnClickListener(view1 -> {

            String desc = null;

            if (radioBtn1.isChecked()) {
                desc = radioBtn1.getText().toString();
            } else if (radioBtn2.isChecked()) {
                desc = radioBtn2.getText().toString();
            } else if (radioBtn3.isChecked()) {
                desc = radioBtn3.getText().toString();
            }

            if (desc != null) {
                addData.put("Status", "Cancelled");
                addData.put("Description", desc);

                db.collection("Bookings")
                        .whereEqualTo("BookingID", BookingID)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<DocumentSnapshot> doc = queryDocumentSnapshots.getDocuments();
                                DocumentSnapshot Doc = doc.get(0);
                                Log.d("docid", Doc.getId());

                                db.collection("Bookings")
                                        .document(Doc.getId())
                                        .set(addData, SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            }
                                        });
                            }
                        });
            }
        });

        close.setOnClickListener(view1 -> dismiss());

        return view;
    }
}