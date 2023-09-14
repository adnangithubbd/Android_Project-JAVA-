package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chatapplication.databinding.ActivityAuthenticationBinding;
import com.example.chatapplication.databinding.ActivityMainBinding;
import com.example.chatapplication.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthenticationActivity extends AppCompatActivity {
    ActivityAuthenticationBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;
    String userName,password,email,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName=binding.name.getText().toString();
                email=binding.email.getText().toString();
                password=binding.password.getText().toString();


                auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        uid=auth.getUid();
                        UserModel userModel=new UserModel(uid, userName,email,password);
                        DatabaseReference userRef =database.getReference().child("users")
                                .child(auth.getUid());
                        userRef.setValue(userModel);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AuthenticationActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                    }
                });

             }
        });

        binding.LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AuthenticationActivity.this, LogInActivity.class));
            }

        });
    }
}