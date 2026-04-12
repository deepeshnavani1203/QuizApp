package com.example.quizapp.ui.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> categories;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.title.setText(category);
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
        
        // Dynamic map logic for icons
        int iconRes;
        switch (category) {
            case "Java": iconRes = R.drawable.ic_java; break;
            case "C": 
            case "C++": iconRes = R.drawable.ic_cpp; break;
            case "Python": iconRes = R.drawable.ic_python; break;
            case "JS": iconRes = R.drawable.ic_js; break;
            case "Git": iconRes = R.drawable.ic_git; break;
            case "OS": iconRes = R.drawable.ic_os; break;
            case "React": iconRes = R.drawable.ic_react; break;
            case "Node.js": iconRes = R.drawable.ic_node; break;
            case "DBMS": iconRes = R.drawable.ic_dbms; break;
            case "Networks": iconRes = R.drawable.ic_network; break;
            default: iconRes = R.drawable.ic_code; break;
        }
        holder.icon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView icon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.categoryTitle);
            icon = itemView.findViewById(R.id.categoryIcon);
        }
    }
}
