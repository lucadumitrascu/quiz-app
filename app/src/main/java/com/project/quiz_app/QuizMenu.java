package com.project.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class QuizMenu extends AppCompatActivity {



    // Score textview
    TextView scoreTextView;

    // Buttons
    Button generateRandomQuiz;

    // Difficulty option
    String[] difficultyItem = {"Any Difficulty","Easy","Medium","Hard"};
    String[] categoryItem = {"Any Category","General Knowledge","Books","Film","Music",
            "Musicals & Theaters","Television","Video Games","Board Games",
            "Science & Nature","Computers","Mathematics","Mythology",
            "Sports","Geography","History","Politics",
            "Art","Celebrities","Animals","Vehicles",
            "Comics","Gadgets","Anime & Manga","Cartoon & Animations"};
    String[] numberItem = {"5","10","25","50","100"};
    AutoCompleteTextView autoCompleteTextViewDifficulty;
    AutoCompleteTextView autoCompleteTextViewCategory;
    AutoCompleteTextView autoCompleteTextViewQuestionsNumber;
    ArrayAdapter<String> adapterItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

        generateRandomQuiz = findViewById(R.id.generate_random_quiz_button);
        generateRandomQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Quiz.class);
            startActivity(intent);
            finish();});


        autoCompleteTextViewDifficulty = findViewById(R.id.auto_complete_textview_difficulty);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, difficultyItem);
        autoCompleteTextViewDifficulty.setAdapter(adapterItems);
        autoCompleteTextViewDifficulty.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Difficulty: "+ item, Toast.LENGTH_SHORT).show();
        });


        autoCompleteTextViewCategory = findViewById(R.id.auto_complete_textview_category);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, categoryItem);
        autoCompleteTextViewCategory.setAdapter(adapterItems);
        autoCompleteTextViewCategory.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Category: "+ item, Toast.LENGTH_SHORT).show();
        });

        autoCompleteTextViewQuestionsNumber = findViewById(R.id.auto_complete_textview_number_of_questions);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, numberItem);
        autoCompleteTextViewQuestionsNumber.setAdapter(adapterItems);
        autoCompleteTextViewQuestionsNumber.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Number of questions: "+ item, Toast.LENGTH_SHORT).show();
        });

        // Fetch last quiz score from DB
        scoreTextView = findViewById(R.id.last_score);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;
        DatabaseReference lastScoreRef = database.getReference()
                .child("Users").child(user.getUid()).child("lastQuizScore");
        lastScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String oldText = scoreTextView.getText().toString();
                oldText += " "+dataSnapshot.getValue(Integer.class);
                scoreTextView.setText(oldText);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });

    }
}