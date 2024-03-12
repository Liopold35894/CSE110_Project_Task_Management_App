package edu.ucsd.cse110.successorator.app;

import static org.junit.Assert.assertEquals;
import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.RoomGoalRepository;
import edu.ucsd.cse110.successorator.app.data.db.SuccessoratorDatabase;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;
import edu.ucsd.cse110.successorator.lib.domain.SimpleTimeKeeper;
import edu.ucsd.cse110.successorator.lib.domain.TimeKeeper;

import org.junit.Test;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

//    @Test
//    public void startApp_NoGoals_DisplayNoGoalsMessage() {
//        // GIVEN an empty data source
//        var dataSource = new InMemoryDataSource();
//        var repo = new SimpleGoalRepository(dataSource);
//        var timeKeeper = new SimpleTimeKeeper();
//        var model = new MainViewModel(repo);
//
//        // WHEN starting the app
//        // (Initialization in ViewModel constructor simulates this)
//
//        // THEN the empty message is displayed
//        assertEquals("No goals for the Day. Click the + at the upper right to enter your Most Important Thing.", model.getDisplayedText().getValue());
//    }

//    @Test
//    public void example() {
//        // GIVEN
//        var dataSource = new InMemoryDataSource();
//        List<Goal> DEFAULT_CARDS = List.of(
//                new Goal(0, "Midterm Tomorrow", false, 0),
//                new Goal(1, "Watering Plant", false, 1),
//                new Goal(2, "Pay Tax", false, 2),
//                new Goal(3, "Feed Pet", false, 3),
//                new Goal(4, "Send Message", false, 4)
//        );
//        // initialize it with default or custom data
//        dataSource.putGoals(DEFAULT_CARDS);
//        var repo = new SimpleGoalRepository(dataSource);
//        var timeKeeper = new SimpleTimeKeeper();
//        var model = new MainViewModel(repo);
//    }
}