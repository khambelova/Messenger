package com.fbusers.tom.diploma;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tom on 20.05.2018.
 */

public class MessagesActivity extends AppCompatActivity {

    @BindView(R.id.message_input)
    EditText et_message;

    @BindView(R.id.send_message)
    Button btn_message;

    @BindView(R.id.settingBtn)
    Button btn_setting;

    @BindView(R.id.messages_recycler)
    RecyclerView recyclerView;

    @BindView(R.id.activity_messages)
    LinearLayout linearLayout;

    DataAdapterMessage adapterMessage;

    FirebaseDatabase database;
    DatabaseReference reference;
    ValueEventListener listener;

    public static String friendPhone;
    public static String friendNick;
    List<Message> messages;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        ButterKnife.bind(this);

        if(getIntent().getStringExtra("friendPhone") != null) {
            friendPhone = getIntent().getStringExtra("friendPhone");
            friendNick = Profile.getContact(friendPhone).getNick();
        }
        messages = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapterMessage = new DataAdapterMessage(this, messages);
        recyclerView.setAdapter(adapterMessage);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        startMessaging();
    }


    @OnClick({R.id.send_message, R.id.settingBtn})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.send_message:
            {
                if(et_message.getText().toString().isEmpty())
                {
                    return;
                }
                if(et_message.getText().toString().length() > 150)
                {
                    et_message.setError("Превышен лимит кол-ва символов!");
                    et_message.requestFocus();
                    return;
                }

                sendMessage();
                break;
            }
            case R.id.settingBtn: {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, 1);
                break;
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){return;}
        int color = data.getIntExtra("color",0);
        int size = data.getIntExtra("size",0);
        int background = data.getIntExtra("background",0);
        SetColor(color);
        SetSize(size);
        SetBackground(background);
    }

    private  void startMessaging()
    {
        listener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                //TODO: требуется тестирование! При успешной работе использовать как улучшение оптимизации кода!
                //GenericTypeIndicator<List<Message>> typeIndicator = new GenericTypeIndicator<List<Message>>(){};
                //messages = dataSnapshot.getValue(typeIndicator);

                for (DataSnapshot message : dataSnapshot.getChildren())
                {
                    if(messages.stream().filter(msg -> msg.message.equals(message.child("message").getValue().toString())).count() <= 0)
                    {
                        messages.add(new Message(
                                (boolean)message.child("you").getValue(),
                                message.child("message").getValue().toString()));

                        messages.get(messages.size() - 1).setuId(message.getKey());
                    }
                }

                adapterMessage.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(messages.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child(Profile.getPhone()).child("messages").child(friendPhone).addValueEventListener(listener);
    }

    private void sendMessage()
    {
        //Сохранение сообщения на своем профиле
        reference.child(Profile.getPhone()).child("messages").child(friendPhone).push().setValue(new Message(true,
                et_message.getText().toString()));
        //Сохранение сообщения у собеседника
        reference.child(friendPhone).child("messages").child(Profile.getPhone()).push().setValue(new Message(false,
                et_message.getText().toString()));
        et_message.setText(null);
    }

    private void RemoveMessage(Message message)
    {
        reference.child(Profile.getPhone()).child("messages").child(friendPhone).child(message.getuId()).removeValue();
    }

    private void SetColor(int position_color){
        switch (position_color){
            case Constants.COLOR_MAGENTA:
                et_message.setTextColor(Color.MAGENTA);
                break;
            case Constants.COLOR_RED:
                et_message.setTextColor(Color.RED);
                break;
            case Constants.COLOR_GREEN:
                et_message.setTextColor(Color.GREEN);
                break;
            case Constants.COLOR_BLUE:
                et_message.setTextColor(Color.BLUE);
                break;
            case Constants.COLOR_BLACK:
                et_message.setTextColor(Color.BLACK);
                break;
            case Constants.COLOR_WHITE:
                et_message.setTextColor(Color.WHITE);
                break;
            case Constants.COLOR_GRAY:
                et_message.setTextColor(Color.GRAY);
                break;
            case Constants.COLOR_YELLOW:
                et_message.setTextColor(Color.YELLOW);
                break;
            case Constants.COLOR_CYAN:
                et_message.setTextColor(Color.CYAN);
                break;
        }

    }

    public void SetSize( int position_size){
        switch (position_size) {
            case (Constants.SIZE_14 - 10):
                et_message.setTextSize(14);
                break;
            case (Constants.SIZE_16 - 10):
                et_message.setTextSize(16);
                break;
            case (Constants.SIZE_18 - 10):
                et_message.setTextSize(18);
                break;
            case (Constants.SIZE_20 - 10):
                et_message.setTextSize(20);
                break;
            case (Constants.SIZE_24 - 10):
                et_message.setTextSize(24);
                break;
            case (Constants.SIZE_30 - 10):
                et_message.setTextSize(30);
                break;
        }
    }

    public void SetBackground( int position_size) {
        switch (position_size) {
            case (Constants.BLUE_FLOWERS - 16):
                linearLayout.setBackgroundResource(R.drawable.blue_flowers);;
                break;
            case (Constants.BLUE_LEAVES - 16):
                linearLayout.setBackgroundResource(R.drawable.blue_leaves);;
                break;
            case (Constants.BUBBLES - 16):
                linearLayout.setBackgroundResource(R.drawable.bubbles);
                break;
            case (Constants.COFFEE - 16):
                linearLayout.setBackgroundResource(R.drawable.coffee);
                break;
            case (Constants.FENCE - 16):
                linearLayout.setBackgroundResource(R.drawable.fence);
                break;
            case (Constants.GRAY - 16):
                linearLayout.setBackgroundResource(R.drawable.gray);
                break;
            case (Constants.JEANS - 16):
                linearLayout.setBackgroundResource(R.drawable.jeans);
                break;
            case (Constants.LEMONS - 16):
                linearLayout.setBackgroundResource(R.drawable.lemons);
                break;
            case (Constants.LIGHT_GRAY - 16):
                linearLayout.setBackgroundResource(R.drawable.light_gray);
                break;
            case (Constants.LIGHT_GREEN - 16):
                linearLayout.setBackgroundResource(R.drawable.light_green);
                break;
            case (Constants.LIGHT_PURPLE - 16):
                linearLayout.setBackgroundResource(R.drawable.light_purple);
                break;
            case (Constants.LOFT - 16):
                linearLayout.setBackgroundResource(R.drawable.loft);
                break;
            case (Constants.PINK_FLOWERS - 16):
                linearLayout.setBackgroundResource(R.drawable.pink_flowers);
                break;
            case (Constants.ROZES - 16):
                linearLayout.setBackgroundResource(R.drawable.rozes);
                break;
            case (Constants.SOFT_GREEN - 16):
                linearLayout.setBackgroundResource(R.drawable.soft_green);
                break;
            case (Constants.UKRAINE - 16):
                linearLayout.setBackgroundResource(R.drawable.ukraine);
                break;
            case (Constants.WHITE_FLOWERS - 16):
                linearLayout.setBackgroundResource(R.drawable.white_flowers);;
                break;
            case (Constants.WHITE_ROZES - 16):
                linearLayout.setBackgroundResource(R.drawable.white_roses);
                break;
            case (Constants.WHITE_SILK - 16):
                linearLayout.setBackgroundResource(R.drawable.white_silk);
                break;
        }
    }
}

class Message
{
    public boolean you;
    public String message;
    private String uId;

    public Message(boolean you, String message)
    {
        this.you = you;
        this.message = message;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isYou() {
        return you;
    }
}