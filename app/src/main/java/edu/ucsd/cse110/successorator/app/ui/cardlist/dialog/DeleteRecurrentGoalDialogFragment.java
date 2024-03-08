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

public class DeleteRecurrentGoalDialogFragment extends DialogFragment {
    private static final String ARG_GOAL_ID = "flashcard_id";
    private int goalId;

    private Goal goal;
    private MainViewModel activityModel;

    public DeleteRecurrentGoalDialogFragment() {
        // Required empty public constructor
    }

    // make sure you return the right type!
    public static DeleteRecurrentGoalDialogFragment newInstance(int goalId) {
        var fragment = new DeleteRecurrentGoalDialogFragment();
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

        activityModel.getRecurrentGoals().observe(cards -> {
            assert cards != null;
            var recurring = cards.stream()
                    .filter(goal -> ((Integer) goalId).equals(goal.getId()))
                    .collect(Collectors.toList());
            if (recurring.size() > 0) {
                goal = (recurring.get(0));
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        Calendar today = Calendar.getInstance();

        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Delete"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            activityModel.remove(goalId);
                            break;
                    }
                });
        return builder.create();
    }
}
