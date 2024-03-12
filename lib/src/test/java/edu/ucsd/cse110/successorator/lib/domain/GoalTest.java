//package edu.ucsd.cse110.successorator.lib.domain;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//import java.util.Optional;
//
//public class GoalTest {
//
//    Goal testGoal1 = new Goal(0, "testGoal", false, 0); // first test
//    Goal testGoal2 = new Goal(1000000000," ", true, 1); // string with only 1 space and a large enough id
//    Goal testGoal3 = new Goal(10, "", false, 2); // string with only an empty string
//    Goal testGoal4 = new Goal(15, "test", false, 3);
//    @Test
//    public void testGetName() {
//        assertEquals(testGoal1.getName(), "testGoal");
//        assertEquals(testGoal2.getName(), " ");
//        assertEquals(testGoal3.getName(), "");
//    }
//
//    @Test
//    public void testGetId() {
//        assertEquals(testGoal1.getId(), (Integer) 0);
//        assertEquals(testGoal2.getId(),(Integer) 1000000000);
//        assertEquals(testGoal3.getId(), (Integer) 10);
//    }
//
//    @Test
//    public void testIsFinished() {
//        assertEquals(testGoal1.isFinished(), false);
//        assertEquals(testGoal2.isFinished(),true);
//        assertEquals(testGoal3.isFinished(), false);
//    }
//
//    @Test
//    public void testSetter() {
//        Boolean expected = true;
//        testGoal1.setIsFinished(true);
//        Boolean actual = testGoal1.isFinished();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testSortOrder() {
//        int expected = 0;
//        int actual = testGoal1.sortOrder();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testWithId() {
//        var card = new Goal(0, "testGoal", false, 0);
//        var expected = new Goal(42, "testGoal", false, 0);
//        var actual = card.withId(42);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testWithSortOrder() {
//        var card = new Goal(0, "testGoal", false, 0);
//        var expected = new Goal(0, "testGoal", false, 42);
//        var actual = card.withSortOrder(42);
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testEquals() {
//        var card1 = new Goal(0, "testGoal", false, 0);
//
//        assertEquals(card1, testGoal1);
//        assertNotEquals(card1, testGoal2);
//    }
//}