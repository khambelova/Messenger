package com.fbusers.tom.diploma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fbusers.tom.diploma.contacts.DataAdapter;

import java.util.List;

public class DataAdapterMessage extends RecyclerView.Adapter<DataAdapterMessage.ViewHolder>
{
    private LayoutInflater inflater;
    private List<Message> messages;

    DataAdapterMessage(Context context, List<Message> messages)
    {
        this.messages = messages;
        this.inflater= LayoutInflater.from(context);
    }

    @Override
    public DataAdapterMessage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataAdapterMessage.ViewHolder holder, int position) {

        Message message = messages.get(position);
        holder.message.setText(message.message);

        if (message.isYou()) {
            holder.user.setText(Profile.getNickname());
            holder.message.setBackgroundColor(Color.parseColor("#FF029789"));
            holder.messageLayout.setGravity(Gravity.RIGHT);
        } else {
            holder.user.setText(MessagesActivity.friendNick);
            holder.message.setBackgroundColor(Color.WHITE);
            holder.messageLayout.setGravity(Gravity.LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        final TextView message, user;
        final LinearLayout messageLayout;

        public ViewHolder(View view) {
            super(view);
            this.message = (TextView) view.findViewById(R.id.message);
            this.user = (TextView) view.findViewById(R.id.user);
            this.messageLayout = (LinearLayout) view.findViewById(R.id.message_layout);
        }
    }
}

