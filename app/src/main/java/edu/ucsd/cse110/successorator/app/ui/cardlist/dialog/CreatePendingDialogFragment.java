package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Date;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreatePendingBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreatePendingDialogFragment extends DialogFragment {
    private FragmentDialogCreatePendingBinding view;

    private MainViewModel activityModel;

    private Goal.Category selectedCategory = Goal.Category.NONE;

    FloatingActionButton homeButton;
    FloatingActionButton workButton;
    FloatingActionButton schoolButton;
    FloatingActionButton errandsButton;

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

        homeButton = view.getRoot().findViewById(R.id.HomeButton);
        workButton = view.getRoot().findViewById(R.id.WorkButton);
        schoolButton = view.getRoot().findViewById(R.id.SchoolButton);
        errandsButton = view.getRoot().findViewById(R.id.ErrandsButton);
        setUpCategoryButtonClickListeners();

        return new AlertDialog.Builder(getActivity())
                .setTitle("New Goal")
                .setMessage("What's your next goal?")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        if (selectedCategory == Goal.Category.NONE) {
            Toast.makeText(getActivity(), "Please select a category", Toast.LENGTH_LONG).show();
        } else {
            //add card if it is name is not empty
            Date date = new Date();
            var front = view.cardFrontEditText.getText().toString();
            if (front.isEmpty()) {
                return;
            }

            var card = new Goal(0, front, false, -1, null, Goal.RepeatInterval.ONE_TIME, selectedCategory);
            activityModel.addBehindUnfinishedAndInFrontOfFinished(card);
            dialog.dismiss();
        }

    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void setUpCategoryButtonClickListeners() {

        homeButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.HOME, "h"));
        workButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.WORK,"w"));
        schoolButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.SCHOOL, "s"));
        errandsButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.ERRANDS, "e"));

    }

    private void onCategoryButtonClick(Goal.Category clickedCategory, String button) {
        selectedCategory = clickedCategory;
        var back = view.getRoot().findViewById(R.id.card_front_edit_text);

        switch (button) {
            case "h":
                back.setBackgroundColor(Color.rgb(255,255,153));
                break;
            case "w":
                back.setBackgroundColor(Color.rgb(153,255,255));
                break;
            case "s":
                back.setBackgroundColor(Color.rgb(204,153,255));
                break;
            case "e":
                back.setBackgroundColor(Color.rgb(153,255,153));
                break;
        }

    }
}
