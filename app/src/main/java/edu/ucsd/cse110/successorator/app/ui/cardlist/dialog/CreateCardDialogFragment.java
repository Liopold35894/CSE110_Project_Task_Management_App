package edu.ucsd.cse110.successorator.app.ui.cardlist.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentDialogCreateCardBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CreateCardDialogFragment extends DialogFragment {
    private FragmentDialogCreateCardBinding view;

    private MainViewModel activityModel;

    private static final String ARG_FRAGMENT_TYPE = "fragment_type";
    private String fragmentType;
    private Goal.Category selectedCategory = Goal.Category.NONE;

    FloatingActionButton homeButton;
    FloatingActionButton workButton;
    FloatingActionButton schoolButton;
    FloatingActionButton errandsButton;

    EditText weeklyStart;
    EditText monthlyStart;
    EditText yearlyStart;

    private static final String ARG_DATE = "date";
    private Date selectedDate;


    public CreateCardDialogFragment() {
    }

    public static CreateCardDialogFragment newInstance(String fragmentType, Date date) {
        CreateCardDialogFragment fragment = new CreateCardDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_TYPE, fragmentType);
        args.putSerializable(ARG_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        fragmentType = requireArguments().getString(ARG_FRAGMENT_TYPE);
        selectedDate = (Date) requireArguments().getSerializable(ARG_DATE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        view = FragmentDialogCreateCardBinding.inflate(LayoutInflater.from(getContext()));

        homeButton = view.getRoot().findViewById(R.id.HomeButton);
        workButton = view.getRoot().findViewById(R.id.WorkButton);
        schoolButton = view.getRoot().findViewById(R.id.SchoolButton);
        errandsButton = view.getRoot().findViewById(R.id.ErrandsButton);
        setUpCategoryButtonClickListeners();

        weeklyStart = view.getRoot().findViewById(R.id.weekly_start);
        monthlyStart = view.getRoot().findViewById(R.id.monthly_start);
        yearlyStart = view.getRoot().findViewById(R.id.yearly_start);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activityModel.getDate());
        updateDateViews(selectedDate);

        return new AlertDialog.Builder(requireContext())
                .setTitle("New Goal")
                .setMessage("What's your next goal?")
                .setView(view.getRoot())
                .setPositiveButton("Create", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void updateDateViews(Date date) {
        // Set the initial date texts for weekly, monthly, and yearly
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // For weekly start date
        Calendar weeklyCalendar = Calendar.getInstance();
        weeklyCalendar.setTime(date);
        if (weeklyCalendar.before(calendar)) {
            weeklyCalendar.add(Calendar.WEEK_OF_YEAR, 1); // If the specified weekday is before the selected date, move to next week
        }
        weeklyStart.setText("Weekly on " + new SimpleDateFormat("EEEE", Locale.US).format(weeklyCalendar.getTime()));

        // For monthly start date
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayOfWeekInMonth = (dayOfMonth - 1) / 7 + 1;
        String dayOfWeek = getDayOfWeekInMonth(dayOfWeekInMonth) + " " + new SimpleDateFormat("EEEE", Locale.US).format(calendar.getTime());
        monthlyStart.setText("Monthly on " + dayOfWeek);

        // For yearly start date
        yearlyStart.setText("Yearly on " + new SimpleDateFormat("MM/dd", Locale.US).format(calendar.getTime()));
    }

    // Helper method to get the ordinal indicator for the day of the week in a month
    private String getDayOfWeekInMonth(int n) {
        String[] suffixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        return n + suffixes[n % 10];
    }


    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        if (selectedCategory == Goal.Category.NONE) {
            Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_LONG).show();
        } else {
            Date date = new Date();
            String front = view.cardFrontEditText.getText().toString();
            if (front.isEmpty()) {
                return;
            }

            Goal.RepeatInterval repeatInterval = Goal.RepeatInterval.ONE_TIME;
            if ("tomorrow".equals(fragmentType)) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, 1);
                date = calendar.getTime();
            }
            if (view.oneTimeButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.ONE_TIME;
            } else if (view.dailyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.DAILY;
            } else if (view.weeklyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.WEEKLY;
            } else if (view.monthlyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.MONTHLY;
            } else if (view.yearlyButton.isChecked()) {
                repeatInterval = Goal.RepeatInterval.YEARLY;
            }

            Goal card = new Goal(0, front, false, -1, date, repeatInterval, selectedCategory);
            activityModel.addBehindUnfinishedAndInFrontOfFinished(card);

            dialog.dismiss();
        }
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }

    private void setUpCategoryButtonClickListeners() {
        homeButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.HOME));
        workButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.WORK));
        schoolButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.SCHOOL));
        errandsButton.setOnClickListener(v -> onCategoryButtonClick(Goal.Category.ERRANDS));
    }

    private void onCategoryButtonClick(Goal.Category clickedCategory) {
        selectedCategory = clickedCategory;
        view.getRoot().findViewById(R.id.card_front_edit_text).setBackgroundColor(getCategoryColor(selectedCategory));
    }

    private int getCategoryColor(Goal.Category category) {
        switch (category) {
            case HOME:
                return Color.rgb(255, 255, 153);
            case WORK:
                return Color.rgb(153, 255, 255);
            case SCHOOL:
                return Color.rgb(204, 153, 255);
            case ERRANDS:
                return Color.rgb(153, 255, 153);
            default:
                return Color.TRANSPARENT;
        }
    }
}
