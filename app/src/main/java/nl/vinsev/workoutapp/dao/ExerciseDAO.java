package nl.vinsev.workoutapp.dao;

import com.google.gson.Gson;

import java.util.ArrayList;

import nl.vinsev.workoutapp.controller.ExerciseController;
import nl.vinsev.workoutapp.model.Exercise;
import nl.vinsev.workoutapp.service.NetworkManager;

public class ExerciseDAO {
    private final String api = "https://fitness-exercise-api.herokuapp.com";

    public void getExercises() {
        String url = api + "/" + "exercises";
        NetworkManager.getInstance().getRequestJsonArray(url, result -> {
            if (result != null) {
                ExerciseController.getInstance()
                        .setExercises(new Gson().fromJson(result.toString(), Exercise[].class));
            }
        });
    }

    public void getImage(String name, String idNum) {
        String url = api + "/" + "exercises" + "/" + "images" + "/" + idNum;
        NetworkManager.getInstance().getRequestJsonArray(url, result -> {
            if (result != null) {
                 for(int i = 0; i < result.length(); i++) {
                     ExerciseController.getInstance()
                             .addImage(name, result.getString(i));
                 }
            }
        });
    }
}


