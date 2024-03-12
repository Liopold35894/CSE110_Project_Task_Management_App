//package edu.ucsd.cse110.successorator.lib.domain;
//
//import edu.ucsd.cse110.successorator.lib.data.InMemoryDataSource;
//import edu.ucsd.cse110.successorator.lib.domain.Goal;
//import edu.ucsd.cse110.successorator.lib.domain.SimpleGoalRepository;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//import java.util.List;
//
//public class SimpleGoalRepositoryTest {
//    private InMemoryDataSource dataSource;
//    private SimpleGoalRepository repository;
//
//    @Before
//    public void setUp() {
//        // Use the actual InMemoryDataSource
//        dataSource = new InMemoryDataSource();
//        List<Goal> DEFAULT_CARDS = List.of(
//                new Goal(0, "Midterm Tomorrow", false, 0),
//                new Goal(1, "Watering Plant", false, 1),
//                new Goal(2, "Pay Tax", false, 2),
//                new Goal(3, "Feed Pet", false, 3),
//                new Goal(4, "Send Message", false, 4)
//        );
//        // initialize it with default or custom data
//        dataSource.putGoals(DEFAULT_CARDS);
//        repository = new SimpleGoalRepository(dataSource);
//    }
//    @Test
//    public void find() {
//        // Assuming there's at least one goal with ID 1 in the default data
//        Goal foundGoal = repository.find(1).getValue();
//        assertNotNull(foundGoal);
//        assertEquals(Integer.valueOf(1), foundGoal.getId());
//    }
//
//    @Test
//    public void findAll() {
//        List<Goal> allGoals = repository.findAll().getValue();
//        assertNotNull(allGoals);
//        assertEquals(5, allGoals.size());
//    }
//
//    @Test
//    public void save() {
//        Goal newGoal = new Goal(5, "New Goal", false, 5);
//        repository.save(newGoal);
//
//        Goal savedGoal = dataSource.getGoal(5);
//        assertNotNull(savedGoal);
//        assertEquals("New Goal", savedGoal.getName());
//    }
//
//    @Test
//    public void testSave() {
//    }
//
//    @Test
//    public void remove() {
//        assertNotNull(dataSource.getGoal(1));
//        repository.remove(1);
//        assertNull(dataSource.getGoal(1));
//    }
//
//    @Test
//    public void append() {
//        Goal appendGoal = new Goal(10, "Append Goal", false, dataSource.getMaxSortOrder() + 1);
//        repository.append(appendGoal);
//
//        // Verify
//        Goal foundGoal = dataSource.getGoal(10);
//        assertNotNull(foundGoal);
//        assertEquals(dataSource.getMaxSortOrder(), foundGoal.sortOrder());
//    }
//
//    @Test
//    public void prepend() {
//        Goal prependGoal = new Goal(11, "Prepend Goal", false, dataSource.getMinSortOrder() - 1);
//        repository.prepend(prependGoal);
//
//        Goal foundGoal = dataSource.getGoal(11);
//        assertNotNull(foundGoal);
//        assertEquals(dataSource.getMinSortOrder(), foundGoal.sortOrder());
//    }
//
//    @Test
//    public void addGoalBetweenFinishedAndUnfinished() {
//        Goal finishedGoal = new Goal(12, "New Finished Goal", true, 7);
//        repository.append(finishedGoal);
//
//        Goal extraGoal = new Goal(12, "New Finished Goal", true, 0);
//        repository.addGoalBetweenFinishedAndUnfinished(extraGoal);
//
//        List<Goal> allGoals = dataSource.getGoals();
//        boolean foundNewGoal = false;
//        boolean unfinishedFoundAfterNewGoal = false;
//
//        for (Goal goal : allGoals) {
//            if (goal.getId().equals(extraGoal.getId())) {
//                foundNewGoal = true;
//            } else if (foundNewGoal && !goal.isFinished()) {
//                unfinishedFoundAfterNewGoal = true;
//                break;
//            }
//        }
//
//        assertTrue("The new goal should be added to the list", foundNewGoal);
////        assertTrue("There should be unfinished goals after the newly added finished goal", unfinishedFoundAfterNewGoal);
//    }
//}