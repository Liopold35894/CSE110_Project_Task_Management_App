package edu.ucsd.cse110.successorator.app.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import java.util.stream.IntStream;

import java.util.List;

@Dao
public interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GoalEntity goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<GoalEntity> goals);

    @Query("SELECT * FROM goals WHERE id = :id")
    GoalEntity find(int id);

    @Query("SELECT * FROM goals ORDER BY sort_order")
    List<GoalEntity> findAll();

    @Query("SELECT * FROM goals WHERE id = :id")
    LiveData<GoalEntity> findAsLiveData(int id);

    @Query("SELECT * FROM goals ORDER BY sort_order")
    LiveData<List<GoalEntity>> findAllAsLiveData();

    @Query("SELECT COUNT(*) FROM goals")
    int count();

    @Query("SELECT MIN(sort_order) FROM goals")
    int getMinSortOrder();

    @Query("SELECT MAX(sort_order) FROM goals")
    int getMaxSortOrder();

    @Query("UPDATE goals SET sort_order = sort_order + :by " +
            "WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrder(int from, int to, int by);

    @Transaction
    default int append(GoalEntity goal){
        var maxSortOrder = getMaxSortOrder();
        var newGoal = new GoalEntity(
                goal.name, goal.isFinished, maxSortOrder + 1
        );
        return Math.toIntExact(insert(newGoal));
    }
    @Transaction
    default int prepend(GoalEntity goal){
        shiftSortOrder(getMinSortOrder(), getMaxSortOrder(), 1);
        var newGoal = new GoalEntity(
                goal.name, goal.isFinished, getMinSortOrder()  -1
        );
        return Math.toIntExact(insert(newGoal));
    }
    @Transaction
    @Query("UPDATE goals SET sort_order = sort_order + 1 " +
            "WHERE sort_order > :unfinishedMaxSortOrder AND sort_order <= :finishedMinSortOrder")
    void shiftSortOrderBehindFinished(int unfinishedMaxSortOrder, int finishedMinSortOrder);

    @Query("SELECT MAX(sort_order) FROM goals WHERE isFinished = 0")
    int getMaxSortOrderUnfinished();

    @Query("SELECT MIN(sort_order) FROM goals WHERE isFinished = 1")
    int getMinSortOrderFinished();

    @Transaction
    default int addGoalBetweenFinishedAndUnfinished(GoalEntity goal) {
        int unfinishedMaxSortOrder = getMaxSortOrderUnfinished();
        int finishedMinSortOrder = getMinSortOrderFinished();

        // Shift sort order behind finished goals
        shiftSortOrderBehindFinished(unfinishedMaxSortOrder, finishedMinSortOrder);

        // Insert the new goal in between
        int newSortOrder = unfinishedMaxSortOrder + 1;
        var newGoal = new GoalEntity(goal.name, goal.isFinished, newSortOrder);
        return Math.toIntExact(insert(newGoal));
    }


    @Query("DELETE FROM goals WHERE id = :id")
    void delete(int id);
}
