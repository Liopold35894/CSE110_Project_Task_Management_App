package edu.ucsd.cse110.secards.app.data.db;

import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.secards.app.util.LiveDataSubjectAdapter;
import edu.ucsd.cse110.secards.lib.domain.Goal;
import edu.ucsd.cse110.secards.lib.domain.GoalRepository;
import edu.ucsd.cse110.secards.lib.util.Subject;

public class RoomGoalRepository implements GoalRepository {
    private final FlashcardDao flashcardDao;

    public RoomGoalRepository(FlashcardDao flashcardDao) {
        this.flashcardDao = flashcardDao;
    }

    @Override
    public Subject<Goal> find(int id) {
        var entityLiveData = flashcardDao.findAsLiveData(id);
        var flashcardLiveData = Transformations.map(entityLiveData, FlashcardEntity::toFlashcard);
        return new LiveDataSubjectAdapter<>(flashcardLiveData);
    }

    @Override
    public Subject<List<Goal>> findAll() {
        var entitiesLiveData = flashcardDao.findAllAsLiveData();
        var flashcardsLiveData = Transformations.map(entitiesLiveData, entities -> {
            return entities.stream()
                    .map(FlashcardEntity::toFlashcard)
                    .collect(Collectors.toList());
        });
        return new LiveDataSubjectAdapter<>(flashcardsLiveData);
    }

    @Override
    public void save(Goal goal) {
        flashcardDao.insert(FlashcardEntity.fromFlashcard(goal));
    }

    @Override
    public void save(List<Goal> goals) {
        var entities = goals.stream()
                .map(FlashcardEntity::fromFlashcard)
                .collect(Collectors.toList());
        flashcardDao.insert(entities);

    }

    @Override
    public void remove(int id) {
        flashcardDao.delete(id);
    }

    @Override
    public void append(Goal goal) {
        flashcardDao.append(FlashcardEntity.fromFlashcard(goal));
    }

    @Override
    public void prepend(Goal goal) {
        flashcardDao.prepend(FlashcardEntity.fromFlashcard(goal));
    }
}
