package com.example.gymapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WorkoutBuilder extends Fragment {

    private List<Exercise> exercises = new ArrayList<>(); // List to store the exercises
    private String dayOfWeek = ""; // The day of the week for which the exercises are being built
    private static final String SHARED_PREFS = "user_info"; // Name of the shared preferences file
    private static final String EXERCISES_KEY = "exercises_key"; // Key to save and retrieve exercises list from bundle

    public WorkoutBuilder(String dayOfWeek) { // Constructor to create a new instance of WorkoutBuilder with given day of week
        this.dayOfWeek = dayOfWeek;
    }

    public static WorkoutBuilder newInstance(String dayOfWeek) { // Static factory method to create a new instance of WorkoutBuilder with given day of week
        return new WorkoutBuilder(dayOfWeek);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) { // Restore the exercises list if it was saved in the bundle
            Gson gson = new Gson(); // Create a new instance of Gson
            String jsonExercises = savedInstanceState.getString(EXERCISES_KEY, null); // Retrieve the serialized exercises list from the bundle
            Type type = new TypeToken<ArrayList<Exercise>>() {}.getType(); // Create a TypeToken for ArrayList<Exercise>
            exercises = gson.fromJson(jsonExercises, type); // Deserialize the exercises list and set it to the instance variable
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_builder, container, false); // Inflate the layout for this fragment
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.topAppBar); // Find the Toolbar view in the layout

        toolbar.setNavigationOnClickListener(v -> { // Set an OnClickListener for the navigation button in the toolbar
            if (getActivity() != null) {
                getActivity().onBackPressed(); // Call onBackPressed() of the parent activity
            }
        });

        FloatingActionButton addWorkoutFab = view.findViewById(R.id.addWorkout); // Find the FloatingActionButton view in the layout
        addWorkoutFab.setOnClickListener(this::showAddExercisePopupMenu); // Set an OnClickListener for the FAB to show a popup menu
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new Gson();
        String jsonExercises = gson.toJson(exercises); // Convert exercises to JSON string
        outState.putString(EXERCISES_KEY, jsonExercises); // Save JSON string to bundle
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            exercises.clear(); // Clear exercises list before loading exercises
            loadExercises(); // Load exercises from shared preferences
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveExercises(); // Save exercises to shared preferences
    }

    private void showAddExercisePopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.fab_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.add_custom_exercise) {
                showExerciseDialog(); // Show dialog to add custom exercise
                return true;
            } else if (itemId == R.id.add_premade_exercise) {
                showPremadeExercisesDialog(); // Show dialog to add pre-made exercise
                return true;
            }
            return false;
        });

        popupMenu.show(); // Show popup menu
    }

    private void saveExercises() {
        Log.d("WorkoutBuilder", "saveExercises()");
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String jsonExercises = gson.toJson(exercises); // Convert exercises to JSON string
        editor.putString(dayOfWeek, jsonExercises); // Save JSON string to shared preferences
        editor.apply();
        Log.d("WorkoutBuilder", "JSON Exercises: " + jsonExercises);
        Log.d("WorkoutBuilder", "Saved exercises to key: " + dayOfWeek);
    }

    private void loadExercises() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(dayOfWeek, null); // Get JSON string from shared preferences
        Type type = new TypeToken<ArrayList<Exercise>>() {}.getType();

        List<Exercise> loadedExercises = gson.fromJson(json, type); // Convert JSON string to list of exercises

        if (loadedExercises != null) {
            List<Exercise> exercisesToAdd = new ArrayList<>();
            for (Exercise exercise : loadedExercises) {
                if (!exercises.contains(exercise)) {
                    exercisesToAdd.add(exercise); // Add exercise to list if it's not already there
                }
            }
            exercises.addAll(exercisesToAdd); // Add new exercises to list
            Log.d("WorkoutBuilder", "Loaded exercises: " + exercises);
            for (Exercise exercise : exercisesToAdd) {
                addExerciseToWorkout(exercise); // Add new exercises to UI
            }
        } else {
            Log.d("WorkoutBuilder", "No saved exercises found");
        }
    }

    private void showExerciseDialog() {
        // Show the custom exercise dialog directly
        showCustomExerciseDialog();
    }

    private void showCustomExerciseDialog() {
        // Inflate the exercise dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.exercise_dialog, null);

        // Find the EditText views for exercise name, weight, and repetitions
        final EditText exerciseNameEditText = view.findViewById(R.id.edit_text_exercise_name);
        final EditText exerciseWeightEditText = view.findViewById(R.id.edit_text_exercise_weight);
        final EditText exerciseRepetitionsEditText = view.findViewById(R.id.edit_text_exercise_repetitions);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle(R.string.create_exercise_dialog_title)
                .setPositiveButton(R.string.create_exercise_dialog_create_button, (dialog, which) -> {
                    // Get the input values from the EditText views
                    String exerciseName = exerciseNameEditText.getText().toString();
                    String exerciseWeightStr = exerciseWeightEditText.getText().toString();
                    String exerciseRepetitionsStr = exerciseRepetitionsEditText.getText().toString();

                    // Check for invalid input
                    if (exerciseName.isEmpty() || exerciseWeightStr.isEmpty() || exerciseRepetitionsStr.isEmpty()) {
                        // Display an error message using Snackbar if input is invalid
                        Snackbar.make(getView(), R.string.create_exercise_dialog_error_message, Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Parse the input values to their appropriate types
                    float exerciseWeight = Float.parseFloat(exerciseWeightStr);
                    int exerciseRepetitions = Integer.parseInt(exerciseRepetitionsStr);

                    // Create a new Exercise object with the input values and add it to the exercises list
                    Exercise exercise = new Exercise(exerciseName, exerciseWeight, exerciseRepetitions);
                    exercises.add(exercise);

                    // Add the new Exercise to the workout and save the exercises to SharedPreferences
                    addExerciseToWorkout(exercise);
                    saveExercises();
                })
                .setNegativeButton(R.string.create_exercise_dialog_cancel_button, null);

        // Create and display the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showPremadeExercisesDialog() {
        // Get all premade exercises from the database
        ExerciseDatabaseHelper dbHelper = new ExerciseDatabaseHelper(requireContext());
        List<Exercise> premadeExercises = dbHelper.getAllPremadeExercises();

        // Log the number of premade exercises found
        Log.d("TAG", "Number of premade exercises: " + premadeExercises.size());

        // Create an array of exercise names to use for the AlertDialog
        String[] exerciseNames = new String[premadeExercises.size()];
        for (int i = 0; i < premadeExercises.size(); i++) {
            exerciseNames[i] = premadeExercises.get(i).getName();
        }

        // Build the AlertDialog for choosing a premade exercise
        AlertDialog.Builder premadeExercisesDialog = new AlertDialog.Builder(requireContext());
        premadeExercisesDialog.setTitle(R.string.choose_premade_exercise)
                .setItems(exerciseNames, (innerDialog, index) -> {
                    // Get the selected exercise and add it to the exercises list
                    Exercise selectedExercise = premadeExercises.get(index);
                    exercises.add(selectedExercise);

                    // Add the selected Exercise to the workout and save the exercises to SharedPreferences
                    addExerciseToWorkout(selectedExercise);
                    saveExercises();
                });

        // Create and display the AlertDialog
        premadeExercisesDialog.create().show();
    }


    private void addExerciseToWorkout(Exercise exercise) {
        // Log that the method is called
        Log.d("WorkoutBuilder", "addExerciseToWorkout()");

        // Inflate the layout for the exercise card view
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        MaterialCardView cardView = (MaterialCardView) inflater.inflate(R.layout.exercise_card, null, false);

        // Set the tag of the card view to the exercise object
        cardView.setTag(exercise);

        // Set the exercise details in the card view
        MaterialTextView nameTextView = cardView.findViewById(R.id.exercise_name);
        String name = exercise.getName();
        if (name.length() > 15) {
            name = name.substring(0, 15) + "..."; // truncate name to 15 characters and add ellipsis
        }
        nameTextView.setText(name);
        nameTextView.setMaxLines(1); // set max lines to 1 to restrict to one line
        nameTextView.setEllipsize(TextUtils.TruncateAt.END); // add ellipsis if the name is longer than 15 characters

        MaterialTextView weightTextView = cardView.findViewById(R.id.exercise_weight);
        String weightText = getString(R.string.weight, exercise.getWeight());
        weightText += " x ";
        weightTextView.setText(weightText);

        MaterialTextView repetitionsTextView = cardView.findViewById(R.id.exercise_repetitions);
        repetitionsTextView.setText(String.valueOf(exercise.getRepetitions()));

        // Add the card view to the layout
        LinearLayout layout = getView().findViewById(R.id.workout_exercises_layout);
        layout.addView(cardView);

        // Set the click listeners for the edit and delete buttons of the card view
        MaterialButton editButton = cardView.findViewById(R.id.edit_button);
        editButton.setOnClickListener(view -> showEditExerciseDialog(exercise, weightTextView, repetitionsTextView));

        MaterialButton deleteButton = cardView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> new AlertDialog.Builder(getContext())
                .setTitle("Delete Exercise")
                .setMessage("Are you sure you want to delete this exercise?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Delete the exercise from the list and remove the card view
                    exercises.remove(exercise);
                    layout.removeView(cardView);
                })
                .setNegativeButton(android.R.string.no, null)
                .show());

        cardView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                case DragEvent.ACTION_DRAG_ENDED:
                    // Unhighlight the card view when the drag event ends
                    // Unhighlight the card view when the drag event exits the drop zone
                    // Highlight the card view to indicate the drop zone
                    cardView.setCardBackgroundColor(MaterialColors.getColor(cardView, com.google.android.material.R.attr.colorSurface));
                    break;
                case DragEvent.ACTION_DROP:
                    // Get the item being dragged and its original parent view
                    View dragView = (View) event.getLocalState();
                    ViewGroup parent = (ViewGroup) dragView.getParent();

                    // Remove the item from its original parent and add it to the new parent
                    parent.removeView(dragView);

                    // Find the position to insert the dragged view
                    int index = -1;
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        View child = parent.getChildAt(i);
                        if (child != cardView) {
                            // Check if the dragged view is being dropped above or below this child view
                            if (event.getY() < child.getTop() + child.getHeight() / 2) {
                                index = i;
                                break;
                            }
                        }
                    }

                    // Add the dragged view at the correct position
                    if (index != -1) {
                        parent.addView(dragView, index);
                    } else {
                        parent.addView(dragView);
                    }

                    // Make the item visible
                    dragView.setVisibility(View.VISIBLE);

                    // Update the order of the exercises in the workout list
                    List<Exercise> newExercisesOrder = new ArrayList<>();
                    for (int i = 0; i < parent.getChildCount(); i++) {
                        Exercise exercise1 = (Exercise) parent.getChildAt(i).getTag();
                        newExercisesOrder.add(exercise1);
                    }
                    exercises.clear();
                    exercises.addAll(newExercisesOrder);
                    break;
            }
            return true;
        });

        cardView.setOnLongClickListener(v -> {
            // Start a drag and drop operation
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(null, shadowBuilder, v, 0);
            return true;
        });

        Log.d("WorkoutBuilder", "Added exercise: " + exercise);

    }

    private void showEditExerciseDialog(Exercise exercise, MaterialTextView weightTextView, MaterialTextView repetitionsTextView) {
        // Inflate the edit exercise dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_exercise_dialog, null);

        // Get references to the weight and repetitions EditText views and set their initial text to the exercise's weight and repetitions
        final EditText exerciseWeightEditText = view.findViewById(R.id.edit_text_exercise_weight);
        final EditText exerciseRepetitionsEditText = view.findViewById(R.id.edit_text_exercise_repetitions);
        exerciseWeightEditText.setText(String.valueOf(exercise.getWeight()));
        exerciseRepetitionsEditText.setText(String.valueOf(exercise.getRepetitions()));

        // Create the edit exercise dialog and set its title and buttons
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle(R.string.edit_exercise_dialog_title)
                .setPositiveButton(R.string.edit_exercise_dialog_save_button, (dialog, which) -> {
                    // Get the new weight and repetitions from the EditText views
                    String exerciseWeightStr = exerciseWeightEditText.getText().toString();
                    String exerciseRepetitionsStr = exerciseRepetitionsEditText.getText().toString();

                    // Check for invalid input
                    if (exerciseWeightStr.isEmpty() || exerciseRepetitionsStr.isEmpty()) {
                        Snackbar.make(getView(), R.string.edit_exercise_dialog_error_message, Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // Parse the new weight and repetitions as floats and ints, respectively
                    float exerciseWeight = Float.parseFloat(exerciseWeightStr);
                    int exerciseRepetitions = Integer.parseInt(exerciseRepetitionsStr);

                    // Update the exercise with the new weight and repetitions
                    exercise.setWeight(exerciseWeight);
                    exercise.setRepetitions(exerciseRepetitions);

                    // Update the weight and repetitions TextViews in the exercise card view
                    String weightText = getString(R.string.weight, exercise.getWeight());
                    weightText += " x ";
                    weightTextView.setText(weightText);
                    repetitionsTextView.setText(String.valueOf(exercise.getRepetitions()));

                    // Save the updated exercises to file
                    saveExercises();
                })
                .setNegativeButton(R.string.edit_exercise_dialog_cancel_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}