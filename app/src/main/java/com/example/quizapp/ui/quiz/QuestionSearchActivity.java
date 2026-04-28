package com.example.quizapp.ui.quiz;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import com.example.quizapp.utils.BackendService;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionSearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private ChipGroup filterChipGroup;
    private RecyclerView searchRecyclerView;
    private ProgressBar progressBar;
    private List<Question> allQuestions = new ArrayList<>();
    private List<Question> filteredQuestions = new ArrayList<>();
    private SearchAdapter adapter;
    private String selectedTopic = "All Topics";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_search);

        searchView = findViewById(R.id.searchView);
        filterChipGroup = findViewById(R.id.filterChipGroup);
        searchRecyclerView = findViewById(R.id.searchRecyclerView);
        progressBar = findViewById(R.id.searchProgressBar);

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        setupRecyclerView();
        loadAllQuestions();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

        filterChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip chip = findViewById(checkedId);
            if (chip != null) {
                selectedTopic = chip.getText().toString();
                filter(searchView.getQuery().toString());
            }
        });
    }

    private void setupRecyclerView() {
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchAdapter(filteredQuestions);
        searchRecyclerView.setAdapter(adapter);
    }

    private void loadAllQuestions() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                JSONArray quizzes = BackendService.getQuizList();
                if (quizzes == null) return;

                Set<String> topics = new HashSet<>();
                for (int i = 0; i < quizzes.length(); i++) {
                    JSONObject quiz = quizzes.getJSONObject(i);
                    String quizId = quiz.getString("_id");
                    String quizTitle = quiz.getString("title");
                    topics.add(quizTitle);

                    JSONObject detailedQuiz = BackendService.getQuizById(quizId);
                    if (detailedQuiz != null) {
                        JSONArray qArray = detailedQuiz.optJSONArray("questions");
                        if (qArray != null) {
                            for (int j = 0; j < qArray.length(); j++) {
                                JSONObject q = qArray.getJSONObject(j);
                                String qText = q.optString("questionText", "");
                                List<String> options = new ArrayList<>();
                                JSONArray opts = q.optJSONArray("options");
                                for (int k = 0; opts != null && k < opts.length(); k++) options.add(opts.getString(k));
                                
                                int correctIdx = q.optInt("correctOptionIndex", 0);
                                String topic = q.optString("topic", quizTitle);
                                String diff = q.optString("difficulty", "medium");
                                String id = q.optString("_id", q.optString("id", String.valueOf(j)));
                                String explanation = q.optString("explanation", "");

                                allQuestions.add(new Question(id, qText, options, correctIdx, topic, diff, explanation));
                            }
                        }
                    }
                }

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    populateTopicChips(topics);
                    filteredQuestions.addAll(allQuestions);
                    adapter.notifyDataSetChanged();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error loading bank", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void populateTopicChips(Set<String> topics) {
        for (String topic : topics) {
            Chip chip = new Chip(this);
            chip.setText(topic);
            chip.setCheckable(true);
            chip.setClickable(true);
            filterChipGroup.addView(chip);
        }
    }

    private void filter(String query) {
        filteredQuestions.clear();
        for (Question q : allQuestions) {
            boolean matchesQuery = q.getQuestionText().toLowerCase().contains(query.toLowerCase());
            boolean matchesTopic = selectedTopic.equals("All Topics") || q.getTopic().equalsIgnoreCase(selectedTopic);
            
            if (matchesQuery && matchesTopic) {
                filteredQuestions.add(q);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
        private List<Question> list;
        SearchAdapter(List<Question> list) { this.list = list; }

        @androidx.annotation.NonNull
        @Override
        public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_question, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
            Question q = list.get(position);
            holder.text.setText(q.getQuestionText());
            holder.topic.setText(q.getTopic());
            holder.diff.setText(q.getDifficulty());
        }

        @Override
        public int getItemCount() { return list.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            android.widget.TextView text, topic, diff;
            ViewHolder(View v) {
                super(v);
                text = v.findViewById(R.id.searchQuestionText);
                topic = v.findViewById(R.id.searchTopicTag);
                diff = v.findViewById(R.id.searchDiffTag);
            }
        }
    }
}
