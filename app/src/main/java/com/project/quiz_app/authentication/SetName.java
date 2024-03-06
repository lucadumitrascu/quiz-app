package com.project.quiz_app.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;

import java.util.ArrayList;
import java.util.List;

public class SetName extends AppCompatActivity {
    Button button;
    TextView nameTextView;
    List<User> users = new ArrayList<>();
    String name = "";
    boolean nameTaken = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        button = findViewById(R.id.set_name_button);
        nameTextView = findViewById(R.id.name_input);
        getAllUsers();


        button.setOnClickListener(v -> {
            this.name = nameTextView.getText().toString().trim();
            if (!name.isEmpty()) {
                checkName();
                if (nameTaken) {
                    Toast.makeText(this, "This name already exists", Toast.LENGTH_SHORT).show();
                    nameTextView.setTextColor(Color.RED);
                } else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String userId = user.getUid();
                        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        mRef.child("name").setValue(name);

                        // Launch Main activity
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                Toast.makeText(this, "You have to enter your name!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllUsers() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    users.add(ds.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }

    private void checkName() {
        for (int i = 0; i < users.size(); i++) {
            nameTaken = false;
            String name = users.get(i).getName();
            if (name.equals(this.name)) {
                nameTaken = true;
                i = users.size();
            }
        }
    }
}