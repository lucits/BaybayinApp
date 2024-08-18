package com.capstonearc.baybayinquizapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizResultsAdapter extends RecyclerView.Adapter<QuizResultsAdapter.QuizResultsViewHolder> {

    private final List<QuestionsList> questionsLists;

    public QuizResultsAdapter(List<QuestionsList> questionsLists) {
        this.questionsLists = questionsLists;
    }

    @NonNull
    @Override
    public QuizResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz_result, parent, false);
        return new QuizResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizResultsViewHolder holder, int position) {
        QuestionsList question = questionsLists.get(position);

        // Add numbering to the question
        String numberedQuestion = (position + 1) + ". " + question.getQuestion();
        holder.questionTextView.setText(numberedQuestion);

        holder.option1TextView.setText("A. " + question.getOption1());
        holder.option2TextView.setText("B. " + question.getOption2());
        holder.option3TextView.setText("C. " + question.getOption3());
        holder.option4TextView.setText("D. " + question.getOption4());

        // Reset text color to default (black)
        int defaultTextColor = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.white);
        holder.option1TextView.setTextColor(defaultTextColor);
        holder.option2TextView.setTextColor(defaultTextColor);
        holder.option3TextView.setTextColor(defaultTextColor);
        holder.option4TextView.setTextColor(defaultTextColor);

        int correctAnswer = question.getAnswer();
        int userSelectedAnswer = question.getUserSelectedAnswer();

        // Highlight the correct answer
        if (correctAnswer == 1) {
            holder.option1TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else if (correctAnswer == 2) {
            holder.option2TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else if (correctAnswer == 3) {
            holder.option3TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        } else if (correctAnswer == 4) {
            holder.option4TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark));
        }

        // Highlight the user's incorrect answer, if any
        if (userSelectedAnswer != 0 && userSelectedAnswer != correctAnswer) {
            if (userSelectedAnswer == 1) {
                holder.option1TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            } else if (userSelectedAnswer == 2) {
                holder.option2TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            } else if (userSelectedAnswer == 3) {
                holder.option3TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            } else if (userSelectedAnswer == 4) {
                holder.option4TextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark));
            }
        }
    }


    @Override
    public int getItemCount() {
        return questionsLists.size();
    }

    static class QuizResultsViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, option1TextView, option2TextView, option3TextView, option4TextView;

        QuizResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            option1TextView = itemView.findViewById(R.id.option1TextView);
            option2TextView = itemView.findViewById(R.id.option2TextView);
            option3TextView = itemView.findViewById(R.id.option3TextView);
            option4TextView = itemView.findViewById(R.id.option4TextView);
        }
    }
}
