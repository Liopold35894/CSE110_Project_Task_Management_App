package edu.ucsd.cse110.successorator.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.app.databinding.ActivityMainBinding;
import edu.ucsd.cse110.successorator.app.ui.cardlist.CardListFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.PendingFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.TomorrowFragment;
import edu.ucsd.cse110.successorator.app.ui.cardlist.RecurrentFragment;
import edu.ucsd.cse110.successorator.lib.domain.Goal;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private AlertDialog focusDialog;
    private Switch actionViewSwitch;

    private MainViewModel mainViewModel;

    private boolean isUserInitiatedSwitch = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Successorator");
        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        // Find the menu item with the Switch
        MenuItem switchItem = menu.findItem(R.id.action_bar_menu_switch); // ID for the menu item
        // Get the action view of the item
        actionViewSwitch = (Switch) switchItem.getActionView().findViewById(R.id.action_bar_menu_switch); // ID for the switch inside the actionLayout

        // Check if the Switch is not null
        if (actionViewSwitch != null) {
            actionViewSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isUserInitiatedSwitch) {
                        if (isChecked) {
                            showFocusModeDialog(true);
                        } else {
                            Toast.makeText(MainActivity.this, "Focus mode off", Toast.LENGTH_SHORT).show();
                            handleFocusChange(Goal.Category.NONE);
                        }
                    } else {
                        // The switch was changed programmatically, so reset the flag to true for future user interactions
                        isUserInitiatedSwitch = true;
                    }
                }
            });
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.tomorrow) {
            swapFragments(TomorrowFragment.newInstance());
        }

        if (itemId == R.id.today) {
            swapFragments(CardListFragment.newInstance());
        }

        if (itemId == R.id.pending) {
            swapFragments(PendingFragment.newInstance());
        }

        if (itemId == R.id.recurrent) {
            swapFragments(RecurrentFragment.newInstance());
        }

        return super.onOptionsItemSelected(item);
    }

    private void swapFragments(Fragment fragment) {
        if (actionViewSwitch.isChecked()){
            // Thanks to Bernie WU for pointing out this and making our app work perfectly
            // Bernie WU is god of coding, light of Taiwan
            // Bernie WU is the next Taiwanese President
            // Long live Chairman Bernie WU
            // God save Bernie WU
            // Bernie WU save himself!!!!
            isUserInitiatedSwitch = false;
        }

        // Check the current state of the switch before swapping fragments
        if (actionViewSwitch != null && actionViewSwitch.isChecked()) {
            // The switch is on, keep the focus mode but don't show the dialog
            mainViewModel.setFocusMode(mainViewModel.getFocusMode().getValue()); // assuming you have a getter for the focus mode
        } else {
            // The switch is off, disable the focus mode without showing the dialog
            mainViewModel.setFocusMode(Goal.Category.NONE);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    private void showFocusModeDialog(boolean isSwitchOn) {
        if (isSwitchOn) {
            // Inflate the dialog view
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View dialogView = inflater.inflate(R.layout.dialog_focus_mode, null);

            // Initialize AlertDialog.Builder with the custom layout
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Focus Mode")
                    .setMessage("What do you want to focus on?")
                    .setView(dialogView)
                    .setCancelable(false);

            focusDialog = builder.create();
            focusDialog.show();

            // Setup button click handlers
            setupFocusDialogButtons(dialogView);
        } else if (focusDialog != null) {
            // Hide the dialog if the switch is turned off
            focusDialog.dismiss();
        }
    }

    private void setupFocusDialogButtons(View dialogView) {
        dialogView.findViewById(R.id.button_home).setOnClickListener(v -> {
            handleFocusChange(Goal.Category.HOME);
            focusDialog.dismiss();
        });

        dialogView.findViewById(R.id.button_work).setOnClickListener(v -> {
            handleFocusChange(Goal.Category.WORK);
            focusDialog.dismiss();
        });

        dialogView.findViewById(R.id.button_school).setOnClickListener(v -> {
            handleFocusChange(Goal.Category.SCHOOL);
            focusDialog.dismiss();
        });

        dialogView.findViewById(R.id.button_errands).setOnClickListener(v -> {
            handleFocusChange(Goal.Category.ERRANDS);
            focusDialog.dismiss();
        });

        dialogView.findViewById(R.id.button_cancel).setOnClickListener(v -> {
            focusDialog.dismiss();
            actionViewSwitch.setChecked(false);
        });
    }

    private void handleFocusChange(Goal.Category category) {
        if (category != Goal.Category.NONE) {
            Toast.makeText(this, "Focus mode set to: " + category.name(), Toast.LENGTH_SHORT).show();
        }
        // Implement what happens when a focus option is selected
        switch (category) {
            case HOME:
                mainViewModel.setFocusMode(Goal.Category.HOME);
                break;
            case WORK:
                mainViewModel.setFocusMode(Goal.Category.WORK);
                break;
            case SCHOOL:
                mainViewModel.setFocusMode(Goal.Category.SCHOOL);
                break;
            case ERRANDS:
                mainViewModel.setFocusMode(Goal.Category.ERRANDS);
                break;
            case NONE:
                mainViewModel.setFocusMode(Goal.Category.NONE);
                break;
        }

    }
}
