package com.dojo.passport;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static android.view.KeyEvent.KEYCODE_DEL;

public class LoginActivity extends AppCompatActivity {

    String PhoneNumber = "", PhoneValue;
    TextView PhoneNum, termnCondition;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    LinearLayout NameEmailLayout;
    Button createAccount, Submit;
    EditText[] otpETs = new EditText[6];
    private String VerificationID;
    FirebaseDatabase firebaseDatabase;
    EditText Email, Name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intentReceived = getIntent();
        Bundle data = intentReceived.getExtras();
        if (data != null){
            PhoneNumber = data.getString("PhoneNumber");
        }
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Customer's").child(PhoneNumber);

        PhoneNum = findViewById(R.id.phoneNum);
        NameEmailLayout = findViewById(R.id.linearLayout5);
        termnCondition = findViewById(R.id.termnCondition);
        createAccount = findViewById(R.id.create_account);
        Submit = findViewById(R.id.submit);
        otpETs[0] = findViewById(R.id.otpET1);
        otpETs[1] = findViewById(R.id.otpET2);
        otpETs[2] = findViewById(R.id.otpET3);
        otpETs[3] = findViewById(R.id.otpET4);
        otpETs[4] = findViewById(R.id.otpET5);
        otpETs[5] = findViewById(R.id.otpET6);
        Email = findViewById(R.id.email);
        Name = findViewById(R.id.personName);

        PhoneNum.setText(PhoneNumber);

        databaseReference.child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PhoneValue = (String) snapshot.child("phone").getValue();
                if (PhoneValue != null && PhoneValue.equals(PhoneNumber)){
                    //welcome back
                    NameEmailLayout.setVisibility(View.GONE);
                    termnCondition.setVisibility(View.GONE);
                    createAccount.setVisibility(View.GONE);
                    Submit.setVisibility(View.VISIBLE);

                    sendVerificationCode(PhoneNumber);
                    Submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Xcode = Helpers.rS(otpETs[0]) + Helpers.rS(otpETs[1]) + Helpers.rS(otpETs[2]) + Helpers.rS(otpETs[3])
                                    + Helpers.rS(otpETs[4]) + Helpers.rS(otpETs[5]);

                            verifyVerificationCode(Xcode);
                        }
                    });

                } else {
                    NameEmailLayout.setVisibility(View.VISIBLE);
                    termnCondition.setVisibility(View.VISIBLE);
                    createAccount.setVisibility(View.VISIBLE);
                    Submit.setVisibility(View.GONE);

                    sendVerificationCode(PhoneNumber);
                    createAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String email = Email.getText().toString().trim();
                            String name = Name.getText().toString().trim();

                            if(email.isEmpty() && !(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
                                Email.setError("Enter a valid email address");
                                return;
                            }
                            else if (name.isEmpty()){
                                Name.setError("Please enter your name");
                                return;
                            }
                            String Xcode = Helpers.rS(otpETs[0]) + Helpers.rS(otpETs[1]) + Helpers.rS(otpETs[2]) + Helpers.rS(otpETs[3])
                                    + Helpers.rS(otpETs[4]) + Helpers.rS(otpETs[5]);

                            verifyVerificationCode(Xcode);

                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationID, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (PhoneValue == null){
                                AddCustomerDB();
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("Phone",PhoneNumber);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void AddCustomerDB(){

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Customer's").child(PhoneNumber).child("Profile");

        CustomerDB customerDB = new CustomerDB(Email.getText().toString(), Name.getText().toString(), PhoneNumber);
        databaseReference.setValue(customerDB);
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,
                20,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            VerificationID = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String otp = phoneAuthCredential.getSmsCode();

            if (PhoneValue!= null){
                if (otp != null) {
                    verifyVerificationCode(otp);
                }
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    public static class Helpers {

        public static String rS(EditText editText) {
            return editText.getText().toString().trim();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (keyCode == 7 || keyCode == 8 ||
                keyCode == 9 || keyCode == 10 ||
                keyCode == 11 || keyCode == 12 ||
                keyCode == 13 || keyCode == 14 ||
                keyCode == 15 || keyCode == 16 ||
                keyCode == 67) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KEYCODE_DEL) {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            if (index != 0) {
                                otpETs[index - 1].requestFocus();
                            }
                        } else {
                            return super.dispatchKeyEvent(event);
                        }
                    }
                } else {
                    int index = checkWhoHasFocus();
                    if (index != 123) {
                        if (Helpers.rS(otpETs[index]).equals("")) {
                            return super.dispatchKeyEvent(event);
                        } else {
                            if (index != 5) {
                                otpETs[index + 1].requestFocus();
                            }
                        }
                    }
                    return super.dispatchKeyEvent(event);
                }
            }
        } else {
            return super.dispatchKeyEvent(event);
        }
        return true;
    }


    private int checkWhoHasFocus() {
        for (int i = 0; i < otpETs.length; i++) {
            EditText tempET = otpETs[i];
            if (tempET.hasFocus()) {
                return i;
            }
        }
        return 123;
    }

}