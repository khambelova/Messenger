package com.fbusers.tom.diploma;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fbusers.tom.diploma.contacts.ContactsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MenuActivity extends AppCompatActivity  {


        @BindView(R.id.profile)
        Button profile_btn;

        @BindView(R.id.messages)
        Button messages_btn;

        @BindView(R.id.contacts)
        Button contacts_btn;

        @BindView(R.id.settings)
        Button settings_btn;

        @BindView(R.id.about_program)
        Button about_program_btn;

        @BindView(R.id.exit)
        Button exit_btn;


        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_menu);
                ButterKnife.bind(this);
        }

        @OnClick({R.id.profile,R.id.messages,R.id.contacts, R.id.settings, R.id.about_program,R.id.exit})
        public void onClick(View view) {
                switch (view.getId()) {
                        case R.id.profile: {
                                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                                startActivity(intent);
                                break;
                        }
                        case R.id.messages: {
                                Intent intent = new Intent(MenuActivity.this, ContactsActivity.class);
                                intent.putExtra("contacts", "false");
                                startActivity(intent);
                                break;
                        }
                        case R.id.contacts: {
                                Intent intent = new Intent(MenuActivity.this, ContactsActivity.class);
                                intent.putExtra("contacts", "true");
                                startActivity(intent);
                                break;
                        }
                        case R.id.settings: {
                                Intent intent = new Intent(MenuActivity.this, SettingsActivity.class);
                                startActivity(intent);
                                break;
                        }
                        case R.id.about_program: {
                                Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
                                startActivity(intent);
                                break;
                        }
                        case R.id.exit: {
                                this.finish();
                                break;
                        }
                }
        }
}
