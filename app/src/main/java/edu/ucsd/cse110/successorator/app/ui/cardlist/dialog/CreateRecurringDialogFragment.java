package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreateRecurringBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateRecurringDialogFragment extends DialogFragment {
    private FragmentDialogCreateRecurringBinding binding;
    private MainViewModel activityModel;
    private Calendar selectedDate;

    private Goal.Category selectedCategory = Goal.Category.NONE;

    FloatingActionButton homeButton;
    FloatingActionButton workButton;
    FloatingActionButton schoolButton;
    FloatingActionButton errandsButton;


    public static CreateRecurringDialogFragment newInstance() {
        return new CreateRecurringDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        selectedDate = Calendar.getInstance(); // Default to current date, can adjust as necessary
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        binding = FragmentDialogCreateRecurringBinding.inflate(getLayoutInflater());

        homeButton = binding.getRoot().findViewById(R.id.HomeButton);
        workButton = binding.getRoot().findViewById(R.id.WorkButton);
        schoolButton = binding.getRoot().findViewById(R.id.SchoolButton);
        errandsButton = binding.getRoot().findViewById(R.id.ErrandsButton);
        updateDateInView();
        setUpCategoryButtonClickListeners();
        binding.editTextTime.setOnClickListener(v -> showDatePicker());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Create Recurring Goal")
                .setView(binding.getRoot())
                .setPositiveButton("Save", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void showDatePicker() {
        new DatePickerDialog(getContext(), dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            selectedDate.set(Calendar.YEAR, year);
            selectedDate.set(Calendar.MONTH, monthOfYear);
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        }
    };

    private void updateDateInView() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.US);
        binding.editTextTime.setText(sdf.format(selectedDate.getTime()));
    }

    private void onPositiveButtonClick(DialogInterface dialogInterface, int i) {
        if (selectedCategory == Goal.Category.NONE) {
            Toast.makeText(getActivity(), "Please select a category", Toast.LENGTH_LONG).show();
        } else {
            String goalName = binding.cardFrontEditText.getText().toString();
            Goal.RepeatInterval repeatInterval = Goal.RepeatInterval.ONE_TIME; // default value


            if (binding.dailyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.DAILY;
            } else if (binding.weeklyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.WEEKLY;
            } else if (binding.monthlyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.MONTHLY;
            } else if (binding.yearlyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.YEARLY;
            }



            if (!goalName.isEmpty()) {
                Goal newGoal = new Goal(0, goalName, false, -1, selectedDate.getTime(), repeatInterval, selectedCategory);
                activityModel.addBehindUnfinishedAndInFrontOfFinished(newGoal);
            }
        }
    }

    private void onNegativeButtonClick(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
    }

    private void setUpCategoryButtonClickListeners() {

        homeButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.HOME, "h"));
        workButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.WORK,"w"));
        schoolButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.SCHOOL, "s"));
        errandsButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.ERRANDS, "e"));

    }

    private void onCategoryButtonClick(Goal.Category clickedCategory, String button) {
        selectedCategory = clickedCategory;
        var back = binding.getRoot().findViewById(R.id.card_front_edit_text);

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
