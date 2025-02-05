package dk.team.playbits4all.Normal_Mode;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dk.team.playbits4all.R;

public class YesOrNoActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView text5;
    private ImageView imgView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yes_or_no2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Scan Yes or No");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);



        text5 = findViewById(R.id.textviewer);
        imgView = findViewById(R.id.imgView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
        }

        String question = getIntent().getStringExtra("question");
        boolean answer = getIntent().getBooleanExtra("answer", false);

        text5.setText(question);
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
        Intent intent = new Intent(this, dk.team.playbits4all.Normal_Mode.YesOrNoActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
                    if ("yes".equalsIgnoreCase(text) || "no".equalsIgnoreCase(text)) {
                        handleNfcScan(text);
                    } else {
                        showToast("Invalid NFC tag result");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleNfcScan(String text) {
        boolean scannedAnswer = "yes".equalsIgnoreCase(text);

        boolean expectedAnswer = getIntent().getBooleanExtra("answer", false);

        if (scannedAnswer == expectedAnswer) {
            displayResult(true);
        } else {
            displayResult(false);
        }
    }

    private void displayResult(boolean isCorrect) {
        String resultText;
        int audio;
        int image;

        if (isCorrect) {
            resultText = "Correct!";
            audio = R.raw.crt;
            image = R.drawable.crt;
        } else {
            resultText = "Incorrect!";
            audio = R.raw.wrong;
            image = R.drawable.wrg;
        }

        text5.setText(resultText);
        if (audio!= 0) {
            mediaPlayer = MediaPlayer.create(this, audio);
            mediaPlayer.start();
        }
        if (image != 0) {
            imgView.setImageResource(image);
            imgView.setVisibility(View.VISIBLE);
        } else {
            imgView.setVisibility(View.GONE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}