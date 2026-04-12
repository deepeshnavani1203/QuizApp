package com.example.quizapp.ui.review;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizapp.R;
import com.example.quizapp.models.Question;
import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView reviewRecyclerView;
    private Button backToHomeBtn;
    private List<Question> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);

        // Get questions from intent
        questionList = (ArrayList<Question>) getIntent().getSerializableExtra("QUESTIONS");
        if (questionList == null) questionList = new ArrayList<>();

        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ReviewAdapter adapter = new ReviewAdapter(questionList);
        reviewRecyclerView.setAdapter(adapter);

        backToHomeBtn.setOnClickListener(v -> finish());
    }
}
