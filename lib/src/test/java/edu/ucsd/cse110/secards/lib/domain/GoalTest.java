package edu.ucsd.cse110.secards.lib.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class GoalTest {
    @Test
    public void testGetters() {
        var card = new Goal(1, "front", 0);
        assertEquals(Integer.valueOf(1), card.id());
        assertEquals("front", card.name());
        assertEquals(0, card.sortOrder());
    }

    @Test
    public void testWithId() {
        var card = new Goal(1, "front", 0);
        var expected = new Goal(42, "front" ,0);
        var actual = card.withId(42);
        assertEquals(expected, actual);
    }

    @Test
    public void testWithSortOrder() {
        var card = new Goal(1, "front", 0);
        var expected = new Goal(1, "front", 42);
        var actual = card.withSortOrder(42);
        assertEquals(expected, actual);
    }

    @Test
    public void testEquals() {
        var card1 = new Goal(1, "front", 0);
        var card2 = new Goal(1, "front", 0);
        var card3 = new Goal(2, "front", 0);

        assertEquals(card1, card2);
        assertNotEquals(card1, card3);
    }
}