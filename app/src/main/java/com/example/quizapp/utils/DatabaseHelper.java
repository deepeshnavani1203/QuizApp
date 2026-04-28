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
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_HISTORY = "quiz_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUIZ_NAME = "quiz_name";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TOTAL = "total_questions";
    private static final String COLUMN_TIME_TAKEN = "time_taken";
    private static final String COLUMN_ACCURACY = "accuracy";
    private static final String COLUMN_DATE = "date";

    private static final String TABLE_NOTES = "question_notes";
    private static final String COLUMN_NOTE_ID = "id";
    private static final String COLUMN_QUESTION_ID = "question_id";
    private static final String COLUMN_NOTE_CONTENT = "note_content";

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
                COLUMN_TIME_TAKEN + " INTEGER, " +
                COLUMN_ACCURACY + " INTEGER, " +
                COLUMN_DATE + " TEXT)";
        db.execSQL(createTable);

        String createNotesTable = "CREATE TABLE " + TABLE_NOTES + " (" +
                COLUMN_NOTE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QUESTION_ID + " TEXT UNIQUE, " +
                COLUMN_NOTE_CONTENT + " TEXT)";
        db.execSQL(createNotesTable);
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
        values.put(COLUMN_TIME_TAKEN, result.getTimeTaken());
        values.put(COLUMN_ACCURACY, result.getAccuracy());
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
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME_TAKEN)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ACCURACY)),
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

    public void clearHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, null, null);
        db.close();
    }

    public void saveNote(String questionId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION_ID, questionId);
        values.put(COLUMN_NOTE_CONTENT, content);

        db.insertWithOnConflict(TABLE_NOTES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getNote(String questionId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES, new String[]{COLUMN_NOTE_CONTENT},
                COLUMN_QUESTION_ID + "=?", new String[]{questionId}, null, null, null);

        String note = "";
        if (cursor != null && cursor.moveToFirst()) {
            note = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return note;
    }
}
