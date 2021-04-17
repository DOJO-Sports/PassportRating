package com.dojo.passport;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.dojo.passport.Notifications.APIService;
import com.dojo.passport.Notifications.Client;
import com.dojo.passport.Notifications.Data;
import com.dojo.passport.Notifications.MyResponse;
import com.dojo.passport.Notifications.Sender;
import com.dojo.passport.Notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.valueOf;

public class MessageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    EditText text;
    ImageButton sendBtn;
    ImageView call, more, backBtn, image;
    String token;
    List<ChatModel> mchat;
    LinearLayoutManager linearLayoutManager;
    MessageAdapter adapter;
    FirebaseUser fuser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String phone, name;
    Map<String, Object> addToDB = new HashMap<>();
    Map<String, Object> addDB = new HashMap<>();
    Map<String, Object> send1 = new HashMap<>();
    Map<String, Object> send2 = new HashMap<>();

    APIService apiService;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    String[] cameraPermissions;
    String[] storagePermissions;
    Uri image_uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> onBackPressed());

        image = findViewById(R.id.img_send);
        recyclerView = findViewById(R.id.recyclerView);
        text = findViewById(R.id.text);
        sendBtn = findViewById(R.id.sendBtn);
        call = findViewById(R.id.call);
        more = findViewById(R.id.more);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        phone = fuser.getPhoneNumber();

        Bundle bundle = getIntent().getExtras();
        token = bundle.getString("Token");

        //getting name
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Customer's").child(phone).child("Profile").child("name");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name = snapshot.getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Support").child("Chats").child(uid);

        //reading messages on data change
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readMessage(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //first message
        ref.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.hasChildren()) {

                    Date date = new Date();

                    send1.put("Message", "Hello, Welcome to DOJO Support");
                    send1.put("Send", "0");
                    send1.put("Seen", true);
                    send1.put("Type", "Text");
                    send1.put("Time", date);

                    ref.child("0").setValue(send1)
                            .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "FAILED", Toast.LENGTH_SHORT).show())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    send2.put("Message", "An Agent will be assigned to you shortly, Meanwhile it would be fine, if you tell us your query");
                                    send2.put("Send", "0");
                                    send2.put("Seen", true);
                                    send2.put("Type", "Text");
                                    send2.put("Time", date);

                                    ref.child("1").setValue(send2)
                                            .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "FAILED", Toast.LENGTH_SHORT).show());

                                }
                            });

                    Map<String, Object> hashMap = new HashMap<>();

                    Timestamp time = Timestamp.now();
                    hashMap.put("Message", "An Agent will be assigned to you shortly, Meanwhile it would be fine, if you tell us your query");
                    hashMap.put("Name", name);
                    hashMap.put("Time", time);
                    hashMap.put("Phone", phone.substring(3));
                    hashMap.put("Token", token);

                    db.collection("Support").document(uid).set(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        DocumentReference docRef = db.collection("Support").document(uid);

        //showing alert dialog
        db.collection("Support").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if (doc.exists()){

                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (value.get("Message").equals("Resolved")){

                                AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                                builder.setMessage("Looks like your issue is resolved");
                                builder.setTitle("Issue Resolved");

                                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference().child("Support").child("Chats").child(uid).removeValue();
                                        db.collection("Support").document(uid).delete();
                                        //onBackPressed();
                                        Intent intent = new Intent(getApplicationContext(), ViewDetailsActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                            }
                        }
                    });
                }
            }
        });

        call.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:8825947843"));
            startActivity(intent);
        });

        image.setOnClickListener(v -> showImagePickDialogue());

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //sending text msg
        sendBtn.setOnClickListener(v -> {

            if (!TextUtils.isEmpty(text.getText().toString())) {

                Date date = new Date();
                Timestamp time = Timestamp.now();
                String msg = text.getText().toString();
                text.setText("");

                addDB.put("Message", msg);
                addDB.put("Send", "1");
                addDB.put("Seen", false);
                addDB.put("Type", "Text");
                addDB.put("Time", date);

                addToDB.put("Message", msg);
                addToDB.put("Name", name);
                addToDB.put("Time", time);
                addToDB.put("Phone", phone.substring(3));
                addToDB.put("Token", token);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String k = valueOf(snapshot.getChildrenCount());
                        ref.child(k).setValue(addDB)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        db.collection("Support")
                                                .document(uid)
                                                .set(addToDB)
                                                .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Try after some time", Toast.LENGTH_SHORT).show());

                                        sendNotifications(name, msg);
                                    }
                                })
                                .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "PLease try after some time", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            } else {
                Toast.makeText(this, "Empty message cannot be sent", Toast.LENGTH_SHORT).show();
            }
        });
        //UpdateToken();
    }

    private void readMessage(String uid) {
        mchat = new ArrayList<>();
        mchat.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Support").child("Chats").child(uid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    mchat.add(chat);

                    if (!mchat.isEmpty()) {
                        adapter = new MessageAdapter(MessageActivity.this, mchat);
                        recyclerView.removeAllViews();
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showImagePickDialogue() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                }
                if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.show();
    }

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                sendImageMessage(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImageMessage(image_uri);
            }
        }
    }

    private void sendImageMessage(Uri image_uri) {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Sending..");
        pd.show();

        Date date = new Date();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Support").child("Chats").child(uid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String k = valueOf(snapshot.getChildrenCount());
                String fileNameAndPath = "Support/" + uid + "/" + k;

                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), image_uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
                    ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while ((!uriTask.isSuccessful())) ;
                            //String downloaduri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()) {
                                String downloaduri = uriTask.getResult().toString();
                                Map<String, Object> send = new HashMap<>();

                                send.put("Message", downloaduri);
                                send.put("Send", "1");
                                send.put("Time", date);
                                send.put("Type", "Image");
                                send.put("Seen", false);

                                reference.child(k).setValue(send)
                                        .addOnFailureListener(e -> Toast.makeText(MessageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());

                            } else {
                                Toast.makeText(MessageActivity.this, "Task is not successful", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MessageActivity.this, "failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //readMessage(uid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        FirebaseFirestore.getInstance().collection("Support").document(uid).update("Message", "Photo");
        FirebaseFirestore.getInstance().collection("Support").document(uid).update("Time", date);
        sendNotifications(name, "Photo");
        //UpdateToken();
    }

    //not called anywhere
    private void UpdateToken() {
        Map<String, Object> addDB = new HashMap<>();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String TOKEN = task.getResult();
                        Token token = new Token(TOKEN);
                        addDB.put("Token", token);
                        db.collection("Support").document(uid).set(addDB, SetOptions.merge());
                    } else {
                        Toast.makeText(MessageActivity.this, "task failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendNotifications(String title, String message) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DojoTokens");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String token = snapshot.child("token").getValue().toString();

                Data data = new Data(title, message);
                Sender sender = new Sender(data, token);
                apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                    @Override
                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                        if (response.code() == 200) {
                            if (!response.isSuccessful()){
                                Toast.makeText(MessageActivity.this, " Failed ", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MyResponse> call, Throwable t) {
                        Toast.makeText(MessageActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}