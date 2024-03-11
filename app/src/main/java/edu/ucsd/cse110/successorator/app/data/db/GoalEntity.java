package edu.ucsd.cse110.successorator.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import edu.ucsd.cse110.successorator.lib.domain.Goal;

@Entity(tableName = "goals")
public class GoalEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "isFinished")
    public Boolean isFinished;

    @ColumnInfo(name = "sort_Order")
    public int sortOrder;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "repeat_interval")
    public Goal.RepeatInterval repeatInterval;

    @ColumnInfo(name = "category")
    public Goal.Category category;

    GoalEntity(@NonNull String name, Boolean isFinished, int sortOrder, String date, Goal.RepeatInterval repeatInterval, Goal.Category category) {
        this.name = name;
        this.isFinished = isFinished;
        this.sortOrder = sortOrder;
        this.date = date;
        this.repeatInterval = repeatInterval;
        this.category = category;
    }

    public static GoalEntity fromGoal(@NonNull Goal goal) {
        var card = new GoalEntity(goal.getName(), goal.isFinished(), goal.sortOrder(), Converters.dateToString(goal.getDate()), goal.getRepeatInterval(), goal.getCategory());
        card.id = goal.getId();
        return card;
    }

    public @NonNull Goal toGoal() {
        return new Goal(id, name, isFinished, sortOrder, Converters.fromString(date), repeatInterval, category);
    }
}