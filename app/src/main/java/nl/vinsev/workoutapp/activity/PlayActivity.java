package nl.vinsev.workoutapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.controller.ExerciseController;
import nl.vinsev.workoutapp.controller.ProgressController;
import nl.vinsev.workoutapp.controller.WorkoutController;
import nl.vinsev.workoutapp.databinding.ActivityPlayBinding;
import nl.vinsev.workoutapp.model.Exercise;
import nl.vinsev.workoutapp.model.Workout;

public class PlayActivity extends AppCompatActivity {

    private ActivityPlayBinding activityPlayBinding;
    private ConstraintLayout cImageView;
    private ImageView imageView;
    private TextView exerciseTitle;
    private TextView state;
    private TextView countDownTimerText;

    private CountDownTimer countDownTimer;
    private long duration = 5;

    private Workout workout;
    private ArrayList<Exercise> exercises;
    private int currentExerciseIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        workout = (Workout) getIntent().getSerializableExtra("workout");
        exercises = workout.getExercises();

        activityPlayBinding = ActivityPlayBinding.inflate(getLayoutInflater());
        cImageView = activityPlayBinding.cImageView;
        exerciseTitle = activityPlayBinding.tvExerciseTitle;
        state = activityPlayBinding.tvState;
        countDownTimerText = activityPlayBinding.tvCountDownTimer;
        View view = activityPlayBinding.getRoot();
        setContentView(view);

        setupImageView();
        updateExerciseTitle();
        setCountDownTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_play, menu);

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

    private void newExercise() {
        updateImageView();
        updateExerciseTitle();
    }

    private void setupImageView() {
        imageView = new ImageView(this);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
        cImageView.addView(imageView);
        updateImageView();
    }

    private void updateImageView() {
        new DownloadImageTask(imageView)
                .execute(ExerciseController.getInstance().getImage(exercises.get(currentExerciseIndex).getName()));
    }

    private void updateExerciseTitle() {
        exerciseTitle.setText(exercises.get(currentExerciseIndex).getTitle());
    }

    private void switchState() {
        String state = this.state.getText().toString();
        int rId = 0;
        if(state.equals(getString(R.string.state_go))) {
            newExercise();
            rId = R.string.state_pause;
        } else if(state.equals(getString(R.string.state_pause))) {
            currentExerciseIndex++;
            rId = R.string.state_go;
        }
        this.state.setText(rId);
    }

    private void setCountDownTimerText(Long count) {
        countDownTimerText.setText(String.valueOf(count));
    }

    private void setCountDownTimer() {
        setCountDownTimerText(duration);

        new CountDownTimer(TimeUnit.SECONDS.toMillis(duration + 1), TimeUnit.SECONDS.toMillis(1)) {
            @Override
            public void onTick(long l) {
                setCountDownTimerText(TimeUnit.MILLISECONDS.toSeconds(l));
            }

            @Override
            public void onFinish() {
                if(!isLastExercise()) {
                    switchState();
                    if(duration == workout.getExerciseIntervalLength()) {
                        duration = workout.getRestIntervalLength();
                    } else {
                        duration = workout.getExerciseIntervalLength();
                    }
                    setCountDownTimer();
                } else {
                    cImageView.removeAllViews();
                    exerciseTitle.setText(R.string.state_end);
                    state.setText(R.string.state_end);
                    ProgressController.getInstance().addProgress();
                }
            }
        }.start();
    }

    private boolean isLastExercise() {
        return currentExerciseIndex > exercises.size() - 1;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new java.net.URL(urlDisplay).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

}