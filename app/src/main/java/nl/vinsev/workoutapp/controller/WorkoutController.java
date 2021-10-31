package nl.vinsev.workoutapp.controller;

import java.util.ArrayList;
import java.util.Arrays;

import nl.vinsev.workoutapp.dao.WorkoutDAO;
import nl.vinsev.workoutapp.model.Workout;

public class WorkoutController {

    private static WorkoutController workoutController;
    private ArrayList<Workout> workouts;

    private WorkoutController() {

    }

    public synchronized static WorkoutController getInstance() {
        if(workoutController == null) {
            workoutController = new WorkoutController();
        }
        return workoutController;
    }

    public ArrayList<Workout> getWorkouts() {
        return workouts;
    }

    public ArrayList<Workout> updateWorkouts() {
        workouts = new WorkoutDAO().getWorkouts();
        return workouts;
    }

    public void setWorkouts(ArrayList<Workout> workouts) {
        this.workouts = workouts;
    }

    public void addWorkout(Workout workout) {
        new WorkoutDAO().addWorkout(workout);
    }

    public void removeWorkout(int index) {
        new WorkoutDAO().removeWorkout(index);
    }

    public void updateWorkout(int index, Workout workout) {
        new WorkoutDAO().updateWorkout(index, workout);
    }
}
