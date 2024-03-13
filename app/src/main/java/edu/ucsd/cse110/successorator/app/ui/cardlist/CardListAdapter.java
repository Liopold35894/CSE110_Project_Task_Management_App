package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.ListItemCardBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class CardListAdapter extends ArrayAdapter<Goal> {

    Consumer<Integer> onDeleteClick;
    Consumer<Goal> toggleCompleted;


    public CardListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick, Consumer<Goal> togggleCompleted) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
        this.toggleCompleted = togggleCompleted;


    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
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
        //this is the way to use strikethrough
//        binding.cardFrontText.setPaintFlags(binding.cardFrontText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (goal.isFinished()) {
            binding.cardFrontText.setPaintFlags( binding.cardFrontText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.cardFrontText.setPaintFlags( binding.cardFrontText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
        binding.cardFrontText.setOnClickListener(v -> toggleCompleted.accept(goal));
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
