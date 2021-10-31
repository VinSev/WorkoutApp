package nl.vinsev.workoutapp.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ServerValue;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import nl.vinsev.workoutapp.R;
import nl.vinsev.workoutapp.activity.MainActivity;
import nl.vinsev.workoutapp.controller.ProgressController;
import nl.vinsev.workoutapp.databinding.FragmentProgressBinding;
import nl.vinsev.workoutapp.observer.ProgressFragmentObserver;
import nl.vinsev.workoutapp.subject.ProgressFragmentSubject;

public class ProgressFragment extends Fragment implements ProgressFragmentObserver {
    private static ProgressFragment progressFragment;

    private FragmentProgressBinding fragmentProgressBinding;
    private BarChart barChart;
    private MainActivity mainActivity;

    private FirebaseAuth firebaseAuth;

    private ExecutorService executor;

    private ArrayList<Long> progress;
    private List<AbstractMap.SimpleEntry<Date, Integer>> dates = new ArrayList<>();

    public synchronized static ProgressFragment newInstance() {
        progressFragment = new ProgressFragment();
        return progressFragment;
    }

    public synchronized static ProgressFragment getInstance() {
        if(progressFragment == null) {
            progressFragment = new ProgressFragment();
        }
        return progressFragment;
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }

        progressFragment = this;
        firebaseAuth = FirebaseAuth.getInstance();
        executor = Executors.newSingleThreadExecutor();

        fragmentProgressBinding = FragmentProgressBinding.inflate(inflater, container, false);
        barChart = fragmentProgressBinding.bcProgress;

        return fragmentProgressBinding.getRoot();
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = Objects.requireNonNull((MainActivity) getActivity());
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
        getActivity().setTitle(R.string.title_fragment_progress);
        executor.execute(ProgressController.getInstance()::updateProgress);
        progress = ProgressController.getInstance().getProgress();

        setupDates();
        fillDates();
        setupBarChart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
    
    private void setupDates() {
        dates.clear();
        long currentTime = System.currentTimeMillis();
        for(int i = 7; i >= 0; i--) {
            Date date = new Date(currentTime - TimeUnit.DAYS.toMillis(i));
            dates.add(new AbstractMap.SimpleEntry<>(date, 0));
        }
    }

    private void fillDates() {
        if(progress == null) {
            return;
        }
        for (Long time : progress) {
            Date date = new Date(time);
            for(int i = 0; i < dates.size(); i++) {
                AbstractMap.SimpleEntry<Date, Integer> dateEntry = dates.get(i);
                if(dateEntry.getKey().toString().equals(date.toString())) {
                    dateEntry.setValue(dateEntry.getValue() + 1);
                }
            }
        }
    }

    private void setupBarChart() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> xAxisLabels = new ArrayList<>();
        for (int i = 0; i < dates.size(); i ++) {
            AbstractMap.SimpleEntry<Date, Integer> dateEntry = dates.get(i);

            int count = dateEntry.getValue();
            barEntries.add(new BarEntry(i, count));

            String date = dateEntry.getKey().toString().substring(5, 10);
            xAxisLabels.add(date);
        }
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Day");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("The amount of time you have trained in the last 7 days");
        barChart.animateY(666);
    }

    @Override
    public synchronized void update(ProgressFragmentSubject state) {
        if (!progressFragment.isVisible() || !progressFragment.isAdded()) {
            return;
        }
        barChart.removeAllViews();
        barChart.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.fade));
        progress = ProgressController.getInstance().getProgress();
        setupDates();
        fillDates();
        setupBarChart();
    }
}
