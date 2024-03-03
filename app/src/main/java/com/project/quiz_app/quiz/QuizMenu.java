package com.project.quiz_app.quiz;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.quiz_app.DialogObject;
import com.project.quiz_app.MainActivity;
import com.project.quiz_app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class QuizMenu extends AppCompatActivity {

    // Buttons
    Button generateRandomQuizButton;
    Button generateQuizWithConfigurationButton;

    // Options
    String[] difficultyItem = {"Any Difficulty", "Easy", "Medium", "Hard"};
    String[] categoryItem = {"Any Category", "General Knowledge", "Books", "Film", "Music",
            "Musicals & Theaters", "Television", "Video Games", "Board Games",
            "Science & Nature", "Computers", "Mathematics", "Mythology",
            "Sports", "Geography", "History", "Politics",
            "Art", "Celebrities", "Animals", "Vehicles",
            "Comics", "Gadgets", "Anime & Manga", "Cartoon & Animations"};
    String[] numberItem = {"5", "10", "25", "50"};
    AutoCompleteTextView autoCompleteTextViewDifficulty;
    AutoCompleteTextView autoCompleteTextViewCategory;
    AutoCompleteTextView autoCompleteTextViewQuestionsNumber;
    ArrayAdapter<String> adapterItems;

    // Time variable
    Calendar dateAndTimeNow;
    Calendar dateAndTimeAfter24h;

    // Dialog object
    DialogObject dialogObject = new DialogObject(QuizMenu.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);
        checkDailyQuiz();

        autoCompleteTextViewDifficulty = findViewById(R.id.auto_complete_textview_difficulty);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, difficultyItem);
        autoCompleteTextViewDifficulty.setAdapter(adapterItems);
        autoCompleteTextViewDifficulty.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Difficulty: " + item, Toast.LENGTH_SHORT).show();
        });


        autoCompleteTextViewCategory = findViewById(R.id.auto_complete_textview_category);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, categoryItem);
        autoCompleteTextViewCategory.setAdapter(adapterItems);
        autoCompleteTextViewCategory.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Category: " + item, Toast.LENGTH_SHORT).show();
        });

        autoCompleteTextViewQuestionsNumber = findViewById(R.id.auto_complete_textview_number_of_questions);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, numberItem);
        autoCompleteTextViewQuestionsNumber.setAdapter(adapterItems);
        autoCompleteTextViewQuestionsNumber.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(QuizMenu.this, "Number of questions: " + item, Toast.LENGTH_SHORT).show();
        });

        // Generate quiz with configuration
        generateQuizWithConfigurationButton = findViewById(R.id.generate_quiz_button);
        generateQuizWithConfigurationButton.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), Quiz.class);

            QuizConfiguration quizConfiguration = new QuizConfiguration();
            String category = autoCompleteTextViewCategory.getText().toString();
            String difficulty = autoCompleteTextViewDifficulty.getText().toString().toLowerCase();
            String amount = autoCompleteTextViewQuestionsNumber.getText().toString();

            difficulty = checkDifficulty(difficulty);
            category = getCategoryNumber(category);
            amount = checkAmount(amount);
            // All of the categories have a numeric value in "get" URL
            // So, it is needed a conversion between text values to numeric values
            quizConfiguration.setDifficulty(difficulty);
            quizConfiguration.setCategory(category);
            quizConfiguration.setNumberOfQuestions(amount);


            intent.putExtra("config", quizConfiguration);
            startActivity(intent);
            finish();
        });

        generateRandomQuizButton = findViewById(R.id.generate_random_quiz_button);
        generateRandomQuizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Quiz.class);
            intent.putExtra("config", new QuizConfiguration(
                    "0", "0", "10"));
            startActivity(intent);
            finish();
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

    private void checkDailyQuiz() {

        dateAndTimeAfter24h = Calendar.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;

        DatabaseReference date = database.getReference()
                .child("Users").child(user.getUid()).child("dailyQuizAvailableDate");

        date.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String dateString = dataSnapshot.getValue(String.class);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                Date data;
                try {
                    assert dateString != null;
                    data = dateFormat.parse(dateString);
                    assert data != null;
                    dateAndTimeAfter24h.setTime(data);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dateAndTimeNow = Calendar.getInstance();

                long compare = dateAndTimeNow.getTimeInMillis() - dateAndTimeAfter24h.getTimeInMillis();
                if (compare >= 0) {
                    dialogObject.dailyQuizDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });
    }

    private String checkAmount(String amount) {
        if (amount == null || amount.equals("Choose number of questions")) {
            return "10";
        }
        return amount;
    }

    private String checkDifficulty(String difficulty) {
        if (difficulty == null || difficulty.equals("choose difficulty")
                || difficulty.equals("any difficulty")) {
            return "0";
        }
        return difficulty;
    }

    public String getCategoryNumber(String category) {
        switch (category) {
            case "General Knowledge":
                return "9";
            case "Books":
                return "10";
            case "Film":
                return "11";
            case "Music":
                return "12";
            case "Musicals & Theaters":
                return "13";
            case "Television":
                return "14";
            case "Video Games":
                return "15";
            case "Board Games":
                return "16";
            case "Science & Nature":
                return "17";
            case "Computers":
                return "18";
            case "Mathematics":
                return "19";
            case "Mythology":
                return "20";
            case "Sports":
                return "21";
            case "Geography":
                return "22";
            case "History":
                return "23";
            case "Politics":
                return "24";
            case "Art":
                return "25";
            case "Celebrities":
                return "26";
            case "Animals":
                return "27";
            case "Vehicles":
                return "28";
            case "Comics":
                return "29";
            case "Gadgets":
                return "30";
            case "Anime & Manga":
                return "31";
            case "Cartoon & Animations":
                return "32";
            default:
                return "0";
        }
    }
}