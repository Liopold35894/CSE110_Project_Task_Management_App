package edu.ucsd.cse110.secards.app.data.db;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.ucsd.cse110.secards.lib.domain.Goal;

@Entity(tableName = "flashcards")
public class FlashcardEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id = null;

    @ColumnInfo(name = "front")
    public String front;

    @ColumnInfo(name = "back")
    public String back;

    @ColumnInfo(name = "sort_order")
    public int sortOrder;

    FlashcardEntity(@NonNull String front, int sortOrder) {
        this.front = front;
        this.back = back;
        this.sortOrder = sortOrder;
    }

    public static FlashcardEntity fromFlashcard(@NonNull Goal goal) {
        var card = new FlashcardEntity(goal.name(), goal.sortOrder());
        card.id = goal.id();
        return card;
    }

    public @NonNull Goal toFlashcard() {
        return new Goal(id, front, sortOrder);
    }
}
