package com.project.quiz_app;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.project.quiz_app.quiz.DailyQuiz;
import com.project.quiz_app.quiz.QuizMenu;

import java.util.Objects;

public class DialogObject {

    Activity activity;
    AlertDialog dialog;

    public DialogObject(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        activity.getLayoutInflater();
        builder.setView(R.layout.progress_dialog);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void seeQuizResultsDialog(int score, int totalScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.quiz_results_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_quiz_results);
        TextView lastQuizScoreTextView = view.findViewById(R.id.textview_show_score);
        TextView totalScoreTextView = view.findViewById(R.id.textview_show_total_score);

        String stringToSetToTextView = lastQuizScoreTextView.getText().toString();
        stringToSetToTextView += " " + score;
        lastQuizScoreTextView.setText(stringToSetToTextView);

        stringToSetToTextView = totalScoreTextView.getText().toString();
        stringToSetToTextView += " " + totalScore;
        totalScoreTextView.setText(stringToSetToTextView);

        okButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, QuizMenu.class);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void finishQuizDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.finish_quiz_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button yesButton = view.findViewById(R.id.yes_close_quiz_button);
        Button noButton = view.findViewById(R.id.no_close_quiz_button);

        yesButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });

        noButton.setOnClickListener(v -> dismissDialog());
    }


    public void dailyQuizDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.daily_quiz_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button proceedButton = view.findViewById(R.id.proceed_with_daily_quiz);
        Button laterButton = view.findViewById(R.id.later_daily_quiz);

        proceedButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, DailyQuiz.class);
            activity.startActivity(intent);
            activity.finish();
        });

        laterButton.setOnClickListener(v -> dismissDialog());
    }

    public void seeDailyQuizResultsDialog(int score, int dailyQuizTotalScore) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.quiz_results_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_quiz_results);
        TextView lastQuizScoreTextView = view.findViewById(R.id.textview_show_score);
        TextView totalScoreTextView = view.findViewById(R.id.textview_show_total_score);

        String stringToSetToTextView = lastQuizScoreTextView.getText().toString();
        stringToSetToTextView += " " + score;
        lastQuizScoreTextView.setText(stringToSetToTextView);


        stringToSetToTextView = "Daily quiz total score: "+ dailyQuizTotalScore;
        totalScoreTextView.setText(stringToSetToTextView);

        okButton.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
        });
    }

    public void dailyQuizNotAvailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.daily_quiz_not_available_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button okButton = view.findViewById(R.id.ok_quiz_results);

        okButton.setOnClickListener(v -> {
            Intent intent = new Intent(activity, MainActivity.class);
            ActivityOptions options = ActivityOptions.makeCustomAnimation(activity,
                    R.anim.slide_in_left, android.R.anim.slide_out_right);
            activity.startActivity(intent, options.toBundle());
            activity.finish();
        });
    }

    public void exitAppDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.exit_quiz_app_dialog, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button yesButton = view.findViewById(R.id.yes_close_app_button);
        Button noButton = view.findViewById(R.id.no_close_app_button);

        yesButton.setOnClickListener(v -> {
            dismissDialog();
            activity.finish();
        });
        noButton.setOnClickListener(v -> dismissDialog());
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
