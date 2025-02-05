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
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import dk.team.playbits4all.R;

public class GamesActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private NfcAdapter nfcAdapter;
    private TextView textView;
    private View rootView;
    private ImageView imgView;

    private MediaPlayer mediaPlayer;
    private boolean isAction = false;
    private String[] numbs = {"codeK","4", "6", "6", "1", "3", "9"};

    private String[] odd = {"codeL","7", "4", "9", "9", "2"};

    private String[] food = {"codeA","forward", "forward", "right", "forward", "forward", "forward","left","forward"};
    private String[] sky = {"codeB","forward", "left", "forward", "right","forward","left", "forward","right","forward","left", "forward","right","forward","left", "forward"};
    private String[] fox = {"codeC","forward","forward", "right","forward","right","forward","forward","left", "forward","forward","left", "forward","forward","left","forward","right","forward","left","forward","forward"};

    private String[] tea = {"codeD","forward", "forward","forward","left","forward","forward","forward","right","forward","forward","forward","left","forward","left","forward","right","forward","right","forward"};

    private String[] direction = {"codeE","forward", "right", "backward", "right", "right","left"};

    private String[] number = {"codeI","3", "1", "5", "6", "2", "9"};
    private String[] mix = {"codeG","3", "4", "red", "green", "4","pink"};
    private String[] symbol = {"codeH","green", "green", "backward", "pink", "3","9"};

    private String[] animals ={"codeF","forward","forward","forward","forward","left","forward"};

    private String[] color = {"codeJ","green", "yellow", "green", "green", "yellow","blue"};
    private int currentStep = 0;

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Games");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);

        textView = findViewById(R.id.textviewer);
        imgView = findViewById(R.id.imgView);
        rootView = findViewById(android.R.id.content);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
        }
        textToSpeech = new TextToSpeech(this, this);
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

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, GamesActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            readFromNFC(intent);
        }
    }

    private void readFromNFC(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            Toast.makeText(this, "NFC tag is not NDEF formatted.", Toast.LENGTH_LONG).show();
            return;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();

        for (NdefRecord record : records) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && java.util.Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;
                    String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                    handleActions(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleActions(String text) {
        if (isAction) {
            return;
        }

        String display = text;
        int bgColor = 0;
        int imageResId = 0;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        boolean isCorrectSequence = false;


        if (text.equals("codeK") || text.equals("codeL") || text.equals("codeJ") || text.equals("codeI") || text.equals("codeA") || text.equals("codeB") || text.equals("codeC") || text.equals("codeD") || text.equals("codeE") || text.equals("codeF") || text.equals("codeG") || text.equals("codeH")) {
            currentStep = 1;
            isCorrectSequence = true;
        } else {
            if (currentStep < color.length && text.equals(color[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < numbs.length && text.equals(numbs[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < odd.length && text.equals(odd[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < number.length && text.equals(number[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < symbol.length && text.equals(symbol[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < mix.length && text.equals(mix[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < direction.length && text.equals(direction[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < tea.length && text.equals(tea[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < fox.length && text.equals(fox[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < sky.length && text.equals(sky[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else if (currentStep < animals.length && text.equals(animals[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            }else if (currentStep < food.length && text.equals(food[currentStep])) {
                currentStep++;
                isCorrectSequence = true;
            } else {
                currentStep = 0;
                display = "Incorrect sequence Please start again.";
            }
        }

        if (isCorrectSequence) {

            if (currentStep == color.length || currentStep == numbs.length || currentStep == animals.length || currentStep == tea.length || currentStep == fox.length || currentStep == sky.length || currentStep == odd.length || currentStep == number.length ||
                    currentStep == symbol.length || currentStep == mix.length || currentStep == direction.length || currentStep == food.length) {
                display = "Congratulations..! Amazing";
                isAction = true;
                speakText(display);
            } else {
                display = "Congratulations..! Learn to Code row" + currentStep  ;
                speakText(display);
            }
        }

        String finalDisplay = display;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAction = false;
                textView.setText(finalDisplay);
                rootView.setBackgroundColor(bgColor);

                if (imageResId != 0) {
                    imgView.setImageResource(imageResId);
                }
            }
        }, 1000);
    }


    private void speakText(String text) {
        if (textToSpeech != null && !textToSpeech.isSpeaking()) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentStep", currentStep);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentStep = savedInstanceState.getInt("currentStep");
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.setLanguage(Locale.US);
        } else {
            Toast.makeText(this, "Text-to-Speech failed.", Toast.LENGTH_SHORT).show();
        }
    }
}

