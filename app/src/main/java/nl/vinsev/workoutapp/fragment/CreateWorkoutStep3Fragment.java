package nl.vinsev.workoutapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.CreateWorkoutActivity;
import nl.vinsev.workoutapp.activity.MainActivity;
import nl.vinsev.workoutapp.controller.WorkoutController;
import nl.vinsev.workoutapp.databinding.FragmentCreateWorkoutStep3Binding;
import nl.vinsev.workoutapp.model.Workout;

public class CreateWorkoutStep3Fragment extends Fragment {

    private FragmentCreateWorkoutStep3Binding fragmentCreateWorkoutStep3Binding;
    private EditText workoutName;
    private EditText exerciseInterval;
    private EditText restInterval;
    private Button nextStep;
    private CreateWorkoutActivity createWorkoutActivity;

    public synchronized static CreateWorkoutStep3Fragment newInstance() {
        return new CreateWorkoutStep3Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        fragmentCreateWorkoutStep3Binding = FragmentCreateWorkoutStep3Binding.inflate(getLayoutInflater());
        workoutName = fragmentCreateWorkoutStep3Binding.etWorkoutName;
        exerciseInterval = fragmentCreateWorkoutStep3Binding.etExerciseInterval;
        restInterval = fragmentCreateWorkoutStep3Binding.etRestInterval;

        return fragmentCreateWorkoutStep3Binding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createWorkoutActivity = (CreateWorkoutActivity) getActivity();
        nextStep = createWorkoutActivity.getNextStep();

        setupEditTextViews();
        setupNextStepButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_create_workout_step3);
    }

    private void setupEditTextViews() {
        Workout workout = createWorkoutActivity.getWorkout();
        workoutName.setText(workout.getName());
        exerciseInterval.setText(String.format("%s", workout.getExerciseIntervalLength()));
        restInterval.setText(String.format("%s", workout.getRestIntervalLength()));
    }

    private void setupNextStepButton() {
        nextStep.setText(getString(R.string.create_workout_step3_button));
        nextStep.setOnClickListener(onClick -> {
            Workout workout = createWorkoutActivity.getWorkout();
            if(workout.getName() != null) {
                setupWorkout(workout);
                WorkoutController.getInstance().updateWorkout(createWorkoutActivity.getIndex(), workout);
            } else {
                setupWorkout(workout);
                WorkoutController.getInstance().addWorkout(workout);
            }
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            startActivity(intent);
        });
    }

    private void setupWorkout(Workout workout) {
        workout.setName(workoutName.getText().toString());
        workout.setExerciseIntervalLength(Integer.parseInt(exerciseInterval.getText().toString()));
        workout.setRestIntervalLength(Integer.parseInt(restInterval.getText().toString()));
    }
}