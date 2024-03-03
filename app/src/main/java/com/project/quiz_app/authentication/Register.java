package com.project.quiz_app.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;

public class Register extends AppCompatActivity {


    FirebaseAuth mAuth;
    TextInputEditText emailInput, passwordInput;
    Button registerButton;
    Button goToLogin;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email_register);
        passwordInput = findViewById(R.id.password_register);
        registerButton = findViewById(R.id.register_button);
        goToLogin = findViewById(R.id.login_in_register);
        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        registerButton.setOnClickListener(v -> {
            String email, password;
            email = String.valueOf(emailInput.getText());
            password = String.valueOf(passwordInput.getText());

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Account created.",
                                    Toast.LENGTH_SHORT).show();

                            // Add user to database
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userEmail = null;
                            if (user != null) {
                                userEmail = user.getEmail();
                            }
                            User newUser = new User();
                            newUser.setEmail(userEmail);
                            newUser.setName("null");
                            newUser.setTotalScore(0);
                            newUser.setLastQuizScore(0);
                            newUser.setPlace(0);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            assert user != null;
                            String userId = user.getUid();
                            DatabaseReference mRef = database.getReference().child("Users").child(userId);
                            mRef.setValue(newUser);

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
        });
    }
}