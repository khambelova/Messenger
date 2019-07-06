package com.fbusers.tom.diploma;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;

    @BindView(R.id.registration)
    Button regBtn;

    EditText eLogin, eFName, eLName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();


        eLogin = (EditText) findViewById(R.id.loginText);
        eFName = (EditText) findViewById(R.id.nameText);
        eLName = (EditText) findViewById(R.id.surnameText);
    }

    @OnClick(R.id.registration)
    public void onClick(View view) {
        if(checkReg() && checkName() && checkSurname())
        {
            Registration();
        }
        else return;
    }

    private boolean checkReg()
        {
        String login = eLogin.getText().toString();

        if(login.isEmpty())
        {
            eLogin.setError("Введите логин.");
            eLogin.requestFocus();
            return  false;
        }
        else if(login.length() > 12)
        {
            eLogin.setError("Максимальная длина логина 12 символов.");
            eLogin.requestFocus();
            return  false;
        }
        else if(login.length() < 4)
        {
            eLogin.setError("Минимальная длина логина 4 символов.");
            eLogin.requestFocus();
            return  false;
        }

        return true;
    }
    private boolean checkName()
    {
        String name = eFName.getText().toString();

        if(name.isEmpty())
        {
            eFName.setError("Введите имя.");
            eFName.requestFocus();
            return  false;
        }
        else if(!name.matches("^\\D*$"))
        {
            eFName.setError("В имени не может быть цифр.");
            eFName.requestFocus();
            return  false;
        }

        return true;
    }
    private boolean checkSurname()
    {
        String surname = eLName.getText().toString();

        if(surname.isEmpty())
        {
            eLName.setError("Введите имя.");
            eLName.requestFocus();
            return  false;
        }
        else if(!surname.matches("^\\D*$"))
        {
            eLName.setError("В имени не может быть цифр.");
            eLName.requestFocus();
            return  false;
        }

        return true;
    }

    private void Registration()
    {
        reference.child(Profile.getPhone()).setValue(new User(
                eLogin.getText().toString(),
                (eFName.getText().toString() + " " + eLName.getText().toString()))
        );

        Profile.updateProfile(
                eLogin.getText().toString(),
                (eFName.getText().toString() + " " + eLName.getText().toString()));

        Intent intent = new Intent(RegistrationActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    //TODO: В дальнейшем добавить возможность загрузки фото!
}

class User
{
    public String nickname;
    public String fullName;

    public User(String nickname, String fullName)
    {
        this.nickname = nickname;
        this.fullName = fullName;
    }
}
