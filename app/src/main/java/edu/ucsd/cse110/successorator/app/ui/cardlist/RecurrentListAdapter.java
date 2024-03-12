package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.databinding.ListItemCardBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.DeleteRecurrentGoalDialogFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class RecurrentListAdapter extends ArrayAdapter<Goal> {

    Consumer<Integer> onLongPress;

    public RecurrentListAdapter(Context context, List<Goal> goals, Consumer<Integer> onLongPress) {
        super(context, 0, new ArrayList<>(goals));
        this.onLongPress = onLongPress;

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the flashcard for this position.
        var goal = getItem(position);
        assert goal != null;

        // Check if a view is being reused...
        ListItemCardBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemCardBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemCardBinding.inflate(layoutInflater, parent, false);
        }
        // Populate the view with the flashcard's data.
        binding.cardFrontText.setText(goal.getName());
        String emoji = "";
        switch (goal.getCategory()) {
            case WORK:
                emoji = "💼";
                break;
            case SCHOOL:
                emoji = "🏫";
                break;
            case HOME:
                emoji = "🏠";
                break;
            case ERRANDS:
                emoji = "🗒";
                break;
        }
        binding.categoryName.setText(emoji + " " + goal.getCategory().name());
        int goalId = goal.getId();
        binding.cardFrontText.setOnLongClickListener(v -> {
            // Create a new instance of MoveGoalDialogFragment
            DeleteRecurrentGoalDialogFragment dialogFragment = DeleteRecurrentGoalDialogFragment.newInstance(goalId);
            // Show the dialog
            dialogFragment.show(((FragmentActivity) v.getContext()).getSupportFragmentManager(), "DeleteRecurrentGoalDialogFragment");

            return true;
        });
        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var goal = getItem(position);
        assert goal != null;

        var id = goal.getId();
        assert id != null;

        return id;
    }
}