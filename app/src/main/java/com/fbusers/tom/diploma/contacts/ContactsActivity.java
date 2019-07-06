package com.fbusers.tom.diploma.contacts;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fbusers.tom.diploma.MessagesActivity;
import com.fbusers.tom.diploma.Profile;
import com.fbusers.tom.diploma.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Tom on 05.05.2018.
 */

public class ContactsActivity extends AppCompatActivity {

    @BindView(R.id.add)
    Button btn_add;

    @BindView(R.id.list)
    RecyclerView recyclerView;

    DatabaseReference reference;
    DataAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        if(getIntent().getStringExtra("contacts") != null) {
            boolean isContact = Boolean.valueOf(getIntent().getStringExtra("contacts"));
            btn_add.setVisibility(isContact ? View.VISIBLE : View.INVISIBLE);
        }

        adapter = new DataAdapter(this, Profile.getContacts(), new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Contact item) {
                Toast.makeText(getApplicationContext(),"Написать сообщение", Toast.LENGTH_LONG).show();
                goToChat(item.getPhone());
            }
        });
        recyclerView.setAdapter(adapter);

        reference = FirebaseDatabase.getInstance().getReference();
    }

    @OnClick(R.id.add)
    public void onClick(View view){
        Intent intent = new Intent(this,AddContactActivity.class);
        startActivityForResult(intent, 1);
    }

    public void goToChat(String phone)
     {
        Intent intent = new Intent(ContactsActivity.this, MessagesActivity.class);
        intent.putExtra("friendPhone", phone);
        startActivity(intent);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
        {
            return;
        }
        String nm = data.getStringExtra("name");
        String ph = data.getStringExtra("phone");
    }

    public static int addPhoto(String name)
    {
        Random rand = new Random();

        if(name.endsWith("а")||name.endsWith("я")){
            return rand.nextInt(11);
        }
        else return rand.nextInt(10)+11;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void RemoveContact()
    {
        //TODO: Вместо пустой строки должен быть номер выбранного контакта
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot con:dataSnapshot.getChildren())
                {
                    if(con.getValue().toString() == "")
                    {
                        reference.child(Profile.getPhone()).child("friends").child(con.getKey().toString()).removeValue();
                        Profile.removeContact("");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child(Profile.getPhone()).child("friends").addListenerForSingleValueEvent(listener);
    }
}
