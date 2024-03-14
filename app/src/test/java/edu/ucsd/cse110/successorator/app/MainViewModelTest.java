package edu.ucsd.cse110.successorator.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class MainViewModelTest {
    private MainViewModel model;
    private InMemoryDataSource dataSource;
    private SimpleGoalRepository repo;

    @Before
    public void setUp() throws Exception {
        dataSource = new InMemoryDataSource();
        repo = new SimpleGoalRepository(dataSource);
        model = new MainViewModel(repo);
    }

    // Main View Model Test for MS2

    // MS2_US1: Check Tomorrow Goals

    // MS2_US1 Scenario 1: User have no goals for tomorrow
    @Test
    public void checkTomorrowGoalsWithNoTomorrowGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        List<Goal> TOMORROW_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", true, 1, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 2, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", false, 3, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", true, 4, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(TOMORROW_GOALS);

        List<Goal> result = model.getTomorrowGoals().getValue();

        assertTrue(result.isEmpty());
    }

    // MS2_US1 Scenario 2.1: User have only goals for tomorrow
    @Test
    public void checkTomorrowGoalsWithOnlyTomorrowGoals() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();

        List<Goal> TOMORROW_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", true, 1, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 2, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 3, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 4, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(TOMORROW_GOALS);

        List<Goal> result = model.getTomorrowGoals().getValue();
        ;
        List<Goal> EXPECTED = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", true, 1, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 2, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 3, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 4, date, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US1 Scenario 2.2: User have today and tomorrow goals
    @Test
    public void checkTomorrowGoalsWithMixedGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> TOMORROW_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 1, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", true, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 3, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 4, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(5, "Call Dentist", false, 5, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(6, "Finish Project", false, 6, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(TOMORROW_GOALS);

        List<Goal> result = model.getTomorrowGoals().getValue();
        ;
        List<Goal> EXPECTED = List.of(
                new Goal(1, "Watering Plant", false, 1, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 3, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US2: Add Tomorrow Goals
    @Test
    public void addTomorrowGoalsGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> TOMORROW_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", true, 3, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(TOMORROW_GOALS);
        Goal goal_to_add = new Goal(0, "Test for Adding Tomorrow Goals", false, -1, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_add);

        List<Goal> result = model.getTomorrowGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(0, "Test for Adding Tomorrow Goals", false, 2, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 6, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US3: Finish(and Unfinish) Tomorrow Goals
    @Test
    public void finishTomorrowGoalsGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> TOMORROW_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", true, 3, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(TOMORROW_GOALS);
        model.toggleCompleted(new Goal(4, "Send Message", false, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE));

        List<Goal> result = model.getTomorrowGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(3, "Feed Pet", true, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", true, 6, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }

        model.toggleCompleted(new Goal(4, "Send Message", true, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE));

        result = model.getTomorrowGoals().getValue();

        EXPECTED = List.of(
                new Goal(3, "Feed Pet", true, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 6, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US4: Check Pending Goals
    // MS2_US4 Scenario 1: Check Pending Goals with no Pending Goals
    @Test
    public void checkPendingGoalsWithNoPendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", true, 3, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        List<Goal> result = model.getPendingGoals().getValue();

        List<Goal> EXPECTED = List.of();

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US4 Scenario 2.1: Check Pending Goals with All Pending Goals
    @Test
    public void checkPendingGoalsWithAllPendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        List<Goal> result = model.getPendingGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(0, "Midterm Tomorrow", false, 1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US4 Scenario 2.2: Check Pending Goals with Some Pending Goals
    @Test
    public void checkPendingGoalsWithSomePendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", false, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        List<Goal> result = model.getPendingGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(0, "Midterm Tomorrow", false, 1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US5: Add Pending Goals
    @Test
    public void addPendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        Goal goal_to_add = new Goal(0, "Test for Adding Pending Goals", false, -1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_add);

        List<Goal> result = model.getPendingGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(0, "Test for Adding Pending Goals", false, -1, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 6, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US6: Move Pending Goals to Today and Tomorrow
    // MS2_US6 Scenario 1: Move Pending Goals to Today's view
    @Test
    public void movePendingGoalsToToday() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        Goal goal_to_move = new Goal(0, "Send Message", false, -1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_move);
        model.remove(4);

        List<Goal> result_pending = model.getPendingGoals().getValue();
        List<Goal> result_today = model.getTodayGoals().getValue();

        List<Goal> EXPECTED_PENDING = List.of(
                new Goal(1, "Watering Plant", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        List<Goal> EXPECTED_TODAY = List.of(
                new Goal(0, "Send Message", false, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 5, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_pending.size(); i++) {
            assertEquals(result_pending.get(i), EXPECTED_PENDING.get(i));
        }

        for (int i = 0; i < result_today.size(); i++) {
            assertEquals(result_today.get(i), EXPECTED_TODAY.get(i));
        }
    }

    // MS2_US6 Scenario 2: Move Pending Goals to Tomorrow's view
    @Test
    public void movePendingGoalsToTomorrow() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        Goal goal_to_move = new Goal(0, "Send Message", false, -1, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_move);
        model.remove(4);

        List<Goal> result_pending = model.getPendingGoals().getValue();
        List<Goal> result_today = model.getTodayGoals().getValue();

        List<Goal> EXPECTED_PENDING = List.of(
                new Goal(1, "Watering Plant", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        List<Goal> EXPECTED_TODAY = List.of(
                new Goal(0, "Send Message", false, 2, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 5, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_pending.size(); i++) {
            assertEquals(result_pending.get(i), EXPECTED_PENDING.get(i));
        }

        for (int i = 0; i < result_today.size(); i++) {
            assertEquals(result_today.get(i), EXPECTED_TODAY.get(i));
        }
    }

    // MS2_US7: Finish Pending Goals
    @Test
    public void finishPendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        Goal goal_to_finish = new Goal(0, "Send Message", true, -1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_finish);
        model.remove(4);

        List<Goal> result_pending = model.getPendingGoals().getValue();
        List<Goal> result_today = model.getTodayGoals().getValue();

        List<Goal> EXPECTED_PENDING = List.of(
                new Goal(1, "Watering Plant", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        List<Goal> EXPECTED_TODAY = List.of(
                new Goal(0, "Send Message", true, 2, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 5, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_pending.size(); i++) {
            assertEquals(result_pending.get(i), EXPECTED_PENDING.get(i));
        }

        for (int i = 0; i < result_today.size(); i++) {
            assertEquals(result_today.get(i), EXPECTED_TODAY.get(i));
        }
    }

    // MS2_US8: Check Recurring Goals
    // MS2_US8 Scenario 1: Check Recurring Goals with no recurring goals yet
    @Test
    public void checkRecurringGoalsWithNoRecurringGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        List<Goal> result = model.getRecurrentGoals().getValue();

        List<Goal> EXPECTED = List.of();

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US8 Scenario 2: Check Recurring Goals with some recurring goals
    @Test
    public void checkRecurringGoalsWithOnlyRecurringGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> RECURRING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, date_tomorrow, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, date_after_tomorrow, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, next_week, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, next_month, Goal.RepeatInterval.YEARLY, Goal.Category.NONE)
        );

        dataSource.putGoals(RECURRING_GOALS);
        List<Goal> result = model.getRecurrentGoals().getValue();

        List<Goal> EXPECTED = List.of(
                new Goal(1, "Watering Plant", false, 2, date_tomorrow, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, date_after_tomorrow, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, next_week, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, next_month, Goal.RepeatInterval.YEARLY, Goal.Category.NONE)
        );

        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // MS2_US8 Scenario 3: Check Recurring Goals with some recurring goals that should appear in today's view
    @Test
    public void checkRecurringGoalsWithRecurringGoalsAppearInToday() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> RECURRING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_after_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, date_after_tomorrow, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_today, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, next_month, Goal.RepeatInterval.YEARLY, Goal.Category.NONE)
        );

        dataSource.putGoals(RECURRING_GOALS);
        List<Goal> result_recurring = model.getRecurrentGoals().getValue();
        List<Goal> result_today = model.getTodayGoals().getValue();


        List<Goal> EXPECTED_RECURRING = List.of(
                new Goal(1, "Watering Plant", false, 2, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, date_after_tomorrow, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_today, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, next_month, Goal.RepeatInterval.YEARLY, Goal.Category.NONE)
        );
        List<Goal> EXPECTED_TODAY = List.of(
                new Goal(3, "Feed Pet", true, 4, date_today, Goal.RepeatInterval.DAILY, Goal.Category.NONE)
        );

        for (int i = 0; i < result_recurring.size(); i++) {
            assertEquals(result_recurring.get(i), EXPECTED_RECURRING.get(i));
        }
        for (int i = 0; i < result_today.size(); i++) {
            assertEquals(result_today.get(i), EXPECTED_TODAY.get(i));
        }
    }

    // MS2_US9: Add Recurring Goals
    // MS2_US9 Scenario 1: Add a recurring goal that starts a week later
    @Test
    public void addRecurringGoalsStartsInAWeek() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(GOALS);
        Goal goal_to_recur = new Goal(0, "Test Recursive Goal", false, -1, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_recur);

        List<Goal> result_recurring = model.getRecurrentGoals().getValue();
        List<Goal> result_tomorrow = model.getTomorrowGoals().getValue();

        List<Goal> EXPECTED_RECURRING = List.of(
                new Goal(0, "Test Recursive Goal", false, 2, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE)
        );

        List<Goal> EXPECTED_TOMORROW = List.of(
                new Goal(3, "Feed Pet", true, 5, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_recurring.size(); i++) {
            assertEquals(result_recurring.get(i), EXPECTED_RECURRING.get(i));
        }

        for (int i = 0; i < result_tomorrow.size(); i++) {
            assertEquals(result_tomorrow.get(i), EXPECTED_TOMORROW.get(i));
        }
    }

    // MS2_US9 Scenario 2: Add a recurring goal that starts tomorrow
    @Test
    public void addRecurringGoalsStartsTomorrow() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(GOALS);
        Goal goal_to_recur = new Goal(0, "Test Recursive Goal", false, -1, date_tomorrow, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE);
        model.addBehindUnfinishedAndInFrontOfFinished(goal_to_recur);

        List<Goal> result_recurring = model.getRecurrentGoals().getValue();
        List<Goal> result_tomorrow = model.getTomorrowGoals().getValue();

        List<Goal> EXPECTED_RECURRING = List.of(
                new Goal(0, "Test Recursive Goal", false, 2, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE)
        );

        List<Goal> EXPECTED_TOMORROW = List.of(
                new Goal(0, "Test Recursive Goal", false, 2, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 5, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_recurring.size(); i++) {
            assertEquals(result_recurring.get(i), EXPECTED_RECURRING.get(i));
        }

        for (int i = 0; i < result_tomorrow.size(); i++) {
            assertEquals(result_tomorrow.get(i), EXPECTED_TOMORROW.get(i));
        }
    }

    // MS2_US10: Delete Goals
    // MS2_US10 Scenario 1: Delete Pending Goals
    @Test
    public void deletePendingGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();

        List<Goal> PENDING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        dataSource.putGoals(PENDING_GOALS);
        model.remove(4);

        List<Goal> result_pending = model.getPendingGoals().getValue();

        List<Goal> EXPECTED_PENDING = List.of(
                new Goal(1, "Watering Plant", false, 3, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 4, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE)
        );

        for (int i = 0; i < result_pending.size(); i++) {
            assertEquals(result_pending.get(i), EXPECTED_PENDING.get(i));
        }
    }

    // MS2_US10 Scenario 2: Delete Recurring Goals
    @Test
    public void deleteRecurringGoals() {

        Calendar calendar = Calendar.getInstance();
        Date date_today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_tomorrow = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date_after_tomorrow = calendar.getTime();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        Date next_week = calendar.getTime();
        calendar.add(Calendar.MONTH, 1);
        Date next_month = calendar.getTime();

        List<Goal> RECURRING_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(1, "Watering Plant", false, 2, null, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE),
                new Goal(3, "Feed Pet", true, 4, date_tomorrow, Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE),
                new Goal(4, "Send Message", false, 5, next_month, Goal.RepeatInterval.WEEKLY, Goal.Category.NONE)
        );

        dataSource.putGoals(RECURRING_GOALS);
        model.remove(4);

        List<Goal> result_recurring = model.getRecurrentGoals().getValue();

        List<Goal> EXPECTED_RECURRING = List.of(
                new Goal(0, "Midterm Tomorrow", true, 1, date_today, Goal.RepeatInterval.DAILY, Goal.Category.NONE),
                new Goal(2, "Pay Tax", false, 3, next_week, Goal.RepeatInterval.MONTHLY, Goal.Category.NONE)
        );

        for (int i = 0; i < result_recurring.size(); i++) {
            assertEquals(result_recurring.get(i), EXPECTED_RECURRING.get(i));
        }
    }

    //US 11 Scenario 1: Assign Category When Adding Goals
    @Test
    public void testAssignCategoryWhenAddingGoals() {
        List<Goal> GOALS = List.of(
                new Goal(0, "Home", false, 1, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.HOME),
                new Goal(1, "Work", false, 2, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.WORK),
                new Goal(2, "School", false, 3, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.SCHOOL),
                new Goal(3, "Errands", false, 4, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.ERRANDS)
        );
        dataSource.putGoals(GOALS);

        List<String> EXPECTED_NAMES= List.of(
                "Home",
                "Work",
                "School",
                "Errands"
        );

        List<Goal.Category> EXPECTED_CATS= List.of(
                Goal.Category.HOME,
                Goal.Category.WORK,
                Goal.Category.SCHOOL,
                Goal.Category.ERRANDS
        );

        for (int i = 0; i < model.getOrderedCards().getValue().size(); i++) {
            assertEquals(model.get(i).getName(), EXPECTED_NAMES.get(i));
            assertEquals(model.get(i).getCategory(), EXPECTED_CATS.get(i));
        }
    }


    //US 11 Scenario 2: Sort/Display Goals According By Their Category
    @Test
    public void testDisplayGoalsSortedByCategory() {
        Goal homeGoal = new Goal(1, "Home", false, 1, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.HOME);
        Goal workGoal = new Goal(2, "Work", false, 2, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.WORK);
        Goal schoolGoal = new Goal(3, "School", false, 3, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.SCHOOL);
        Goal errandsGoal = new Goal(4, "Errands", false, 4, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.ERRANDS);
        dataSource.putGoal(errandsGoal);
        assertEquals("Errands", model.getOrderedCards().getValue().get(0).getName());
        dataSource.putGoal(schoolGoal);
        assertEquals("School", model.getOrderedCards().getValue().get(0).getName());
        dataSource.putGoal(homeGoal);
        assertEquals("Home",  model.getOrderedCards().getValue().get(0).getName());
        dataSource.putGoal(workGoal);
        //all goals should be sorted by order home->work->school->errands
        assertEquals("Home", model.getOrderedCards().getValue().get(0).getName());
        assertEquals("Work",  model.getOrderedCards().getValue().get(1).getName());
        assertEquals("School",  model.getOrderedCards().getValue().get(2).getName());
        assertEquals("Errands",  model.getOrderedCards().getValue().get(3).getName());
        //remove goals one by one and still should be in order home->work->school->errands
        dataSource.removeGoal(1);
        assertEquals("Work",  model.getOrderedCards().getValue().get(0).getName());
        dataSource.removeGoal(2);
        assertEquals("School",  model.getOrderedCards().getValue().get(0).getName());
        dataSource.removeGoal(3);
        assertEquals("Errands", model.getOrderedCards().getValue().get(0).getName());
    }

    //US 12 Scenario 1: Using Focus Mode to Filter Goals by Category
    @Test
    public void testFocusModeFilterByCategory() {
        List<Goal> GOALS = List.of(
                new Goal(0, "Home", false, 1, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.HOME),
                new Goal(1, "Work", false, 2, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.WORK),
                new Goal(2, "School", false, 3, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.SCHOOL),
                new Goal(3, "Errands", false, 4, new Date(), Goal.RepeatInterval.ONE_TIME, Goal.Category.ERRANDS)
        );
        dataSource.putGoals(GOALS);

        model.setFocusMode(Goal.Category.HOME);
        assertEquals(Goal.Category.HOME, model.getFocusMode().getValue());

        model.setFocusMode(Goal.Category.WORK);
        assertEquals(Goal.Category.WORK, model.getFocusMode().getValue());

        model.setFocusMode(Goal.Category.SCHOOL);
        assertEquals(Goal.Category.SCHOOL, model.getFocusMode().getValue());

        model.setFocusMode(Goal.Category.ERRANDS);
        assertEquals(Goal.Category.ERRANDS, model.getFocusMode().getValue());
    }
}