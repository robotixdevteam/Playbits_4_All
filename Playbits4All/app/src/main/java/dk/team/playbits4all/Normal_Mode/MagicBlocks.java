package dk.team.playbits4all.Normal_Mode;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dk.team.playbits4all.R;

public class MagicBlocks extends AppCompatActivity {

    // Define arrays to store references to the CheckBox views for each box
    private CheckBox[][] checkBoxes = new CheckBox[5][2]; // Yes, No for each box
    private NfcAdapter nfcAdapter;
    private int currentBox = 0;
    private TextView boxTextView;

    // Store the results based on user/NFC input for each box
    private boolean[] userResponses = new boolean[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magic_blocks);

        // Initialize checkboxes for each box
        checkBoxes[0][0] = findViewById(R.id.checkBox1Yes);
        checkBoxes[0][1] = findViewById(R.id.checkBox1No);
        checkBoxes[1][0] = findViewById(R.id.checkBox2Yes);
        checkBoxes[1][1] = findViewById(R.id.checkBox2No);
        checkBoxes[2][0] = findViewById(R.id.checkBox3Yes);
        checkBoxes[2][1] = findViewById(R.id.checkBox3No);
        checkBoxes[3][0] = findViewById(R.id.checkBox4Yes);
        checkBoxes[3][1] = findViewById(R.id.checkBox4No);
        checkBoxes[4][0] = findViewById(R.id.checkBox5Yes);
        checkBoxes[4][1] = findViewById(R.id.checkBox5No);

        // Disable checkboxes initially
        for (int i = 0; i < 5; i++) {
            checkBoxes[i][0].setEnabled(false); // Disable "Yes"
            checkBoxes[i][1].setEnabled(false); // Disable "No"
        }

        boxTextView = findViewById(R.id.boxTextView);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
        }

        // Show the first box
        showCurrentBox();
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
        Intent intent = new Intent(this, MagicBlocks.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

        boolean validTag = false; // Flag to track if a valid tag is detected

        for (NdefRecord record : records) {
            if (record.getTnf() == NdefRecord.TNF_WELL_KNOWN && java.util.Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                try {
                    byte[] payload = record.getPayload();
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    int languageCodeLength = payload[0] & 0x3F;
                    String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

                    // Process the NFC input and check for valid "yes" or "no"
                    if (text.equalsIgnoreCase("yes")) {
                        checkBoxes[currentBox][0].setChecked(true);
                        checkBoxes[currentBox][1].setChecked(false);
                        userResponses[currentBox] = true; // Yes
                        validTag = true;

                        // Enable "Yes" and disable "No"
                        checkBoxes[currentBox][0].setEnabled(true); // Keep "Yes" enabled
                        checkBoxes[currentBox][1].setEnabled(false); // Disable "No"
                    } else if (text.equalsIgnoreCase("no")) {
                        checkBoxes[currentBox][0].setChecked(false);
                        checkBoxes[currentBox][1].setChecked(true);
                        userResponses[currentBox] = false; // No
                        validTag = true;

                        // Enable "No" and disable "Yes"
                        checkBoxes[currentBox][0].setEnabled(false); // Disable "Yes"
                        checkBoxes[currentBox][1].setEnabled(true); // Keep "No" enabled
                    } else {
                        // Invalid tag content (neither "yes" nor "no")
                        validTag = false;
                    }

                    if (validTag) {
                        // Move to the next box after valid NFC input
                        moveToNextBox();
                    } else {
                        // If invalid tag, show a toast message
                        Toast.makeText(this, "Invalid tag. Please scan a 'yes' or 'no' tag.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void moveToNextBox() {
        if (currentBox < 4) {
            currentBox++;
            showCurrentBox();
        } else {
            showResult(); // All boxes completed, show the result
        }
    }

    private void showCurrentBox() {
        boxTextView.setText("Box " + (currentBox + 1) + ": Is your number in this box?");

        // Reset checkboxes for the current box
        checkBoxes[currentBox][0].setChecked(false);
        checkBoxes[currentBox][1].setChecked(false);
        checkBoxes[currentBox][0].setEnabled(false); // Disable "Yes"
        checkBoxes[currentBox][1].setEnabled(false); // Disable "No"
    }

    private void showResult() {
        int result = 0;
        for (int i = 0; i < 5; i++) {
            if (userResponses[i]) {
                result += Math.pow(2, i);
            }
        }

        // Show the result in a Toast
        Toast.makeText(MagicBlocks.this, "The number in your mind is: " + result, Toast.LENGTH_SHORT).show();

        // Display the result in the boxTextView
        boxTextView.setText("The number in your mind is: " + result);

        // Define the win condition, for example, if result equals 31 (5 bits all true)
        boolean won = (result == 31); // Change this condition based on your game's logic

        // Show the result and the appropriate dialog
        showEndGameDialog(won); // Pass the win/loss state to the dialog
    }


    private void showEndGameDialog(boolean won) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_magic, null);

        TextView titleView = dialogView.findViewById(R.id.title);
        TextView messageView = dialogView.findViewById(R.id.message);

        // Set different title and image based on whether the user won or lost
        titleView.setText(won ? "You Won!" : "Game Over");
        messageView.setText("Do you want to play again or exit?");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();

        Button playButton = dialogView.findViewById(R.id.btn_play);
        playButton.setOnClickListener(v -> {
            resetGame();
            dialog.dismiss();
        });

        Button exitButton = dialogView.findViewById(R.id.btn_exit);
        exitButton.setOnClickListener(v -> {
            finish();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(1000, 580);
    }




    private void resetGame() {
        // Reset all checkboxes and userResponses
        for (int i = 0; i < 5; i++) {
            checkBoxes[i][0].setChecked(false);
            checkBoxes[i][1].setChecked(false);
            checkBoxes[i][0].setEnabled(false); // Disable "Yes"
            checkBoxes[i][1].setEnabled(false); // Disable "No"
            userResponses[i] = false; // Reset the responses
        }

        // Reset the current box to the first one
        currentBox = 0;

        // Show the first box again
        showCurrentBox();
    }
}
