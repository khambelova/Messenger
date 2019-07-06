package com.fbusers.tom.diploma;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tom on 06.03.2018.
 */

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.exit)
    Button exit_btn;

    @BindView(R.id.name)
    TextView tv_name;

    @BindView(R.id.login)
    TextView tv_login;

    @BindView(R.id.phone)
    TextView tv_phone;

    @BindView(R.id.image)
    ImageView iv_photo;

    StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        tv_name.setText(Profile.getName());
        tv_login.setText(Profile.getNickname());
        tv_phone.setText(Profile.getPhone());


        reference = FirebaseStorage.getInstance().getReference();
        //TODO: добавить загрузку изображения!
    }

    @OnClick({R.id.exit, R.id.changeData})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit: {
                AlertDialog.Builder confirm = new AlertDialog.Builder(ProfileActivity.this);
                confirm.setMessage("Вы действительно хотите выйти с текущей учетной записи?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                FirebaseAuth.getInstance().signOut();

                                Profile.setProfile(null,null,null);

                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog dialog = confirm.create();
                dialog.setTitle("Выход с учетной записи");
                dialog.show();

                break;
            }
            case R.id.changeData:
            {
                Intent intent = new Intent(this, ChangeDataActivity.class);
                startActivityForResult(intent, 1);

                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {return;}
        if(resultCode == RESULT_OK)
        {
            tv_name.setText(Profile.getName());
            tv_login.setText(Profile.getNickname());
            tv_phone.setText(Profile.getPhone());
        }
        //TODO: добавить обновление изображения!
    }

//    private void updatePhoto(Uri uri) throws IOException {
//        reference.getFile(uri).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//            }
//        });
//    }
}
