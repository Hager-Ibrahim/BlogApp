package com.example.blog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.blog.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class RegisterActivity extends AppCompatActivity {

    ImageView userImg , cameraIcon;
    Uri imgUri ;
    Button registerBtn ;
    ProgressBar progressBar ;
    String name, email, password, confirmPass ;
    FirebaseAuth mAuth ;
    StorageReference storageReference, imgPath;
    StringBuilder builder;
    static int PERMISSION_REQUEST_CODE = 1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        ClickAddPhotoBtn();
        clickOnRegisterBtn();
        openLoginActivity();
    }


    public void clickOnRegisterBtn(){
        final EditText nameEditText = findViewById(R.id.register_name_edit_text);
        final EditText emailEditText = findViewById(R.id.register_mail_edit_text);
        final EditText passEditText = findViewById(R.id.register_pass_edit_text);
        final EditText confirmPassEditText = findViewById(R.id.register_confirm_pass_edit_text);
        progressBar = findViewById(R.id.register_progress_bar);

        registerBtn = findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                registerBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                name = nameEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passEditText.getText().toString();
                confirmPass= confirmPassEditText.getText().toString();


                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPass.isEmpty()){


                    if(!confirmPass.equals(password)){
                        registerBtn.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        showMessage("your passwords are not the same");
                    }
                    else {
                        createNewAccount();
                    }
                }
                else {
                    registerBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    showMessage("Please fill in all fields");
                }
            }
        });


    }

    public void saveImageToFirebase(){
        if(imgUri != null){

            imgPath = storageReference.child("user_image").child(imgUri.getLastPathSegment());

            imgPath.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            saveUserProfileChanges(imgPath);
                        }
                    });

                }
            });
        }
        else{
            imgPath = storageReference.child("user_image").child("unknown.gif");
            saveUserProfileChanges(imgPath);
        }
    }

    public void saveUserProfileChanges(StorageReference imgPath){

        imgPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .setDisplayName(saveNameAsUpperCase(name))
                        .build();
                mAuth.getCurrentUser().updateProfile(userProfile).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }

    public void createNewAccount(){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                saveImageToFirebase();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                registerBtn.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    public String saveNameAsUpperCase(String userName){
        String[] strArray = userName.split(" ");
        builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        return builder.toString();
    }
    public void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    public void ClickAddPhotoBtn(){

        cameraIcon = findViewById(R.id.register_add_photo_btn);
        userImg = findViewById(R.id.register_user_img);
        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    checkAndRequestPermissions();
                }
                else{
                    openGallery();
                }
            }
        });
    }

    public void checkAndRequestPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                new AlertDialog.Builder(this)
                        .setTitle("Permission Needed")
                        .setMessage("Accept permission to be able to choose an image")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(RegisterActivity.this,
                                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                                        ,PERMISSION_REQUEST_CODE);
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            }
            else{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}
                        ,PERMISSION_REQUEST_CODE);
            }
        }
        else {
            openGallery();
        }
    }

    public void openGallery(){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                cameraIcon.setVisibility(View.INVISIBLE);
                imgUri = result.getUri();
                userImg.setImageURI(imgUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                showMessage(error.getMessage());
            }
        }
    }

    public void openLoginActivity(){
        findViewById(R.id.login_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(openLogin);
                finish();
            }
        });
    }
}
