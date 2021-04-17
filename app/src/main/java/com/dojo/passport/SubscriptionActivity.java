package com.dojo.passport;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.easyupipayment.EasyUpiPayment;
import com.shreyaspatil.easyupipayment.exception.AppNotFoundException;
import com.shreyaspatil.easyupipayment.listener.PaymentStatusListener;
import com.shreyaspatil.easyupipayment.model.TransactionDetails;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class SubscriptionActivity extends AppCompatActivity {

    Button Subscribe;
    ImageView backBtn;
    FirebaseAuth mAuth;
    FirebaseUser user;
    String Phone, membershipPrice, membershipCredit;
    TextView Price, Credit;
    boolean applied = false;
    HashMap bookingMap;
    String TotalAmountToPay;
    String ExpiryDate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        Subscribe = findViewById(R.id.subscribe);
        backBtn = findViewById(R.id.backBtn);
        Price = findViewById(R.id.price);
        Credit = findViewById(R.id.credits);

        bookingMap = new HashMap<String, String>();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user = mAuth.getCurrentUser();
            Phone = user.getPhoneNumber();
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        Date date = cal.getTime();

        ExpiryDate = simpleDateFormat.format(date);

        Log.d("check", ExpiryDate);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference("Passport").child("MembershipPrice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                membershipPrice = snapshot.child("Price").getValue().toString();
                membershipCredit = snapshot.child("Credit").getValue().toString();
                Price.setText("₹"+membershipPrice);
                Credit.setText("Includes "+ membershipCredit+ " credits");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog dialog = new BottomSheetDialog(SubscriptionActivity.this);
                dialog.setContentView(R.layout.dailog_payment);
                String setText = membershipPrice;
                ImageView closeDialog = dialog.findViewById(R.id.closeDialog);
                TextView apply = dialog.findViewById(R.id.apply);
                TextView remove = dialog.findViewById(R.id.remove);
                TextView retry = dialog.findViewById(R.id.retry);
                TextView membershipPrice = dialog.findViewById(R.id.membership_price);
                Button continueToPay = dialog.findViewById(R.id.continue_to_pay);
                ConstraintLayout CutPrice = dialog.findViewById(R.id.cut_price);
                TextView cuttedPrice = dialog.findViewById(R.id.cutted_price);
                EditText cupon = dialog.findViewById(R.id.cupon);

                DatabaseReference couponRef = FirebaseDatabase.getInstance().getReference("Passport");

                apply.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
                retry.setVisibility(View.GONE);
                CutPrice.setVisibility(View.INVISIBLE);


                continueToPay.setText("CONTINUE TO PAY ₹ "+setText);

                membershipPrice.setText("₹"+setText);

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cupon.getText().toString().isEmpty()){
                            cupon.setError("Enter Coupon Code");
                            cupon.requestFocus();
                            return;
                        }


                        couponRef.child("Membership").child("Discount").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

//                                Log.d("check",snapshot.getValue().toString());

                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){

//                                for (int i=0;i<snapshot.getChildrenCount();i++){

                                    if(dataSnapshot.child("CouponCode").getValue().toString().equals(cupon.getText().toString())){
                                        if (dataSnapshot.child("Status").getValue().toString().equals("Active")){
                                            applied = true;
                                            int discountPrice = Integer.parseInt(dataSnapshot.child("Discount").getValue().toString());
                                            int actualPrice = Integer.parseInt(setText);
                                            CutPrice.setVisibility(View.VISIBLE);
                                            cuttedPrice.setText("₹"+setText);
                                            int newPrice = actualPrice-discountPrice;
                                            continueToPay.setText("CONTINUE TO PAY ₹ "+newPrice);
                                            membershipPrice.setText("₹"+newPrice);
                                            cupon.setText("Coupon code applied successfully");
                                            apply.setVisibility(View.GONE);
                                            remove.setVisibility(View.VISIBLE);
                                            retry.setVisibility(View.GONE);
                                        }

                                    }
                                }
                                if (!applied){
                                    cupon.setText("Coupon code not valid");
                                    apply.setVisibility(View.GONE);
                                    remove.setVisibility(View.GONE);
                                    retry.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                retry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cupon.setText("");
                        retry.setVisibility(View.GONE);
                        apply.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.GONE);
                    }
                });

                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cupon.setText("");
                        continueToPay.setText("CONTINUE TO PAY ₹ "+setText);
                        membershipPrice.setText("₹"+setText);
                        CutPrice.setVisibility(View.INVISIBLE);
                        retry.setVisibility(View.GONE);
                        apply.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.GONE);
                    }
                });

                continueToPay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Toast.makeText(SubscriptionActivity.this, "To UPI app", Toast.LENGTH_SHORT).show();

                        TotalAmountToPay = membershipPrice.getText().toString().substring(1);

                        onTransactionSuccess();

                        EasyUpiPayment.Builder builder = new EasyUpiPayment.Builder(SubscriptionActivity.this)
                                .setPayeeVpa("deep.anuraj10@oksbi")
                                .setPayeeName("Anuraj Deep")
                                .setPayeeMerchantCode("12345")
                                .setTransactionId("T2020090212345")
                                .setTransactionRefId("T2020090212345")
                                .setDescription("Description")
                                .setAmount(TotalAmountToPay+".00");

                        try {
                            EasyUpiPayment easyUpiPayment = builder.build();
                            easyUpiPayment.startPayment();
                            easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
                                @Override
                                public void onTransactionCompleted(TransactionDetails transactionDetails) {
                                    Log.d("check",transactionDetails.toString());
                                    Log.d("check",transactionDetails.getTransactionStatus().toString());

                                    switch (transactionDetails.getTransactionStatus()) {
                                        case SUCCESS:
                                            onTransactionSuccess();
                                            break;
                                        case FAILURE:
                                            onTransactionFailed();
                                            break;
                                        case SUBMITTED:
                                            onTransactionSubmitted();
                                            break;
                                    }

                                }

                                @Override
                                public void onTransactionCancelled() {
                                    Toast.makeText(SubscriptionActivity.this, "Transaction Canceled", Toast.LENGTH_SHORT).show();
                                }

                            });
                        } catch (AppNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });
                dialog.show();
            }
        });
    }

    private void onTransactionSuccess(){

        bookingMap.put("Paid_Amount",TotalAmountToPay);
        bookingMap.put("Credits", membershipCredit);
        bookingMap.put("Status","Complete");
        bookingMap.put("ExpiryDate",ExpiryDate);
        bookingMap.put("BookingId", genString());
        FirebaseDatabase.getInstance().getReference("Customer's").child(Phone).child("Membership").setValue(bookingMap);

        Intent intent = new Intent(SubscriptionActivity.this, SubscriptionActiveActivity.class);
        startActivity(intent);
        finish();

    }
    private void onTransactionFailed(){

        bookingMap.put("Paid_Amount",TotalAmountToPay);
        bookingMap.put("Credits", membershipCredit);
        bookingMap.put("Status","Failed");
        bookingMap.put("BookingId", genString());
        FirebaseDatabase.getInstance().getReference("Customer's").child(Phone).child("Membership").setValue(bookingMap);

    }
    private void onTransactionSubmitted(){

        bookingMap.put("Paid_Amount",TotalAmountToPay);
        bookingMap.put("Credits", membershipCredit);
        bookingMap.put("Status","Submitted");
        bookingMap.put("BookingId", genString());
        FirebaseDatabase.getInstance().getReference("Customer's").child(Phone).child("Membership").setValue(bookingMap);
    }

    public String genString() {
        final char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        final Random random = new Random();
        String randomStr = "";
        for (int i = 0; i < 10; i++) {
            randomStr += chars[random.nextInt(chars.length)];
        }
        return randomStr;
    }

}