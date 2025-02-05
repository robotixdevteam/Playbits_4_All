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

import java.util.Random;

import dk.team.playbits4all.R;
public class SmartMathActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_smart_math);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Yes/No");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);

        text5 = findViewById(R.id.textviewer);
        imgView = findViewById(R.id.imgView);
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
        Intent intent = new Intent(this, SmartMathActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

                    if ("yes/no".equalsIgnoreCase(expectedAction)) {
                        handleSmartMath(text);
                    } else {
                        handleActions(text);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
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



            switch (text.toLowerCase()) {
                case "yes/no":
                    expectedAction = "yes/no";
                    handleSmartMath(text);
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void handleSmartMath(String text) {
        if (isAction) {
            return;
        }

        String display = "";
        int audioResId = 0;
        int imageResId = 0;

        if ("yes/no".equalsIgnoreCase(text)) {
            state = 0;
        }

        switch (state) {
            case 0:
                if ("yes/no".equalsIgnoreCase(text)) {
                    display = "Is 9 greater than 8";
                    audioResId = R.raw.greater9;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                }
                break;
            case 1:
                if ("yes".equalsIgnoreCase(text)) {
                    display = " Does a dog say";
                    audioResId = R.raw.dogsound;
                    imageResId = R.drawable.dog;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 2:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is 7 greater than 6";
                    audioResId = R.raw.greater6;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 3:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Does a Cow say";
                    audioResId = R.raw.cowsound;
                    imageResId = R.drawable.cow;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 4:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is 9 lesser than 2";
                    audioResId = R.raw.lesser9;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 5:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Dog Say..";
                    audioResId = R.raw.dogwrgsound;
                    imageResId = R.drawable.dog;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 6:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Donkey Say..";
                    audioResId = R.raw.donkey;
                    imageResId = R.drawable.donkey;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 7:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Does a Horse Say..";
                    audioResId = R.raw.horsesound;
                    imageResId = R.drawable.horse;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 8:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Does a Cat Say..";
                    audioResId = R.raw.wrgcatsound;
                    imageResId = R.drawable.cat;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 9:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Elephant Say..";
                    audioResId = R.raw.elephantsound;
                    imageResId = R.drawable.elephant;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 10:
                if ("yes".equalsIgnoreCase(text)) {
                    display = " Do birds live in nest";
                    audioResId = R.raw.birdsinnest;
                    imageResId = R.drawable.birds;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 11:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is 6 lesser that 1";
                    audioResId = R.raw.sixlessone;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 12:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Is a Baby Pig as a Piglet";
                    audioResId = R.raw.pigapiglet;
                    imageResId = R.drawable.piglet;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 13:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is 2 greater than 4";
                    audioResId = R.raw.twogreatfour;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 14:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Is 9 lesser than 8";
                    audioResId = R.raw.ninelesseight;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 15:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Rooster Say..";
                    audioResId = R.raw.rooster;
                    imageResId = R.drawable.rooster;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 16:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is a Baby dog a puppy";
                    audioResId = R.raw.dogapuppy;
                    imageResId = R.drawable.puppy;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 17:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Do Lion live in house";
                    audioResId = R.raw.lioninhouse;
                    imageResId = R.drawable.lion;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 18:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Is 5 greater than 4 ";
                    audioResId = R.raw.fivegreatfour;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 19:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Does a Frog Say..";
                    audioResId = R.raw.frogsound;
                    imageResId = R.drawable.frog;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 20:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Does a Sheep Say..";
                    audioResId = R.raw.wrgsheepsou;
                    imageResId = R.drawable.sheep;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 21:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Is 9 greater than 5";
                    audioResId = R.raw.ninegreaterfive;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 22:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is a Baby cow a calf";
                    audioResId = R.raw.cowbabycalf;
                    imageResId = R.drawable.calf ;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 23:
                if ("yes".equalsIgnoreCase(text)) {
                    display = "Is 1 greater than 2";
                    audioResId = R.raw.onegreatertwo;
                    imageResId = R.drawable.ques;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 24:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Pig Say.. ";
                    audioResId = R.raw.pigwrgsou;
                    imageResId = R.drawable.pig;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 25:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Does a Cat Say..";
                    audioResId = R.raw.wrgcatsound;
                    imageResId = R.drawable.cat;
                    state++;
                } else {
                    display = "Incorrect";
                    audioResId = R.raw.wrong;
                    imageResId = R.drawable.no_image;
                }
                break;
            case 26:
                if ("no".equalsIgnoreCase(text)) {
                    display = "Wow..";
                    audioResId = R.raw.crt;
                    imageResId = R.drawable.wow;
                    state = 0;
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


    private boolean isSupportedTag(String text) {
        return "yes".equalsIgnoreCase(text) || "no".equalsIgnoreCase(text) || "yes/no".equalsIgnoreCase(text);
    }
}

