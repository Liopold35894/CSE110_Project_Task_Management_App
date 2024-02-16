package edu.ucsd.cse110.secards.app.util;
import androidx.lifecycle.MutableLiveData;
import edu.ucsd.cse110.secards.lib.util.MutableSubject;

public class MutableLiveDataSubjectAdapter<T>
        extends LiveDataSubjectAdapter<T>
        implements MutableSubject<T>
{
    private final MutableLiveData<T> mutableAdapter;

    public MutableLiveDataSubjectAdapter(MutableLiveData<T> adaptee) {
        super(adaptee);
        this.mutableAdapter = adaptee;
    }

    @Override
    public void setValue(T value) {
        mutableAdapter.setValue(value);
    }
}
