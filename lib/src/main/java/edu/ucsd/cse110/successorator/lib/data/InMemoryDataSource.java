package edu.ucsd.cse110.successorator.lib.data;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;
import edu.ucsd.cse110.successorator.lib.util.Subject;

/**
 * Class used as a sort of "database" of list and goals that exist. This
 * will be replaced with a real database in the future, but can also be used
 * for testing.
 */
public class InMemoryDataSource {
    private int nextId = 0;

    private int minSortOrder = Integer.MAX_VALUE;
    private int maxSortOrder = Integer.MIN_VALUE;

    private final Map<Integer, Goal> goals
            = new HashMap<>();
    private final Map<Integer, MutableSubject<Goal>> goalSubjects
            = new HashMap<>();
    private final MutableSubject<List<Goal>> allGoalsSubject
            = new SimpleSubject<>();

    public InMemoryDataSource() {
    }

    public final static List<Goal> DEFAULT_CARDS = List.of(

    );

    public static InMemoryDataSource fromDefault() {
        var data = new InMemoryDataSource();
        data.putGoals(DEFAULT_CARDS);
        return data;
    }

    public List<Goal> getGoals() {
        return List.copyOf(goals.values());
    }

    public Goal getGoal(int id) {
        return goals.get(id);
    }

    public Subject<Goal> getGoalSubject(int id) {
        if (!goalSubjects.containsKey(id)) {
            var subject = new SimpleSubject<Goal>();
            subject.setValue(getGoal(id));
            goalSubjects.put(id, subject);
        }
        return goalSubjects.get(id);
    }

    public Subject<List<Goal>> getAllGoalsSubject() {
        return allGoalsSubject;
    }

    public int getMinSortOrder() {
        return minSortOrder;
    }

    public int getMaxSortOrder() {
        return maxSortOrder;
    }

    public void putGoal(Goal card) {
        var fixedCard = preInsert(card);

        goals.put(fixedCard.getId(), fixedCard);
        postInsert();
        assertSortOrderConstraints();

        if (goalSubjects.containsKey(fixedCard.getId())) {
            goalSubjects.get(fixedCard.getId()).setValue(fixedCard);
        }
        allGoalsSubject.setValue(getGoals());
    }

    public void putGoals(List<Goal> cards) {
        var fixedCards = cards.stream()
                .map(this::preInsert)
                .collect(Collectors.toList());

        fixedCards.forEach(card -> goals.put(card.getId(), card));
        postInsert();
        assertSortOrderConstraints();

        fixedCards.forEach(card -> {
            if (goalSubjects.containsKey(card.getId())) {
                goalSubjects.get(card.getId()).setValue(card);
            }
        });
        allGoalsSubject.setValue(getGoals());
    }

    public void removeGoal(int id) {
        var card = goals.get(id);
        var sortOrder = card.sortOrder();

        goals.remove(id);
        shiftSortOrders(sortOrder, maxSortOrder, -1);

        if (goalSubjects.containsKey(id)) {
            goalSubjects.get(id).setValue(null);
        }
        allGoalsSubject.setValue(getGoals());
    }


    public void shiftSortOrders(int from, int to, int by) {
        var cards = goals.values().stream()
                .filter(card -> card.sortOrder() >= from && card.sortOrder() <= to)
                .map(card -> card.withSortOrder(card.sortOrder() + by))
                .collect(Collectors.toList());

        putGoals(cards);
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that new
     * cards inserted have an id, and updates the nextId if necessary.
     */
    private Goal preInsert(Goal card) {
        int id = card.getId();
        if (id > nextId) {
            // If the card has an id, update nextId if necessary to avoid giving out the same
            // one. This is important for when we pre-load cards like in fromDefault().
            nextId = id + 1;
        }

        return card;
    }

    /**
     * Private utility method to maintain state of the fake DB: ensures that the
     * min and max sort orders are up to date after an insert.
     */
    private void postInsert() {
        // Keep the min and max sort orders up to date.
        minSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .min(Integer::compareTo)
                .orElse(Integer.MAX_VALUE);

        maxSortOrder = goals.values().stream()
                .map(Goal::sortOrder)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE);
    }

    /**
     * Safety checks to ensure the sort order constraints are maintained.
     * <p></p>
     * Will throw an AssertionError if any of the constraints are violated,
     * which should never happen. Mostly here to make sure I (Dylan) don't
     * write incorrect code by accident!
     */
    private void assertSortOrderConstraints() {
        // Get all the sort orders...
        var sortOrders = goals.values().stream()
                .map(Goal::sortOrder)
                .collect(Collectors.toList());

        //Non-negative...
        assert sortOrders.stream().allMatch(i -> i >= -1);

        // Unique...
        assert sortOrders.size() == sortOrders.stream().distinct().count();

        // Between min and max...
        assert sortOrders.stream().allMatch(i -> i >= minSortOrder);
        assert sortOrders.stream().allMatch(i -> i <= maxSortOrder);
    }

    public Goal getUnfinishedGoals() {
        return goals.values().stream()
                .filter(goal -> !goal.isFinished())
                .findFirst()
                .orElse(null);
    }

    public Goal getFinishedGoals() {
        return goals.values().stream()
                .filter(goal -> goal.isFinished())
                .findFirst()
                .orElse(null);
    }

    public void removeFinishedGoals() {
        var finishedGoals = goals.values().stream()
                .filter(Goal::isFinished)
                .filter(goal -> goal.getRepeatInterval() == Goal.RepeatInterval.ONE_TIME)
                .collect(Collectors.toList());

        for (Goal goal : finishedGoals) {
            removeGoal(goal.getId());
        }
    }
}
