package nl.vinsev.workoutapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Workout implements Serializable {
    private ArrayList<Exercise> exercises;
    private String name;
    private long exerciseIntervalLength;
    private long restIntervalLength;

    public Workout() {

    }

    public Workout(ArrayList<Exercise> exercises,
                   String name,
                   long exerciseIntervalLength,
                   long restIntervalLength) {
        this.exercises = exercises;
        this.name = name;
        this.exerciseIntervalLength = exerciseIntervalLength;
        this.restIntervalLength = restIntervalLength;
    }

    public ArrayList<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getExerciseIntervalLength() {
        return exerciseIntervalLength;
    }

    public void setExerciseIntervalLength(int exerciseIntervalLength) {
        this.exerciseIntervalLength = exerciseIntervalLength;
    }

    public long getRestIntervalLength() {
        return restIntervalLength;
    }

    public void setRestIntervalLength(int restIntervalLength) {
        this.restIntervalLength = restIntervalLength;
    }

    public void addExercise(Exercise exercise) {
        exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        exercises.remove(exercise);
    }
}
