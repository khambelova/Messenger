package com.fbusers.tom.diploma.contacts;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fbusers.tom.diploma.Profile;
import com.fbusers.tom.diploma.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Tom on 05.05.2018.
 */

public class AddContactActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.conSearch)
    Button search;

    @BindView(R.id.addContact)
    Button addContact;

    EditText phoneNumber;
    TextView login, name, phone;

    FirebaseDatabase database;
    DatabaseReference reference;
    ValueEventListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        phoneNumber = findViewById(R.id.input_phone);
        phoneNumber.setHint("Введите номер телефона");

        login = findViewById(R.id.conLogin);
        name = findViewById(R.id.conName);
        phone = findViewById(R.id.conPhone);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @OnClick({R.id.conSearch, R.id.addContact})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.conSearch: {
                if (phoneNumber.getText().toString().isEmpty()) {
                    phoneNumber.setError("Введите номер телефона!");
                    phoneNumber.requestFocus();
                    phoneNumber.setText(null);
                    return;
                } else if (phoneNumber.length() < 10) {
                    phoneNumber.setError("Номер введен некорректно!");
                    phoneNumber.requestFocus();
                    phoneNumber.setText(null);
                    return;
                }

                searchContact();
                break;
            }
            case R.id.addContact: {
                if (login.getText().toString().isEmpty() && name.getText().toString().isEmpty() && phone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Сначала найдите пользователя.", Toast.LENGTH_LONG).show();
                    return;
                }

                addContact();
                break;
            }
        }
    }

    private void searchContact() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    login.setText(dataSnapshot.child("nickname").getValue().toString());
                    name.setText(dataSnapshot.child("fullName").getValue().toString());
                    phone.setText(dataSnapshot.getKey());
                } else {
                    Toast.makeText(getApplicationContext(), "Пользователя с таким номером не найдено.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        reference.child(phoneNumber.getText().toString()).addListenerForSingleValueEvent(listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addContact() {
        if (Profile.getContacts().stream().filter(con -> con.getPhone().equals(phone.getText().toString())).count() > 0)
        {
            Toast.makeText(getApplicationContext(), "Вы уже добавили данного пользователя.", Toast.LENGTH_LONG).show();
            return;
        }
        else if (Profile.getPhone().equals(phone.getText().toString()) || Profile.getPhone().equals(phoneNumber.getText().toString()))
        {
            Toast.makeText(getApplicationContext(), "Вы не можете добавить самого себя.", Toast.LENGTH_LONG).show();
            return;
        }

        String index = String.valueOf(Profile.getContacts().size() != 0 ? Profile.getContacts().size() : 0);
        reference.child(Profile.getPhone()).child("friends").child(index).setValue(phone.getText());

        Profile.addContact(new Contact(login.getText().toString(), name.getText().toString(), phone.getText().toString(), 1));

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}

