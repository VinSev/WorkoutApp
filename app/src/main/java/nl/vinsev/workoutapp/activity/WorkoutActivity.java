package nl.vinsev.workoutapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.controller.ExerciseController;
import nl.vinsev.workoutapp.controller.ProgressController;
import nl.vinsev.workoutapp.controller.WorkoutController;
import nl.vinsev.workoutapp.databinding.ActivityWorkoutBinding;
import nl.vinsev.workoutapp.model.Exercise;
import nl.vinsev.workoutapp.model.Workout;

public class WorkoutActivity extends AppCompatActivity {

    private ActivityWorkoutBinding activityWorkoutBinding;
    private TextView description;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private Button startButton;

    private Workout workout;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityWorkoutBinding = ActivityWorkoutBinding.inflate(getLayoutInflater());
        description = activityWorkoutBinding.descriptionWorkout;
        scrollView = activityWorkoutBinding.scrollExercises;
        linearLayout = activityWorkoutBinding.lExercises;
        startButton = activityWorkoutBinding.bStart;
        View view = activityWorkoutBinding.getRoot();
        setContentView(view);

        workout = (Workout) getIntent().getSerializableExtra("workout");
        index = getIntent().getIntExtra("index", -1);

        ExerciseController.getInstance().getAllImages();

        setupDescription();
        setupListExercises();
        setupStartButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_workout, menu);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, CreateWorkoutActivity.class);
                intent.putExtra("workout", workout);
                intent.putExtra("index", index);
                startActivity(intent);
                break;
            case R.id.action_delete:
                WorkoutController.getInstance().removeWorkout(index);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            onBackPressed();
        }
        getSupportFragmentManager().popBackStack();
        return true;
    }

    private void setupDescription() {
        int countExercises = workout.getExercises().size();
        float intervalLength = workout.getExerciseIntervalLength() + workout.getRestIntervalLength();
        int minutes = Math.round((countExercises * intervalLength - workout.getRestIntervalLength()) / 60);
        description.setText(getString(
                R.string.description_activity_workout,
                minutes,
                countExercises));
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
        ArrayList<Exercise> exercises = workout.getExercises();
        int childCount = linearLayout.getChildCount();
        int newChildCount = childCount + 50;

        for(int i = childCount; i < newChildCount; i++) {
            if(exercises == null) {
                break;
            }
            if(i >= exercises.size()) {
                break;
            }
            CardView cardView = setupCardView(exercises.get(i));
            linearLayout.addView(cardView);
        }
    }

    private CardView setupCardView(Exercise exercise) {
        Context context = this;
        CardView cardView = new CardView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        cardView.setLayoutParams(layoutParams);
        cardView.setRadius(15);
        cardView.setMaxCardElevation(6);
        cardView.setFocusable(true);

        TextView text = new TextView(context);
        text.setText(exercise.getTitle());
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setPadding(21, 0, 21, 0);
        text.setTextSize(16);

        cardView.addView(text);

        return cardView;
    }

    private void setupStartButton() {
        startButton.setOnClickListener(onClick -> {
            ExerciseController.getInstance().getAllImages();
            Intent intent = new Intent(this, PlayActivity.class);
            intent.putExtra("workout", workout);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

    }
}