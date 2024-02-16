package edu.ucsd.cse110.secards.lib.domain;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class Goal implements Serializable {
    private final @Nullable Integer id;
    private final @NonNull String name;

    private Integer sortOrder;

    public Goal(@Nullable Integer id, @NonNull String name, int sortOrder) {
        this.id = id;
        this.name = name;
        this.sortOrder = sortOrder;
    }

    public @Nullable Integer id() {
        return id;
    }

    public @NonNull String name() {
        return name;
    }


    public int sortOrder() {
        return sortOrder;
    }

    public Goal withId(int id) {
        return new Goal(id, this.name, this.sortOrder);
    }

    public Goal withSortOrder(int sortOrder) {
        return new Goal(this.id, this.name, sortOrder);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return Objects.equals(id, goal.id) && Objects.equals(name, goal.name) && Objects.equals(sortOrder, goal.sortOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sortOrder);
    }
}
