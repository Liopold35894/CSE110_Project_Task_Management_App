package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentTomorrowBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.ConfirmDeleteCardDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.CreateCardDialogFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TomorrowFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTomorrowBinding view;
    private CardListAdapter adapter;

    private Date date;

    public TomorrowFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new TomorrowFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public void onPrepareMenu(@NonNull Menu menu) {
                MenuItem thisItem = menu.findItem(R.id.tomorrow);
                if (thisItem != null) {
                    thisItem.setVisible(false);
                }

                thisItem = menu.findItem(R.id.today);
                if (thisItem != null) {
                    thisItem.setVisible(true);
                }

                thisItem = menu.findItem(R.id.pending);
                if (thisItem != null) {
                    thisItem.setVisible(true);
                }

                thisItem = menu.findItem(R.id.recurrent);
                if (thisItem != null) {
                    thisItem.setVisible(true);
                }


            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }

        });

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new CardListAdapter(requireContext(), List.of(), id -> {
            var dialogFragment = ConfirmDeleteCardDialogFragment.newInstance(id);
            dialogFragment.show(getParentFragmentManager(), "ConfirmDeleteCardDialogFragment");
        }, activityModel::toggleCompleted);
        activityModel.getTomorrowGoals().observe(cards -> {
            if (cards == null) return;
            if (activityModel.getFocusMode().getValue() != Goal.Category.NONE) {
                cards = cards.stream().filter(goal -> goal.getCategory() == activityModel.getFocusMode().getValue()).collect(Collectors.toList());
            }
            adapter.clear();
            adapter.addAll(new ArrayList<>(cards)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });

        activityModel.getFocusMode().observe(category -> {
            var card = activityModel.getTomorrowGoals();
            var cards = card.getValue();
            if (cards == null) return;
            if (activityModel.getFocusMode().getValue() != Goal.Category.NONE) {
                cards = cards.stream().filter(goal -> goal.getCategory() == activityModel.getFocusMode().getValue()).collect(Collectors.toList());
            }
            adapter.clear();
            adapter.addAll(new ArrayList<>(cards)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTomorrowBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);

        view.createCardButton.setOnClickListener(v -> {
            var dialogFragment = CreateCardDialogFragment.newInstance("tomorrow",date);
            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
        });

        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(activityModel.getDate());
        calendar.add(Calendar.DAY_OF_YEAR, 1);  // Add 1 day to get tomorrow's date
        this.date = calendar.getTime();


        var dateFormat = new SimpleDateFormat("EEE M/dd", Locale.getDefault());
        var formattedDate = dateFormat.format(date);

        this.view.currentDate.setText(String.format("Tomorrow %s", formattedDate));

        activityModel.getTomorrowGoals().observe(goals -> {
            if (goals == null || goals.size() == 0 ) {
                this.view.emptyText.setText(R.string.empty_text_tomorrow);
                this.view.emptyText.setVisibility(View.VISIBLE);
            } else {
                this.view.emptyText.setVisibility(View.GONE);
            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
