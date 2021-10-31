package nl.vinsev.workoutapp.service;

import org.json.JSONException;

public interface Listener<T> {
    void getResult(T object) throws JSONException;
}
