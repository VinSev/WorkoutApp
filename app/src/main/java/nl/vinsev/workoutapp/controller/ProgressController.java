package nl.vinsev.workoutapp.controller;

import java.util.ArrayList;
import java.util.Map;

import nl.vinsev.workoutapp.dao.ProgressDAO;

public class ProgressController {

    private static ProgressController progressController;
    private ArrayList<Long> progress;

    private ProgressController() {

    }

    public synchronized static ProgressController getInstance() {
        if(progressController == null) {
            progressController = new ProgressController();
        }
        return progressController;
    }

    public ArrayList<Long> getProgress() {
        return progress;
    }

    public ArrayList<Long> updateProgress() {
        progress = new ProgressDAO().getProgressThisWeek();
        return progress;
    }

    public void setProgress(ArrayList<Long> progress) {
        this.progress = progress;
    }

    public void addProgress() {
        new ProgressDAO().addProgress();
    }
}
