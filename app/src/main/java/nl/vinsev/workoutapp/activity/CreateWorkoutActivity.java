package nl.vinsev.workoutapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.databinding.ActivityCreateWorkoutBinding;
import nl.vinsev.workoutapp.fragment.CreateWorkoutStep1Fragment;
import nl.vinsev.workoutapp.model.Workout;

public class CreateWorkoutActivity extends AppCompatActivity {

    private ActivityCreateWorkoutBinding activityCreateWorkoutBinding;
    private FragmentContainerView fragmentContainerView;
    private Button nextStep;

    private Workout workout;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityCreateWorkoutBinding = ActivityCreateWorkoutBinding.inflate(getLayoutInflater());
        fragmentContainerView = activityCreateWorkoutBinding.fcExercises;
        nextStep = activityCreateWorkoutBinding.nextStep;
        View view = activityCreateWorkoutBinding.getRoot();
        setContentView(view);

        workout = (Workout) getIntent().getSerializableExtra("workout");
        index = getIntent().getIntExtra("index", -1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_create_workout, menu);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_cancel) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            onBackPressed();
        } else {
            fragmentContainerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));
            getSupportFragmentManager().popBackStack();
        }
        return true;
    }

    public void addFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(fragmentContainerView.getId(), fragment, null)
                .commit();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(fragmentContainerView.getId(), fragment, null)
                .addToBackStack(fragment.getTag())
                .commit();
    }

    public Workout getWorkout() {
        return workout;
    }

    public int getIndex() {
        return index;
    }

    public FragmentContainerView getFragmentContainerView() {
        return fragmentContainerView;
    }

    public Button getNextStep() {
        return nextStep;
    }
}
