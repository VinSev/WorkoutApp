package nl.vinsev.workoutapp.dao;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.vinsev.workoutapp.controller.ProgressController;
import nl.vinsev.workoutapp.fragment.ProgressFragment;
import nl.vinsev.workoutapp.fragment.WorkoutsFragment;
import nl.vinsev.workoutapp.observer.Observer;
import nl.vinsev.workoutapp.subject.ProgressFragmentSubject;
import nl.vinsev.workoutapp.subject.WorkoutsFragmentSubject;

public class ProgressDAO implements ProgressFragmentSubject {
    private final List<Observer<ProgressFragmentSubject>> observers = new ArrayList<>();
    private final FirebaseAuth firebaseAuth;

    public ProgressDAO() {
        registerObserver(ProgressFragment.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private DatabaseReference getDatabase(String path) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(path);
    }

    public ArrayList<Long> getProgressThisWeek() {
        ArrayList<Long> progress = new ArrayList<>();

        String path = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = getDatabase(path).child("progress").getRef();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<Long> longGenericTypeIndicator = new GenericTypeIndicator<Long>() {};
                    Long date = child.getValue(longGenericTypeIndicator);
                    progress.add(date);
                }
                ProgressController.getInstance().setProgress(progress);
                notifyObservers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      return progress;
    }

    public void addProgress() {
        String path = firebaseAuth.getCurrentUser().getUid();
        ArrayList<Long> progress = ProgressController.getInstance().getProgress();
        if(progress == null) {
            progress = new ArrayList<>();
        }
        Map<String, Object> map = new HashMap<>();
        map.put(String.valueOf(progress.size()), ServerValue.TIMESTAMP);
        getDatabase(path).child("progress").updateChildren(map);
    }

    @Override
    public void registerObserver(Observer<ProgressFragmentSubject> observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer<ProgressFragmentSubject> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer<ProgressFragmentSubject> observer : observers) {
            observer.update(this);
        }
    }
}
