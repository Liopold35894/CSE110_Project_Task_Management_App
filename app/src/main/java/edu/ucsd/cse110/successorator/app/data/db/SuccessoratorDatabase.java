package edu.ucsd.cse110.successorator.app.data.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {GoalEntity.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class SuccessoratorDatabase extends RoomDatabase {
    public abstract GoalDao goalDao();
}
