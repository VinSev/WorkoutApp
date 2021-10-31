package nl.vinsev.workoutapp.dao;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.vinsev.workoutapp.controller.WorkoutController;
import nl.vinsev.workoutapp.fragment.WorkoutsFragment;
import nl.vinsev.workoutapp.model.Workout;
import nl.vinsev.workoutapp.observer.Observer;
import nl.vinsev.workoutapp.subject.WorkoutsFragmentSubject;

public class WorkoutDAO implements WorkoutsFragmentSubject {
    private final List<Observer<WorkoutsFragmentSubject>> observers = new ArrayList<>();
    private final FirebaseAuth firebaseAuth;

    public WorkoutDAO() {
        registerObserver(WorkoutsFragment.getInstance());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private DatabaseReference getDatabase(String path) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference(path);
    }

    public ArrayList<Workout> getWorkouts() {
        ArrayList<Workout> workouts = new ArrayList<>();

        String path = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference databaseReference = getDatabase(path).child("workouts").getRef();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Workout workout = child.getValue(Workout.class);
                    workouts.add(workout);
                }
                WorkoutController.getInstance().setWorkouts(workouts);
                notifyObservers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return workouts;
    }

    public void addWorkout(Workout workout) {
        String path = firebaseAuth.getCurrentUser().getUid();
        ArrayList<Workout> workouts = WorkoutController.getInstance().getWorkouts();
        if(workouts == null) {
            workouts = new ArrayList<>();
        }
        workouts.add(workout);
        getDatabase(path).child("workouts").setValue(workouts);
    }

    public void removeWorkout(int index) {
        String path = firebaseAuth.getCurrentUser().getUid();
        getDatabase(path).child("workouts").child(String.valueOf(index)).removeValue();
    }

    public void updateWorkout(int index, Workout workout) {
        String path = firebaseAuth.getCurrentUser().getUid();
        Map<String, Object> map = new HashMap<>();
        map.put(String.valueOf(index), workout);
        getDatabase(path).child("workouts").updateChildren(map);
    }

    @Override
    public void registerObserver(Observer<WorkoutsFragmentSubject> observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer<WorkoutsFragmentSubject> observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer<WorkoutsFragmentSubject> observer : observers) {
            observer.update(this);
        }
    }
}
