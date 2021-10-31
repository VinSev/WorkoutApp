package nl.vinsev.workoutapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nl.vinsev.workoutapp.dao.ExerciseDAO;
import nl.vinsev.workoutapp.model.Exercise;

public class ExerciseController {

    private static ExerciseController exerciseController;
    private ArrayList<Exercise> exercises;
    private final Map<String, String> exerciseImages = new HashMap<>();

    private ExerciseController() {

    }

    public synchronized static ExerciseController getInstance() {
        if(exerciseController == null) {
            exerciseController = new ExerciseController();
        }
        return exerciseController;
    }

    public ArrayList<Exercise> getExercises() {
        if(exercises == null) {
            new ExerciseDAO().getExercises();
        }
        return exercises;
    }

    public void setExercises() {
        new ExerciseDAO().getExercises();
    }

    public void setExercises(Exercise[] exercises) {
        this.exercises = new ArrayList<>(Arrays.asList(exercises));
    }

    public void setExercises(ArrayList<Exercise> exercises) {
        this.exercises = exercises;
    }

    public String getImage(String name) {
        return exerciseImages.get(name);
    }

    public void getImage(String name, String idNum) {
        new ExerciseDAO().getImage(name, idNum);
    }

    public Map<String, String> getImages() {
        return exerciseImages;
    }

    public void getAllImages() {
        if(exercises == null) {
            return;
        }
        for (Exercise exercise : exercises) {
            if(!exerciseImages.containsKey(exercise.getName())) {
                getImage(exercise.getName(), exercise.getId_num());
            }
        }
    }

    public void addImage(String name, String image) {
        exerciseImages.put(name, image);
    }
}
