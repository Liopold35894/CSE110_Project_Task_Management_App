package edu.ucsd.cse110.secards.app.data.db;

import android.se.omapi.SEService;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(FlashcardEntity flashcard);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<FlashcardEntity> flashcards);

    @Query("SELECT * FROM flashcards WHERE id = :id")
    FlashcardEntity find(int id);

    @Query("SELECT * FROM flashcards ORDER BY sort_order")
    List<FlashcardEntity> findAll();

    @Query("SELECT * FROM flashcards WHERE id = :id")
    LiveData<FlashcardEntity> findAsLiveData(int id);

    @Query("SELECT * FROM flashcards ORDER BY sort_order")
    LiveData<List<FlashcardEntity>> findAllAsLiveData();

    @Query("SELECT COUNT(*) FROM flashcards")
    int count();

    @Query("SELECT MIN(sort_order) FROM flashcards")
    int getMinSortOrder();

    @Query("SELECT MAX(sort_order) FROM flashcards")
    int getMaxSortOrder();

    @Query("UPDATE flashcards SET sort_order = sort_order + :by " +
        "WHERE sort_order >= :from AND sort_order <= :to")
    void shiftSortOrders(int from, int to, int by);

    @Transaction
    default int append(FlashcardEntity flashcard) {
        var maxSortOrder = getMaxSortOrder();
        var newFlashcard = new FlashcardEntity(
                flashcard.front, maxSortOrder + 1
        );
        return Math.toIntExact(insert(newFlashcard));
    }

    @Transaction
    default int prepend(FlashcardEntity flashcard) {
        shiftSortOrders(getMinSortOrder(), getMaxSortOrder(), 1);
        var newFlashcard = new FlashcardEntity(
                flashcard.front, getMinSortOrder() - 1
        );
        return Math.toIntExact(insert(newFlashcard));
    }

    @Query("DELETE FROM flashcards WHERE id = :id")
    void delete(int id);


}
