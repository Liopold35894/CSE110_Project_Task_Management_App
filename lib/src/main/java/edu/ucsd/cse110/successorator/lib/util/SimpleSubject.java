package edu.ucsd.cse110.successorator.lib.util;

import androidx.annotation.Nullable;

import java.util.List;


public class SimpleSubject<T> implements MutableSubject<T> {
    private @Nullable T value = null;
    private final List<Observer<T>> observers = new java.util.ArrayList<>();

    @Override
    @Nullable
    public T getValue() {
        return value;
    }

    private void notifyObservers() {
        for (var observer : observers) {
            observer.onChanged(value);
        }
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        notifyObservers();
    }

    @Override
    public void observe(Observer<T> observer) {
        observers.add(observer);
        observer.onChanged(value);
    }

    @Override
    public void removeObserver(Observer<T> observer) {
        observers.remove(observer);
    }
}
