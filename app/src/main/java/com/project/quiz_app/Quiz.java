package com.project.quiz_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class Quiz extends AppCompatActivity implements View.OnClickListener {

    interface Request {
        @GET("https://opentdb.com/api.php?amount=10&type=multiple")
        Call<QuizObject> get();
    }

    // Quiz variable
    QuizObject quiz;

    // Visual variables
    TextView questionsTextView;
    Button respA, respB, respC, respD;
    Button nextButton;

    // Quiz running variables
    int questionIndex = 0;
    String selectedAnswer = "null";
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionsTextView = findViewById(R.id.question);
        respA = findViewById(R.id.A_response);
        respB = findViewById(R.id.B_response);
        respC = findViewById(R.id.C_response);
        respD = findViewById(R.id.D_response);
        nextButton = findViewById(R.id.next_button);

        getQuestions();

        respA.setOnClickListener(this);
        respB.setOnClickListener(this);
        respC.setOnClickListener(this);
        respD.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        respA.setBackgroundColor(Color.WHITE);
        respB.setBackgroundColor(Color.WHITE);
        respC.setBackgroundColor(Color.WHITE);
        respD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) v;

        if (clickedButton.getId() == R.id.next_button) {

            if (questionIndex <= 9) {
                if (Objects.equals(selectedAnswer, quiz.results.get(questionIndex).correct_answer)) {
                    score++;
                }
                if (!Objects.equals(selectedAnswer, "null")) {
                    questionIndex++;
                    setValuesToQuiz(quiz, questionIndex);
                    selectedAnswer = "null";
                } else {
                    Toast.makeText(Quiz.this, "You have to select an answer!", Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.CYAN);
        }
    }

    void getQuestions() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opentdb.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Request request = retrofit.create(Request.class);
        request.get().enqueue(new Callback<QuizObject>() {

            @Override
            public void onResponse(@NonNull Call<QuizObject> call, @NonNull Response<QuizObject> response) {
                if (response.body() != null && response.body().results != null) {
                    QuizObject quiz = response.body();
                    setGlobalVariableQuiz(quiz);
                    setValuesToQuiz(quiz, 0);
                } else {
                    questionsTextView.setText("Questions were not generated...");
                    setGlobalVariableQuiz(quiz);
                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizObject> call, @NonNull Throwable t) {
                questionsTextView.setText("Questions were not generated...");
                // Relaunch quiz screen
            }
        });
    }

    void setGlobalVariableQuiz(QuizObject quiz) {
        this.quiz = quiz;
    }

    void setValuesToQuiz(QuizObject quiz, int index) {
        if (index <= 9) {

            String question = quiz.results.get(index).getQuestion();
            question = question.replace("&quot;", "'");
            question = question.replace("&#039;", "'");
            question = question.replace("&amp;", "&");
            question = question.replace("&eacute;", "e");
            questionsTextView.setText(question);

            int[] position = getRandomIndexVector();
            Random rand = new Random();
            int randomOrder = (rand.nextInt((3 - 1) + 1) + 1) - 1;

            switch (randomOrder) {
                case 0:
                    respA.setText(quiz.results.get(index).getCorrect_answer());
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[0]));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1]));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2]));
                    break;

                case 1:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0]));
                    respB.setText(quiz.results.get(index).getCorrect_answer());
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[1]));
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2]));
                    break;

                case 2:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0]));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1]));
                    respC.setText(quiz.results.get(index).getCorrect_answer());
                    respD.setText(quiz.results.get(index).getIncorrect_answers().get(position[2]));
                    break;

                default:
                    respA.setText(quiz.results.get(index).getIncorrect_answers().get(position[0]));
                    respB.setText(quiz.results.get(index).getIncorrect_answers().get(position[1]));
                    respC.setText(quiz.results.get(index).getIncorrect_answers().get(position[2]));
                    respD.setText(quiz.results.get(index).getCorrect_answer());
                    break;
            }

        } else {
            questionsTextView.setText("Score: " + score);
            questionIndex++;
        }
    }

    int[] getRandomIndexVector() {
        int[] indexVector = new int[]{5, 5, 5};
        Random rand = new Random();
        int randomValue = 0;
        indexVector[0] = (rand.nextInt((3 - 1) + 1) + 1) - 1;
        int currentElem = 1;

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

