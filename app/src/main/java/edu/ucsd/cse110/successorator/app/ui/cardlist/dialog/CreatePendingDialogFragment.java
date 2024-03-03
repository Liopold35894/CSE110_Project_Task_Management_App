package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Calendar;
import java.util.Date;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreatePendingBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreatePendingDialogFragment extends DialogFragment {
    private FragmentDialogCreatePendingBinding view;

    private MainViewModel activityModel;

    CreatePendingDialogFragment() {

    }

    public static CreatePendingDialogFragment newInstance() {
        var fragment = new CreatePendingDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        this.view = FragmentDialogCreatePendingBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("What's your next goal?")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        //add card if it is name is not empty
        Date date = new Date();
        var front = view.cardFrontEditText.getText().toString();
        if (front.isEmpty()) {
            return;
        }

        var card = new Goal(0, front, false, -1, null, Goal.RepeatInterval.ONE_TIME);
        activityModel.addBehindUnfinishedAndInFrontOfFinished(card);
        dialog.dismiss();
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

}
