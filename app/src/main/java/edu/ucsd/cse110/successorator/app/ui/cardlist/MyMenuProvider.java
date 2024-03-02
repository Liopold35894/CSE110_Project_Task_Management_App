package edu.ucsd.cse110.successorator.app.ui.cardlist;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;

import edu.ucsd.cse110.successorator.app.R;

public class MyMenuProvider implements MenuProvider {
    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        return true;
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.action_bar, menu);
    }

}
