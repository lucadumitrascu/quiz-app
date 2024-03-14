package com.project.quiz_app.leaderboard;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;
import com.project.quiz_app.authentication.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class Leaderboard extends AppCompatActivity {

    Button nextButton, previousButton;
    TableLayout tableLayout;
    ArrayList<User> userList = new ArrayList<>();
    boolean fetchedFromDB = false;
    int userCounter = 0;
    int usersLeftToShow = 0;
    int usersToShowNow = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        previousButton = findViewById(R.id.leaderboard_previous_button);
        nextButton = findViewById(R.id.leaderboard_next_button);
        setValuesToLeaderboard();

        nextButton.setOnClickListener(v -> {
            previousButton.setVisibility(View.VISIBLE);
            tableLayout.removeAllViews();
            userCounter += 10;
            usersLeftToShow -= 10;
            usersToShowNow += 10;
            if (usersLeftToShow <= 10) {
                nextButton.setVisibility(View.GONE);
            }
            createTableRows(userList, usersToShowNow);

        });

        previousButton.setOnClickListener(v -> {
            nextButton.setVisibility(View.VISIBLE);
            tableLayout.removeAllViews();
            userCounter -= 10;
            usersToShowNow -= 10;
            usersLeftToShow += 10;
            if (usersLeftToShow == userList.size()) {
                previousButton.setVisibility(View.GONE);
            }
            createTableRows(userList, usersToShowNow);
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                        R.anim.slide_in_left, android.R.anim.slide_out_right);
                startActivity(intent, options.toBundle());
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    private void setValuesToLeaderboard() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    userList.add(ds.getValue(User.class));
                }
                // Sorting by place
                userList.sort(Comparator.comparingInt(User::getTotalDailyScore).reversed());

                if (!(userList.size() % 10 == 0)) {
                    User extraUser = new User("-", "-", "-",
                             0,0, 0, 0, 0);

                    for (int i = 0; i < (userList.size() % 10); i++) {
                        userList.add(extraUser);
                    }
                }

                for (int i = 0; i < userList.size(); i++) {
                    userList.get(i).setPlaceInLeaderboard(i + 1);
                }

                if (!fetchedFromDB) {
                    createTableRows(userList, usersToShowNow);
                    fetchedFromDB = true;
                    if (userList.size() <= 10) {
                        nextButton.setVisibility(View.GONE);
                    }
                    previousButton.setVisibility(View.GONE);
                    usersLeftToShow = userList.size();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error
            }
        });
    }

    private void createTableRows(ArrayList<User> userList, int count) {
        tableLayout = findViewById(R.id.leadeboard_table_layout);
        for (int i = userCounter; i < count; i++) {

            User user = userList.get(i);

            TableRow row = new TableRow(Leaderboard.this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            if (i == userCounter) {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(R.string.leaderboard_place);
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.WHITE);
                placeView.setTextSize(20);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(R.string.leaderboard_name);
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.WHITE);
                nameView.setTextSize(20);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(R.string.leaderboard_score);
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.WHITE);
                scoreView.setTextSize(20);
                row.addView(scoreView);
                tableLayout.addView(row);

                row = new TableRow(Leaderboard.this);
                row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            }

            if (user.getPlaceInLeaderboard() == 1) {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(String.valueOf(i + 1));
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.rgb(212, 175, 55));
                placeView.setTextSize(20);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(user.getName());
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.rgb(212, 175, 55));
                nameView.setTextSize(20);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(String.valueOf(user.getTotalDailyScore()));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.rgb(212, 175, 55));
                scoreView.setTextSize(20);
                row.addView(scoreView);

            } else if (user.getPlaceInLeaderboard() == 2) {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(String.valueOf(i + 1));
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.rgb(192, 192, 192));
                placeView.setTextSize(20);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(user.getName());
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.rgb(192, 192, 192));
                nameView.setTextSize(20);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(String.valueOf(user.getTotalDailyScore()));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.rgb(192, 192, 192));
                scoreView.setTextSize(20);
                row.addView(scoreView);

            } else if (user.getPlaceInLeaderboard() == 3) {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(String.valueOf(i + 1));
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.rgb(205, 127, 50));
                placeView.setTextSize(20);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(user.getName());
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.rgb(205, 127, 50));
                nameView.setTextSize(20);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(String.valueOf(user.getTotalDailyScore()));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.rgb(205, 127, 50));
                scoreView.setTextSize(20);
                row.addView(scoreView);

            } else if (Objects.equals(user.getEmail(),
                    Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())) {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(String.valueOf(i + 1));
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.GREEN);
                placeView.setTextSize(21);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(user.getName());
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.GREEN);
                nameView.setTextSize(21);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(String.valueOf(user.getTotalDailyScore()));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.GREEN);
                scoreView.setTextSize(21);
                row.addView(scoreView);

            } else {

                TextView placeView = new TextView(Leaderboard.this);
                placeView.setText(String.valueOf(i + 1));
                placeView.setGravity(Gravity.CENTER);
                placeView.setTextColor(Color.WHITE);
                placeView.setTextSize(20);
                row.addView(placeView);

                TextView nameView = new TextView(Leaderboard.this);
                nameView.setText(user.getName());
                nameView.setGravity(Gravity.CENTER);
                nameView.setTextColor(Color.WHITE);
                nameView.setTextSize(20);
                row.addView(nameView);

                TextView scoreView = new TextView(Leaderboard.this);
                scoreView.setText(String.valueOf(user.getTotalDailyScore()));
                scoreView.setGravity(Gravity.CENTER);
                scoreView.setTextColor(Color.WHITE);
                scoreView.setTextSize(20);
                row.addView(scoreView);
            }

            tableLayout.addView(row);
        }
    }
}
