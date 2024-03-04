package com.project.quiz_app.quiz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import com.project.quiz_app.DialogObject;
import com.project.quiz_app.R;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


public class PracticeQuiz extends AppCompatActivity implements View.OnClickListener {

    interface Request {
        @GET("https://opentdb.com/api.php?type=multiple")
        Call<QuizObject> get(@Query("amount") String amount,
                             @Query("difficulty") String difficulty,
                             @Query("category") String category);

    }

    // Quiz variables
    QuizConfiguration quizConfiguration;
    QuizObject quiz;

    // Visual variables
    TextView questionsTextView;
    TextView questionsLeftTextView;
    Button respA, respB, respC, respD;
    Button nextButton;

    // Quiz running variables
    int questionIndex = 0;
    String selectedAnswer = "null";
    int score = 0;
    int totalQuestions = 0;

    // Loading screen
    DialogObject dialogObject = new DialogObject(PracticeQuiz.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_quiz);

        questionsLeftTextView = findViewById(R.id.questions_left);


        questionsTextView = findViewById(R.id.question);
        respA = findViewById(R.id.A_response);
        respB = findViewById(R.id.B_response);
        respC = findViewById(R.id.C_response);
        respD = findViewById(R.id.D_response);
        nextButton = findViewById(R.id.next_button);

        quizConfiguration = (QuizConfiguration) getIntent().getSerializableExtra("config");
        if (quizConfiguration != null) {
            getQuestions();
        } else {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), QuizMenu.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(),
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            startActivity(intent, options.toBundle());
            finish();
        }

        respA.setOnClickListener(this);
        respB.setOnClickListener(this);
        respC.setOnClickListener(this);
        respD.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.finishQuizDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public void onClick(View v) {
        respA.setBackgroundColor(Color.WHITE);
        respB.setBackgroundColor(Color.WHITE);
        respC.setBackgroundColor(Color.WHITE);
        respD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) v;

        if (clickedButton.getId() == R.id.next_button) {

            if (questionIndex <= Integer.parseInt(quizConfiguration.getNumberOfQuestions())) {
                if (Objects.equals(selectedAnswer, quiz.results.get(questionIndex).correct_answer)) {
                    score++;
                }
                if (!Objects.equals(selectedAnswer, "null")) {
                    questionIndex++;
                    setQuestionsLeftTextView();
                    setValuesToQuiz(quiz, questionIndex);
                    selectedAnswer = "null";
                } else {
                    Toast.makeText(PracticeQuiz.this, "You have to select an answer!", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.CYAN);
        }
    }

    private void setQuestionsLeftTextView() {
        String helper = "Questions left: " + --totalQuestions;
        questionsLeftTextView.setText(helper);
    }

    void getQuestions() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dialogObject.startLoadingDialog();
        Request request = retrofit.create(Request.class);
        request.get(
                quizConfiguration.getNumberOfQuestions(),
                quizConfiguration.getDifficulty(),
                quizConfiguration.getCategory()).enqueue(new Callback<QuizObject>() {

            @Override
            public void onResponse(@NonNull Call<QuizObject> call, @NonNull Response<QuizObject> response) {
                if (response.body() != null && response.body().results != null) {
                    QuizObject quiz = response.body();

                    // append question left to textview
                    totalQuestions = Integer.parseInt(quizConfiguration.getNumberOfQuestions());
                    questionsLeftTextView.append(" " + totalQuestions);

                    setGlobalVariableQuiz(quiz);
                    setValuesToQuiz(quiz, 0);
                } else {
                    questionsTextView.setText(R.string.questions_were_not_generated);
                    Intent intent = new Intent(getApplicationContext(), PracticeQuiz.class);
                    startActivity(intent);
                }
                dialogObject.dismissDialog();
            }

            @Override
            public void onFailure(@NonNull Call<QuizObject> call, @NonNull Throwable t) {
                questionsTextView.setText(R.string.questions_were_not_generated);
                Intent intent = new Intent(getApplicationContext(), PracticeQuiz.class);
                startActivity(intent);
            }
        });
    }

    void setGlobalVariableQuiz(QuizObject quiz) {
        this.quiz = quiz;
    }

    void setValuesToQuiz(QuizObject quiz, int index) {
        if (index < Integer.parseInt(quizConfiguration.getNumberOfQuestions())) {

            String question = quiz.results.get(index).getQuestion();
            question = question.replace("&quot;", "'");
            question = question.replace("&#039;", "'");
            question = question.replace("&amp;", "&");
            question = question.replace("&eacute;", "e");
            questionsTextView.setText(question);
            int[] position = getRandomIndexVector();
            Random rand = new Random();
            int randomOrder = (rand.nextInt((4 - 1) + 1) + 1) - 1;

            switch (randomOrder) {
                case 0:
                    respA.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                case 1:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                case 2:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;

                default:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[2])
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    respD.setText(quiz.results.get(index).getCorrect_answer()
                            .replace("&quot;", "'")
                            .replace("&#039;", "'")
                            .replace("&amp;", "&"));
                    break;
            }

        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            assert user != null;

            DatabaseReference totalScoreRef = database.getReference().child("Users").child(user.getUid()).child("totalScore");
            DatabaseReference lastScoreRef = database.getReference().child("Users").child(user.getUid()).child("lastQuizScore");
            totalScoreRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer totalScoreDB = dataSnapshot.getValue(Integer.class);
                    totalScoreDB += score;

                    // Modify database information
                    totalScoreRef.setValue(totalScoreDB);
                    lastScoreRef.setValue(score);

                    dialogObject.seeQuizResultsDialog(score, totalScoreDB);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error
                }
            });
            questionsLeftTextView.setVisibility(View.GONE);
            questionsTextView.setVisibility(View.GONE);
            respA.setVisibility(View.GONE);
            respB.setVisibility(View.GONE);
            respC.setVisibility(View.GONE);
            respD.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    // Generate random order to questions
    int[] getRandomIndexVector() {
        int[] indexVector = new int[]{5, 5, 5};
        Random rand = new Random();
        int randomValue;
        indexVector[0] = (rand.nextInt((3 - 1) + 1) + 1) - 1;

        for (int i = 1; i < 3; i++) {
            do {
                randomValue = (rand.nextInt((3 - 1) + 1) + 1) - 1;
                indexVector[i] = randomValue;
            }
            while (!checkDuplicate(i, randomValue, indexVector));
        }
        return indexVector;
    }

    boolean checkDuplicate(int index, int value, int[] vector) {
        for (int i = index - 1; i >= 0; i--) {
            if (value == vector[i]) {
                return false;
            }
        }
        return true;
    }

}