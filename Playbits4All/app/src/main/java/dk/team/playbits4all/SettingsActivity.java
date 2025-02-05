package dk.team.playbits4all;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import dk.team.playbits4all.Normal_Mode.AlphabetsActivity;
import dk.team.playbits4all.Normal_Mode.GamesActivity;
import dk.team.playbits4all.Normal_Mode.MathFunction;
import dk.team.playbits4all.Normal_Mode.MusicActivity;
import dk.team.playbits4all.Normal_Mode.NumbersActivity;
import dk.team.playbits4all.Normal_Mode.SmartMathActivity;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        TextView selectLang = findViewById(R.id.text_languages);
        TextView mathFunc = findViewById(R.id.math);
        selectLang.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, Question_Tags.class);
            startActivity(intent);
        });
        mathFunc.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MathFunction.class);
            startActivity(intent);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Settings");

        // Back button click listener
        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> onBackPressed());

    }
}