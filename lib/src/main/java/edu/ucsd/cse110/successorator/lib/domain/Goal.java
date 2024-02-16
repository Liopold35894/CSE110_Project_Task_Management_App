package edu.ucsd.cse110.successorator.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable String name;
    private final @NonNull Integer id;
    public boolean isFinished;
    private int sortOrder;

    public Goal(
            @NonNull Integer id,
            @Nullable String name,
            boolean isFinished,
            int sortOrder
    ) {
        this.id = id;
        this.name = name;
        this.isFinished = isFinished;
        this.sortOrder = sortOrder;
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

    public int sortOrder() {
        return sortOrder;
    }

    public Goal withId(int id) {
        return new Goal(id, name, isFinished, sortOrder);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(id, name, isFinished, sortOrder);
    }

    public void setIsFinished(Boolean isFinished) { this.isFinished = isFinished; }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sortOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return sortOrder == goal.sortOrder && Objects.equals(id, goal.id) && Objects.equals(isFinished, goal.isFinished) && Objects.equals(name, goal.name);
    }
}
