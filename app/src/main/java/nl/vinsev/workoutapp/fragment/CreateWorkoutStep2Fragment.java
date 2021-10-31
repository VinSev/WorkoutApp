package nl.vinsev.workoutapp.fragment;

import static androidx.core.content.ContextCompat.getDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.CreateWorkoutActivity;
import nl.vinsev.workoutapp.databinding.FragmentCreateWorkoutStep2Binding;
import nl.vinsev.workoutapp.model.Exercise;
import nl.vinsev.workoutapp.model.Workout;

public class CreateWorkoutStep2Fragment extends Fragment {

    private FragmentCreateWorkoutStep2Binding fragmentCreateWorkoutStep2Binding;
    private Button nextStep;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private CreateWorkoutActivity createWorkoutActivity;

    public synchronized static CreateWorkoutStep2Fragment newInstance() {
        return new CreateWorkoutStep2Fragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        fragmentCreateWorkoutStep2Binding = FragmentCreateWorkoutStep2Binding.inflate(getLayoutInflater());
        scrollView = fragmentCreateWorkoutStep2Binding.scrollRemoveExercises;
        linearLayout = fragmentCreateWorkoutStep2Binding.lExercises;

        return fragmentCreateWorkoutStep2Binding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createWorkoutActivity = (CreateWorkoutActivity) getActivity();
        nextStep = createWorkoutActivity.getNextStep();

        setupNextStepButton();
        setupListExercises();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_create_workout_step2);
    }

    private void setupNextStepButton() {
        nextStep.setText(getString(R.string.create_workout_step2_button, createWorkoutActivity.getWorkout().getExercises().size()));
        nextStep.setOnClickListener(onClick -> {
            createWorkoutActivity.replaceFragment(CreateWorkoutStep3Fragment.newInstance());
            createWorkoutActivity.getFragmentContainerView().startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade));
        });
    }

    private void setupListExercises() {
        getExercisesForScrollView();

        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                        getExercisesForScrollView();
                    }
                });
    }

    private void getExercisesForScrollView() {
        ArrayList<Exercise> exercises = createWorkoutActivity.getWorkout().getExercises();
        int childCount = linearLayout.getChildCount();
        int newChildCount = childCount + 50;

        for(int i = childCount; i < newChildCount; i++) {
            if(i >= exercises.size()) {
                break;
            }
            CardView cardView = setupCardView(exercises.get(i));
            linearLayout.addView(cardView);
        }
    }

    private CardView setupCardView(Exercise exercise) {
        Context context = this.getContext();
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(15);
        cardView.setMaxCardElevation(6);
        cardView.setClickable(true);
        cardView.setFocusable(true);

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        cardView.setForeground(getDrawable(getContext(), outValue.resourceId));

        TextView text = new TextView(context);
        text.setText(exercise.getTitle());
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setPadding(21, 0, 84, 0);
        text.setTextSize(16);

        TextView arrow = new TextView(context);
        arrow.setText("-");
        arrow.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);
        arrow.setPadding(0, 0, 42, 0);
        arrow.setTextSize(21);

        cardView.addView(text);
        cardView.addView(arrow);

        Workout workout = createWorkoutActivity.getWorkout();

        cardView.setOnClickListener(onClick -> {
            linearLayout.removeView(cardView);
            workout.removeExercise(exercise);
            updateNextStepButtonTitle();
        });

        return cardView;
    }

    private void updateNextStepButtonTitle() {
        nextStep.setText(getString(R.string.create_workout_step2_button, createWorkoutActivity.getWorkout().getExercises().size()));
    }
}