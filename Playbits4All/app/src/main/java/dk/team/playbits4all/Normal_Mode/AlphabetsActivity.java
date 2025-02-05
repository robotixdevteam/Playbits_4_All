package dk.team.playbits4all.Normal_Mode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.team.playbits4all.R;

public class AlphabetsActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView text5;
    private View rootView;
    private ImageView imgView;

    private MediaPlayer mediaPlayer;
    private Map<String, Integer> letterStates = new HashMap<>();
    private boolean isAction = false;
    private boolean isGameMode = false;
    private int gameModeCounter = 0;
    private boolean isFollowMode = false;


    private List<Character> randomLetterSequence = new ArrayList<>();
    private int currentRandomIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alphabets);

        text5 = findViewById(R.id.textviewer);
        text5.setVisibility(View.VISIBLE);
        imgView = findViewById(R.id.imgView);
        rootView = findViewById(android.R.id.content);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Alphabets");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
        }

        for (char c = 'a'; c <= 'z'; c++) {
            letterStates.put(String.valueOf(c), 0);
        }

        if (isGameMode) {
            startRandom();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            enableForegroundDispatch();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            disableForegroundDispatch();
        }
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            isAction = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, AlphabetsActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    private void disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            String scannedAlphabet = readFromNFC(intent);
            if (scannedAlphabet != null) {
                if (scannedAlphabet.matches("[a-z]")) {
                    handleAlphabetTag(scannedAlphabet);
                } else if (scannedAlphabet.equalsIgnoreCase("game")) {
                    handleGameTag();
                } else if (scannedAlphabet.equalsIgnoreCase("reset")) {
                    handleResetTag();
                } else if (scannedAlphabet.equalsIgnoreCase("follow")) {
                    handleFollowTag();
                } else {
                    Toast.makeText(this, "Unsupported Tag: " + scannedAlphabet, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void handleAlphabetTag(String scannedAlphabet) {
        if (isFollowMode) {
            handleCorrectLetter(scannedAlphabet.charAt(0));
        } else if (isGameMode) {
            handleGameMode(scannedAlphabet);
        } else {
            handleActions(scannedAlphabet);
        }
    }

    private void handleGameTag() {
        isGameMode = true;
        gameModeCounter = 0;
        isFollowMode = false;
        Toast.makeText(this, "Play Hello English Game", Toast.LENGTH_SHORT).show();
    }

    private void handleResetTag() {
        resetGameState();
        Toast.makeText(this, "Game reset", Toast.LENGTH_SHORT).show();
    }

    private void handleFollowTag() {
        isGameMode = false;
        isFollowMode = true;
        displayRandomLetter();
    }

    private void startRandom() {
        randomLetterSequence = generateRandom();
        displayNextFollowLetter();
    }

    private List<Character> generateRandom() {
        List<Character> alphabetList = new ArrayList<>();
        for (char c = 'a'; c <= 'z'; c++) {
            alphabetList.add(c);
        }

        Collections.shuffle(alphabetList);

        return alphabetList;
    }

    private void displayNextFollowLetter() {
        if (currentRandomIndex < randomLetterSequence.size()) {
            char nextLetter = randomLetterSequence.get(currentRandomIndex);
            text5.setText("Follow this letter: " + nextLetter);
            currentRandomIndex++;
        } else {
            currentRandomIndex = 0;
            randomLetterSequence = generateRandom();
            displayNextFollowLetter();
        }
    }


    private void displayRandomLetter() {
        char randomLetter = generateRandom().get(0);
        text5.setText("Follow this letter: " + randomLetter);
        imgView.setImageResource(R.drawable.ques);
        isAction = false;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAction = true;
            }
        }, 10000); // 10 sec
    }

    private void handleCorrectLetter(char scannedLetter) {

        String displayedText = text5.getText().toString();
        char displayedLetter = displayedText.charAt(displayedText.length() - 1);
        if (scannedLetter == displayedLetter) {
            displayNextFollowLetter();
            mediaPlayer = MediaPlayer.create(this,R.raw.correct);
            mediaPlayer.start();
            imgView.setImageResource(R.drawable.yes_image);

        } else {
            mediaPlayer = MediaPlayer.create(this,R.raw.wrong);
            mediaPlayer.start();
            imgView.setImageResource(R.drawable.no_image);
            Toast.makeText(this, "Invalid Letter,Scan Display Letter : " + displayedLetter , Toast.LENGTH_SHORT).show();

        }
    }


    private void handleActions(String scannedAlphabet) {
        if (isAction) {
            return;
        }

        String display = scannedAlphabet;
        int bgColor = 0;
        int imageResId = 0;

        int state = letterStates.get(scannedAlphabet.toLowerCase());

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        switch (scannedAlphabet.toLowerCase()) {
            case "a":
                display = "A for Apple";
                mediaPlayer = MediaPlayer.create(this, R.raw.apple);
                imageResId = R.drawable.a_for_apple;
                mediaPlayer.start();
                break;
            case "b":
                display = "B for Ball";
                mediaPlayer = MediaPlayer.create(this, R.raw.ball);
                imageResId = R.drawable.b_for_ball;
                mediaPlayer.start();
                break;
            case "c":
                display = "C for Cat";
                mediaPlayer = MediaPlayer.create(this, R.raw.cat);
                imageResId = R.drawable.c_for_cat;
                mediaPlayer.start();
                break;
            case "d":
                display = "D for Dog";
                mediaPlayer = MediaPlayer.create(this, R.raw.dog);
                imageResId = R.drawable.d_for_dog;
                mediaPlayer.start();
                break;
            case "e":
                display = "E for Elephant";
                mediaPlayer = MediaPlayer.create(this, R.raw.elephant);
                imageResId = R.drawable.e_for_elephant;
                mediaPlayer.start();
                break;
            case "f":
                display = "F for Fish";
                mediaPlayer = MediaPlayer.create(this, R.raw.fish);
                imageResId = R.drawable.f_for_fish;
                mediaPlayer.start();
                break;
            case "g":
                display = "G for Goat";
                mediaPlayer = MediaPlayer.create(this, R.raw.goat);
                imageResId = R.drawable.g_for_goat;
                mediaPlayer.start();
                break;
            case "h":
                display = "H for Hat";
                mediaPlayer = MediaPlayer.create(this, R.raw.hat);
                imageResId = R.drawable.h_for_hat;
                mediaPlayer.start();
                break;
            case "i":
                display = "I for Ice-cream";
                mediaPlayer = MediaPlayer.create(this, R.raw.icecream);
                imageResId = R.drawable.i_for_icecream;
                mediaPlayer.start();
                break;
            case "j":
                display = "J for Juice";
                mediaPlayer = MediaPlayer.create(this, R.raw.juice);
                imageResId = R.drawable.j_for_juice;
                mediaPlayer.start();
                break;
            case "k":
                display = "K for King";
                mediaPlayer = MediaPlayer.create(this, R.raw.king);
                imageResId = R.drawable.k_for_king;
                mediaPlayer.start();
                break;
            case "l":
                display = "L for Lion";
                mediaPlayer = MediaPlayer.create(this, R.raw.lion);
                imageResId = R.drawable.l_for_lion;
                mediaPlayer.start();
                break;
            case "m":
                display = "M for Monkey";
                mediaPlayer = MediaPlayer.create(this, R.raw.monkey);
                imageResId = R.drawable.m_for_monkey;
                mediaPlayer.start();
                break;
            case "n":
                display = "N for Nest";
                mediaPlayer = MediaPlayer.create(this, R.raw.nest);
                imageResId = R.drawable.n_for_nest;
                mediaPlayer.start();
                break;
            case "o":
                display = "O for Octopus";
                mediaPlayer = MediaPlayer.create(this, R.raw.octopus);
                imageResId = R.drawable.o_for_octopus;
                mediaPlayer.start();
                break;
            case "p":
                display = "P for Pig";
                mediaPlayer = MediaPlayer.create(this, R.raw.pig);
                imageResId = R.drawable.p_for_pig;
                mediaPlayer.start();
                break;
            case "q":
                display = "Q for Queen";
                mediaPlayer = MediaPlayer.create(this, R.raw.queen);
                imageResId = R.drawable.q_for_queen;
                mediaPlayer.start();
                break;
            case "r":
                display = "R for Rabbit";
                mediaPlayer = MediaPlayer.create(this, R.raw.rabbit);
                imageResId = R.drawable.r_for_rabbit;
                mediaPlayer.start();
                break;
            case "s":
                display = "S for Sheep";
                mediaPlayer = MediaPlayer.create(this, R.raw.sheep);
                imageResId = R.drawable.s_for_sheep;
                mediaPlayer.start();
                break;
            case "t":
                display = "T for Tiger";
                mediaPlayer = MediaPlayer.create(this, R.raw.tiger);
                imageResId = R.drawable.t_for_tiger;
                mediaPlayer.start();
                break;
            case "u":
                display = "U for Umbrella";
                mediaPlayer = MediaPlayer.create(this, R.raw.umbrella);
                imageResId = R.drawable.u_for_umbrella;
                mediaPlayer.start();
                break;
            case "v":
                display = "V for Van";
                mediaPlayer = MediaPlayer.create(this, R.raw.van);
                imageResId = R.drawable.v_for_van;
                mediaPlayer.start();
                break;
            case "w":
                display = "W for Watch";
                mediaPlayer = MediaPlayer.create(this, R.raw.watch);
                imageResId = R.drawable.w_for_watch;
                mediaPlayer.start();
                break;
            case "x":
                display = "X for Xylophone";
                mediaPlayer = MediaPlayer.create(this, R.raw.xylophone);
                imageResId = R.drawable.x_for_xylophone;
                mediaPlayer.start();
                break;
            case "y":
                display = "Y for Yak";
                mediaPlayer = MediaPlayer.create(this, R.raw.yak);
                imageResId = R.drawable.y_for_yak;
                mediaPlayer.start();
                break;
            case "z":
                display = "Z for Zebra";
                mediaPlayer = MediaPlayer.create(this, R.raw.zebra);
                imageResId = R.drawable.z_for_zebra;
                mediaPlayer.start();
                break;
        }

        isAction = true;

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                    isAction = false;
                }
            });
        }

        text5.setText(display);
        rootView.setBackgroundColor(bgColor);

        if (imageResId != 0) {
            imgView.setImageResource(imageResId);
        }

        letterStates.put(scannedAlphabet.toLowerCase(), (state + 1) % 3);
    }

    private void handleGameMode(String scannedAlphabet) {
        if (isAction) {
            return;
        }

        int state = letterStates.get(scannedAlphabet.toLowerCase());

        switch (scannedAlphabet.toLowerCase()) {
            case "a":
            case "b":
            case "c":
            case "d":
            case "e":
            case "f":
            case "g":
            case "h":
            case "i":
            case "j":
            case "k":
            case "l":
            case "m":
            case "n":
            case "o":
            case "p":
            case "q":
            case "r":
            case "s":
            case "t":
            case "u":
            case "v":
            case "w":
            case "x":
            case "y":
            case "z":

                handleGameModeLetter(getGameModeDisplay(scannedAlphabet),
                        getGameModeSoundResource(scannedAlphabet, state),
                        getGameModeImageResource(scannedAlphabet, state));
                break;
        }
    }

    private String getGameModeDisplay(String scannedAlphabet) {
        switch (scannedAlphabet.toLowerCase()) {
            case "a":
                switch (gameModeCounter) {
                    case 0:
                        return "A-P-P-L-E -> Apple";
                    case 1:
                        return "A-N-T -> Ant";
                    case 2:
                        return "A-R-M -> ARM";
                }
                break;
            case "b":
                switch (gameModeCounter) {
                    case 0:
                        return "B-A-L-L -> Ball";
                    case 1:
                        return "B-O-A-T -> Boat";
                    case 2:
                        return "B-E-A-R -> Bear";
                }
                break;

            case "c":
                switch (gameModeCounter) {
                    case 0:
                        return "C-A-T -> Cat";
                    case 1:
                        return "C-A-R -> Car";
                    case 2:
                        return "C-O-W -> Cow";
                }
                break;
            case "d":
                switch (gameModeCounter) {
                    case 0:
                        return "D-O-G -> Dog";
                    case 1:
                        return "D-O-L-L -> Doll";
                    case 2:
                        return "D-U-C-K -> Duck";
                }
                break;
            case "e":
                switch (gameModeCounter) {
                    case 0:
                        return "E-L-E-P-H-A-N-T -> Elephant";
                    case 1:
                        return "E-A-R -> Ear";
                    case 2:
                        return "E-G-G -> Egg";
                }
                break;

            case "f":
                switch (gameModeCounter) {
                    case 0:
                        return "F-I-S-H -> Fish";
                    case 1:
                        return "F-A-N -> Fan";
                    case 2:
                        return "F-R-O-G -> Frog";
                }
                break;
            case "g":
                switch (gameModeCounter) {
                    case 0:
                        return "G-O-A-T -> Goat";
                    case 1:
                        return "G-L-A-S-S -> Glass";
                    case 2:
                        return "G-U-I-T-A-R -> Guitar";
                }
                break;
            case "h":
                switch (gameModeCounter) {
                    case 0:
                        return "H-O-R-S-E -> Horse";
                    case 1:
                        return "H-O-M-E -> Home";
                    case 2:
                        return "H-A-T -> Hat";
                }
                break;

            case "i":
                switch (gameModeCounter) {
                    case 0:
                        return "I-C-E-C-R-E-A-M -> Ice-cream";
                    case 1:
                        return "I-G-L-O-O -> Igloo";
                    case 2:
                        return "I-N-S-E-C-T -> Insect";
                }
                break;
            case "j":
                switch (gameModeCounter) {
                    case 0:
                        return "J-U-I-C-E -> Juice";
                    case 1:
                        return "J-A-M -> Jam";
                    case 2:
                        return "J-U-G -> Jug";
                }
                break;
            case "k":
                switch (gameModeCounter) {
                    case 0:
                        return "K-I-N-G -> King";
                    case 1:
                        return "K-I-T-E -> Kite";
                    case 2:
                        return "K-E-Y -> Key";
                }
                break;

            case "l":
                switch (gameModeCounter) {
                    case 0:
                        return "L-I-O-N -> Lion";
                    case 1:
                        return "L-E-G -> Leg";
                    case 2:
                        return "L-A-M-P -> Lamp";
                }
                break;
            case "m":
                switch (gameModeCounter) {
                    case 0:
                        return "M-O-N-K-E-Y -> Monkey";
                    case 1:
                        return "M-O-O-N -> Moon";
                    case 2:
                        return "M-U-G -> Mug";
                }
                break;
            case "n":
                switch (gameModeCounter) {
                    case 0:
                        return "N-E-S-T -> Nest";
                    case 1:
                        return "N-E-T -> Net";
                    case 2:
                        return "N-A-I-L -> Nail";
                }
                break;

            case "o":
                switch (gameModeCounter) {
                    case 0:
                        return "O-C-T-O-P-U-S -> Octopus";
                    case 1:
                        return "O-W-L -> Owl";
                    case 2:
                        return "O-N-I-O-N -> Onion";
                }
                break;
            case "p":
                switch (gameModeCounter) {
                    case 0:
                        return "P-I-G -> Pig";
                    case 1:
                        return "P-L-A-N-T -> Plant";
                    case 2:
                        return "P-E-N-C-I-L -> Pencil";
                }
                break;
            case "q":
                switch (gameModeCounter) {
                    case 0:
                        return "Q-U-E-E-N -> Queen";
                    case 1:
                        return "Q-U-E-U-E -> Queue";
                    case 2:
                        return "Q-U-A-I-L -> Quail";
                }
                break;

            case "r":
                switch (gameModeCounter) {
                    case 0:
                        return "R-A-B-B-I-T -> Rabbit";
                    case 1:
                        return "R-A-I-N -> Rain";
                    case 2:
                        return "R-O-S-E -> Rose";
                }
                break;
            case "s":
                switch (gameModeCounter) {
                    case 0:
                        return "S-H-E-E-P -> Sheep";
                    case 1:
                        return "S-U-N -> Sun";
                    case 2:
                        return "S-H-I-P -> Ship";
                }
                break;
            case "t":
                switch (gameModeCounter) {
                    case 0:
                        return "T-I-G-E-R -> Tiger";
                    case 1:
                        return "T-O-Y -> Toy";
                    case 2:
                        return "T-R-E-E -> Tree";
                }
                break;

            case "u":
                switch (gameModeCounter) {
                    case 0:
                        return "U-M-B-R-E-L-L-A -> Umbrella";
                    case 1:
                        return "U-N-I-C-O-R-N -> Unicorn";
                    case 2:
                        return "U-R-N -> Urn";
                }
                break;
            case "v":
                switch (gameModeCounter) {
                    case 0:
                        return "V-A-N -> Van";
                    case 1:
                        return "V-A-S-E -> Vase";
                    case 2:
                        return "V-I-O-L-I-N -> Violin";
                }
                break;
            case "w":
                switch (gameModeCounter) {
                    case 0:
                        return "W-A-T-C-H -> Watch";
                    case 1:
                        return "W-H-A-L-E -> Whale";
                    case 2:
                        return "W-A-N-D -> Wand";
                }
                break;

            case "x":
                switch (gameModeCounter) {
                    case 0:
                        return "X-Y-L-O-P-H-O-N-E -> Xylophone";
                    case 1:
                        return "X-M-A-S -> Xmas";
                    case 2:
                        return "X-R-A-Y -> Xray";
                }
                break;
            case "y":
                switch (gameModeCounter) {
                    case 0:
                        return "Y-A-K -> Yak";
                    case 1:
                        return "Y-A-W-N -> Yawn";
                    case 2:
                        return "Y-A-C-H-T-> Yacht";
                }
                break;
            case "z":
                switch (gameModeCounter) {
                    case 0:
                        return "Z-E-B-R-A -> Zebra";
                    case 1:
                        return "Z-O-O -> Zoo";
                    case 2:
                        return "Z-E-R-O -> Zero";
                }
                break;
        }
        return "";
    }

    private void resetGameState() {
        isGameMode = false;
        isFollowMode = false;
        gameModeCounter = 0;
        currentRandomIndex = 0;
        randomLetterSequence.clear();
        isAction = false;


        for (char c = 'a'; c <= 'z'; c++) {
            letterStates.put(String.valueOf(c), 0);
        }

        text5.setText("");
        imgView.setImageResource(0);
    }

    private int getGameModeSoundResource(String scannedAlphabet, int state) {
        switch (scannedAlphabet.toLowerCase()) {
            case "a":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.a;
                    case 1:
                        return R.raw.ant;
                    case 2:
                        return R.raw.arm;
                }
                break;
            case "b":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.b;
                    case 1:
                        return R.raw.boat;
                    case 2:
                        return R.raw.bear;
                }
                break;
            case "c":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.c;
                    case 1:
                        return R.raw.car;
                    case 2:
                        return R.raw.cow;
                }
                break;
            case "d":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.d;
                    case 1:
                        return R.raw.doll;
                    case 2:
                        return R.raw.duck;
                }
                break;
            case "e":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.e;
                    case 1:
                        return R.raw.ear;
                    case 2:
                        return R.raw.egg;
                }
                break;
            case "f":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.f;
                    case 1:
                        return R.raw.fan;
                    case 2:
                        return R.raw.frog;
                }
                break;
            case "g":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.g;
                    case 1:
                        return R.raw.glass;
                    case 2:
                        return R.raw.guit;
                }
                break;
            case "h":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.horse;
                    case 1:
                        return R.raw.home;
                    case 2:
                        return R.raw.hatlet;
                }
                break;
            case "i":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.i;
                    case 1:
                        return R.raw.igloo;
                    case 2:
                        return R.raw.insect;
                }
                break;
            case "j":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.j;
                    case 1:
                        return R.raw.jam;
                    case 2:
                        return R.raw.jug;
                }
                break;
            case "k":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.k;
                    case 1:
                        return R.raw.kite;
                    case 2:
                        return R.raw.key;
                }
                break;
            case "l":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.l;
                    case 1:
                        return R.raw.leg;
                    case 2:
                        return R.raw.lamp;
                }
                break;
            case "m":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.m;
                    case 1:
                        return R.raw.moon;
                    case 2:
                        return R.raw.mug;
                }
                break;
            case "n":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.n;
                    case 1:
                        return R.raw.net;
                    case 2:
                        return R.raw.nail;
                }
                break;
            case "o":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.o;
                    case 1:
                        return R.raw.owl;
                    case 2:
                        return R.raw.onion;
                }
                break;
            case "p":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.p;
                    case 1:
                        return R.raw.plant;
                    case 2:
                        return R.raw.pencil;
                }
                break;
            case "q":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.q;
                    case 1:
                        return R.raw.queue;
                    case 2:
                        return R.raw.quail;
                }
                break;
            case "r":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.r;
                    case 1:
                        return R.raw.rain;
                    case 2:
                        return R.raw.rose;
                }
                break;
            case "s":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.s;
                    case 1:
                        return R.raw.sun;
                    case 2:
                        return R.raw.ship;
                }
                break;
            case "t":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.t;
                    case 1:
                        return R.raw.toy;
                    case 2:
                        return R.raw.tree;
                }
                break;
            case "u":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.u;
                    case 1:
                        return R.raw.unicorn;
                    case 2:
                        return R.raw.urn;
                }
                break;
            case "v":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.v;
                    case 1:
                        return R.raw.vase;
                    case 2:
                        return R.raw.violin;
                }
                break;
            case "w":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.w;
                    case 1:
                        return R.raw.whale;
                    case 2:
                        return R.raw.wand;
                }
                break;
            case "x":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.x;
                    case 1:
                        return R.raw.xmas;
                    case 2:
                        return R.raw.xray;
                }
                break;
            case "y":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.y;
                    case 1:
                        return R.raw.yawn;
                    case 2:
                        return R.raw.yacht;
                }
                break;
            case "z":
                switch (gameModeCounter) {
                    case 0:
                        return R.raw.z;
                    case 1:
                        return R.raw.zoo;
                    case 2:
                        return R.raw.zero;
                }
                break;



        }
        return 0;
    }

    private int getGameModeImageResource(String scannedAlphabet, int state) {
        switch (scannedAlphabet.toLowerCase()) {
            case "a":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.apple;
                    case 1:
                        return R.drawable.ant;
                    case 2:
                        return R.drawable.arm;
                }
                break;
            case "b":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.ball;
                    case 1:
                        return R.drawable.boat;
                    case 2:
                        return R.drawable.bear;
                }
                break;
            case "c":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.cat;
                    case 1:
                        return R.drawable.car;
                    case 2:
                        return R.drawable.cow;
                }
                break;
            case "d":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.dog;
                    case 1:
                        return R.drawable.doll;
                    case 2:
                        return R.drawable.duck;
                }
                break;
            case "e":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.elephant;
                    case 1:
                        return R.drawable.ear;
                    case 2:
                        return R.drawable.egg;
                }
                break;
            case "f":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.fish;
                    case 1:
                        return R.drawable.fan;
                    case 2:
                        return R.drawable.frog;
                }
                break;
            case "g":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.goat;
                    case 1:
                        return R.drawable.glass;
                    case 2:
                        return R.drawable.guitar_bg;
                }
                break;
            case "h":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.horse;
                    case 1:
                        return R.drawable.home;
                    case 2:
                        return R.drawable.hat;
                }
                break;
            case "i":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.icecream;
                    case 1:
                        return R.drawable.igloo;
                    case 2:
                        return R.drawable.insect;
                }
                break;
            case "j":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.juice;
                    case 1:
                        return R.drawable.jam;
                    case 2:
                        return R.drawable.jug;
                }
                break;
            case "k":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.king;
                    case 1:
                        return R.drawable.kite;
                    case 2:
                        return R.drawable.key;
                }
                break;
            case "l":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.lion;
                    case 1:
                        return R.drawable.leg;
                    case 2:
                        return R.drawable.lamp;
                }
                break;
            case "m":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.monkey;
                    case 1:
                        return R.drawable.moon;
                    case 2:
                        return R.drawable.mug;
                }
                break;
            case "n":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.nest;
                    case 1:
                        return R.drawable.net;
                    case 2:
                        return R.drawable.nail;
                }
                break;
            case "o":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.octopus;
                    case 1:
                        return R.drawable.owl;
                    case 2:
                        return R.drawable.onion;
                }
                break;
            case "p":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.pig;
                    case 1:
                        return R.drawable.plant;
                    case 2:
                        return R.drawable.pencil;
                }
                break;
            case "q":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.queen;
                    case 1:
                        return R.drawable.queue;
                    case 2:
                        return R.drawable.quail;
                }
                break;
            case "r":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.rabbit;
                    case 1:
                        return R.drawable.rain;
                    case 2:
                        return R.drawable.rose;
                }
                break;
            case "s":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.sheep;
                    case 1:
                        return R.drawable.sun;
                    case 2:
                        return R.drawable.ship;
                }
                break;
            case "t":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.tiger;
                    case 1:
                        return R.drawable.toy;
                    case 2:
                        return R.drawable.tree;
                }
                break;
            case "u":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.umbrella;
                    case 1:
                        return R.drawable.unicorn;
                    case 2:
                        return R.drawable.urn;
                }
                break;
            case "v":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.van;
                    case 1:
                        return R.drawable.vase;
                    case 2:
                        return R.drawable.violin;
                }
                break;
            case "w":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.watch;
                    case 1:
                        return R.drawable.whale;
                    case 2:
                        return R.drawable.wand;
                }
                break;
            case "x":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.xylo_bg;
                    case 1:
                        return R.drawable.xmas;
                    case 2:
                        return R.drawable.xray;
                }
                break;
            case "y":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.yak;
                    case 1:
                        return R.drawable.yawn;
                    case 2:
                        return R.drawable.yacht;
                }
                break;
            case "z":
                switch (gameModeCounter) {
                    case 0:
                        return R.drawable.zebra;
                    case 1:
                        return R.drawable.zoo;
                    case 2:
                        return R.drawable.zero;
                }
                break;


        }
        return 0;
    }

    private void handleGameModeLetter(String display, int soundResource, int imageResource) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, soundResource);
        imgView.setImageResource(imageResource);
        mediaPlayer.start();

        isAction = true;

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                    isAction = false;

                    gameModeCounter = (gameModeCounter + 1) % 3;


                }
            });
        }

        text5.setText(display);
    }

    private String readFromNFC(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            Toast.makeText(this, "NFC tag is not NDEF formatted.", Toast.LENGTH_LONG).show();
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();

        for (NdefRecord record : records) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && java.util.Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;
                    return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}

