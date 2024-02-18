package edu.ucsd.cse110.successorator.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

public class ClearFinishedGoalsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Clear finished goals
        GoalRepository goalRepository = ((SuccessoratorApplication) context.getApplicationContext()).getGoalRepository();
        goalRepository.findAll().getValue().stream()
                .filter(Goal::isFinished)
                .forEach(goal -> goalRepository.remove(goal.getId()));
    }
}
