package com.example.quizapp.ui.quiz;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;

public class OptionAdapter extends RecyclerView.Adapter<OptionAdapter.ViewHolder> {

    private Question currentQuestion;
    private List<String> optionsList = new ArrayList<>();
    private int selectedIndex = -1;
    private int correctIndex = -1;
    private boolean isFinished = false;
    private boolean isLearnMode = false;
    private OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(int index);
    }

    public OptionAdapter(Question question, OnOptionClickListener listener) {
        this.currentQuestion = question;
        this.listener = listener;
        updateOptionsList();
    }

    private void updateOptionsList() {
        optionsList.clear();
        optionsList.addAll(currentQuestion.getOptions());
    }

    public void setLearnMode(boolean learnMode) {
        this.isLearnMode = learnMode;
    }

    public void setSelection(int index) {
        this.selectedIndex = index;
        notifyDataSetChanged();
    }

    public void showResult(int correctIndex) {
        this.correctIndex = correctIndex;
        this.isFinished = true;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.optionText.setText(optionsList.get(position));
        holder.optionIndex.setText(String.valueOf((char) ('A' + position)));

        // Reset UI
        holder.card.setStrokeColor(Color.TRANSPARENT);
        holder.card.setCardBackgroundColor(Color.WHITE);
        holder.card.setStrokeWidth(0);

        if (isLearnMode && (isFinished || selectedIndex != -1)) {
            if (position == correctIndex) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFF176")); // Correct Yellow
                holder.card.setStrokeColor(Color.parseColor("#FBC02D"));
                holder.card.setStrokeWidth(4);
            } else if (position == selectedIndex && position != correctIndex) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFCCBC")); // Wrong Red
                holder.card.setStrokeColor(Color.parseColor("#F44336"));
                holder.card.setStrokeWidth(4);
            }
        } else if (isFinished) {
            if (position == correctIndex) {
                holder.card.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.card.setStrokeColor(Color.parseColor("#4CAF50"));
                holder.card.setStrokeWidth(4);
            } else if (position == selectedIndex) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFEBEE"));
                holder.card.setStrokeColor(Color.parseColor("#F44336"));
                holder.card.setStrokeWidth(4);
            }
        } else {
            if (position == selectedIndex) {
                holder.card.setCardBackgroundColor(Color.parseColor("#E3F2FD"));
                holder.card.setStrokeColor(Color.parseColor("#2196F3"));
                holder.card.setStrokeWidth(4);
            }
        }

        if (isFinished && !isLearnMode) {
            holder.itemView.setClickable(false);
        } else {
            holder.itemView.setOnClickListener(v -> listener.onOptionClick(position));
        }
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView optionText, optionIndex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.optionCard);
            optionText = itemView.findViewById(R.id.optionText);
            optionIndex = itemView.findViewById(R.id.optionIndex);
        }
    }
}
