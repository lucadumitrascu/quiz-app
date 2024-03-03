package com.project.quiz_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        View view = inflater.inflate(R.layout.quiz_results, null);
        builder.setView(view);
        builder.setCancelable(false);

        this.dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        Button button = view.findViewById(R.id.ok_quiz_results);
        TextView lastQuizScoreTextView = view.findViewById(R.id.textview_show_score);
        TextView totalScoreTextView = view.findViewById(R.id.textview_show_total_score);

        String stringToSetToTextView = lastQuizScoreTextView.getText().toString();
        stringToSetToTextView += " " + score;
        lastQuizScoreTextView.setText(stringToSetToTextView);

        stringToSetToTextView = totalScoreTextView.getText().toString();
        stringToSetToTextView += " " + totalScore;
        totalScoreTextView.setText(stringToSetToTextView);

        button.setOnClickListener(v -> {
            dismissDialog();
            Intent intent = new Intent(activity, QuizMenu.class);
            activity.startActivity(intent);
        });
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

}
