package edu.ucsd.cse110.successorator.app;

import static androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.util.MutableLiveDataSubjectAdapter;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.Observer;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

public class MainViewModel extends ViewModel {
    // Domain state (true "Model" state)
    private final GoalRepository goalRepository;
    // UI state
    private final MutableSubject<Boolean> isEmpty;
    private final MutableSubject<List<Goal>> orderedCards;
    private final MutableSubject<String> displayedText;

    private  MutableSubject<List<Goal>> todayGoal;

    private final MutableSubject<List<Goal>> tomorrowGoal;
    private final MutableSubject<List<Goal>> pendingGoals;

    private final MutableSubject<List<Goal>> recurrentGoals;

    private final MutableSubject<Goal.Category> focusMode;

    private Date date;

    public static final ViewModelInitializer<MainViewModel> initializer =
        new ViewModelInitializer<>(
            MainViewModel.class,
            creationExtras -> {
                var app = (SuccessoratorApplication) creationExtras.get(APPLICATION_KEY);
                assert app != null;
                return new MainViewModel(app.getGoalRepository());
            });

    public MainViewModel(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;

        // Create the observable subjects.
        this.date = new Date();
        this.isEmpty = new SimpleSubject<>();
        this.orderedCards = new SimpleSubject<>();
        this.displayedText = new SimpleSubject<>();
        this.todayGoal = new SimpleSubject<>();
        this.tomorrowGoal = new SimpleSubject<>();
        this.pendingGoals = new SimpleSubject<>();
        this.recurrentGoals = new SimpleSubject<>();
        this.focusMode = new SimpleSubject<>();
        this.setFocusMode(Goal.Category.NONE);
        update();

    }

    public void setFocusMode(Goal.Category mode) {
        this.focusMode.setValue(mode);
    }

