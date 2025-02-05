package dk.team.playbits4all;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dk.team.playbits4all.Normal_Mode.GamesActivity;
import dk.team.playbits4all.Normal_Mode.MagicBlocks;
import dk.team.playbits4all.Normal_Mode.Mastermind;

public class Option_Games extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option_games);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] games = {"Taco Games", "Mastermind", "Magic Blocks"};
        ListView gamesListView = findViewById(R.id.games_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, games);
        gamesListView.setAdapter(adapter);


        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(Option_Games.this, GamesActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(Option_Games.this, Mastermind.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(Option_Games.this, MagicBlocks.class);
                    startActivity(intent);
                }
            }
        });
    }
}
