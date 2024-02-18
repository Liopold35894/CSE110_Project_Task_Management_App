package edu.ucsd.cse110.successorator.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.TimeKeeper;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    private final GoalRepository goalRepository;
    private final TimeKeeper timeKeeper;


    // UI state
    private final MutableSubject<Boolean> isEmpty;
    private final MutableSubject<List<Goal>> orderedCards;
    private final MutableSubject<Goal> topCard;
    private final MutableSubject<String> displayedText;


    public static final ViewModelInitializer<MainViewModel> initializer =
        new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new MainViewModel(app.getGoalRepository(), app.getTimeKeeper());
            });

    public MainViewModel(GoalRepository goalRepository, TimeKeeper timeKeeper) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.isEmpty = new SimpleSubject<>();
        this.orderedCards = new SimpleSubject<>();
        this.topCard = new SimpleSubject<>();
        this.displayedText = new SimpleSubject<>();
        this.timeKeeper = timeKeeper;


        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(cards -> {
            if (cards == null) {
                this.isEmpty.setValue(Boolean.TRUE);
                return; // not ready yet, ignore
            }
            this.isEmpty.setValue(Boolean.FALSE);

            var newOrderedCards = cards.stream()
                    .sorted(Comparator.comparingInt(Goal::sortOrder))
                    .collect(Collectors.toList());

            orderedCards.setValue(newOrderedCards);
        });

        // When the ordering changes, update the top card.
        orderedCards.observe(cards -> {
            if (cards == null || cards.size() == 0) {
                this.isEmpty.setValue(Boolean.TRUE);
                return;
            }
            var card = cards.get(0);
            this.topCard.setValue(card);
        });

        // When the top card changes, update the displayed text and display the front side.
        topCard.observe(card -> {
            if (card == null) return;

            displayedText.setValue(card.getName());
        });
    }
    private void scheduleAlarmToClearFinishedGoals(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ClearFinishedGoalsReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Set the time to 2:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 2);
        calendar.set(Calendar.MINUTE, 0);

        // Set the alarm to repeat every day at 2:00
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
    public Subject<Boolean> getIsEmpty() {

        if (Boolean.TRUE.equals(isEmpty.getValue())) {
            return isEmpty;
        } else {
            isEmpty.setValue(false);
            return isEmpty;
        }
    }


    public void toggleCompleted(Goal goal) {
        //if goal is unfinished we do this
        if (!goal.isFinished()) {
            var newGoal = new Goal(goal.getId(), goal.getName(), !goal.isFinished(), goal.sortOrder());
            goalRepository.save(newGoal);
            // remove the goal
            goalRepository.remove(goal.getId());
            goalRepository.append(newGoal);
        }
        //if goal is finished we do this
        else {
            var newGoal = new Goal(goal.getId(), goal.getName(), !goal.isFinished(), goal.sortOrder());
            goalRepository.save(newGoal);
            // remove the goal
            goalRepository.remove(goal.getId());
            goalRepository.prepend(newGoal);
        }

    }

    public Subject<String> getDisplayedText() {
        return displayedText;
    }

    public Subject<List<Goal>> getOrderedCards() {
        return orderedCards;
    }



    public void remove(int id) {
        goalRepository.remove(id);
    }

    public void append(Goal card) {
        goalRepository.append(card);
    }

    public void prepend(Goal card) {
        goalRepository.prepend(card);
    }


    public Goal get(int goalId) {
        return goalRepository.find(goalId).getValue();
    }

    public void addBehindUnfinishedAndInFrontOfFinished(Goal card) {
        goalRepository.addGoalBetweenFinishedAndUnfinished(card);
    }

    public void removeFinishedGoals() {
        goalRepository.removeFinishedGoals();
    }
}
