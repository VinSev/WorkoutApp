package nl.vinsev.workoutapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.controller.ExerciseController;
import nl.vinsev.workoutapp.databinding.ActivityMainBinding;
import nl.vinsev.workoutapp.fragment.CustomFragment;
import nl.vinsev.workoutapp.fragment.ProgressFragment;
import nl.vinsev.workoutapp.fragment.WorkoutsFragment;
import nl.vinsev.workoutapp.service.NetworkManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private FragmentContainerView fragmentContainerView;

    private FirebaseAuth firebaseAuth;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NetworkManager.getInstance(this);

        firebaseAuth = FirebaseAuth.getInstance();

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        fragmentContainerView = activityMainBinding.fcMainContent;
        View view = activityMainBinding.getRoot();
        setContentView(view);

        executor = Executors.newSingleThreadExecutor();
        executor.execute(ExerciseController.getInstance()::getExercises);

        setupBottomNavigation();
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_signout:
                SignOut();
                break;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    private void setupBottomNavigation() {
        activityMainBinding.BottomNavigationView.setOnItemSelectedListener(item -> {
            switch(item.getItemId()) {
                case R.id.bottom_nav_workouts:
                    fragmentContainerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));
                    replaceFragment(WorkoutsFragment.newInstance());
                    break;
                case R.id.bottom_nav_progress:
                    fragmentContainerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));
                    replaceFragment(ProgressFragment.newInstance());
                    break;
            }
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(fragmentContainerView.getId(), fragment, null)
                .commit();
    }

    private void SignOut() {
        Toast.makeText(this, R.string.signed_out, Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        updateUI(null);
    }

    public void reload() {

    }

    public void updateUI(FirebaseUser user) {
        if(user != null) {
            reload();
        } else {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            startActivity(intent);
        }
    }
}
