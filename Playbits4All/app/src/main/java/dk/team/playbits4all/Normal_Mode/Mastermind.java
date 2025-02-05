package dk.team.playbits4all.Normal_Mode;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import dk.team.playbits4all.R;


public class Mastermind extends AppCompatActivity {

    private LinearLayout rowsContainer;
    private List<int[]> allSelectedColors = new ArrayList<>();
    private int[] SECRET_CODE;
    private int currentRow = 0;
    private boolean isBtn1Clicked = false;
    private boolean isBtn2Clicked = false;
    private boolean isBtn3Clicked = false;
    int difficulty = 1;


    private void initializeSecretCode(int difficulty) {
        SECRET_CODE = generateSecretCode(difficulty);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mastermind);
        showStartGameDialog(false);

        rowsContainer = findViewById(R.id.rowsContainer);

        for (int i = 0; i < 8; i++) {
            addNewRow();
        }
        enableRow(0);
    }

    private void addNewRow() {
        int[] selectedColors = new int[4];
        allSelectedColors.add(selectedColors);

        LinearLayout rowLayout = new LinearLayout(this);

        LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        rowLayoutParams.setMargins(0, 0, 0, 20);
        rowLayout.setLayoutParams(rowLayoutParams);

        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);


        Button[] guessButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            final int index = i;
            guessButtons[i] = new Button(this);
            LinearLayout.LayoutParams largeButtonLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    100,
                    1f
            );
            largeButtonLayoutParams.setMargins(8, 0, 8, 0);
            guessButtons[i].setLayoutParams(largeButtonLayoutParams);

            guessButtons[i].setBackground(getResources().getDrawable(R.drawable.circle_button));

            guessButtons[i].setEnabled(false);

            guessButtons[i].setOnClickListener(v -> {
                if (isBtn1Clicked) {
                    showDialogColors((Button) v, index, selectedColors);
                } else if (isBtn2Clicked) {
                    showDialog((Button) v, index, selectedColors);
                } else if (isBtn3Clicked) {
                    MultiColors((Button) v, index, selectedColors);
                }
            });
            rowLayout.addView(guessButtons[i]);
        }

        Button[] feedbackButtons = new Button[4];
        for (int i = 0; i < 4; i++) {
            feedbackButtons[i] = new Button(this);

            LinearLayout.LayoutParams smallButtonLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    45,
                    0.5f
            );
            smallButtonLayoutParams.setMargins(4, 0, 4, 0); // Smaller margins
            smallButtonLayoutParams.gravity = Gravity.CENTER_VERTICAL; // Center the feedback buttons vertically
            feedbackButtons[i].setLayoutParams(smallButtonLayoutParams);

            feedbackButtons[i].setBackground(getResources().getDrawable(R.drawable.small_circle_button));

            rowLayout.addView(feedbackButtons[i]);
        }

        Button checkButton = new Button(this);
        checkButton.setText("Check");

        LinearLayout.LayoutParams checkButtonLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        checkButtonLayoutParams.setMargins(8, 0, 8, 0);
        checkButton.setLayoutParams(checkButtonLayoutParams);
        checkButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        checkButton.setEnabled(false);
        checkButton.setOnClickListener(v -> checkColors(guessButtons, feedbackButtons, selectedColors));

        rowLayout.addView(checkButton);

        rowsContainer.addView(rowLayout);
    }


    private void showDialogColors(Button button, int index, int[] selectedColors) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.option_colors);

        Button buttonRed = dialog.findViewById(R.id.colorRed);
        Button buttonGreen = dialog.findViewById(R.id.colorGreen);
        Button buttonBlue = dialog.findViewById(R.id.colorBlue);
        Button buttonOrange = dialog.findViewById(R.id.colorOrange);
        Button buttonPink = dialog.findViewById(R.id.colorViolet);
        Button buttonYellow = dialog.findViewById(R.id.colorYellow);


        buttonRed.setOnClickListener(v -> setColor(button, index, R.color.red, dialog, selectedColors));
        buttonGreen.setOnClickListener(v -> setColor(button, index, R.color.darkGreen, dialog, selectedColors));
        buttonBlue.setOnClickListener(v -> setColor(button, index, R.color.darkBlue, dialog, selectedColors));
        buttonOrange.setOnClickListener(v -> setColor(button, index, R.color.orange, dialog, selectedColors));
        buttonPink.setOnClickListener(v -> setColor(button, index, R.color.pink, dialog, selectedColors));
        buttonYellow.setOnClickListener(v -> setColor(button, index, R.color.yellow, dialog, selectedColors));


        dialog.show();
    }

    private void showDialog(Button button, int index, int[] selectedColors) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_color);

        Button buttonRed = dialog.findViewById(R.id.colorRed);
        Button buttonGreen = dialog.findViewById(R.id.colorGreen);
        Button buttonBlue = dialog.findViewById(R.id.colorBlue);
        Button buttonYellow = dialog.findViewById(R.id.colorYellow);
        Button buttonOrange = dialog.findViewById(R.id.colorOrange);
        Button buttonPink = dialog.findViewById(R.id.colorViolet);
        Button buttonGold = dialog.findViewById(R.id.colorSilver);
        Button buttonBrown = dialog.findViewById(R.id.colorBrown);

        buttonRed.setOnClickListener(v -> setColor(button, index, R.color.red, dialog, selectedColors));
        buttonGreen.setOnClickListener(v -> setColor(button, index, R.color.darkGreen, dialog, selectedColors));
        buttonBlue.setOnClickListener(v -> setColor(button, index, R.color.darkBlue, dialog, selectedColors));
        buttonYellow.setOnClickListener(v -> setColor(button, index, R.color.yellow, dialog, selectedColors));
        buttonOrange.setOnClickListener(v -> setColor(button, index, R.color.orange, dialog, selectedColors));
        buttonPink.setOnClickListener(v -> setColor(button, index, R.color.pink, dialog, selectedColors));
        buttonGold.setOnClickListener(v -> setColor(button, index, R.color.silver, dialog, selectedColors));
        buttonBrown.setOnClickListener(v -> setColor(button, index, R.color.brown, dialog, selectedColors));

        dialog.show();
    }

    private void MultiColors(Button button, int index, int[] selectedColors) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.multi_colors);

        Button buttonRed = dialog.findViewById(R.id.colorRed);
        Button buttonGreen = dialog.findViewById(R.id.colorGreen);
        Button buttonBlue = dialog.findViewById(R.id.colorBlue);
        Button buttonYellow = dialog.findViewById(R.id.colorYellow);
        Button buttonOrange = dialog.findViewById(R.id.colorOrange);
        Button buttonPink = dialog.findViewById(R.id.colorViolet);
        Button buttonGold = dialog.findViewById(R.id.colorSilver);
        Button buttonBrown = dialog.findViewById(R.id.colorBrown);
        Button buttonBlack= dialog.findViewById(R.id.colorBlack);
        Button buttonLightGreen = dialog.findViewById(R.id.colorLightGreen);

        buttonRed.setOnClickListener(v -> setColor(button, index, R.color.red, dialog, selectedColors));
        buttonGreen.setOnClickListener(v -> setColor(button, index, R.color.darkGreen, dialog, selectedColors));
        buttonBlue.setOnClickListener(v -> setColor(button, index, R.color.darkBlue, dialog, selectedColors));
        buttonYellow.setOnClickListener(v -> setColor(button, index, R.color.yellow, dialog, selectedColors));
        buttonOrange.setOnClickListener(v -> setColor(button, index, R.color.orange, dialog, selectedColors));
        buttonPink.setOnClickListener(v -> setColor(button, index, R.color.pink, dialog, selectedColors));
        buttonGold.setOnClickListener(v -> setColor(button, index, R.color.silver, dialog, selectedColors));
        buttonBrown.setOnClickListener(v -> setColor(button, index, R.color.brown, dialog, selectedColors));
        buttonLightGreen.setOnClickListener(v -> setColor(button, index, R.color.lightGreen, dialog, selectedColors));
        buttonBlack.setOnClickListener(v -> setColor(button, index, R.color.black, dialog, selectedColors));

        dialog.show();
    }

    private void setColor(Button button, int index, int colorId, Dialog dialog, int[] selectedColors) {
        GradientDrawable drawable = (GradientDrawable) getResources().getDrawable(R.drawable.circle_button).mutate();
        drawable.setColor(getResources().getColor(colorId));
        button.setBackground(drawable);
        selectedColors[index] = colorId;
        dialog.dismiss();
    }

    private void checkColors(Button[] guessButtons, Button[] feedbackButtons, int[] selectedColors) {
        boolean allButtonsFilled = true;
        for (int i = 0; i < 4; i++) {
            if (selectedColors[i] == 0) {
                allButtonsFilled = false;
                break;
            }
        }

        if (!allButtonsFilled) {
            Toast.makeText(this, "Pick 4 colors", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] feedbackColors = new int[4];
        boolean[] usedInCode = new boolean[4];
        boolean[] usedInGuess = new boolean[4];


        for (int i = 0; i < SECRET_CODE.length; i++) {
            if (selectedColors[i] == SECRET_CODE[i]) {
                feedbackColors[i] = R.color.darkGreen;
                usedInCode[i] = true;
                usedInGuess[i] = true;
            } else {
                feedbackColors[i] = R.color.white;
            }
        }


        for (int i = 0; i < SECRET_CODE.length; i++) {
            if (!usedInGuess[i]) {
                for (int j = 0; j < SECRET_CODE.length; j++) {
                    if (!usedInCode[j] && selectedColors[i] == SECRET_CODE[j]) {
                        feedbackColors[i] = R.color.orange;
                        usedInCode[j] = true;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < feedbackButtons.length; i++) {
            feedbackButtons[i].setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(feedbackColors[i])));
        }


        if (allColorsMatch(feedbackColors)) {
            Toast.makeText(this, "Congratulations! You guessed the correct code!", Toast.LENGTH_LONG).show();
            disableAllRows();
            showEndGameDialog(true);
        } else {
            disableRow(currentRow);
            if (currentRow < 7) {
                enableRow(++currentRow);
            } else {
                showRetryDialog(true);
                Toast.makeText(this, "Game over! You've used all your attempts.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void disableRow(int rowIndex) {
        LinearLayout rowLayout = (LinearLayout) rowsContainer.getChildAt(rowIndex);
        for (int i = 0; i < rowLayout.getChildCount(); i++) {
            View view = rowLayout.getChildAt(i);
            if (view instanceof Button) {
                view.setEnabled(false);
            }
        }
    }


    private boolean allColorsMatch(int[] feedbackColors) {
        for (int color : feedbackColors) {
            if (color != R.color.darkGreen) {
                return false;
            }
        }
        // If all colors match, trigger the animation
        LinearLayout animationContainer = findViewById(R.id.animationContainer);
        animationContainer.setVisibility(View.VISIBLE); // Make the container visible
        animationContainer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up));
        return true;


    }


    private void enableRow(int rowIndex) {
        LinearLayout rowLayout = (LinearLayout) rowsContainer.getChildAt(rowIndex);

        // Enable the guess buttons
        for (int i = 0; i < 4; i++) {
            Button guessButton = (Button) rowLayout.getChildAt(i);
            guessButton.setEnabled(true);
        }

        // Enable the check button
        Button checkButton = (Button) rowLayout.getChildAt(8);
        checkButton.setEnabled(true);
    }

    private void disableAllRows() {
        for (int i = 0; i < rowsContainer.getChildCount(); i++) {
            LinearLayout rowLayout = (LinearLayout) rowsContainer.getChildAt(i);
            for (int j = 0; j < rowLayout.getChildCount(); j++) {
                View view = rowLayout.getChildAt(j);
                if (view instanceof Button) {
                    view.setEnabled(false);
                }
            }
        }
    }

    private void showStartGameDialog(boolean won) {
        LinearLayout cardLayout = findViewById(R.id.cardLayout);

        cardLayout.setVisibility(View.VISIBLE);


        Button btnEasy = findViewById(R.id.btn_easy);
        Button btnMiddle = findViewById(R.id.btn_middle);
        Button btnHard = findViewById(R.id.btn_hard);


        btnEasy.setOnClickListener(v -> {
            handleDifficultySelection("Easy");
            closeDialogAndShowMainContent();
        });

        btnMiddle.setOnClickListener(v -> {
            handleDifficultySelection("Middle");
            closeDialogAndShowMainContent();
        });

        btnHard.setOnClickListener(v -> {
            handleDifficultySelection("Hard");
            closeDialogAndShowMainContent();
        });
    }

    private void closeDialogAndShowMainContent() {
        LinearLayout cardLayout = findViewById(R.id.cardLayout);
        cardLayout.setVisibility(View.GONE);

        LinearLayout mainContentLayout = findViewById(R.id.mainContentLayout);
        mainContentLayout.setVisibility(View.VISIBLE);
    }


    private void handleDifficultySelection(String difficulty) {
        switch (difficulty) {
            case "Easy":
                isBtn1Clicked = true;
                break;
            case "Middle":
                isBtn2Clicked = true;
                break;
            case "Hard":
                isBtn3Clicked = true;
                break;
        }
        restartGame();
    }



    private void showEndGameDialog(boolean won) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_end_game, null);

        TextView titleView = dialogView.findViewById(R.id.title);
        TextView messageView = dialogView.findViewById(R.id.message);
        ImageView imgView = dialogView.findViewById(R.id.happy);


        titleView.setText(won ? "You Won!" : "Game Over");
        messageView.setText("Do you want to play again or exit?");
        imgView.animate();


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);


        AlertDialog dialog = builder.create();

        Button playButton = dialogView.findViewById(R.id.btn_play);
        playButton.setOnClickListener(v -> {
            restartGame();
            dialog.dismiss();
        });

        Button exitButton = dialogView.findViewById(R.id.btn_exit);
        exitButton.setOnClickListener(v -> {
            finish();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(1000, 650);
    }



    private void showRetryDialog(boolean lose) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.retry_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();


        Button btnRetry = dialogView.findViewById(R.id.btn_retry);
        Button btnExit = dialogView.findViewById(R.id.btn_exit);
        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        ImageView imgView = dialogView.findViewById(R.id.sad);

        title.setText("Game Over");
        message.setText("Do you want to retry or exit?");
        imgView.animate();

        btnRetry.setOnClickListener(v -> {
            restartGame();
            dialog.dismiss();
        });

        btnExit.setOnClickListener(v -> {
            finish();
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(1000, 650);
    }


    private void restartGame() {
        currentRow = 0;
        allSelectedColors.clear();
        initializeSecretCode(difficulty);

        rowsContainer.removeAllViews();
        for (int i = 0; i < 8; i++) {
            addNewRow();
        }

        enableRow(0);

        LinearLayout animationContainer = findViewById(R.id.animationContainer);
        animationContainer.setVisibility(View.GONE);
    }


    private int[] generateSecretCode(int difficulty) {
        Random random = new Random();
        int[] code = new int[4];

        int[] easyColors = new int[]{R.color.red, R.color.darkGreen, R.color.darkBlue, R.color.yellow, R.color.orange, R.color.pink};
        int[] middleColors = new int[]{R.color.red, R.color.darkGreen, R.color.darkBlue, R.color.yellow, R.color.orange, R.color.pink, R.color.silver, R.color.brown};
        int[] hardColors = new int[]{R.color.red, R.color.darkGreen, R.color.darkBlue, R.color.yellow, R.color.black, R.color.orange, R.color.pink, R.color.silver, R.color.brown, R.color.lightGreen};


        int[] possibleColors;
        if (difficulty == 1) { // Easy
            possibleColors = easyColors;
        } else if (difficulty == 2) { // Medium
            possibleColors = middleColors;
        } else if (difficulty == 3) { // Hard
            possibleColors = hardColors;
        } else {
            throw new IllegalArgumentException("Invalid difficulty level");
        }

        List<Integer> colorList = new ArrayList<>();
        for (int color : possibleColors) {
            colorList.add(color);
        }
        Collections.shuffle(colorList);

        for (int i = 0; i < code.length; i++) {
            code[i] = colorList.get(random.nextInt(colorList.size()));
        }

        return code;
    }

}
