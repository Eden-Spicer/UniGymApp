package com.example.gymapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutBuilder extends Fragment {

    private List<Exercise> exercises = new ArrayList<>();
    private String dayOfWeek = "";

    public WorkoutBuilder(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public static WorkoutBuilder newInstance(String dayOfWeek) {
        WorkoutBuilder fragment = new WorkoutBuilder(dayOfWeek);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_builder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        FloatingActionButton addWorkoutFab = view.findViewById(R.id.addWorkout);
        addWorkoutFab.setOnClickListener(v -> showExerciseDialog()
        );
    }

    private void showExerciseDialog() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.exercise_dialog, null);

        final EditText exerciseNameEditText = view.findViewById(R.id.edit_text_exercise_name);
        final EditText exerciseWeightEditText = view.findViewById(R.id.edit_text_exercise_weight);
        final EditText exerciseRepetitionsEditText = view.findViewById(R.id.edit_text_exercise_repetitions);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setTitle(R.string.create_exercise_dialog_title)
                .setPositiveButton(R.string.create_exercise_dialog_create_button, (dialog, which) -> {
                    String exerciseName = exerciseNameEditText.getText().toString();
                    String exerciseWeightStr = exerciseWeightEditText.getText().toString();
                    String exerciseRepetitionsStr = exerciseRepetitionsEditText.getText().toString();

                    // Check for invalid input
                    if (exerciseName.isEmpty() || exerciseWeightStr.isEmpty() || exerciseRepetitionsStr.isEmpty()) {
                        Snackbar.make(getView(), R.string.create_exercise_dialog_error_message, Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    float exerciseWeight = Float.parseFloat(exerciseWeightStr);
                    int exerciseRepetitions = Integer.parseInt(exerciseRepetitionsStr);

                    Exercise exercise = new Exercise(exerciseName, exerciseWeight, exerciseRepetitions);
                    exercises.add(exercise);
                    addExerciseToWorkout(exercise);
                })
                .setNegativeButton(R.string.create_exercise_dialog_cancel_button, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addExerciseToWorkout(Exercise exercise) {
        // Add the exercise to the list
        exercises.add(exercise);

        // Create a new MaterialCardView for the exercise
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        MaterialCardView cardView = (MaterialCardView) inflater.inflate(R.layout.exercise_card, null, false);
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

        MaterialButton deleteButton = cardView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Delete Exercise")
                    .setMessage("Are you sure you want to delete this exercise?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        // Delete the exercise from the list and remove the card view
                        exercises.remove(exercise);
                        layout.removeView(cardView);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });

        cardView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
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
                            Exercise exercise = (Exercise) parent.getChildAt(i).getTag();
                            newExercisesOrder.add(exercise);
                        }
                        exercises.clear();
                        exercises.addAll(newExercisesOrder);
                        break;
                }
                return true;
            }
        });


        cardView.setOnLongClickListener(v -> {
            // Start a drag and drop operation
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(null, shadowBuilder, v, 0);
            return true;
        });

    }
}