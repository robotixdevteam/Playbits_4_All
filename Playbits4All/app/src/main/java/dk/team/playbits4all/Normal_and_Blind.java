package dk.team.playbits4all;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class Normal_and_Blind extends AppCompatActivity {

    private Button Normal,Blind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_and_blind);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Home");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);

        Normal = findViewById(R.id.normal_one);
        Blind = findViewById(R.id.blind_two);

        Normal.setOnClickListener(view -> {
            Intent intent = new Intent(Normal_and_Blind.this,MainActivity.class);
            startActivity(intent);
        });

    }
}