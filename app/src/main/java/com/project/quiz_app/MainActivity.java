package com.project.quiz_app;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.authentication.Login;
import com.project.quiz_app.authentication.SetName;
import com.project.quiz_app.authentication.User;
import com.project.quiz_app.quiz.QuizMenu;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextView displayName;
    Button logoutButton, generateQuizButton;
    FirebaseUser user;

    DialogObject dialogObject = new DialogObject(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogObject.startLoadingDialog();


        logoutButton = findViewById(R.id.logout);
        generateQuizButton = findViewById(R.id.generate_quiz_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            Query query = reference.orderByChild("email").equalTo(user.getEmail());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        DataSnapshot snapshot = dataSnapshot.getChildren().iterator().next();
                        User userFromDB = snapshot.getValue(User.class);
                        assert userFromDB != null;
                        if (Objects.equals(userFromDB.getName(), "null")) {
                            Intent intent = new Intent(getApplicationContext(), SetName.class);
                            startActivity(intent);
                            finish();
                        } else {
                            displayName = findViewById(R.id.display_name);
                            String name = "Hello, ";
                            name += userFromDB.getName();
                            displayName.setText(name);
                        }
                        dialogObject.dismissDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                }
            });

        }

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        generateQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), QuizMenu.class);
            startActivity(intent);
            finish();
        });
    }
}