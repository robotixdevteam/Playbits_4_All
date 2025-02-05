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

public class NumbersActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView text5;
    private View rootView;
    private ImageView imgView;

    private MediaPlayer mediaPlayer;
    private boolean isAction = false;
    private String expectedAction = "";

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numbers_actvity);

        text5 = findViewById(R.id.textviewer);
        imgView = findViewById(R.id.imgView);
        rootView = findViewById(android.R.id.content);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Numbers ");

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
        Intent intent = new Intent(this, NumbersActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

                    if (expectedAction.equals("smart math1")) {
                        handleSmartMath(text);
                    } else if (expectedAction.equals("smart math2")) {
                        handleGames(text);
                    }else {
                        handleActions(text);
                    }
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


        if (isSupportedTag(text)) {
            String display = text;
            int bgColor = 0;
            int imageResId = 0;

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            switch (text.toLowerCase()) {
                case "1":
                    display = "O-N-E -> One";
                    mediaPlayer = MediaPlayer.create(this, R.raw.one);
                    imageResId = R.drawable.one_image;
                    mediaPlayer.start();
                    break;
                case "2":
                    display = "T-W-O -> Two";
                    mediaPlayer = MediaPlayer.create(this, R.raw.two);
                    imageResId = R.drawable.two_image;
                    mediaPlayer.start();
                    break;
                case "3":
                    display = "T-H-R-E-E -> Three";
                    mediaPlayer = MediaPlayer.create(this, R.raw.three);
                    imageResId = R.drawable.three_image;
                    mediaPlayer.start();
                    break;
                case "4":
                    display = "F-O-U-R -> Four";
                    mediaPlayer = MediaPlayer.create(this, R.raw.four);
                    imageResId = R.drawable.four_image;
                    mediaPlayer.start();
                    break;
                case "5":
                    display = "F-I-V-E -> Five";
                    mediaPlayer = MediaPlayer.create(this, R.raw.five);
                    imageResId = R.drawable.five_image;
                    mediaPlayer.start();
                    break;
                case "6":
                    display = "S-I-X -> Six";
                    mediaPlayer = MediaPlayer.create(this, R.raw.six);
                    imageResId = R.drawable.six_image;
                    mediaPlayer.start();
                    break;

                case "7":
                    display = "S-E-V-E-N -> Seven";
                    mediaPlayer = MediaPlayer.create(this, R.raw.seven);
                    imageResId = R.drawable.seven_image;
                    mediaPlayer.start();
                    break;
                case "8":
                    display = "E-I-G-H-T -> Eight";
                    mediaPlayer = MediaPlayer.create(this, R.raw.eight);
                    imageResId = R.drawable.eight_image;
                    mediaPlayer.start();
                    break;
                case "9":
                    display = "N-I-N-E -> Nine";
                    mediaPlayer = MediaPlayer.create(this, R.raw.nine);
                    imageResId = R.drawable.nine_image;
                    mediaPlayer.start();
                    break;
                case "reset":
                    resetUI();
                    return;
                case "smart math1":
                    expectedAction = "smart math1";
                    handleSmartMath(text);
                    break;
                case "smart math2":
                    expectedAction = "smart math2";
                    handleGames(text);
                    break;
                default:
                    showToast("Unsupported Tag");
                    return;
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
        } else {
            showToast("Unsupported Tag");
        }
    }
    private void resetUI() {
        text5.setText("");
        rootView.setBackgroundColor(0);
        imgView.setImageResource(0);
        state = 0;
        isAction = false;
        expectedAction = "";
        showToast("Reset complete");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void handleSmartMath(String text) {

        if (isAction) {
            return;
        }

        if ("reset".equalsIgnoreCase(text)) {
            resetUI();
            return;
        }

        String display = "";
        int audioResId = 0;
        int imageResId = 0;


        if ("smart math1".equalsIgnoreCase(text)) {
            state = 0;
        }

        switch (state) {
            case 0:
                if ("smart math1".equals(text.toLowerCase())) {
                    display = "Find 1";
                    audioResId = R.raw.find1;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 1:
                if ("1".equals(text)) {
                    display = "Find 9";
                    audioResId = R.raw.find9;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 2:
                if ("9".equals(text)) {
                    display = "What comes in between 2 and 4";
                    audioResId = R.raw.whtcomes3;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 3:
                if ("3".equals(text)) {
                    display = "Find 5";
                    audioResId = R.raw.find5;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 4:
                if ("5".equals(text)) {
                    display = "What comes after 3";
                    audioResId = R.raw.whtcomaft3;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 5:
                if ("4".equals(text)) {
                    display = "What comes in between 1 and 3";
                    audioResId = R.raw.whtcomes2;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 6:
                if ("2".equals(text)) {
                    display = "Find 8";
                    audioResId = R.raw.find8;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 7:
                if ("8".equals(text)) {
                    display = "What comes in between 5 and 7";
                    audioResId = R.raw.whtcomes6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 8:
                if ("6".equals(text)) {
                    display = "Find 3";
                    audioResId = R.raw.find3;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 9:
                if ("3".equals(text)) {
                    display = "What comes in between 6 and 8";
                    audioResId = R.raw.whtcomes7;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 10:
                if ("7".equals(text)) {
                    display = "What comes before 6";
                    audioResId = R.raw.before6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 11:
                if ("5".equals(text)) {
                    display = "What comes in between 3 and 5";
                    audioResId = R.raw.btn3nd5;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 12:
                if ("4".equals(text)) {
                    display = "Find 6";
                    audioResId = R.raw.find6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 13:
                if ("6".equals(text)) {
                    display = "What comes in between 8 and 10";
                    audioResId = R.raw.btn8nd10;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 14:
                if ("9".equals(text)) {
                    display = "What comes after 7";
                    audioResId = R.raw.after7;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 15:
                if ("8".equals(text)) {
                    display = "Wow..!";
                    audioResId = R.raw.crt;
                    state=0;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            default:
                state = 0;
                break;
        }

        text5.setText(display);

        if (audioResId != 0) {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                    isAction = false;
                    initiateNextTagScan();
                }
            });
            mediaPlayer.start();
        }


        if (audioResId != 0) {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            mediaPlayer.start();
        }
        if (imageResId != 0) {
            imgView.setImageResource(imageResId);
            imgView.setVisibility(View.VISIBLE);
        } else {
            imgView.setVisibility(View.GONE);
        }

        isAction = true;
    }

    private void initiateNextTagScan() {
        enableForegroundDispatch();
    }


    private void handleGames(String text) {

        if (isAction) {
            return;
        }

        if ("reset".equalsIgnoreCase(text)) {
            resetUI();
            return;
        }

        String display = "";
        int audioResId = 0;
        int imageResId = 0;


        if ( "smart math2".equalsIgnoreCase(text) || "smart math1".equalsIgnoreCase(text)) {
            state = 0;
        }

        switch (state) {
            case 0:
                if ("smart math2".equals(text.toLowerCase())) {
                    display = "What is 1 take away from 2";
                    audioResId = R.raw.takeaway1;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 1:
                if ("1".equals(text)) {
                    display = "What is 2 take away from 5";
                    audioResId = R.raw.takeaway3;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 2:
                if ("3".equals(text)) {
                    display = "How many more added to 2 gives 6";
                    audioResId = R.raw.add4;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 3:
                if ("4".equals(text)) {
                    display = "What is 2 take away from 9";
                    audioResId = R.raw.takeaway7;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 4:
                if ("7".equals(text)) {
                    display = "How many more added to 5 gives 10";
                    audioResId = R.raw.add5;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 5:
                if ("5".equals(text)) {
                    display = "What is 8 more added to 1";
                    audioResId = R.raw.more9;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 6:
                if ("9".equals(text)) {
                    display = "What is 4 take away from 10";
                    audioResId = R.raw.takeaway6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 7:
                if ("6".equals(text)) {
                    display = "What is 4 take away from 6";
                    audioResId = R.raw.takeaway2;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 8:
                if ("2".equals(text)) {
                    display = "What is 4 more added to 4";
                    audioResId = R.raw.more8;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 9:
                if ("8".equals(text)) {
                    display = "What is 5 more added to 1";
                    audioResId = R.raw.more6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 10:
                if ("6".equals(text)) {
                    display = "What is 1 more added to 1";
                    audioResId = R.raw.onemore1;
                    state++;
                }else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 11:
                if ("2".equals(text)) {
                    display = "How many more added to 4 gives 5";
                    audioResId = R.raw.fourgives5;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 12:
                if ("1".equals(text)) {
                    display = "What is 2 more added to 5";
                    audioResId = R.raw.twoadd5;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 13:
                if ("7".equals(text)) {
                    display = "What is 1 take away from 6";
                    audioResId = R.raw.onetake6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 14:
                if ("5".equals(text)) {
                    display = "How many more added to 3 gives 6";
                    audioResId = R.raw.threegives6;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            case 15:
                if ("3".equals(text)) {
                    display = "Wow..";
                    audioResId = R.raw.crt;
                    state=0;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                    state = 0;
                }
                break;
            default:
                break;
        }

        text5.setText(display);

        if (audioResId != 0) {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    mediaPlayer = null;
                    isAction = false;
                    initiateNextTagScan();
                }
            });
            mediaPlayer.start();
        }

        if (audioResId != 0) {
            mediaPlayer = MediaPlayer.create(this, audioResId);
            mediaPlayer.start();
        }
        if (imageResId != 0) {
            imgView.setImageResource(imageResId);
            imgView.setVisibility(View.VISIBLE);
        } else {
            imgView.setVisibility(View.GONE);
        }
        isAction = true;
    }

    private boolean isSupportedTag(String text) {
        if (text.matches("[1-9]|10")) {
            return true;
        }  else {
            return "smart math1".equalsIgnoreCase(text)
                    || "smart math2".equalsIgnoreCase(text)
                    || "reset".equalsIgnoreCase(text);
        }
    }
}

