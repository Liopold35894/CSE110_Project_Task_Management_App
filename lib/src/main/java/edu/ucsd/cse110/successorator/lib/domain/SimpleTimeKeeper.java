package edu.ucsd.cse110.successorator.lib.domain;

import java.time.LocalDateTime;

import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class SimpleTimeKeeper implements TimeKeeper {
    private final MutableSubject<LocalDateTime> keptDateTime = new SimpleSubject<>();

    @Override
    public void setKeptDateTime(LocalDateTime dateTime) {
        keptDateTime.setValue(dateTime);
    }

    @Override
    public Subject<LocalDateTime> getKeptDateTime() {
        return keptDateTime;
    }
}
