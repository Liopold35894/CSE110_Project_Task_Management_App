package edu.ucsd.cse110.successorator.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTimeKeeper;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class MainViewModelTest {
    private MainViewModel model;
    private InMemoryDataSource dataSource;
    private SimpleGoalRepository repo;
    private SimpleTimeKeeper timeKeeper;

    @Before
    public void setUp() throws Exception {
        dataSource = new InMemoryDataSource();
        repo = new SimpleGoalRepository(dataSource);
        timeKeeper = new SimpleTimeKeeper();
        model = new MainViewModel(repo);
    }

//    @Test
//    public void startApp_NoGoals_DisplayNoGoalsMessage() {
//        // GIVEN an empty data source
//        // WHEN starting the app
//        // THEN the empty message is displayed
//        assertEquals("No goals for the Day. Click the + at the upper right to enter your Most Important Thing.", model.getDisplayedText().getValue());
//    }

    // US 2 Scenario 1: List is empty
    //    Given that the list is empty
    //    When the user tap on the plus sign on the top right
    //    Then a keyboard came up
    //    When the user inputs 'Prepare for the midterm' via typing or speaking and taps the check mark button
    //    Then the keyboard disappeared and 'Prepare for the midterm' appeared near the top of the page, just below the data bar.
    @Test
    public void addGoal_ListEmpty_GoalAdded() {
        // WHEN adding a new goal
        Goal newGoal = new Goal(1, "Prepare for the midterm", false, 0);
        model.prepend(newGoal);

        // THEN the goal list should contain the new goal
        List<Goal> goals = model.getOrderedCards().getValue();
        assertEquals(1, goals.size());
        assertEquals("Prepare for the midterm", goals.get(0).getName());
    }



    //  US 2 Scenario 2.1: List is not empty
    //    Given there exists some tasks
    //    When the user tap on the plus sign on the top right
    //    Then a keyboard came up
    //    When the user inputs 'Prepare for the midterm' via typing or speaking and taps the check mark button
    //    Then the new task should be below the previous tasks and  before the inactive list
    @Test
    public void addGoal_ListNotEmpty_GoalAdded() {
        List<Goal> DEFAULT_CARDS = List.of(
                new Goal(0, "Watering Plant", false, 0)
        );

        // initialize it with default or custom data
        dataSource.putGoals(DEFAULT_CARDS);

        // WHEN adding a new goal
        Goal newGoal = new Goal(1, "Prepare for the midterm", false, 1);
        model.prepend(newGoal);

        // THEN the goal list should contain the new goal
        List<Goal> goals = model.getOrderedCards().getValue();
        assertEquals(2, goals.size());
        assertEquals("Prepare for the midterm", goals.get(0).getName());
        assertEquals("Watering Plant", goals.get(1).getName());
    }

    // US 2 Scenario 3: Close the input prompt if no longer want to add the goal
    //    Given that user want to add a task
    //    When the user tap on the plus sign on the top right
    //    Then a keyboard came up
    //    When the user tap on the cross mark
    //    Then the input prompt disappeared


    // US 2 Scenario 4: Input is empty
    //    Given that user want to add add a task
    //    When the user tap on the plus sign on the top right
    //    Then a keyboard came up
    //    When the user’s input is empty
    //    Then the user should see an error message.

    @Test
    public void addGoal_InputEmpty_Error() {
        List<Goal> DEFAULT_CARDS = List.of(
                new Goal(0, "Watering Plant", false, 0)
        );

        // initialize it with default or custom data
        dataSource.putGoals(DEFAULT_CARDS);

        // WHEN adding a new goal
        Goal newGoal = new Goal(1, null, false, 1);
        try {
            model.prepend(newGoal);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Goal name cannot be null", e.getMessage());
        }

        // THEN the goal list should contain the new goal
        List<Goal> goals = model.getOrderedCards().getValue();
        assertEquals(1, goals.size());
        //assertEquals(null, goals.get(0).getName());
        assertEquals("Watering Plant", goals.get(0).getName());
    }


    // US 3 Scenario 1.1: The target goal is not marked as finished
    //    Given the goal is in the list
    //    AND the goal is not finished
    //    When the user tap on that goal
    //    Then the goal is moved down to the bottom of the list
    //    And the goal appears in strike-through
    @Test
    public void markGoalAsCompleted_GoalMovesToBottom() {
        List<Goal> DEFAULT_CARDS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", false, 3),
                new Goal(4, "Send Message", false, 4)
        );
        // initialize it with default or custom data
        dataSource.putGoals(DEFAULT_CARDS);
        Goal goalToMark = model.getOrderedCards().getValue().get(0);

        int id = goalToMark.getId();
        // WHEN marking the goal as completed
        model.toggleCompleted(goalToMark);

        // THEN the goal is marked as completed and moved accordingly
        Goal updatedGoal = model.get(goalToMark.getId());
        Goal expectedFirstFinishedGoal = dataSource.getFinishedGoals();
        assertTrue(updatedGoal.isFinished());
        assertEquals("Midterm Tomorrow", updatedGoal.getName());
        assertEquals(expectedFirstFinishedGoal, updatedGoal);

        // AND the previous second goal becomes the new first Goal.
        Goal newFirstUnifinishedGoal = model.getOrderedCards().getValue().get(0);
        Goal expectedFirstUnfinishedGoal = dataSource.getUnfinishedGoals();
        assertEquals("Watering Plant", newFirstUnifinishedGoal.getName());
        assertEquals(expectedFirstUnfinishedGoal, newFirstUnifinishedGoal);
    }
    // US 3 Scenario 1.2: The target goal is not marked as finished (with only finished goals
    //    Given there's only finished goals other than the goal we are trying to mark as finished in the list
    //    When the user tap on that goal
    //    Then the goal is moved down to the bottom of the list
    //    And the goal appears in strike-through
    @Test
    public void markGoalAsCompletedWithOnlyFinishedGoals() {
        List<Goal> ONE_GOAL = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", true, 1),
                new Goal(2, "Pay Tax", true, 2),
                new Goal(3, "Feed Pet", true, 3),
                new Goal(4, "Send Message", true, 4)
        );
        // initialize it with default or custom data
        dataSource.putGoals(ONE_GOAL);
        Goal goalToMark = model.getOrderedCards().getValue().get(0);

        model.toggleCompleted(goalToMark);

        List<Goal> result = model.getOrderedCards().getValue();
        Goal EXPECTED = new Goal(0, "Midterm Tomorrow", true, 0);
        assertEquals(result.get(4).isFinished, EXPECTED.isFinished);
        assertEquals(result.get(4).getId(), EXPECTED.getId());
    }

    // US 3 Scenario 1.3: The target goal is not marked as finished
    //    Given there's only one goal in the list
    //    AND the goal is not finished
    //    When the user tap on that goal
    //    Then the goal is moved down to the bottom of the list (which is no moving at all)
    //    And the goal appears in strike-through
    @Test
    public void markGoalAsCompletedWithUnFinishedGoals() {
        List<Goal> ONE_GOAL = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", false, 3),
                new Goal(4, "Send Message", false, 4)
        );
        // initialize it with default or custom data
        dataSource.putGoals(ONE_GOAL);
        Goal goalToMark = model.getOrderedCards().getValue().get(1);

        model.toggleCompleted(goalToMark);

        List<Goal> result = model.getOrderedCards().getValue();
        Goal EXPECTED = new Goal(1, "Watering Plant", true, 1);
        assertEquals(result.get(4).isFinished, EXPECTED.isFinished);
        assertEquals(result.get(4).getId(), EXPECTED.getId());
    }

    // US 3 Scenario 2: The target goal is marked as finished
    //    Given the goal is in the list
    //    AND the goal is finished
    //    When the user tap on that goal
    //    Then goal is moved up to the top of the list
    @Test
    public void undoGoalCompleted_GoalMovesToTop() {
        List<Goal> DEFAULT_CARDS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", false, 3),
                new Goal(4, "Send Message", true, 4)
        );
        int id = 4;
        dataSource.putGoals(DEFAULT_CARDS);
        Goal goalToUndoMark = model.getOrderedCards().getValue().get(id);
        assertTrue(goalToUndoMark.isFinished);
        // WHEN undo marking (tapping) the completed goal
        model.toggleCompleted(goalToUndoMark);


        // THEN the goal is marked as unfinished and moved accordingly
        Goal updatedGoal = model.get(goalToUndoMark.getId());
        assertEquals("Send Message", updatedGoal.getName());
        assertFalse(updatedGoal.isFinished);

        // AND the original first goal becomes the second Goal.
        Goal actualFirstGoal = model.getOrderedCards().getValue().get(0);
        assertEquals("Send Message", actualFirstGoal.getName());

    }

    // US 3 Scenario 2.2: The target goal is not marked as finished (with only finished goals
    //    Given there's only finished goals other than the goal we are trying to mark as finished in the list
    //    When the user tap on that goal
    //    Then the goal is moved down to the bottom of the list
    //    And the goal appears in strike-through
    @Test
    public void undoGoalCompletedWithOnlyFinishedGoals() {
        List<Goal> FINISHED_GOAL = List.of(
                new Goal(0, "Midterm Tomorrow", true, 0),
                new Goal(1, "Watering Plant", true, 1),
                new Goal(2, "Pay Tax", true, 2),
                new Goal(3, "Feed Pet", true, 3),
                new Goal(4, "Send Message", true, 4)
        );
        // initialize it with default or custom data
        dataSource.putGoals(FINISHED_GOAL);
        Goal goalToMark = model.getOrderedCards().getValue().get(3);

        model.toggleCompleted(goalToMark);

        List<Goal> result = model.getOrderedCards().getValue();
        Goal EXPECTED = new Goal(3, "Feed Pet", false, 3);
        assertEquals(result.get(0).isFinished, EXPECTED.isFinished);
        assertEquals(result.get(0).getId(), EXPECTED.getId());
    }

    // US 4 Scenario 1: User has unfinished goals as well as finished ones.
    //    Given the user has unfinished goals
    //    When the user launch the app the other day
    //    Then the user can see only their unfinished goals

    @Test
    public void removeFinishedGoalsWithMixedGoals(){

        List<Goal> MIXED_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", true, 3),
                new Goal(4, "Send Message", true, 4)
        );

        dataSource.putGoals(MIXED_GOALS);
        model.removeFinishedGoals();

        List<Goal> result = model.getOrderedCards().getValue();
        List<Goal> EXPECTED = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }

    // US 4 Scenario 2: User has no goals
    //    Given the user has unfinished goals
    //    When the user launch the app the other day
    //    Then no change need to be done
    @Test
    public void removeFinishedGoalsWithNoGoals(){

        List<Goal> NO_GOALS = List.of();

        dataSource.putGoals(NO_GOALS);
        model.removeFinishedGoals();

        List<Goal> result = model.getOrderedCards().getValue();
        List<Goal> EXPECTED = List.of();
        assertArrayEquals(new List[]{result}, new List[]{EXPECTED});
    }

    // US 4 Scenario 3: User has only unfinished goals.
    //    Given the user has only unfinished goals
    //    When the user launch the app the other day
    //    Then no change need to be done
    @Test
    public void removeFinishedGoalsWithOnlyUnfinishedGoals(){

        List<Goal> UNFINISHED_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", false, 3),
                new Goal(4, "Send Message", false, 4)
        );

        dataSource.putGoals(UNFINISHED_GOALS);
        model.removeFinishedGoals();

        List<Goal> result = model.getOrderedCards().getValue();
        List<Goal> EXPECTED = List.of(
                new Goal(0, "Midterm Tomorrow", false, 0),
                new Goal(1, "Watering Plant", false, 1),
                new Goal(2, "Pay Tax", false, 2),
                new Goal(3, "Feed Pet", false, 3),
                new Goal(4, "Send Message", false, 4)
        );
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }
    // US 4 Scenario 4: User has only finished goals.
    //    Given the user has only finished goals
    //    When the user launch the app the other day
    //    Then no goals should exist.
    @Test
    public void removeFinishedGoalsWithOnlyFinishedGoals() {

        List<Goal> UNFINISHED_GOALS = List.of(
                new Goal(0, "Midterm Tomorrow", true, 0),
                new Goal(1, "Watering Plant", true, 1),
                new Goal(2, "Pay Tax", true, 2),
                new Goal(3, "Feed Pet", true, 3),
                new Goal(4, "Send Message", true, 4)
        );

        dataSource.putGoals(UNFINISHED_GOALS);
        model.removeFinishedGoals();

        List<Goal> result = model.getOrderedCards().getValue();
        List<Goal> EXPECTED = List.of();
        for (int i = 0; i < result.size(); i++) {
            assertEquals(result.get(i), EXPECTED.get(i));
        }
    }
}
