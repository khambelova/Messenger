package com.fbusers.tom.diploma;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeDataActivity extends AppCompatActivity{

    public static final int REQUEST_GALLERY = 0;
    public static final int REQUEST_CAPTURE = 1;

    @BindView(R.id.loginText)
    EditText eLogin;

    @BindView(R.id.nameText)
    EditText eFName;

    @BindView(R.id.surnameText)
    EditText eLName;

    @BindView(R.id.photo)
    ImageView ePhoto;

    StorageReference storage;

    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_change);
        ButterKnife.bind(this);

        eLogin.setText(Profile.getNickname());
        eFName.setText(Profile.getName().split(" ")[0]);
        eLName.setText(Profile.getName().split(" ")[1]);

        ePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogWIndow();
            }
        });

        storage = FirebaseStorage.getInstance().getReference();
    }


    @OnClick(R.id.change)
    public void onClick(View view)
    {
        if(eLogin.getText().toString().isEmpty())
        {
            eLogin.setError("Поле не может быть пустым!");
            eLogin.requestFocus();
            return;
        }
        if(eFName.getText().toString().isEmpty())
        {
            eFName.setError("Поле не может быть пустым!");
            eFName.requestFocus();
            return;
        }
        if(eLName.getText().toString().isEmpty())
        {
            eLName.setError("Поле не может быть пустым!");
            eLName.requestFocus();
            return;
        }

        changeData();
    }

    private void changeData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();

        reference.child(Profile.getPhone()).child("nickname").setValue(eLogin.getText().toString());
        reference.child(Profile.getPhone()).child("fullName").setValue(eFName.getText().toString() + " " + eLName.getText().toString());

        Profile.updateProfile(eLogin.getText().toString(), eFName.getText().toString() + " " + eLName.getText().toString());

        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();

        //loadPhoto();

        //TODO: добавить возможность смены изображения!
    }

    private void openDialogWIndow()
    {
        AlertDialog.Builder confirm = new AlertDialog.Builder(ChangeDataActivity.this);
        confirm.setMessage("Выберите источник фотографии")
                .setCancelable(true)
                .setNeutralButton("Камера", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        launchCamera();
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Галерея", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openGallery();
                        dialog.cancel();
                    }
                });

        AlertDialog dialog = confirm.create();
        dialog.setTitle("Загрузка фотографии");
        dialog.show();
    }

    private  void launchCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    private void openGallery()
    {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_GALLERY);
    }

//    private void loadPhoto() {
////        Intent intent = new Intent();
////        setResult(RESULT_OK, intent);
////        finish();
//
//        storage.child(Profile.getPhone() + "/photo/photo.jpg").putFile(imageUri)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Intent intent = new Intent();
//                intent.putExtra("result",
//                        taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(),"Не удалось загрузить фотографию.", Toast.LENGTH_LONG).show();
//            }
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CAPTURE && resultCode == RESULT_OK)
        {
            Uri selectedImage = data.getData();
            //Bundle extras = data.getExtras();
            //Bitmap photo = (Bitmap) extras.get("data");
            imageUri = data.getData();
            //ePhoto.setImageBitmap(photo);
            ePhoto.setImageURI(selectedImage);
        }
        else if(requestCode == REQUEST_GALLERY && resultCode == RESULT_OK)
        {
            Uri selectedImage = data.getData();
            imageUri = data.getData();
            ePhoto.setImageURI(selectedImage);
        }
    }
}
