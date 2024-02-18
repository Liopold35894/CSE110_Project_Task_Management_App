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
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class ConfirmDeleteCardDialogFragment extends DialogFragment {
    private static final String ARG_FLASHCARD_ID = "flashcard_id";
    private int flashcardId;

    private MainViewModel activityModel;

    public ConfirmDeleteCardDialogFragment() {
        // Required empty public constructor
    }

    // make sure you return the right type!
    public static ConfirmDeleteCardDialogFragment newInstance(int flashcardId) {
        var fragment = new ConfirmDeleteCardDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FLASHCARD_ID, flashcardId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.flashcardId = requireArguments().getInt(ARG_FLASHCARD_ID);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);

        this.activityModel = modelProvider.get(MainViewModel.class);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Deleted Card")
                .setMessage("Are you sure you want to delete this card?")
                .setPositiveButton("Confirm", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    //I need not only remove from the curlist
    //but also add to the other list
    private void onPositiveButtonClick(DialogInterface dialog, int which) {

        activityModel.remove(flashcardId); // wait... how do we know which ID?

        dialog.dismiss();;
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
