package edu.ucsd.cse110.successorator.lib.util;

import androidx.annotation.Nullable;

public interface Subject<T> {
    @Nullable
    T getValue();

    void observe(Observer<T> observer);

    void removeObserver(Observer<T> observer);
}
