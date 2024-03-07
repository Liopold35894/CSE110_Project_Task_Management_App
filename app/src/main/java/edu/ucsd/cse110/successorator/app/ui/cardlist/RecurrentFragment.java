package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.ucsd.cse110.successorator.app.MainViewModel;
import edu.ucsd.cse110.successorator.app.R;
import edu.ucsd.cse110.successorator.app.databinding.FragmentRecurrentBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.ConfirmDeleteCardDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.CreateCardDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.CreatePendingDialogFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.dialog.MoveGoalDialogFragment;

public class RecurrentFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRecurrentBinding view;
    private CardListAdapter adapter;
    private Date date;
    private boolean isMenuProviderAdded = false;

    public RecurrentFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        Fragment fragment = new RecurrentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.date = Calendar.getInstance().getTime();

        if (!isMenuProviderAdded) {
            requireActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
//                    menuInflater.inflate(R.menu.action_bar, menu);
                }

                @Override
                public void onPrepareMenu(@NonNull Menu menu) {
                    MenuItem thisItem = menu.findItem(R.id.recurrent);
                    if (thisItem != null) {
                        thisItem.setVisible(false);
                    }

                    thisItem = menu.findItem(R.id.today);
                    if (thisItem != null) {
                        thisItem.setVisible(true);
                    }

                    thisItem = menu.findItem(R.id.tomorrow);
                    if (thisItem != null) {
                        thisItem.setVisible(true);
                    }

                    thisItem = menu.findItem(R.id.pending);
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
        this.adapter = new CardListAdapter(requireContext(), List.of(), date, id -> {
            var dialogFragment = ConfirmDeleteCardDialogFragment.newInstance(id);
            dialogFragment.show(getParentFragmentManager(), "ConfirmDeleteCardDialogFragment");
        }, activityModel::toggleCompleted);
        activityModel.getRecurrentGoals().observe(recurrentGoals -> {
            adapter.clear();
            adapter.addAll(new ArrayList<>(recurrentGoals)); // Ensure you're working with a mutable copy
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentRecurrentBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);

        view.createCardButton.setOnClickListener(v -> {
            var dialogFragment = CreatePendingDialogFragment.newInstance();
            dialogFragment.show(getParentFragmentManager(), "CreatePendingDialogFragment");
        });
        return view.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateFragment();
    }

    private void updateFragment() {
//        this.view.currentDate.setText(String.format("Today"));

        // Observe isGoalRepositoryEmpty and update the TextView
        activityModel.getRecurrentGoals().observe(goals -> {
            if (goals == null || goals.size() == 0 ) {
                this.view.emptyText.setText("No Recurrent goal");
                this.view.emptyText.setVisibility(View.VISIBLE);
            } else {
                this.view.emptyText.setVisibility(View.GONE);
            }
        });
        activityModel.scheduleToClearFinishedGoals(requireContext(), date);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateFragment();
    }

}
