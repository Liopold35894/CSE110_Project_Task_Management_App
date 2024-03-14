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
import edu.ucsd.cse110.successorator.app.databinding.FragmentCardListBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.ConfirmDeleteCardDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.CreateCardDialogFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.GoalRepository;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CardListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentCardListBinding view;
    private CardListAdapter adapter;

    private Date date;

    private boolean isMenuProviderAdded = false;

    public CardListFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isMenuProviderAdded) {
            requireActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                }

                @Override
                public void onPrepareMenu(@NonNull Menu menu) {
                    MenuItem thisItem = menu.findItem(R.id.today);
                    if (thisItem != null) {
                        thisItem.setVisible(false);
                    }
                    thisItem = menu.findItem(R.id.tomorrow);
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

            this.isMenuProviderAdded = true;
        }

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
        activityModel.getTodayGoals().observe(cards -> {
            if (cards == null) return;
            if (activityModel.getFocusMode().getValue() != Goal.Category.NONE) {
                cards = cards.stream().filter(goal -> goal.getCategory() == activityModel.getFocusMode().getValue()).collect(Collectors.toList());
            }
            adapter.clear();
            adapter.addAll(new ArrayList<>(cards)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });

        activityModel.getFocusMode().observe(category -> {
            var card = activityModel.getTodayGoals();
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
        this.view = FragmentCardListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);

        view.createCardButton.setOnClickListener(v -> {
            var dialogFragment = CreateCardDialogFragment.newInstance("today",date);
            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
        });

        view.forward.setOnClickListener(v -> {
            // Simulate the passing of 24 hours
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            this.date = calendar.getTime();
            updateFragment();
            activityModel.setDate(date);
        });

        view.reset.setOnClickListener(v -> {
            this.date = new Date();
            activityModel.setDate(date);
            updateFragment();
        });
        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.date = activityModel.getDate();
        updateFragment();
    }

    private void updateFragment() {

        var dateFormat = new SimpleDateFormat("EEE M/dd", Locale.getDefault());
        var formattedDate = dateFormat.format(date);

        this.view.currentDate.setText(String.format("Today %s", formattedDate));

        // Observe isGoalRepositoryEmpty and update the TextView
        activityModel.getTodayGoals().observe(goals -> {
            if (goals == null || goals.size() == 0 ) {
                this.view.emptyText.setText(R.string.empty_text);
                this.view.emptyText.setVisibility(View.VISIBLE);
            } else {
                this.view.emptyText.setVisibility(View.GONE);
            }
        });
        activityModel.scheduleToClearFinishedGoals(requireContext(), date);
    }

    @Override
    public void onResume() {
        updateFragment();
        super.onResume();
    }


}
