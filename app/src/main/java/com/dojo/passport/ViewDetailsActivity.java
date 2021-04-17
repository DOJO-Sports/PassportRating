package com.dojo.passport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import static java.lang.Math.floor;

public class ViewDetailsActivity extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    RelativeLayout toMessageActivity;
    ImageView imageView;
    TextView activityName1, activityName2, location, credit, time;
    TextView assetName, address;
    double latitude, longitude, lati, longi;
    String Id, name, activityType, loc, Credit, locate;
    double amount;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter adapter;
    RelativeLayout toMap;
    Button reserveBtn, share;
    double dist;
    String Distance;
    String token,phone;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> Day;
    String docid;
    TextView todaydate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        Uri uri = getIntent().getData();
        if (uri != null) {
            String path = uri.toString();

            String id = path.substring(path.indexOf("/-") + 2, path.indexOf("#"));
            String Type;
            if (id.substring(3).equals("D")) {
                Type = "DOJO";
            } else {
                Type = "STUDIO";
            }

            Id = Type + " " + id.substring(0, 3);
            activityType = path.substring(path.indexOf("#") + 1, path.indexOf("?"));
            amount = Double.parseDouble(path.substring(path.indexOf("?") + 1, path.indexOf("=") - 1));
            Credit = path.substring(path.indexOf("=") + 1);

        } else {

            Bundle bundle = getIntent().getExtras();
            Id = bundle.getString("Id");
            phone = bundle.getString("phone");
            activityType = bundle.getString("activityType");
            Credit = bundle.getString("credit");
            amount = bundle.getDouble("amount");

            docid = bundle.getString("docid");
            Day = bundle.getStringArrayList("Day");
        }

        share = findViewById(R.id.share);
        toMessageActivity = findViewById(R.id.toMessageActivity);
        imageView = findViewById(R.id.imageView);
        activityName1 = findViewById(R.id.activityName1);
        activityName2 = findViewById(R.id.activityName2);
        location = findViewById(R.id.location);
        assetName = findViewById(R.id.assetName);
        address = findViewById(R.id.address);
        recyclerView = findViewById(R.id.recyclerView);
        reserveBtn = findViewById(R.id.reserveBtn);
        toMap = findViewById(R.id.toMap);
        credit = findViewById(R.id.credit);
        time = findViewById(R.id.time);

        todaydate = findViewById(R.id.todaydate);
        Date date = new java.util.Date();
        String Today = date.toString().substring(0, 10);
        todaydate.setText(Today);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //permission check
        if (ActivityCompat.checkSelfPermission(ViewDetailsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permission granted
            fusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(task -> {

                        Location location = task.getResult();
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(ViewDetailsActivity.this,
                                        Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(
                                        location.getLatitude(),
                                        location.getLongitude(),
                                        1
                                );

                                Toast.makeText(ViewDetailsActivity.this, "got the Location", Toast.LENGTH_SHORT).show();
                                lati = addresses.get(0).getLatitude();
                                longi = addresses.get(0).getLongitude();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } else {
            //when permission denied
            ActivityCompat.requestPermissions(ViewDetailsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        switch (activityType) {
            case "Gym":
                imageView.setImageResource(R.drawable.gym_big);
                break;
            case "Badminton":
                imageView.setImageResource(R.drawable.badminton_big);
                break;
            case "Boxing":
                imageView.setImageResource(R.drawable.boxing_big);
                break;
            case "Cricket":
                imageView.setImageResource(R.drawable.cricket_big);
                break;
            case "Football":
                imageView.setImageResource(R.drawable.football_big);
                break;
            case "Swimming":
                imageView.setImageResource(R.drawable.swimming_big);
                break;
            case "Tennis":
                imageView.setImageResource(R.drawable.tennis_big);
                break;
            case "Yoga":
                imageView.setImageResource(R.drawable.yoga_big);
                break;
            case "Aerobics":
                imageView.setImageResource(R.drawable.aerobics_big);
                break;
            case "Cardio":
                imageView.setImageResource(R.drawable.cardio_big);
                break;
            case "CrossFit":
                imageView.setImageResource(R.drawable.crossfit_big);
                break;
            case "Dance":
                imageView.setImageResource(R.drawable.dance_big);
                break;
            case "Karate":
                imageView.setImageResource(R.drawable.karate_big);
                break;
            case "Taekwondo":
                imageView.setImageResource(R.drawable.taekwondo_big);
                break;
        }

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("DOJO").child("Assets").child(Id);
        reference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Name").exists()) {
                    name = snapshot.child("Name").getValue().toString();
                    assetName.setText(Id + " " + name);
                }

                if (snapshot.child("City").exists()) {
                    String city = snapshot.child("City").getValue().toString();
                    if (snapshot.child("Profile").child("State").exists()) {
                        String state = snapshot.child("Profile").child("State").getValue().toString();
                        loc = city + ", " + state;
                        location.setText(loc);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        activityName1.setText(activityType);
        activityName2.setText(activityType);
        credit.setText(Credit + " Credits");

        GridLayout l1 = findViewById(R.id.monlay);
        Random r = new Random();

        int i = 0;
        if (Day != null) {
            if (Day.size() < 3) {
                for (String s : Day) {
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.bottomMargin = 15;
                    params.rightMargin = 1;
                    l1.setVisibility(View.VISIBLE);
                    Button v1 = new Button(getBaseContext());
                    v1.setLayoutParams(params);
                    v1.setText(s);
                    v1.setId(i++);
                    v1.setBackgroundColor(Color.WHITE);

                    l1.addView(v1);
                }
            } else {
                for (int j = 0; j < 3; j++) {
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                    params.bottomMargin = 15;
                    params.rightMargin = 1;
                    l1.setVisibility(View.VISIBLE);
                    Button v1 = new Button(getBaseContext());

                    v1.setLayoutParams(params);
                    v1.setText(Day.get(j));
                    v1.setId(j);
                    v1.setBackgroundColor(Color.WHITE);
                    l1.addView(v1);
                }
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.width = GridLayout.LayoutParams.WRAP_CONTENT;
                params.bottomMargin = 15;
                params.rightMargin = 1;
                l1.setVisibility(View.VISIBLE);
                Button v1 = new Button(getBaseContext());
                v1.setLayoutParams(params);
                v1.setText("More");
                v1.setId(0);
                v1.setBackgroundColor(Color.RED);
                l1.addView(v1);
                v1.setOnClickListener(v -> {
                    l1.removeView(v1);
                    for (int j = 3; j < Day.size(); j++) {
                        GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();
                        params1.height = GridLayout.LayoutParams.WRAP_CONTENT;
                        params1.width = GridLayout.LayoutParams.WRAP_CONTENT;
                        params1.bottomMargin = 15;
                        params1.rightMargin = 1;
                        params1.rightMargin = 15;
                        l1.setVisibility(View.VISIBLE);
                        Button v11 = new Button(getBaseContext());
                        v11.setLayoutParams(params1);
                        v11.setText(Day.get(j));
                        v11.setId(j);
                        v11.setBackgroundColor(Color.WHITE);
                        l1.addView(v11);
                    }
                });
            }
        }

        DatabaseReference reference2 = reference1.child("Profile");
        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    address.setText(Objects.requireNonNull(snapshot.child("Address").getValue()).toString());
                }
                if (snapshot.child("Latitude").exists()) {
                    latitude = Double.parseDouble((String) snapshot.child("Latitude").getValue());
                    longitude = Double.parseDouble((String) snapshot.child("Longitude").getValue());

                    if (longitude != 0) {

                        Location startPoint = new Location("locationA");
                        startPoint.setLatitude(lati);
                        startPoint.setLongitude(longi);

                        Location endPoint = new Location("locationA");
                        endPoint.setLatitude(latitude);
                        endPoint.setLongitude(longitude);

                        dist = startPoint.distanceTo(endPoint);
                        Distance = floor(dist / 1000) + " km from your location";
                        time.setText(floor((dist / 1000) / 60) + " hrs");

                        toMap.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("geo:" + latitude + "," + longitude));
                            Intent chooser = Intent.createChooser(intent, "Launch Maps");
                            startActivity(chooser);
                        });
                    } else {
                        time.setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(ViewDetailsActivity.this, "token generation failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (null != task.getResult()) {
                        token = Objects.requireNonNull(task.getResult());
                    }
                });

        toMessageActivity.setOnClickListener(view -> {

            if (!token.isEmpty()) {
                Intent intent = new Intent(ViewDetailsActivity.this, MessageActivity.class);
                intent.putExtra("Token", token);
                startActivity(intent);
            } else {
                Toast.makeText(ViewDetailsActivity.this, "Try after sometime", Toast.LENGTH_SHORT).show();
            }
        });

        share.setOnClickListener(v -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, activityType + " from " + name + "\n https://www.dojosports.in" + "/-" + Id.substring(Id.length() - 3) + Id.charAt(0) + "#" + activityType + "?" + amount + "=" + Credit);
            shareIntent.setType("text/plain");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        //recyclerview query
        Query query = db.collection("Activities").whereEqualTo("Status","Active");

        FirestoreRecyclerOptions<activitySuggestionModel> options = new FirestoreRecyclerOptions.Builder<activitySuggestionModel>()
                .setQuery(query.whereEqualTo("ActivityType", activityType)/*.whereNotEqualTo("AssetNo", Id)*/, activitySuggestionModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<activitySuggestionModel, ViewDetailsActivity.ViewHolder>(options) {
            @NonNull
            @Override
            public ViewDetailsActivity.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item, parent, false);
                return new ViewDetailsActivity.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewDetailsActivity.ViewHolder holder, int position, @NonNull activitySuggestionModel model) {

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
                            if (snapshot.child("Profile").child("State").exists()) {
                                String state = snapshot.child("Profile").child("State").getValue().toString();
                                locate = city + ", " + state;
                                holder.location.setText(locate);
                            }
                        }

                        if (snapshot.child("Profile").child("Latitude").exists()) {
                            double Latitude, Longitude;
                            Latitude = Double.parseDouble((String) snapshot.child("Profile").child("Latitude").getValue());
                            Longitude = Double.parseDouble((String) snapshot.child("Profile").child("Longitude").getValue());
                            //longitude = snapshot.child("Profile").child("Longitude").getValue().toString();
                            if (longitude != 0) {

                                Location startPoint = new Location("locationA");
                                startPoint.setLatitude(lati);
                                startPoint.setLongitude(longi);

                                Location endPoint = new Location("locationA");
                                endPoint.setLatitude(Latitude);
                                endPoint.setLongitude(Longitude);

                                dist = startPoint.distanceTo(endPoint);
                                Distance = floor(dist / 1000) + " km from your location";
                                holder.time.setText(floor((dist / 1000) / 60) + " hrs");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                holder.activityName.setText(model.getActivityType());
                holder.assetName.setText(model.getAssetName());
                double c = Double.parseDouble(String.valueOf(model.getCredit()));
                int cr = (int) c;
                holder.credit.setText(String.valueOf(cr));
                holder.item.setOnClickListener(view -> {
                    Intent intent = new Intent(ViewDetailsActivity.this, ViewDetailsActivity.class);
                    intent.putExtra("Id", model.getAssetNo());
                    intent.putExtra("name", model.getAssetName());
                    intent.putExtra("activityType", model.getActivityType());
                    intent.putExtra("amount", model.getPayPerSession());
                    intent.putExtra("lati", lati);
                    intent.putExtra("longi", longi);
                    intent.putExtra("credit", String.valueOf(cr));
                    startActivity(intent);
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        reserveBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ViewDetailsActivity.this, ConfirmActivity.class);
            intent.putExtra("activityType", activityType);
            intent.putExtra("name", name);
            intent.putExtra("Id", Id);
            intent.putExtra("credit", Credit);
            intent.putExtra("amount", amount);
            intent.putExtra("phone",phone);
            startActivity(intent);
        });
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView activityName, assetName, time, credit, location;
        RelativeLayout item;
        GridLayout l1, l2, l3, l4, l5, l6, l7;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            activityName = itemView.findViewById(R.id.activityName);
            assetName = itemView.findViewById(R.id.assetName);
            location = itemView.findViewById(R.id.location);
            credit = itemView.findViewById(R.id.credit);
            time = itemView.findViewById(R.id.time);
            item = itemView.findViewById(R.id.item);

            l1 = itemView.findViewById(R.id.monlay);
            l2 = itemView.findViewById(R.id.tueslay);
            l3 = itemView.findViewById(R.id.wedlay);
            l4 = itemView.findViewById(R.id.thurslay);
            l5 = itemView.findViewById(R.id.frilay);
            l6 = itemView.findViewById(R.id.satlay);
            l7 = itemView.findViewById(R.id.sunlay);
        }
    }
}