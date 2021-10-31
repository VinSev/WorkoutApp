package nl.vinsev.workoutapp.subject;

import nl.vinsev.workoutapp.observer.Observer;

public interface Subject<T> {
    void registerObserver(final Observer<T> observer);
    void unregisterObserver(final Observer<T> observer);
    void notifyObservers();
}
