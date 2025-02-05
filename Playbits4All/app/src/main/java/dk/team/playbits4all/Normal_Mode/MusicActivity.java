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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dk.team.playbits4all.R;

public class MusicActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView text5;
    private View rootView;
    private ImageView imgView;

    private MediaPlayer mediaPlayer;
    private boolean isAction = false;
    private int state = 0;
    private int musicState = 0;
    private boolean isMusicMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Music");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);


        text5 = findViewById(R.id.textviewer);
        imgView = findViewById(R.id.imgView);
        rootView = findViewById(android.R.id.content);
        text5.setVisibility(View.VISIBLE);

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
        Intent intent = new Intent(this, MusicActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

                    if (isMusicMode) {
                        handleMusicModeActions(text);
                    } else {
                        handleActions(text);
                    }
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

            switch (text.toLowerCase()) {
                case "guitar":
                    display = "Guitar";
                    mediaPlayer = MediaPlayer.create(this, R.raw.guitarz);
                    imageResId = R.drawable.guitar_bg;
                    mediaPlayer.start();
                    break;
                case "piano":
                    display = "Piano";
                    mediaPlayer = MediaPlayer.create(this, R.raw.pianoresize);
                    imageResId = R.drawable.piano_bg;
                    mediaPlayer.start();
                    break;
                case "xylophone":
                    display = "Xylophone";
                    mediaPlayer = MediaPlayer.create(this, R.raw.xylopho);
                    imageResId = R.drawable.xylo_bg;
                    mediaPlayer.start();
                    break;
                case "synth":
                    display = "Synthesizer";
                    mediaPlayer = MediaPlayer.create(this, R.raw.synthresize);
                    imageResId = R.drawable.synth_bg;
                    mediaPlayer.start();
                    break;
                case "drums":
                    display = "Drums";
                    mediaPlayer = MediaPlayer.create(this, R.raw.drumsresize);
                    imageResId = R.drawable.drums_bg;
                    mediaPlayer.start();
                    break;
                case "do":
                    display = "Do";
                    mediaPlayer = MediaPlayer.create(this, R.raw.doresiz);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "re":
                    display = "Re";
                    mediaPlayer = MediaPlayer.create(this, R.raw.reresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "mi":
                    display = "Mi";
                    mediaPlayer = MediaPlayer.create(this, R.raw.miresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "fa":
                    display = "Fa";
                    mediaPlayer = MediaPlayer.create(this, R.raw.fazresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "so":
                    display = "So";
                    mediaPlayer = MediaPlayer.create(this, R.raw.soresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "la":
                    display = "La";
                    mediaPlayer = MediaPlayer.create(this, R.raw.laresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "ti":
                    display = "Ti";
                    mediaPlayer = MediaPlayer.create(this, R.raw.tiresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;
                case "dos":
                    display = "Do:";
                    mediaPlayer = MediaPlayer.create(this, R.raw.dosresize);
                    imageResId = R.drawable.musicins;
                    mediaPlayer.start();
                    break;

                case "rhymes":
                    switch (state) {
                        case 0:
                            display = "Happy Birthday";
                            mediaPlayer = MediaPlayer.create(this, R.raw.happyborn);
                            imageResId = R.drawable.happy_bornday;
                            mediaPlayer.start();
                            break;
                        case 1:
                            display = "Old Macdonald";
                            mediaPlayer = MediaPlayer.create(this, R.raw.oldmacdon);
                            imageResId = R.drawable.old_macdonald;
                            mediaPlayer.start();
                            break;
                        case 2:
                            display = "Wheels on the bus";
                            mediaPlayer = MediaPlayer.create(this, R.raw.wheelson);
                            imageResId = R.drawable.wheelsbus;
                            mediaPlayer.start();
                            break;
                        case 3:
                            display = "Twinkle Twinkle Little";
                            mediaPlayer = MediaPlayer.create(this, R.raw.twinkle);
                            imageResId = R.drawable.little_star;
                            mediaPlayer.start();
                            break;
                    }

                    state = (state + 1) % 4;
                    break;

                case "music":
                    isMusicMode = true;
                    break;


                default:
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
        } else {
            showToast("Unsupported tag detected: " + text);


        }
    }

    private void handleMusicModeActions(String text) {
        if (isSupportedTag(text)) {
            String display = text;
            int bgColor = 0;
            int imageResId = 0;

            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            switch (text.toLowerCase()) {
                case "guitar":
                    display = "Guitar";
                    mediaPlayer = MediaPlayer.create(this, R.raw.guitarz);
                    imageResId = R.drawable.guitar_bg;
                    mediaPlayer.start();
                    musicState = 4;
                    break;
                case "piano":
                    display = "Piano";
                    mediaPlayer = MediaPlayer.create(this, R.raw.pianoresize);
                    imageResId = R.drawable.piano_bg;
                    mediaPlayer.start();
                    musicState = 3;
                    break;
                case "synth":
                    display = "Synth";
                    mediaPlayer = MediaPlayer.create(this, R.raw.synthresize);
                    imageResId = R.drawable.synth_bg;
                    mediaPlayer.start();
                    musicState = 2;
                    break;
                case "drums":
                    display = "Drums";
                    mediaPlayer = MediaPlayer.create(this, R.raw.drumsresize);
                    imageResId = R.drawable.drums_bg;
                    mediaPlayer.start();
                    musicState = 1;
                    break;
                case "xylophone":
                    display = "Xylophone";
                    mediaPlayer = MediaPlayer.create(this, R.raw.xylopho);
                    imageResId = R.drawable.xylo_bg;
                    mediaPlayer.start();
                    musicState = 0;
                    break;
                case "mi":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylom);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drummi);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.misynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.mipiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.miresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "ti":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylot);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumti);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.tisynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.fapiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.tiresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "fa":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylof);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumsfa);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.fasynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.fapiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.fazresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "so":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylos);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumso);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.sosynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.sopiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.soresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "la":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylol);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumla);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.lasynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.mipiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.laresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "do":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.doxylo);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumdo);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.dosynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.dopiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.doresiz);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "dos":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylod);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.dosdrum);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.dossynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.sopiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.dosresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "re":
                    switch (musicState) {
                        case 0:
                            mediaPlayer = MediaPlayer.create(this, R.raw.xylor);
                            break;
                        case 1:
                            mediaPlayer = MediaPlayer.create(this, R.raw.drumre);
                            break;
                        case 2:
                            mediaPlayer = MediaPlayer.create(this, R.raw.resynth);
                            break;
                        case 3:
                            mediaPlayer = MediaPlayer.create(this, R.raw.repiano);
                            break;
                        case 4:
                            mediaPlayer = MediaPlayer.create(this, R.raw.reresize);
                            break;
                    }
                    mediaPlayer.start();
                    break;
                case "rhymes":
                    showToast("Unsupported tag: Rhymes");
                    return;

                case "reset":
                    isMusicMode = false;
                    musicState = 0;
                    break;

                default:
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
        } else {
            showToast("Unsupported tag detected");
        }
    }

    private boolean isSupportedTag(String text) {
        String[] supportedTags = {"piano", "guitar","synth","xylophone","drums","do","re","mi","fa","so","la","ti","dos","music","reset","rhymes"};
        for (String tag : supportedTags) {
            if (tag.equalsIgnoreCase(text)) {
                return true;
            }
        }

        return false;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
