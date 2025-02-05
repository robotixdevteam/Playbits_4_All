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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dk.team.playbits4all.R;

public class MathFunction extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView text5;
    private View rootView;
    private ImageView imgView;

    private MediaPlayer mediaPlayer;

    private int firstValue = 0;
    private int secondValue = 0;
    private String operator = "";
    private boolean isAction = false;
    private int musicState = 0;
    private boolean isMusicMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_math_function);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Math Function");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);

        text5 = findViewById(R.id.value5);
        rootView = findViewById(android.R.id.content);


        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, MathFunction.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
        } else {
            showToast("Unsupported tag detected");
        }
    }

    private void readFromNFC(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            showToast("NFC tag is not NDEF formatted.");
            return;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage == null) {
            showToast("Empty or unsupported NFC tag.");
            return;
        }

        NdefRecord[] records = ndefMessage.getRecords();

        if (records == null || records.length == 0) {
            showToast("Empty or unsupported NFC tag.");
            return;
        }

        for (NdefRecord record : records) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && java.util.Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0063;
                    String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

                    handleActions(text); // Remove the isMusicMode check
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showToast("Unsupported data in NFC tag.");
            }
        }
    }

    private void handleActions(String text) {
        if (isSupportedTag(text)) {
            String display = text;
            int bgColor = 0;
            int imageResId = 0;

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Directly set the first or second value or operator
            if (text.matches("[1-9]|10")) { // Check for numeric values
                if (operator.isEmpty()) {
                    // First value is being set (concatenation logic)
                    firstValue = Integer.parseInt((firstValue) + text);
                    display = "First Value: " + firstValue; // Update display with first value
                } else {
                    // Second value is being set (concatenation logic)
                    secondValue = Integer.parseInt((secondValue) + text);
                    display = "Second Value: " + secondValue; // Update display with second value
                }
            } else if ("result".equalsIgnoreCase(text)) { // Display the result
                int result = calculate(); // Perform calculation
                String resultDisplay = "Result: " + firstValue + " " + operator + " " + secondValue + " = " + result; // Show the result
                text5.setText(resultDisplay); // Update the TextView with result
                resetValues(); // Reset values for the next calculation
                return; // Exit to prevent further execution
            } else if ("reset".equalsIgnoreCase(text)) { // Reset mode
                resetValues();
                display = "Values Reset"; // Update display
            } else {
                operator = text; // Assume text is an operator
                display = "Operator: " + text; // Update display
            }

            text5.setText(display); // Update the TextView
            rootView.setBackgroundColor(bgColor); // Set background color if needed

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

            if (imageResId != 0) {
                imgView.setImageResource(imageResId);
            }
        } else {
            showToast("Unsupported tag detected: " + text);
        }
    }

    private int calculate() {
        int result = 0;
        switch (operator) {
            case "+":
                result = firstValue + secondValue;
                break;
            case "-":
                result = firstValue - secondValue;
                break;
            case "*":
                result = firstValue * secondValue;
                break;
            case "/":
                if (secondValue != 0) {
                    result = firstValue / secondValue;
                } else {
                    Toast.makeText(this, "Division by zero is not allowed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return result;
    }

    private void resetValues() {
        firstValue = 0;
        secondValue = 0;
        operator = "";
    }

    private boolean isSupportedTag(String text) {
        return text.matches("[1-9]|10") || "+".equalsIgnoreCase(text) || "-".equalsIgnoreCase(text) || "result".equalsIgnoreCase(text) || "*".equalsIgnoreCase(text) || "/".equalsIgnoreCase(text);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