    public MutableSubject<Goal.Category> getFocusMode() {
        return focusMode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void update() {
        // When the list of cards changes (or is first loaded), reset the ordering.
        goalRepository.findAll().observe(cards -> {
            if (cards == null) {
                this.isEmpty.setValue(Boolean.TRUE);
                return;
            }
            this.isEmpty.setValue(Boolean.FALSE);

            var newOrderedCards = cards.stream()
                    .sorted(Comparator.comparingInt(Goal::category))
                    .collect(Collectors.toList());

            orderedCards.setValue(newOrderedCards);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrowDate = calendar.getTime();

            var todayGoals = cards.stream()
                    .filter(goal -> goal.getDate() != null)
                    .sorted(Comparator.comparingInt(Goal::category))
                    .filter(goal -> goal.getDate().before(date) || isSameDay(goal.getDate(),date))
                    .collect(Collectors.toList());

            this.todayGoal.setValue(todayGoals);

            var pending = cards.stream()
                    .filter(goal -> goal.getDate() == null)
                    .sorted(Comparator.comparingInt(Goal::category))
                    .collect(Collectors.toList());

            this.pendingGoals.setValue(pending);

            var recurrent = cards.stream()
                    .filter(goal -> goal.getRepeatInterval() != Goal.RepeatInterval.ONE_TIME)
                    .sorted(Comparator.comparingInt(Goal::category))
                    .collect(Collectors.toList());

            this.recurrentGoals.setValue(recurrent);



            var tomorrowGoals = cards.stream()
                    .filter(goal -> goal.getDate() != null)
                    .sorted(Comparator.comparingInt(Goal::category))
                    .filter(goal -> isSameDay(goal.getDate(), tomorrowDate))
                    .collect(Collectors.toList());

            this.tomorrowGoal.setValue(tomorrowGoals);

        });
    }

    public Subject<List<Goal>> getTodayGoals() {
        return todayGoal;
    }

    public Subject<List<Goal>> getTomorrowGoals() {
        return tomorrowGoal;
    }

    public Subject<List<Goal>> getPendingGoals() {
        return pendingGoals;
    }

    public Subject<List<Goal>> getRecurrentGoals() {
        return recurrentGoals;
    }
    public void scheduleToClearFinishedGoals(Context context, Date date) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTimeInMillis(date.getTime());

        SharedPreferences prefs = context.getSharedPreferences("successorator", Context.MODE_PRIVATE);
        long nextClear = prefs.getLong("nextClear", 0);
        Calendar nextClearTime = Calendar.getInstance();
        addRecurringGoals();
        if (nextClear > 0) {
            nextClearTime.setTimeInMillis(nextClear);

            if (currentTime.after(nextClearTime)) {
                goalRepository.removeFinishedGoals();
            }
        }
        nextClearTime.setTimeInMillis(System.currentTimeMillis());
        nextClearTime.set(Calendar.HOUR_OF_DAY, 2);
        nextClearTime.set(Calendar.MINUTE, 0);

        if (nextClearTime.before(currentTime)) {
            nextClearTime.add(Calendar.HOUR_OF_DAY, 24);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("nextClear", nextClearTime.getTimeInMillis());
        editor.apply();
        update();
    }

    public void addRecurringGoals() {

        var recurrent = getRecurrentGoals();

        if (recurrent.getValue() == null) {
            return;
        }
        for (Goal goal : recurrent.getValue()) {
            if (!goal.isFinished) {
                continue;
            }

            var oldId = goal.getId();
            Date goalDate = goal.getDate();
            Calendar yesterday = Calendar.getInstance();
            yesterday.setTime(this.date);
            yesterday.add(Calendar.DAY_OF_YEAR,-1);
            Date today = yesterday.getTime();

            if (goalDate.before(today)){
                Date nextDate = goalDate;
                while (nextDate.before(today)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(nextDate);
                    switch (goal.getRepeatInterval()) {
                        case DAILY:
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            break;
                        case WEEKLY:
                            calendar.add(Calendar.WEEK_OF_YEAR, 1);
                            break;
                        case MONTHLY:
                            Date date = Date.from(getNextMonthSameDayOfWeek(goalDate).atStartOfDay().toInstant(ZoneOffset.ofHours(0)));
                            calendar.setTime(date);
                            calendar.add(Calendar.DAY_OF_YEAR, 1);
                            break;
                        case YEARLY:
                            calendar.add(Calendar.YEAR, 1);
                            break;
                    }
                    nextDate = calendar.getTime();
                }
                Goal newGoal = new Goal(0, goal.getName(), false, goal.sortOrder(), nextDate, goal.getRepeatInterval(), goal.getCategory());
                goalRepository.addGoalBetweenFinishedAndUnfinished(newGoal);
                goalRepository.remove(oldId);
            }
        }
    }
    private static LocalDate getNextMonthSameDayOfWeek(Date date) {
        LocalDate goalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return goalDate.plus(1, ChronoUnit.MONTHS).with(TemporalAdjusters.dayOfWeekInMonth(Integer.parseInt(new SimpleDateFormat("F").format(date)),goalDate.getDayOfWeek()));
    }


    public void toggleCompleted(Goal goal) {
        //if goal is unfinished we do this
        if (!goal.isFinished()) {
            var newGoal = new Goal(goal.getId(), goal.getName(), !goal.isFinished(), goal.sortOrder(), goal.getDate(), goal.getRepeatInterval(), goal.getCategory());
            goalRepository.save(newGoal);
            // remove the goal
            goalRepository.remove(goal.getId());
            goalRepository.append(newGoal);
        }
        //if goal is finished we do this
        else {
            var newGoal = new Goal(goal.getId(), goal.getName(), !goal.isFinished(), goal.sortOrder(), goal.getDate(), goal.getRepeatInterval(), goal.getCategory());
            goalRepository.save(newGoal);
            // remove the goal
            goalRepository.remove(goal.getId());
            goalRepository.prepend(newGoal);
        }

    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isTomorrow(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(date);
        today.add(Calendar.DAY_OF_YEAR, 1);
        return (today.get(Calendar.YEAR) == target.get(Calendar.YEAR)) &&
                (today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR));
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
        update();
    }

    public void removeFinishedGoals() {
        goalRepository.removeFinishedGoals();
    }
}
