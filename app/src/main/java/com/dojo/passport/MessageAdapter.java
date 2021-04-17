package com.dojo.passport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.dojo.passport.ChatModel;
import com.dojo.passport.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private final Context mContext;
    private final List<ChatModel> mChat;
    View view;

    public MessageAdapter(Context mContext, List<ChatModel> mChat) {
        this.mChat = mChat;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        }else {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChatModel chat = mChat.get(position);

        if(chat.getType().equals("Text")) {
            holder.show_message.setVisibility(View.VISIBLE);
            holder.show_image.setVisibility(View.GONE);
            holder.show_message.setText(chat.getMessage());
        }
        else if(chat.getType().equals("Image")) {
            holder.show_message.setVisibility(View.GONE);
            holder.show_image.setVisibility(View.VISIBLE);
            Picasso.get().load(chat.getMessage()).placeholder(R.drawable.image_placeholder).into(holder.show_image);
        }

        holder.send_time.setText(new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(chat.getTime()));

        if(chat.getSeen() && chat.getSend().equals("1")) {
            holder.seen.setVisibility(View.VISIBLE);
            holder.unseen.setVisibility(View.GONE);
        }
        /*else if(!chat.getSeen() && chat.getSend().equals("1")) {
            holder.unseen.setVisibility(View.VISIBLE);
            holder.seen.setVisibility(View.GONE);
        }*/
        else {
            holder.unseen.setVisibility(View.VISIBLE);
            holder.seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView show_message;
        ImageView show_image;
        public TextView send_time;
        ImageView seen,unseen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            send_time = itemView.findViewById(R.id.time_send);
            show_image = itemView.findViewById(R.id.show_image);
            seen = itemView.findViewById(R.id.seen);
            unseen = itemView.findViewById(R.id.unseen);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mChat.get(position).getSend().equals("1")) {
            return MSG_TYPE_RIGHT;
        }
        else if (mChat.get(position).getSend().equals("0")) {
            //mChat.get(position).setSeen(true);
            return MSG_TYPE_LEFT;
        }
        else {
            return MSG_TYPE_RIGHT;
        }
    }
}
