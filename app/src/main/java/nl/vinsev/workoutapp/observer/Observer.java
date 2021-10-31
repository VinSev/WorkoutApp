package nl.vinsev.workoutapp.observer;

public interface Observer<T> {
    void update(final T state);
}
