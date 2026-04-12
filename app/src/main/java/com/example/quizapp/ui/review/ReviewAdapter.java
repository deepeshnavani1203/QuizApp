package com.example.quizapp.ui.review;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private List<Question> questions;

    public ReviewAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Question q = questions.get(position);
        holder.questionText.setText((position + 1) + ". " + q.getQuestionText());
        
        int selected = q.getUserSelectedAnswerIndex();
        int correct = q.getCorrectAnswerIndex();
        
        String selectedText = selected != -1 ? q.getOptions().get(selected) : "No answer (Timeout)";
        holder.selectedAnswer.setText("Your Answer: " + selectedText);
        holder.correctAnswer.setText("Correct Answer: " + q.getOptions().get(correct));
        
        if (selected == correct) {
            holder.status.setText("CORRECT");
            holder.status.setBackgroundColor(Color.parseColor("#4CAF50"));
            holder.selectedAnswer.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.status.setText("WRONG");
            holder.status.setBackgroundColor(Color.parseColor("#F44336"));
            holder.selectedAnswer.setTextColor(Color.parseColor("#F44336"));
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText, selectedAnswer, correctAnswer, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.reviewQuestionText);
            selectedAnswer = itemView.findViewById(R.id.selectedAnswerText);
            correctAnswer = itemView.findViewById(R.id.correctAnswerText);
            status = itemView.findViewById(R.id.statusText);
        }
    }
}
