package com.example.quizapp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.quizapp.models.QuizResult;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizHistory.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_HISTORY = "quiz_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUIZ_NAME = "quiz_name";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TOTAL = "total_questions";
    private static final String COLUMN_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_HISTORY + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUIZ_NAME + " TEXT, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_TOTAL + " INTEGER, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }

    public void addResult(QuizResult result) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUIZ_NAME, result.getQuizName());
        values.put(COLUMN_SCORE, result.getScore());
        values.put(COLUMN_TOTAL, result.getTotalQuestions());
        values.put(COLUMN_DATE, result.getDate());

        db.insert(TABLE_HISTORY, null, values);
        db.close();
    }

    public List<QuizResult> getAllResults() {
        List<QuizResult> results = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                QuizResult result = new QuizResult(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUIZ_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                );
                result.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                results.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return results;
    }
}
