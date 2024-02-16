package edu.ucsd.cse110.secards.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;

import edu.ucsd.cse110.secards.app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        var calendar = Calendar.getInstance().getTime();
        var dateFormat = DateFormat.getDateInstance().format(calendar);
        setTitle(dateFormat);

        this.view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
    }


}
