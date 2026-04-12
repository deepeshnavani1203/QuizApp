package com.example.quizapp.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.models.QuizResult;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<QuizResult> results;

    public HistoryAdapter(List<QuizResult> results) {
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizResult result = results.get(position);
        holder.quizName.setText(result.getQuizName());
        holder.date.setText(result.getDate());
        holder.score.setText(result.getScore() + "/" + result.getTotalQuestions());
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView quizName, date, score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.historyQuizName);
            date = itemView.findViewById(R.id.historyDate);
            score = itemView.findViewById(R.id.historyScore);
        }
    }
}
