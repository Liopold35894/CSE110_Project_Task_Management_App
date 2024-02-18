package edu.ucsd.cse110.successorator.app;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTimeKeeper;
import edu.ucsd.cse110.successorator.lib.domain.TimeKeeper;

public class SuccessoratorApplication extends Application {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;
    private TimeKeeper timeKeeper;

    @Override
    public void onCreate() {
        super.onCreate();

        // OLD:
        // this.dataSource = InMemoryDataSource.fromDefault();
        // this.flashcardRepository = new SimpleFlashcardRepository(dataSource);

        // NEW:
        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        SuccessoratorDatabase.class,
                        "successorator-database"
                )
                .allowMainThreadQueries()
                .build();

        this.goalRepository = new RoomGoalRepository(database.goalDao());
        this.timeKeeper = new SimpleTimeKeeper();

        // Populate the database with some initial data on the first run.
        var sharedPreferences = getSharedPreferences("successorator", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun && database.goalDao().count() == 0) {
            goalRepository.save(InMemoryDataSource.DEFAULT_CARDS);

            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public TimeKeeper getTimeKeeper() {
        return timeKeeper;
    }
    public GoalRepository getGoalRepository() {
        return goalRepository;
    }
}
