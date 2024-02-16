package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreateCardBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateCardDialogFragment extends DialogFragment {
    private FragmentDialogCreateCardBinding view;

    private MainViewModel activityModel;

    CreateCardDialogFragment() {

    }

    public static CreateCardDialogFragment newInstance() {
        var fragment = new CreateCardDialogFragment();
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
        this.view = FragmentDialogCreateCardBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("What's your next goal?")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var front = view.cardFrontEditText.getText().toString();

        var card = new Goal(0, front, false, -1);
        activityModel.addBehindUnfinishedAndInFrontOfFinished(card);


        dialog.dismiss();

    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }




}
