package com.example.gymapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ExerciseDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "exercise_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "premade_exercises";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_REPETITIONS = "repetitions";

    public ExerciseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table for premade exercises
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_WEIGHT + " REAL, "
                + COLUMN_REPETITIONS + " INTEGER)";
        db.execSQL(CREATE_TABLE);

        Log.d("TAG", "Inserting premade exercises...");

        // Insert premade exercises
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, "Bench press");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "Squats");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "Deadlifts");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "T-bar row");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "Shoulder press");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "Bicep curls");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(COLUMN_NAME, "Leg press");
        values.put(COLUMN_WEIGHT, 20.0);
        values.put(COLUMN_REPETITIONS, 1);
        db.insert(TABLE_NAME, null, values);

        Log.d("TAG", "Premade exercises inserted.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table and create a new one if the database version has changed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addPremadeExercise(Exercise exercise) {
        // Add a new premade exercise to the database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, exercise.getName());
        values.put(COLUMN_WEIGHT, exercise.getWeight());
        values.put(COLUMN_REPETITIONS, exercise.getRepetitions());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public List<Exercise> getAllPremadeExercises() {
        // Retrieve all premade exercises from the database
        List<Exercise> exercises = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Log.d("TAG", "Executing database query...");
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_WEIGHT, COLUMN_REPETITIONS},
                null, null, null, null, null);

        Log.d("TAG", "Database query executed.");

        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise(cursor.getString(1), cursor.getFloat(2), cursor.getInt(3));
                exercises.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d("TAG", "Number of exercises retrieved from database: " + exercises.size());

        return exercises;
    }
}
