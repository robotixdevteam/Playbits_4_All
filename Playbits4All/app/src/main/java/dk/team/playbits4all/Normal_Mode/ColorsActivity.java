package dk.team.playbits4all.Normal_Mode;



import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import dk.team.playbits4all.R;

public class ColorsActivity extends AppCompatActivity {

    private static final String TAG_FIND_2 = "find**";
    private static final String TAG_FIND_3 = "find***";
    private static final String TAG_RESET = "reset";
    private static final String TAG_FOLLOW_ME = "followme";
    private static final String TAG_TACO_SAYS = "taco says";
    private static final String TAG_COLOR_MIX = "color mix";

    private TextView colorTextView;
    private NfcAdapter nfcAdapter;
    private ImageView colorView;
    private String[] selectedColors;
    private int colorIndex;
    private String followMeColor;


    private boolean find2GameInProgress = false;
    private boolean find3GameInProgress = false;
    private boolean followMeGameInProgress = false;
    private boolean tacoSaysGameInProgress = false;


    private MediaPlayer mediaPlayer;
    private List<String> ColorMixSequence = new ArrayList<>();
    private List<String> tacoSaysSequence;
    private int tacoSaysIndex;
    private boolean colorMixGameInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colors);

        colorTextView = findViewById(R.id.textviewer);
        colorView = findViewById(R.id.imgView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            showToast("NFC is not available on this device.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    private void enableForegroundDispatch() {
        Intent intent = new Intent(this, ColorsActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
            String colorName = readColorFromNFC(intent);

            switch (colorName.toLowerCase()) {
                case TAG_FIND_2:
                    // Handle "Find 2" game
                    initializeFind2Game();
                    break;
                case TAG_FIND_3:
                    // Handle "Find 3" game
                    initializeFind3Game();

                    break;
                case TAG_FOLLOW_ME:
                    // Handle "Follow Me" game
                    initializeFollowMeGame();
                    break;
                case TAG_TACO_SAYS:
                    // Handle "Taco Says" game
                    initializeTacoSaysGame();
                    break;
                case TAG_COLOR_MIX:
                    // Start the "ColorMix" game
                    initializeColorMixGame();
                    break;
                case TAG_RESET:
                    stopGames();
                    displayColor("Name");
                    break;
                default:
                    if (colorMixGameInProgress) {
                        // Handle "ColorMix" game logic
                        handleColorMixGame(intent);
                    }  else if (find2GameInProgress) {
                        // Handle "Find 2" game logic
                        handleFind2Game(intent);
                    }  else if (find3GameInProgress) {
                        // Handle "Find 3" game logic
                        handleFind3Game(intent);
                    }  else if (followMeGameInProgress) {
                        // Handle "Follow Me" game logic
                        handleFollowMeGame(intent);
                    } else if (tacoSaysGameInProgress) {
                        // Handle "Taco Says" game logic
                        handleTacoSaysGame(colorName);
                    }  else if (isSupportedColor(colorName)) {
                        displayColor(colorName);
                    } else {
                        showToast("Unsupported color: " + colorName);
                    }
                    break;
            }
        }
    }

    private void stopGames() {
        colorMixGameInProgress = false;
        find2GameInProgress = false;
        find3GameInProgress = false;
        followMeGameInProgress = false;
        tacoSaysGameInProgress = false;

        colorView.setBackgroundColor(Color.TRANSPARENT);

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release(); // Release resources
            mediaPlayer = null; // Set to null to avoid future usage without initialization
        }
    }

    private void displayGameColor(String colorName) {
        if (isSupportedColor(colorName)) {
            displayColor(colorName);
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }

    private void initializeColorMixGame() {
        // Start the ColorMix game
        colorMixGameInProgress = true;
        ColorMixSequence.clear();
        showToast("ColorMix game started! Select two colors to mix.");
    }

    private void handleColorMixGame(Intent intent) {
        String colorName = readColorFromNFC(intent);

        if (isSupportedColor(colorName)) {
            // If only the first color is scanned
            if (ColorMixSequence.size() == 0) {
                ColorMixSequence.add(colorName);
                colorView.setBackgroundColor(Color.TRANSPARENT);
                displayColor(colorName); // Display the first color immediately
                showToast("Selected color: " + colorName);

                // If the second color is scanned
            } else if (ColorMixSequence.size() == 1) {
                ColorMixSequence.add(colorName);
                colorView.setBackgroundColor(Color.TRANSPARENT);
                displayColor(colorName); // Show second color briefly

                // Wait 1000ms (1 second) and then show the mixed color result
                new Handler().postDelayed(() -> {
                    mixAndDisplayColors(ColorMixSequence.get(0), ColorMixSequence.get(1));

                    // Clear the color sequence for the next round
                    ColorMixSequence.clear();
                    initializeColorMixGame(); // Restart game for a new round

                }, 1000); // 1000ms delay before showing the mixed color
            }
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }



    private void mixAndDisplayColors(String color1, String color2) {
        int mixedColorValue = mixColors(color1, color2);
        colorView.setBackgroundColor(Color.TRANSPARENT);
        colorView.setBackgroundColor(mixedColorValue);
    }

    private int mixColors(String color1, String color2) {
        // Mix color logic, returning mixed color value
        if (("red".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2)) ||
                ("pink".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff0080");
        } else if (("red".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2)) ||
                ("green".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#808026");
        } else if (("red".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#800080");
        } else if (("red".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "red".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff8000");
        } else if (("pink".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2)) ||
                ("green".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#8080a6");
        } else if (("pink".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#8000ff");
        } else if (("pink".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "pink".equalsIgnoreCase(color2))) {
            return Color.parseColor("#ff8080");
        } else if (("green".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2)) ||
                ("blue".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2))) {
            return Color.parseColor("#0080a6");
        } else if (("green".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "green".equalsIgnoreCase(color2))) {
            return Color.parseColor("#80ff26");
        } else if (("blue".equalsIgnoreCase(color1) && "yellow".equalsIgnoreCase(color2)) ||
                ("yellow".equalsIgnoreCase(color1) && "blue".equalsIgnoreCase(color2))) {
            return Color.parseColor("#80ff26");
        } else {
            return Color.parseColor("#000000"); // Default to black for unsupported combinations
        }
    }

    private boolean isSupportedColor(String colorName) {
        Set<String> supportedColors = new HashSet<>(Arrays.asList("blue", "red", "green", "yellow", "pink"));
        return supportedColors.contains(colorName.toLowerCase());
    }

    private String readColorFromNFC(Intent intent) {
        NdefMessage ndefMessage = (NdefMessage) Objects.requireNonNull(intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES))[0];
        NdefRecord record = ndefMessage.getRecords()[0];
        byte[] payload = record.getPayload();
        int languageCodeLength = payload[0] & 0x1F;
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, StandardCharsets.UTF_8);
    }

    private void displayColor(String colorName) {

        ImageView colorView = findViewById(R.id.imgView);
        colorTextView.setText(String.format("Color: %s", colorName));

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        switch (colorName.toLowerCase()) {
            case "blue":
                colorView.setImageResource(R.drawable.blue_circle);
                mediaPlayer = MediaPlayer.create(this, R.raw.blue_clr);
                break;
            case "red":
                colorView.setImageResource(R.drawable.red_triangle);
                mediaPlayer = MediaPlayer.create(this, R.raw.red);
                break;
            case "green":
                colorView.setImageResource(R.drawable.green_star);
                mediaPlayer = MediaPlayer.create(this, R.raw.green);
                break;
            case "pink":
                colorView.setImageResource(R.drawable.pink_square);
                mediaPlayer = MediaPlayer.create(this, R.raw.pink);
                break;
            case "yellow":
                colorView.setImageResource(R.drawable.yellow_oval);
                mediaPlayer = MediaPlayer.create(this, R.raw.yellow);
                break;
            default:
                colorView.setImageResource(0);
                break;
        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initializeTacoSaysGame() {
        tacoSaysSequence = new ArrayList<>();
        tacoSaysIndex = 0;
        startNextTacoSaysRound();
        Toast.makeText(this, "Taco Says: " + tacoSaysSequence.get(tacoSaysIndex), Toast.LENGTH_SHORT).show();
        tacoSaysGameInProgress = true;
    }
    private void startNextTacoSaysRound() {
        String newColor = getRandomColor();
        tacoSaysSequence.add(newColor);
    }
    private String getRandomColor() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        Random random = new Random();
        return allColors[random.nextInt(allColors.length)];
    }
    private void handleTacoSaysGame(String tappedColor) {
        if (tappedColor.equalsIgnoreCase(tacoSaysSequence.get(tacoSaysIndex))) {
            // User tapped the correct color, continue to the next in the sequence
            tacoSaysIndex++;

            // Check if the user has successfully followed the entire sequence
            if (tacoSaysIndex >= tacoSaysSequence.size()) {
                // User successfully followed the sequence, increase the sequence
                startNextTacoSaysRound();
                Toast.makeText(this, "Taco Says: " + getTacoSaysSequenceString(), Toast.LENGTH_SHORT).show();

                // Reset the index for the user's input
                tacoSaysIndex = 0;
            }
        } else {
            // User tapped the wrong color, game over
            Toast.makeText(this, "Oops! You didn't follow Taco's instructions. Game over.", Toast.LENGTH_SHORT).show();

            // Reset the Taco Says game
            initializeTacoSaysGame();
        }
    }
    private String getTacoSaysSequenceString() {
        StringBuilder sequenceStringBuilder = new StringBuilder();
        for (String color : tacoSaysSequence) {
            sequenceStringBuilder.append(color).append(", ");
        }
        // Remove the trailing comma and space
        return sequenceStringBuilder.substring(0, sequenceStringBuilder.length() - 2);
    }

    private void initializeFollowMeGame() {
        // Implement the logic to initialize the "Follow Me" game
        // For example, you can randomly select a color for the user to follow
        // and then display instructions to the user.
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        String followColor = allColors[new Random().nextInt(allColors.length)];

        // Display instructions to the user
        Toast.makeText(this, "Follow the color: " + followColor, Toast.LENGTH_SHORT).show();

        // Set the color for the "Follow Me" game
        followMeColor = followColor;

        // Start the "Follow Me" game
        followMeGameInProgress = true;
    }
    private void handleFollowMeGame(Intent intent) {
        // Handle logic for the "Follow Me" game
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            // Only display if the color is supported
            displayColor(colorName);

            // Check if the tapped color matches the expected color in the game
            if (colorName.equalsIgnoreCase(followMeColor)) {
                // User tapped the correct color, continue the game
                Toast.makeText(this, "Correct! Now follow the next color.", Toast.LENGTH_SHORT).show();

                // Initialize the next color for the user to follow
                initializeFollowMeGame();
            } else {
                // User tapped the wrong color, game over
                Toast.makeText(this, "Wrong color! Game over.", Toast.LENGTH_SHORT).show();

                // Restart the "Follow Me" game
                initializeFollowMeGame();
            }
        }
    }

    private void initializeFind2Game() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        selectedColors = new String[2];
        Random random = new Random();

        selectedColors[0] = allColors[random.nextInt(allColors.length)];

        do {
            selectedColors[1] = allColors[random.nextInt(allColors.length)];
        } while (selectedColors[1].equals(selectedColors[0]));

        colorIndex = 0;

        showToast("Find the colors in order: " + selectedColors[0] + ", " + selectedColors[1]);

        find2GameInProgress = true;
    }

    private void handleFind2Game(Intent intent) {
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            displayGameColor(colorName);

            if (colorName.equalsIgnoreCase(selectedColors[colorIndex])) {
                colorIndex++;

                if (colorIndex >= selectedColors.length) {
                    showToast("Congratulations! You found both colors.");
                    initializeFind2Game();
                } else {
                    showToast("Correct color! Now find: " + selectedColors[colorIndex]);
                }
            } else {
                showToast("Wrong color! Game over.");
                initializeFind2Game();
            }
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }

    private void initializeFind3Game() {
        String[] allColors = {"blue", "red", "green", "yellow", "pink"};
        selectedColors = new String[3];
        Random random = new Random();

        for (int i = 0; i < selectedColors.length; i++) {
            selectedColors[i] = allColors[random.nextInt(allColors.length)];
        }

        colorIndex = 0;

        showToast("Find the colors in order: " +
                selectedColors[0] + ", " + selectedColors[1] + ", " + selectedColors[2]);

        find3GameInProgress = true;
    }

    private void handleFind3Game(Intent intent) {
        String colorName = readColorFromNFC(intent);
        if (isSupportedColor(colorName)) {
            displayGameColor(colorName);

            if (colorName.equalsIgnoreCase(selectedColors[colorIndex])) {
                colorIndex++;

                if (colorIndex >= selectedColors.length) {
                    showToast("Congratulations! You found all three colors.");
                    initializeFind3Game();
                } else {
                    showToast("Correct color! Now find: " + selectedColors[colorIndex]);
                }
            } else {
                showToast("Wrong color! Game over.");
                initializeFind3Game();
            }
        } else {
            showToast("Unsupported color: " + colorName);
        }
    }

}
