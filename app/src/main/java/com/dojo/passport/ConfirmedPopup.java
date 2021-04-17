package com.dojo.passport;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class ConfirmedPopup {
    public void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.confirmed_popup);

        Button backBtn = dialog.findViewById(R.id.backBtn);
        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());
        backBtn.setOnClickListener(view -> {
            dialog.dismiss();
            Intent intent = new Intent(activity, ListViewActivity.class);
            activity.startActivity(intent);
        });

        dialog.show();

    }
}