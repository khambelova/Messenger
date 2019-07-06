package com.fbusers.tom.diploma.contacts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.fbusers.tom.diploma.R;
import java.util.List;

/**
 * Created by Tom on 22.04.2018.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Contact item);
    }

    private LayoutInflater inflater;
    private List<Contact> contacts;
    private OnItemClickListener listener;

    DataAdapter (Context context, List<Contact> contacts, OnItemClickListener listener) {
        this.contacts = contacts;
        this.inflater= LayoutInflater.from(context);
        this.listener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        //holder.imageView.setImageResource(contact.getImage());
        holder.nameView.setText(contact.getName());
        holder.phoneView.setText(contact.getPhone());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(contact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        final ImageView imageView;
        final TextView nameView, phoneView;

        ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            nameView = (TextView) view.findViewById(R.id.name);
            phoneView = (TextView) view.findViewById(R.id.phone);
        }
    }
}
