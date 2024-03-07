package com.project.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;
import com.project.quiz_app.authentication.User;

import java.util.ArrayList;

public class SetOrChangeName extends AppCompatActivity {
    Button setOrChangeNameButton, cancelButton;
    TextView messageSetOrChangeName;
    TextInputLayout nameTextInputLayout;
    TextInputEditText nameTextInputEditText;
    ArrayList<User> users = new ArrayList<>();
    String name = "";
    boolean nameTaken = false;
    FirebaseUser user;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_or_change_name);

        setOrChangeNameButton = findViewById(R.id.set_or_change_name_button);
        cancelButton = findViewById(R.id.cancel_set_or_change_button);
        nameTextInputEditText = findViewById(R.id.name_input);
        nameTextInputLayout = findViewById(R.id.name_input_layout);
        messageSetOrChangeName = findViewById(R.id.message_set_or_change_name);

        getAllUsers();
        // Get the current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("name");
        }
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                assert value != null;
                if (!(value.equals("null"))) {
                    messageSetOrChangeName.setText("Change name");
                    messageSetOrChangeName.setGravity(Gravity.CENTER);
                    nameTextInputLayout.setHint("Write your new name...");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });

        setOrChangeNameButton.setOnClickListener(v -> {
            this.name = nameTextInputEditText.getText().toString().trim();
            if (!name.isEmpty()) {
                checkName();
                if (nameTaken) {
                    Toast.makeText(this, "This name already exists", Toast.LENGTH_SHORT).show();
                    nameTextInputEditText.setTextColor(Color.RED);
                    nameTaken = false;
                } else {
                    userReference.setValue(name);

                    // Launch Main activity
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, "You have to enter a name!", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent, options.toBundle());
            finish();
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

    public void checkName() {
        for (int i = 0; i < users.size(); i++) {
            String nameFromUsers = users.get(i).getName();
            if (nameFromUsers.equals(this.name)) {
                nameTaken = true;
            }
        }
    }
}