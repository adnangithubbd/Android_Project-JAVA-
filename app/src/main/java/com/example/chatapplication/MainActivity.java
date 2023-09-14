package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;


import com.example.chatapplication.Adapter.UserAdapter;
import com.example.chatapplication.databinding.ActivityMainBinding;
import com.example.chatapplication.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseAuth auth;
    DatabaseReference dbRef;
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userAdapter=new UserAdapter(this);
        binding.recycler.setAdapter(userAdapter);
        auth=FirebaseAuth.getInstance();
        dbRef= FirebaseDatabase.getInstance().getReference("users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAdapter.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String uid=dataSnapshot.getKey();
                    if(!uid.equals(FirebaseAuth.getInstance().getUid())){
                        UserModel model=dataSnapshot.child(uid).getValue(UserModel.class);
                            userAdapter.add(model);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 auth.signOut();
                 startActivity(new Intent(MainActivity.this, LogInActivity.class));
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        Log.d("Log Out","Log is clicked");
        if (itemId ==  R.id.logtou) {
            Toast.makeText(MainActivity.this, "Log out is clicked", Toast.LENGTH_SHORT).show();

            return true; // Return true to indicate that the event has been handled
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }
}
//
//  Log.d("printArray",model.getEmail()+"=>"+ model.getName()+
//          "=>"+model.getPassword()+"=>"+model.getUid());