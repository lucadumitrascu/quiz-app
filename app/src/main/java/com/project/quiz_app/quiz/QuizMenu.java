package com.project.quiz_app.quiz;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import com.project.quiz_app.R;

public class QuizMenu extends AppCompatActivity {


    // Buttons
    Button generateRandomQuiz;
    Button generateQuizWithConfiguration;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_menu);

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
        generateQuizWithConfiguration = findViewById(R.id.generate_quiz_button);
        generateQuizWithConfiguration.setOnClickListener(v -> {

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

        generateRandomQuiz = findViewById(R.id.generate_random_quiz_button);
        generateRandomQuiz.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Quiz.class);
            intent.putExtra("config", new QuizConfiguration(
                    "0", "0", "10"));
            startActivity(intent);
            finish();
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