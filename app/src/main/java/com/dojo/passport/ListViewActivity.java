package com.dojo.passport;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.floor;

public class ListViewActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    double lati;
    double longi;
    String latitude;
    String longitude;
    RelativeLayout cBox;
    FirestoreRecyclerAdapter adapter;
    double dist;
    String Distance, location;
    String Collection;
    Query query;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String phone;

    String Today, docid;
    List<String> Day;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Bundle bundle = getIntent().getExtras();
        Collection = bundle.getString("collection");
        phone=bundle.getString("Phone");

        /*cBox = findViewById(R.id.cBox);
        cBox.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ListViewActivity.this, PhoneActivity.class);
            startActivity(intent);
            finish();
        });*/

        recyclerView = findViewById(R.id.recyclerView);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //permission check
        if (ActivityCompat.checkSelfPermission(ListViewActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {

                Location location = task.getResult();
                if (location != null){
                    try {
                        Geocoder geocoder = new Geocoder(ListViewActivity.this,
                                Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),
                                location.getLongitude(),
                                1
                        );

                        Toast.makeText(ListViewActivity.this, "got the Location", Toast.LENGTH_SHORT).show();
                        lati = addresses.get(0).getLatitude();
                        longi = addresses.get(0).getLongitude();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }else {
            //when permission denied
            ActivityCompat.requestPermissions(ListViewActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        LocalDate date=LocalDate.now();
        DayOfWeek dow=date.getDayOfWeek();
        Today=dow.getDisplayName(TextStyle.FULL,Locale.ENGLISH);

        switch (Collection){
            case "CARD1":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Swimming", "Dance", "Badminton", "Football"));
                break;
            case "CARD2":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Swimming", "Zumba", "Badminton", "Boxing"));
                break;
            case "CARD3":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Swimming", "Cricket", "Badminton", "Tennis", "Football"));
                break;
            case "CARD4":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Swimming", "Karate", "Weights", "Tennis", "Football"));
                break;
            case "CARD5":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Cardio", "Zumba", "Badminton", "Dance", "Aerobics"));
                break;
            case "CARD6":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Swimming", "Yoga", "Tennis", "Football"));
                break;
            case "CARD7":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Cardio", "Zumba", "Aerobics", "Dance"));
                break;
            case "CARD8":
                query = db.collection("Activities").whereEqualTo("Status","Active")
                        .whereIn("ActivityType", Arrays.asList("Karate", "WeightLifting", "Crossfit", "Cardio", "Boxing"));
                break;
            default:
                query = db.collection("Activities").whereEqualTo("Status","Active");
        }

        FirestoreRecyclerOptions<activityModel> options = new FirestoreRecyclerOptions.Builder<activityModel>()
                .setQuery(query, activityModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<activityModel, ListViewActivity.ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ListViewActivity.ViewHolder holder, int position, @NonNull activityModel model) {

                switch (model.getActivityType()) {
                    case "Gym":
                        holder.img.setImageResource(R.drawable.gym_rect);
                        break;
                    case "Badminton":
                        holder.img.setImageResource(R.drawable.badminton_rect);
                        break;
                    case "Boxing":
                        holder.img.setImageResource(R.drawable.boxing_rect);
                        break;
                    case "Cricket":
                        holder.img.setImageResource(R.drawable.cricket_rect);
                        break;
                    case "Football":
                        holder.img.setImageResource(R.drawable.football_rect);
                        break;
                    case "Swimming":
                        holder.img.setImageResource(R.drawable.swimming_rect);
                        break;
                    case "Tennis":
                        holder.img.setImageResource(R.drawable.tennis_rect);
                        break;
                    case "Yoga":
                        holder.img.setImageResource(R.drawable.yoga_rect);
                        break;
                    case "Aerobics":
                        holder.img.setImageResource(R.drawable.aerobics_rect);
                        break;
                    case "Cardio":
                        holder.img.setImageResource(R.drawable.cardio_rect);
                        break;
                    case "CrossFit":
                        holder.img.setImageResource(R.drawable.crossfit_rect);
                        break;
                    case "Dance":
                        holder.img.setImageResource(R.drawable.dance_rect);
                        break;
                    case "Karate":
                        holder.img.setImageResource(R.drawable.karate_rect);
                        break;
                    case "Taekwondo":
                        holder.img.setImageResource(R.drawable.taekwondo_rect);
                        break;
                }

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("DOJO").child("Assets").child(model.getAssetNo());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child("City").exists()) {
                            String city = snapshot.child("City").getValue().toString();
                            if (snapshot.child("Profile").child("State").exists()){
                                String state = snapshot.child("Profile").child("State").getValue().toString();
                                location = city + ", " + state;
                                holder.location.setText(location);
                            }
                        }

                        if (snapshot.child("Profile").child("Latitude").exists()){
                            latitude = snapshot.child("Profile").child("Latitude").getValue().toString();
                            longitude = snapshot.child("Profile").child("Longitude").getValue().toString();
                            if (longitude != null) {

                                Location startPoint = new Location("locationA");
                                startPoint.setLatitude(lati);
                                startPoint.setLongitude(longi);

                                Location endPoint = new Location("locationA");
                                endPoint.setLatitude(Double.parseDouble(latitude));
                                endPoint.setLongitude(Double.parseDouble(longitude));

                                dist = startPoint.distanceTo(endPoint);
                                Distance = floor(dist / 1000) + " km from your location";
                                holder.distance.setText(Distance);
                            }
                            else {
                                holder.distance.setText(" ");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.activityName.setText(model.getActivityType());
                holder.assetNo.setText(model.getAssetNo());
                holder.assetName.setText(model.getAssetName());

                double c = Double.parseDouble(String.valueOf(model.getCredit()));
                int cr = (int) c;
                holder.credit.setText(String.valueOf(cr));

                holder.item.setOnClickListener(view -> {
                    /*Intent intent = new Intent(ListViewActivity.this, ViewDetailsActivity.class);
                    intent.putExtra("Id", model.getAssetNo());
                    //intent.putExtra("name", model.getAssetName());
                    intent.putExtra("activityType", model.getActivityType());
                    //intent.putExtra("lati", String.valueOf(lati));
                    //intent.putExtra("longi", String.valueOf(longi));
                    intent.putExtra("credit", String.valueOf(cr));
                    intent.putExtra("amount", model.getPayPerSession());
                    startActivity(intent);*/

                    docid = getSnapshots().getSnapshot(position).getId();

                    FirebaseFirestore.getInstance().collection("Activities").document(docid).collection("TimeSlot").document(Today).get()
                            .addOnCompleteListener(task -> {

                                DocumentSnapshot doc = task.getResult();

                                Day= (List<String>) doc.get(Today);

                                Intent intent1 = new Intent(ListViewActivity.this, ViewDetailsActivity.class);
                                intent1.putExtra("Id", model.getAssetNo());
                                intent1.putExtra("activityType", model.getActivityType());
                                intent1.putExtra("credit", String.valueOf(cr));
                                intent1.putExtra("amount", model.getPayPerSession());
                                intent1.putExtra("phone",phone);

                                intent1.putStringArrayListExtra("Day", (ArrayList<String>) Day);
                                intent1.putExtra("docid",docid);

                                //intent.putExtra("name", model.getAssetName());
                                //intent.putExtra("latitude", latitude);
                                //intent.putExtra("longitude", longitude);
                                //intent.putExtra("location", location);
                                //intent.putExtra("lati", lati);
                                //intent.putExtra("longi", longi);
                                //intent.putExtra("distance", floor(dist / 1000));

                                startActivity(intent1);
                            });
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView assetNo, activityName, assetName, location, credit, distance;
        RelativeLayout item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img =  itemView.findViewById(R.id.img);
            assetNo = itemView.findViewById(R.id.assetNo);
            activityName = itemView.findViewById(R.id.activityName);
            assetName = itemView.findViewById(R.id.assetName);
            location = itemView.findViewById(R.id.location);
            credit = itemView.findViewById(R.id.credit);
            distance = itemView.findViewById(R.id.distance);
            item = itemView.findViewById(R.id.item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }
}