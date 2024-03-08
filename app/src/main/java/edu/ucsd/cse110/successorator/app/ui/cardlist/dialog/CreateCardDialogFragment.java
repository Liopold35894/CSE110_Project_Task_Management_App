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
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreateCardBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateCardDialogFragment extends DialogFragment {
    private FragmentDialogCreateCardBinding view;

    private MainViewModel activityModel;

    private static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private String fragmentType;

    CreateCardDialogFragment() {

    }

    public static CreateCardDialogFragment newInstance(String fragmentType) {
        var fragment = new CreateCardDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TYPE, fragmentType);
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
        this.fragmentType = requireArguments().getString(ARG_FRAGMENT_TYPE);

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
        //add card if it is name is not empty
        Date date = new Date();
        var front = view.cardFrontEditText.getText().toString();
        if (front.isEmpty()) {
            return;
        }
        var repeatInterval = Goal.RepeatInterval.ONE_TIME;
        if ("tomorrow".equals(fragmentType)) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            date = calendar.getTime();
        }
        if(view.oneTimeButton.isChecked()){
            repeatInterval = Goal.RepeatInterval.ONE_TIME;
        }
        else if(view.dailyButton.isChecked()){
            repeatInterval = Goal.RepeatInterval.DAILY;
        }
        else if(view.weeklyButton.isChecked()){
            repeatInterval = Goal.RepeatInterval.WEEKLY;
        }
        else if(view.monthlyButton.isChecked()){
            repeatInterval = Goal.RepeatInterval.MONTHLY;
        }
        else if(view.yearlyButton.isChecked()){
            repeatInterval = Goal.RepeatInterval.YEARLY;
        }
        var card = new Goal(0, front, false, -1, date, repeatInterval);
        activityModel.addBehindUnfinishedAndInFrontOfFinished(card);

        dialog.dismiss();

    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }




}
