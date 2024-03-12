package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable String name;
    private final @NonNull Integer id;

    private final Date date;
    private final RepeatInterval repeatInterval;

    private final Category category;

    public boolean isFinished;
    private int sortOrder;

    public enum RepeatInterval {
        ONE_TIME,
        DAILY,
        WEEKLY,
        MONTHLY,
        YEARLY
    }

    public enum Category {
        HOME,
        WORK,
        SCHOOL,
        ERRANDS,
        NONE
    }

    public Goal(
            @NonNull Integer id,
            @Nullable String name,
            boolean isFinished,
            int sortOrder,
            Category category
    ) {
        this.id = id;
        this.name = name;
        this.isFinished = isFinished;
        this.sortOrder = sortOrder;
        this.date = new Date();
        this.repeatInterval = RepeatInterval.ONE_TIME;
        this.category = category;
    }

    public Goal(
            @NonNull Integer id,
            @Nullable String name,
            boolean isFinished,
            int sortOrder,
            Date date,
            RepeatInterval repeatInterval,
            Category category
    ) {
        this.id = id;
        this.name = name;
        this.isFinished = isFinished;
        this.sortOrder = sortOrder;
        this.date = date;
        this.repeatInterval = repeatInterval;
        this.category = category;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @NonNull
    public Boolean isFinished() {
        return isFinished;
    }

    @NonNull
    public Integer getId() {
        return id;
    }
    @Nullable
    public Date getDate() {
        return date;
    }

    @NonNull
    public RepeatInterval getRepeatInterval() {
        return repeatInterval;
    }

    @NonNull
    public Category getCategory() {
        return category;
    }

    public Goal withRepeatInterval(RepeatInterval repeatInterval) {
        return new Goal(id, name, isFinished, sortOrder, date, repeatInterval, category);
    }

    public int sortOrder() {
        return sortOrder;
    }

    public int category() {return category.ordinal(); }

    public Goal withId(int id) {
        return new Goal(id, name, isFinished, sortOrder, date, repeatInterval, category);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(id, name, isFinished, sortOrder, date, repeatInterval, category);
    }

    public Goal withCategory(Category category) {
        return new Goal(id, name, isFinished, sortOrder, date, repeatInterval, category);
    }

    public void setIsFinished(Boolean isFinished) { this.isFinished = isFinished; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(name, goal.name) && Objects.equals(id,goal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, sortOrder);
    }
}
