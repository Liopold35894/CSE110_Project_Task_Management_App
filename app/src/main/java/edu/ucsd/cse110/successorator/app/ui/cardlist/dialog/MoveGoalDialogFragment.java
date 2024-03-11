package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.util.MutableSubject;
import edu.ucsd.cse110.successorator.lib.util.SimpleSubject;

public class MoveGoalDialogFragment extends DialogFragment {
    private static final String ARG_GOAL_ID = "flashcard_id";
    private int goalId;

    private Goal goal;
    private MainViewModel activityModel;

    public MoveGoalDialogFragment() {
        // Required empty public constructor
    }

    // make sure you return the right type!
    public static MoveGoalDialogFragment newInstance(int goalId) {
        var fragment = new MoveGoalDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_GOAL_ID, goalId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.goalId = requireArguments().getInt(ARG_GOAL_ID);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);

        this.activityModel = modelProvider.get(MainViewModel.class);

        activityModel.getPendingGoals().observe(cards -> {
            assert cards != null;
            var pending = cards.stream()
                    .filter(goal -> ((Integer) goalId).equals(goal.getId()))
                    .collect(Collectors.toList());
            if (pending.size() > 0) {
                goal = (pending.get(0));
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        Calendar today = Calendar.getInstance();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR,1);

        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Move to Today", "Move to Tomorrow","Finish", "Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            activityModel.addBehindUnfinishedAndInFrontOfFinished(new Goal(0,goal.getName(),false,-1,today.getTime(), Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE));
                            dialog.dismiss();
                            activityModel.remove(goalId);
                            break;
                        case 1:
                            activityModel.addBehindUnfinishedAndInFrontOfFinished(new Goal(0,goal.getName(),false,-1,tomorrow.getTime(), Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE));
                            activityModel.remove(goalId);
                            break;
                        case 2:
                            activityModel.addBehindUnfinishedAndInFrontOfFinished(new Goal(0, goal.getName(), true, -1,today.getTime(), Goal.RepeatInterval.ONE_TIME, Goal.Category.NONE));
                            activityModel.remove(goalId);
                        case 3:
                            activityModel.remove(goalId);
                            break;
                    }
                });
        return builder.create();
    }
}
