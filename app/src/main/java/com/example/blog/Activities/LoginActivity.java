package com.example.blog.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.blog.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Button loginBtn;
    ProgressBar loginProgress ;
    EditText mailEditText, passEditText ;
    String mail, pass;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        clickOnLoginButton();
        clickOnRegisterWord();
    }


    public void clickOnLoginButton(){
        loginBtn = findViewById(R.id.login_button);
        loginProgress = findViewById(R.id.login_progress);
        mailEditText = findViewById(R.id.login_mail);
        passEditText = findViewById(R.id.login_pass);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mail =  mailEditText.getText().toString();
               pass = passEditText.getText().toString();

               loginBtn.setVisibility(View.INVISIBLE);
               loginProgress.setVisibility(View.VISIBLE);

               if(!mail.isEmpty()  && !pass.isEmpty()){
                   userSignIn();
               }
               else{
                   showMessage("Please fill in all fields");
                   loginBtn.setVisibility(View.VISIBLE);
                   loginProgress.setVisibility(View.INVISIBLE);
               }
            }
        });
    }

    public void userSignIn(){
        mAuth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                updateUi();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showMessage(e.getMessage());
                loginBtn.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void showMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void updateUi(){
        Intent openHomeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(openHomeActivity);
        finish();
    }

    public void clickOnRegisterWord(){
        findViewById(R.id.register_word).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRegisterActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(openRegisterActivity);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null){
            updateUi();
        }

    }
}
