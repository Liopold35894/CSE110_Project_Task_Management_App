package edu.ucsd.cse110.secards.app;

import android.app.Application;

import androidx.room.Room;

import edu.ucsd.cse110.secards.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.secards.app.data.db.SECardsDatabase;
import edu.ucsd.cse110.secards.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.secards.lib.domain.GoalRepository;

public class SECardsApplication extends Application {
    private InMemoryDataSource dataSource;
    private GoalRepository goalRepository;

    @Override
    public void onCreate() {
        super.onCreate();

        // OLD:
        // this.dataSource = InMemoryDataSource.fromDefault();
        // this.flashcardRepository = new SimpleFlashcardRepository(dataSource);

        // NEW:
        var database = Room.databaseBuilder(
                        getApplicationContext(),
                        SECardsDatabase.class,
                        "secards-database"
                )
                .allowMainThreadQueries()
                .build();

        this.goalRepository = new RoomGoalRepository(database.flashcardDao());

        // Populate the database with some initial data on the first run.
        var sharedPreferences = getSharedPreferences("secards", MODE_PRIVATE);
        var isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        if (isFirstRun && database.flashcardDao().count() == 0) {
            goalRepository.save(InMemoryDataSource.DEFAULT_CARDS);

            sharedPreferences.edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
        }
    }

    public GoalRepository getFlashcardRepository() {
        return goalRepository;
    }
}
