package com.project.quiz_app.quiz;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.project.quiz_app.authentication.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class DailyQuiz extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onPause() {
        super.onPause();
        countDownTimer.cancel();
        this.millisUntilFinished = Long.parseLong(countdownNumberTextView.getText().toString()) * 1000;
    }

    // onResume is called when the activity is created,
    // to prevent unexpected issues, I created a variable
    // to check if the activity was created
    boolean dailyQuizActivityCreated = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (dailyQuizActivityCreated) {
            countdownNumberTextView.setText(String.valueOf((int) this.millisUntilFinished / 1000));
            createCountDownTimer(this.millisUntilFinished);
            countDownTimer.start();
        } else {
            createCountDownTimer(16000);
            dailyQuizActivityCreated = true;
        }
    }

    interface Request {
        @GET("https://opentdb.com/api.php?amount=10&type=multiple")
        Call<QuizObject> get();
    }

    // Quiz variable
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
    int totalQuestions = 10;

    // Loading screen
    DialogObject dialogObject = new DialogObject(DailyQuiz.this);

    // Daily quiz compare variables
    Calendar dateAndTimeNow;
    Calendar dateAndTimeAfter24h;

    // CountDown variables
    TextView countdownTextTextView;
    TextView countdownNumberTextView;
    CountDownTimer countDownTimer;
    long millisUntilFinished = 16000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_quiz);

        questionsLeftTextView = findViewById(R.id.questions_left);
        questionsLeftTextView.append(" " + totalQuestions);

        countdownTextTextView = findViewById(R.id.countdown_text);
        countdownNumberTextView = findViewById(R.id.countdown_number);
        questionsTextView = findViewById(R.id.question);
        respA = findViewById(R.id.A_response);
        respB = findViewById(R.id.B_response);
        respC = findViewById(R.id.C_response);
        respD = findViewById(R.id.D_response);
        nextButton = findViewById(R.id.next_button);

        questionsLeftTextView.setVisibility(View.GONE);
        countdownTextTextView.setVisibility(View.GONE);
        countdownNumberTextView.setVisibility(View.GONE);
        questionsTextView.setVisibility(View.GONE);
        respA.setVisibility(View.GONE);
        respB.setVisibility(View.GONE);
        respC.setVisibility(View.GONE);
        respD.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        dialogObject.dailyQuizInfoDialog().thenAccept(okPressed -> {
            if (okPressed) {
                checkDailyQuiz();
            }
        });

        respA.setOnClickListener(this);
        respB.setOnClickListener(this);
        respC.setOnClickListener(this);
        respD.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dialogObject.closeDailyQuizDialog();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }


    // CountDownTimer functions:
    private void createCountDownTimer(long millisUntilFinished) {
        this.countDownTimer = new CountDownTimer(millisUntilFinished, 1000) {
            String timeLeft;

            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = String.valueOf(millisUntilFinished / 1000);
                if (millisUntilFinished / 1000 == 10) {
                    countdownNumberTextView.setTextColor(Color.YELLOW);
                }

                if (millisUntilFinished / 1000 == 5) {
                    countdownNumberTextView.setTextColor(Color.RED);
                }
                countdownNumberTextView.setText(timeLeft);
            }

            @Override
            public void onFinish() {
                timeUp();
            }
        };
    }

    private void timeUp() {
        // if 15 second pass, the user will be
        // redirected to the next question with no score modifications
        if (questionIndex < 10) {
            respA.setBackgroundColor(Color.WHITE);
            respB.setBackgroundColor(Color.WHITE);
            respC.setBackgroundColor(Color.WHITE);
            respD.setBackgroundColor(Color.WHITE);
            countdownNumberTextView.setTextColor(Color.WHITE);

            questionIndex++;
            setQuestionsLeftTextView();
            setValuesToQuiz(quiz, questionIndex);
            selectedAnswer = "null";

            countDownTimer.cancel();
            createCountDownTimer(16000);
            countDownTimer.start();
        }
    }


    private void checkDailyQuiz() {

        dateAndTimeAfter24h = Calendar.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert user != null;

        DatabaseReference mRef = database.getReference()
                .child("Users").child(user.getUid()).child("dailyQuizAvailableDate");

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                if (compare <= 0) {
                    dialogObject.dailyQuizNotAvailableDialog();
                } else {
                    dialogObject.startLoadingDialog();

                    questionsLeftTextView.setVisibility(View.VISIBLE);
                    countdownTextTextView.setVisibility(View.VISIBLE);
                    countdownNumberTextView.setVisibility(View.VISIBLE);
                    questionsTextView.setVisibility(View.VISIBLE);
                    respA.setVisibility(View.VISIBLE);
                    respB.setVisibility(View.VISIBLE);
                    respC.setVisibility(View.VISIBLE);
                    respD.setVisibility(View.VISIBLE);
                    nextButton.setVisibility(View.VISIBLE);

                    getQuestions();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });
    }

    @Override
    public void onClick(View v) {
        respA.setBackgroundColor(Color.WHITE);
        respB.setBackgroundColor(Color.WHITE);
        respC.setBackgroundColor(Color.WHITE);
        respD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) v;

        if (clickedButton.getId() == R.id.next_button) {

            if (questionIndex < 10) {
                if (Objects.equals(selectedAnswer, quiz.results.get(questionIndex).correct_answer)) {
                    score++;
                }
                if (!Objects.equals(selectedAnswer, "null")) {
                    questionIndex++;
                    setQuestionsLeftTextView();
                    setValuesToQuiz(quiz, questionIndex);
                    selectedAnswer = "null";

                    countdownNumberTextView.setTextColor(Color.WHITE);

                    countDownTimer.cancel();
                    createCountDownTimer(16000);
                    countDownTimer.start();
                } else {
                    Toast.makeText(DailyQuiz.this, "You have to select an answer!", Toast.LENGTH_SHORT).show();
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

    private void getQuestions() {

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
                    questionsTextView.setText(R.string.questions_were_not_generated);
                    startActivity(new Intent(getApplicationContext(), DailyQuiz.class));
                }
                dialogObject.dismissDialog();
                countDownTimer.start();
            }

            @Override
            public void onFailure(@NonNull Call<QuizObject> call, @NonNull Throwable t) {
                questionsTextView.setText(R.string.questions_were_not_generated);
                Intent intent = new Intent(getApplicationContext(), PracticeQuiz.class);
                startActivity(intent);
            }
        });
    }

    private void setGlobalVariableQuiz(QuizObject quiz) {
        this.quiz = quiz;
    }

    private void setValuesToQuiz(QuizObject quiz, int index) {
        if (index < 10) {

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

            DatabaseReference mRef = database.getReference().child("Users").child(user.getUid());
            mRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userFromDB = dataSnapshot.getValue(User.class);

                    Calendar dateTimeNow = Calendar.getInstance();
                    dateTimeNow.add(Calendar.HOUR_OF_DAY, 24);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                    String dateAndTime = dateFormat.format(dateTimeNow.getTime());

                    assert userFromDB != null;
                    userFromDB.setDailyQuizAvailableDate(dateAndTime);
                    userFromDB.setDailyLastQuizScore(score);
                    userFromDB.setDailyTotalScore(userFromDB.getDailyTotalScore() + score);

                    mRef.setValue(userFromDB);
                    countDownTimer.onFinish();
                    dialogObject.seeDailyQuizResultsDialog(score, userFromDB.getDailyTotalScore());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Error
                }
            });
            questionsLeftTextView.setVisibility(View.GONE);
            countdownTextTextView.setVisibility(View.GONE);
            countdownNumberTextView.setVisibility(View.GONE);
            questionsTextView.setVisibility(View.GONE);
            respA.setVisibility(View.GONE);
            respB.setVisibility(View.GONE);
            respC.setVisibility(View.GONE);
            respD.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
        }
    }

    // Generate random order to questions
    private int[] getRandomIndexVector() {
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

    private boolean checkDuplicate(int index, int value, int[] vector) {
        for (int i = index - 1; i >= 0; i--) {
            if (value == vector[i]) {
                return false;
            }
        }
        return true;
    }
}
