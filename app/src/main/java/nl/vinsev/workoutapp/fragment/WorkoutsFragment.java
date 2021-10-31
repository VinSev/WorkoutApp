package nl.vinsev.workoutapp.fragment;

import static androidx.core.content.ContextCompat.getDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.CreateWorkoutActivity;
import nl.vinsev.workoutapp.activity.MainActivity;
import nl.vinsev.workoutapp.activity.WorkoutActivity;
import nl.vinsev.workoutapp.controller.ExerciseController;
import nl.vinsev.workoutapp.controller.WorkoutController;
import nl.vinsev.workoutapp.databinding.FragmentWorkoutsBinding;
import nl.vinsev.workoutapp.model.Workout;
import nl.vinsev.workoutapp.observer.WorkoutsFragmentObserver;
import nl.vinsev.workoutapp.subject.WorkoutsFragmentSubject;

public class WorkoutsFragment extends Fragment implements WorkoutsFragmentObserver {
    private static WorkoutsFragment workoutsFragment;

    private FragmentWorkoutsBinding fragmentWorkoutsBinding;
    private FloatingActionButton addWorkoutFAB;
    private MainActivity mainActivity;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    private FirebaseAuth firebaseAuth;

    private ExecutorService executor;

    private ArrayList<Workout> workouts;

    public synchronized static WorkoutsFragment newInstance() {
        workoutsFragment = new WorkoutsFragment();
        return workoutsFragment;
    }

    public synchronized static WorkoutsFragment getInstance() {
        if(workoutsFragment == null) {
            workoutsFragment = new WorkoutsFragment();
        }
        return workoutsFragment;
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        workoutsFragment = this;
        firebaseAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();

        fragmentWorkoutsBinding = FragmentWorkoutsBinding.inflate(getLayoutInflater());
        addWorkoutFAB = fragmentWorkoutsBinding.fabAddWorkout;
        scrollView = fragmentWorkoutsBinding.scrollWorkouts;
        linearLayout = fragmentWorkoutsBinding.lWorkoutsContent;

        setupFabAdd();

        return fragmentWorkoutsBinding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        mainActivity.updateUI(user);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_fragment_workouts);
        executor.execute(WorkoutController.getInstance()::updateWorkouts);
        workouts = WorkoutController.getInstance().getWorkouts();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    private void setupFabAdd() {
        addWorkoutFAB.setOnClickListener(onClick -> {
            Intent intent = new Intent(this.getContext(), CreateWorkoutActivity.class);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            int exerciseInterval = Integer.parseInt((String) sharedPreferences.getAll().get("etp_key_exercise_interval"));
            int restInterval = Integer.parseInt((String) sharedPreferences.getAll().get("etp_key_rest_interval"));
            intent.putExtra("workout", new Workout(new ArrayList<>(), null, exerciseInterval, restInterval));
            intent.putExtra("index", -1);
            startActivity(intent);
            mainActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });
    }

    private void setupListWorkouts() {
        getWorkoutsForScrollView();

        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(() -> {
                    if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                        getWorkoutsForScrollView();
                    }
                });
    }

    private void getWorkoutsForScrollView() {
        int childCount = linearLayout.getChildCount();
        int newChildCount = childCount + 50;

        for(int i = childCount; i < newChildCount; i++) {
            if(workouts == null) {
                break;
            }
            if(i >= workouts.size()) {
                break;
            }
            CardView cardView = setupCardView(workouts.get(i), i);
            linearLayout.addView(cardView);
        }
    }

    private CardView setupCardView(Workout workout, int index) {
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
        text.setText(workout.getName());
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setPadding(21, 0, 84, 0);
        text.setTextSize(16);

        TextView arrow = new TextView(context);
        arrow.setText(">");
        arrow.setGravity(Gravity.CENTER_VERTICAL|Gravity.END);
        arrow.setPadding(0, 0, 42, 0);
        arrow.setTextSize(21);

        cardView.addView(text);
        cardView.addView(arrow);

        cardView.setOnClickListener(onClick -> {
            Intent intent = new Intent(this.getContext(), WorkoutActivity.class);
            intent.putExtra("workout", workout);
            intent.putExtra("index", index);
            startActivity(intent);
            mainActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });

        return cardView;
    }

    @Override
    public synchronized void update(WorkoutsFragmentSubject state) {
        if (workoutsFragment.isHidden() || !workoutsFragment.isAdded()) {
            return;
        }
        linearLayout.removeAllViewsInLayout();
        linearLayout.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade));
        workouts = WorkoutController.getInstance().getWorkouts();
        setupListWorkouts();
    }

}
