package com.dojo.passport;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeFragment extends Fragment {

    RelativeLayout toListView;
    CardView cardSummerSpecial, cardWeightLoss, cardSportsPackage, cardRaceAgainstTime, cardWomenSpecial, cardRelaxation, cardFitnessFreak, cardBodyRockingActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        String phone=getArguments().getString("Phone");
        toListView = view.findViewById(R.id.listView);
        cardSummerSpecial = view.findViewById(R.id.cardSummerSpecial);
        cardWeightLoss = view.findViewById(R.id.cardWeightLoss);
        cardSportsPackage = view.findViewById(R.id.cardSportsPackage);
        cardRaceAgainstTime = view.findViewById(R.id.cardRaceAgainstTime);
        cardWomenSpecial = view.findViewById(R.id.cardWomenSpecial);
        cardRelaxation = view.findViewById(R.id.cardRelaxation);
        cardBodyRockingActivity = view.findViewById(R.id.cardBodyRockingActivity);
        cardFitnessFreak = view.findViewById(R.id.cardFitnessFreak);

        Intent intent = new Intent(getActivity(), ListViewActivity.class);
        intent.putExtra("Phone",phone);
        FirebaseFirestore.getInstance().collection("Bookings").whereEqualTo("Phone",phone).whereEqualTo("Activity","Ended").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document:task.getResult()){
                                if(document.exists()&&document.getString("Rate")==null){
                                    String docid=document.getId();
                                    Intent i=new Intent(getActivity(),ReviewActivity.class);
                                    i.putExtra("docid",docid);
                                    startActivity(i);
                                }
                            }
                        }
                    }
                });
        toListView.setOnClickListener(view1 -> {
            intent.putExtra("collection", "ALL");
            startActivity(intent);
        });
        cardSummerSpecial.setOnClickListener(view12 -> {
            intent.putExtra("collection", "CARD1");
            startActivity(intent);
        });
        cardWeightLoss.setOnClickListener(view13 -> {
            intent.putExtra("collection", "CARD2");
            startActivity(intent);
        });
        cardSportsPackage.setOnClickListener(view14 -> {
            intent.putExtra("collection", "CARD3");
            startActivity(intent);
        });
        cardRaceAgainstTime.setOnClickListener(view15 -> {
            intent.putExtra("collection", "CARD4");
            startActivity(intent);
        });
        cardWomenSpecial.setOnClickListener(view16 -> {
            intent.putExtra("collection", "CARD5");
            startActivity(intent);
        });
        cardRelaxation.setOnClickListener(view17 -> {
            intent.putExtra("collection", "CARD6");
            startActivity(intent);
        });
        cardBodyRockingActivity.setOnClickListener(view18 -> {
            intent.putExtra("collection", "CARD7");
            startActivity(intent);
        });
        cardFitnessFreak.setOnClickListener(view19 -> {
            intent.putExtra("collection", "CARD8");
            startActivity(intent);
        });

        return view;
    }
}