package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentCardListBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.ConfirmDeleteCardDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.CreateCardDialogFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

import java.text.DateFormat;
import java.util.Calendar;

public class CardListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentCardListBinding view;
    private CardListAdapter adapter;

    private ArrayAdapter<Goal> unfinishedAdapter;
    private ArrayAdapter<Goal> finishedAdapter;
    private List<Goal> unfinishedGoals;
    private List<Goal> finishedGoals;

    private Calendar date;

    public CardListFragment() {
        // Required empty public constructor
    }

    public static CardListFragment newInstance() {
        CardListFragment fragment = new CardListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.date = Calendar.getInstance();

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
        activityModel.getOrderedCards().observe(cards -> {
            if (cards == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(cards)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
        unfinishedAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, unfinishedGoals);
        finishedAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, finishedGoals);


    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentCardListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);

        view.createCardButton.setOnClickListener(v -> {
            var dialogFragment = CreateCardDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
        });

        view.forward.setOnClickListener(v -> {
            // Simulate the passing of 24 hours
            // You need to implement this method in your MainViewModel class
//            activityModel.forwardTimeBy24Hours();

            date.add(Calendar.HOUR_OF_DAY, 24);
            var currentDate = date.getTime();

            var dateFormat = DateFormat.getDateInstance().format(currentDate);
            this.view.currentDate.setText(dateFormat);
        });



        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        var calendar = Calendar.getInstance().getTime();
        var dateFormat = DateFormat.getDateInstance().format(calendar);

        this.view.currentDate.setText(dateFormat);

        // Observe isGoalRepositoryEmpty and update the TextView
        activityModel.getIsEmpty().observe(isEmpty -> {
            if (isEmpty) {
                this.view.emptyText.setText(R.string.empty_text);
                this.view.emptyText.setVisibility(View.VISIBLE);
            } else {
                this.view.emptyText.setVisibility(View.GONE);
            }

        });
    }
}
