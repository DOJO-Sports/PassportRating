package com.dojo.passport;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;

public class HistoryFragment extends Fragment {

    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    String text;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        Query query = db.collection("Bookings")
                .whereEqualTo("ActivityType", "Gym")
                .orderBy("TimeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<booking_model> options = new FirestoreRecyclerOptions.Builder<booking_model>()
                .setQuery(query, booking_model.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<booking_model, HistoryFragment.ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull HistoryFragment.ViewHolder holder, int position, @NonNull booking_model model) {

                Date timestamp = model.getTimeStamp().toDate();
                int endIndex = timestamp.toString().indexOf(":");
                holder.text3.setText("Booked on " + timestamp.toString().substring(4, endIndex - 3));

                switch (model.getActivityType()) {
                    case "Gym":
                        holder.img.setImageResource(R.drawable.gym_small);
                        break;
                    case "Badminton":
                        holder.img.setImageResource(R.drawable.badminton_small);
                        break;
                    case "Boxing":
                        holder.img.setImageResource(R.drawable.boxing_small);
                        break;
                    case "Cricket":
                        holder.img.setImageResource(R.drawable.cricket_small);
                        break;
                    case "Football":
                        holder.img.setImageResource(R.drawable.football_small);
                        break;
                    case "Swimming":
                        holder.img.setImageResource(R.drawable.swimming_small);
                        break;
                    case "Tennis":
                        holder.img.setImageResource(R.drawable.tennis_small);
                        break;
                    case "Yoga":
                        holder.img.setImageResource(R.drawable.yoga_small);
                        break;
                    case "Aerobics":
                        holder.img.setImageResource(R.drawable.aerobics_small);
                        break;
                    case "Cardio":
                        holder.img.setImageResource(R.drawable.cardio_small);
                        break;
                    case "CrossFit":
                        holder.img.setImageResource(R.drawable.crossfit_small);
                        break;
                    case "Dance":
                        holder.img.setImageResource(R.drawable.dance_small);
                        break;
                    case "Karate":
                        holder.img.setImageResource(R.drawable.karate_small);
                        break;
                    case "Taekwondo":
                        holder.img.setImageResource(R.drawable.taekwondo_small);
                        break;
                    default:
                        holder.img.setImageResource(R.color.white);
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DOJO").child("Assets").child(model.getAssetNo());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String Name = snapshot.child("Name").getValue().toString();
                        text = model.getAssetNo() + " " + Name;
                        holder.text2.setText(text);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.text1.setText(model.getActivityType());
                //holder.credit.setText(String.valueOf(model.getCredit()));

                holder.item.setOnClickListener(view1 -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("activityType", model.getActivityType());
                    bundle.putString("assetName", text);
                    bundle.putString("assetNo", model.getAssetNo());
                    bundle.putString("bookingId", model.getBookingID());
                    bundle.putString("time", timestamp.toString().substring(0, endIndex - 3));

                    CancelledBottomSheetDialog dialog = new CancelledBottomSheetDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "Bottom Sheet");
                });
            }

            /*@Override
            public void onDataChanged() {
                if(adapter.getItemCount() == 0) {

                }
            }*/
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        return view;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2, text3, credit;
        RelativeLayout item;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            text1 = itemView.findViewById(R.id.text1);
            text2 = itemView.findViewById(R.id.text2);
            text3 = itemView.findViewById(R.id.text3);
            credit = itemView.findViewById(R.id.credit);
            img = itemView.findViewById(R.id.img);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        // adapter.startListening();
    }
}