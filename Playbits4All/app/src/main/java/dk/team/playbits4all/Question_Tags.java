package dk.team.playbits4all;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import dk.team.playbits4all.Normal_Mode.YesOrNoActivity;


public class Question_Tags extends AppCompatActivity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private Button btn,btnSpanish,btnGerman,btnItalian;
    private ListView listView;
    private ArrayList<String> questions;
    private ArrayAdapter<String> adapter;
    private HashMap<String, Boolean> questionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_tags);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set title
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText(" ");

        ImageButton backbutton = findViewById(R.id.btn_back);
        backbutton.setOnClickListener(view -> {
            finish();
        });
        ImageButton settingsButton = findViewById(R.id.btn_settings);
        settingsButton.setVisibility(View.INVISIBLE);

        btn = findViewById(R.id.btn);
        btnSpanish = findViewById(R.id.btnSpanish);
        btnGerman = findViewById(R.id.btnGerman);
        btnItalian = findViewById(R.id.btnItalian);

        listView = findViewById(R.id.listView);
        questions = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, questions);
        listView.setAdapter(adapter);

        questionMap = new HashMap<>();

        addVoiceBtn();
        addQuestions();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String selectedQuestion = questions.get(position);
            boolean answer = questionMap.get(selectedQuestion);
            startYesNoActivity(selectedQuestion, answer);
        });

        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition("spanish");
            }
        });

        btnGerman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition("german");
            }
        });

        btnItalian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition("italian");
            }
        });
    }

    private void startVoiceRecognition(String language) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        if (language.equals("german")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "de-DE");
        } else if (language.equals("spanish")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES");
        } else if (language.equals("italian")) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "it-IT");
        }
        else {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        }

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a question");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    showOptions(spokenText);
                }
            }
        }
    }


    private void showOptions(final String question) {
        View dialogView = getLayoutInflater().inflate(R.layout.activity_custom_dialog, null);
        TextView questionView = dialogView.findViewById(R.id.questionTextView);
        Button trueBtn = dialogView.findViewById(R.id.trueButton);
        Button falseBtn = dialogView.findViewById(R.id.falseButton);
        questionView.setText(question);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();


        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestionToList(question, true);
                dialog.dismiss();
            }
        });

        falseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestionToList(question, false);
                dialog.dismiss();
            }
        });
    }

    private void startYesNoActivity(String input, boolean answer) {
        Intent intent = new Intent(Question_Tags.this, YesOrNoActivity.class);
        intent.putExtra("input", input);
        intent.putExtra("answer", answer);
        startActivity(intent);
    }

    private void addQuestionToList(String question, boolean answer) {
        if (!question.trim().isEmpty()) {
            questions.add(question);
            questionMap.put(question, answer);
            adapter.notifyDataSetChanged();
        }
    }
    private void addVoiceBtn(){
        btn.setOnClickListener(view -> startVoiceRecognition(""));
    }

    private void addQuestions() {
        // English questions
        addQuestionToList("Is the sky blue?", true);
        addQuestionToList("Is an apple gold in color?", false);
        addQuestionToList("Is rainbow made up of 7 colors?", true);
        addQuestionToList("Do cats meow?", true);
        addQuestionToList("Do dogs fly?", false);

        // Spanish questions
        addQuestionToList("¿Es el cielo azul?", true);
        addQuestionToList("¿Es una manzana roja en color?", true);
        addQuestionToList("¿El cielo es verde?", false);

        // German questions
        addQuestionToList("Ist 1 + 2 gleich 3?", true);
        addQuestionToList("Ist die Erde flach?", false);

        //Italian questions
        addQuestionToList("Gli uccelli sanno nuotare?", false);
    }
}