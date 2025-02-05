package dk.team.playbits4all;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import dk.team.playbits4all.Normal_Mode.AlphabetsActivity;
import dk.team.playbits4all.Normal_Mode.ColorsActivity;
import dk.team.playbits4all.Normal_Mode.GamesActivity;
import dk.team.playbits4all.Normal_Mode.Mastermind;
import dk.team.playbits4all.Normal_Mode.MusicActivity;
import dk.team.playbits4all.Normal_Mode.NumbersActivity;
import dk.team.playbits4all.Normal_Mode.SmartMathActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Categories");

        // Back button click listener
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

        // Settings button click listener
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        ImageButton colorsButton, musicButton, alphabetsButton, numbersButton, smartMathButton, gamesButton;

        colorsButton = findViewById(R.id.colorsButton);
        musicButton = findViewById(R.id.musicButton);
        alphabetsButton = findViewById(R.id.alphabetsButton);
        numbersButton = findViewById(R.id.numbersButton);
        smartMathButton = findViewById(R.id.smartMathButton);

        gamesButton = findViewById(R.id.gamesButton);

        colorsButton.setOnClickListener(v -> startColorsActivity());
        musicButton.setOnClickListener(v -> startMusicActivity());
        alphabetsButton.setOnClickListener(v -> startAlphabetsActivity());
        numbersButton.setOnClickListener(v -> startNumbersActivity());
        smartMathButton.setOnClickListener(v -> startSmartMathActivity());
        gamesButton.setOnClickListener(v -> startGamesActivity());
    }


    // Methods for starting various activities
    private void startColorsActivity() {
        Intent intent = new Intent(this, ColorsActivity.class);
        startActivity(intent);
    }

    private void startMusicActivity() {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivity(intent);
    }

    private void startAlphabetsActivity() {
        Intent intent = new Intent(this, AlphabetsActivity.class);
        startActivity(intent);
    }

    private void startNumbersActivity() {
        Intent intent = new Intent(this, NumbersActivity.class);
        startActivity(intent);
    }

    private void startSmartMathActivity() {
        Intent intent = new Intent(this, SmartMathActivity.class);
        startActivity(intent);
    }

    private void startGamesActivity() {
        Intent intent = new Intent(this, Option_Games.class);
        startActivity(intent);
    }

}
