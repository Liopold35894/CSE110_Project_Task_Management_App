package edu.ucsd.cse110.secards.lib.util;

public interface MutableSubject<T> extends Subject<T> {

    void setValue(T value);

}
