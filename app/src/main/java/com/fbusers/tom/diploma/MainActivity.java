package com.fbusers.tom.diploma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fbusers.tom.diploma.contacts.Contact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth= FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference  = database.getReference();
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    ValueEventListener profileListener, contactListener;

    @BindView(R.id.edit_phone_number)
    EditText editPhone;

    @BindView(R.id.edit_verify_code)
    EditText editVerifyCode;

    int contactCount;
    String code, phoneNumber;

    @BindView(R.id.progressBar)
    ProgressBar progbar;

    @BindView(R.id.send)
    Button send_btn;

    @BindView(R.id.verify)
    Button verify_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        code = phoneNumber = null;
        contactCount = 0;

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                if(code == null)
                {
                    //Toast.makeText(getApplicationContext(), "Данный номер уже был подтвержден!", Toast.LENGTH_LONG).show();

                    progbar.setVisibility(View.VISIBLE);
                    verify_btn.setVisibility(View.INVISIBLE);
                    send_btn.setVisibility(View.INVISIBLE);
                    editPhone.setVisibility(View.INVISIBLE);
                    editVerifyCode.setVisibility(View.INVISIBLE);

                    phoneNumber = editPhone.getText().toString();

                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
            {
                super.onCodeSent(s, forceResendingToken);

                code = s;

                Toast.makeText(getApplicationContext(), "Код подтверждения был отправлен по SMS!", Toast.LENGTH_LONG).show();
            }
        };

        contactListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.getValue() != null) {
                    Profile.addContact(new Contact(
                            dataSnapshot.child("nickname").getValue().toString(),
                            dataSnapshot.child("fullName").getValue().toString(),
                            dataSnapshot.getKey(), 1));

                    checkAllContacts();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if(auth.getCurrentUser() != null)
        {
            showProgressBar(true);

            phoneNumber = auth.getCurrentUser().getPhoneNumber();

            getProfile();
        }
        else
        {
            showProgressBar(false);
        }
    }

    @OnClick({R.id.send, R.id.verify})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send: {
                phoneNumber = editPhone.getText().toString();
                if (phoneNumber.isEmpty())
                {
                    editPhone.setError("Введите номер телефона!");
                    editPhone.requestFocus();
                    phoneNumber = null;
                    return;
                }
                else if (phoneNumber.length() < 10)
                {
                    editPhone.setError("Номер введен некорректно!");
                    editPhone.requestFocus();
                    phoneNumber = null;
                    return;
                }

                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, callbacks);
                break;
            }
            case R.id.verify: {
                String verifyCode = editVerifyCode.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, verifyCode);

                signInWithPhoneAuthCredential(credential);
                break;
            }
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        auth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Вы успешно авторизировались.", Toast.LENGTH_SHORT).show();

                    showProgressBar(true);

                    getProfile();
                }
                else
                {
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "Код подтверждения введен неверно!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Авторизация не удалась!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void getProfile()
    {
        profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Profile.setProfile(dataSnapshot.child("nickname").getValue().toString(), dataSnapshot.child
                            ("fullName").getValue().toString(), dataSnapshot.getKey());

                    GenericTypeIndicator<List<String>> typeIndicator = new GenericTypeIndicator<List<String>>(){};

                    if (dataSnapshot.child("friends").getValue() != null) {
                        List<String> contacts = dataSnapshot.child("friends").getValue(typeIndicator);

                        contactCount = contacts.size();

                        for (String contact : contacts) {
                            getProfileContacts(contact);
                        }

                    }

                    checkAllContacts();

                } else {
                    Profile.setProfile(null, null, phoneNumber);

                    Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };

        reference.child(phoneNumber).addListenerForSingleValueEvent(profileListener);
    }

    private void getProfileContacts(final String phoneNumber)
    {
        reference.child(phoneNumber).addListenerForSingleValueEvent(contactListener);
    }

    private void checkAllContacts()
    {
        if(Profile.getContacts().size() < contactCount)
        {
            return;
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void showProgressBar(Boolean show)
    {
        if(show) {
            progbar.setVisibility(View.VISIBLE);
            verify_btn.setVisibility(View.INVISIBLE);
            send_btn.setVisibility(View.INVISIBLE);
            editPhone.setVisibility(View.INVISIBLE);
            editVerifyCode.setVisibility(View.INVISIBLE);
        }
        else
        {
            progbar.setVisibility(View.INVISIBLE);
            verify_btn.setVisibility(View.VISIBLE);
            send_btn.setVisibility(View.VISIBLE);
            editPhone.setVisibility(View.VISIBLE);
            editVerifyCode.setVisibility(View.VISIBLE);
        }
    }
}