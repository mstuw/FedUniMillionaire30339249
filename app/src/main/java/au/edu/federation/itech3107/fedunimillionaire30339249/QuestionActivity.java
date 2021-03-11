package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public static final String EXTRA_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS";
    public static final String EXTRA_CURRENT_QUESTION = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_CURRENT_QUESTION";
    public static final String EXTRA_SAFE_MONEY = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_SAFE_MONEY";

    private ArrayList<Question> questions;
    private Question currentQuestion;
    private int currentQuestionIndex;

    private double currentSafeMoney;

    // Views
    private Button btnConfirm;
    private TextView txtQuestion;
    private TextView txtPlayingFor;
    private TextView txtSafeMoney;
    private TextView txtQuestionProgress;
    private ProgressBar progressBar;
    private RadioGroup rgChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Find views.
        btnConfirm = findViewById(R.id.btnConfirm);
        txtQuestion = findViewById(R.id.txtQuestion);
        txtPlayingFor = findViewById(R.id.txtPlayingFor);
        txtSafeMoney = findViewById(R.id.txtSafeMoney);
        txtQuestionProgress = findViewById(R.id.txtQuestionProgress);
        progressBar = findViewById(R.id.progressBar);
        rgChoices = findViewById(R.id.rgChoices);
        rgChoices.setOnCheckedChangeListener(this);


        Intent intent = getIntent();
        questions = intent.getParcelableArrayListExtra(EXTRA_QUESTIONS);
        currentQuestionIndex = intent.getIntExtra(EXTRA_CURRENT_QUESTION, 0);
        currentSafeMoney = intent.getDoubleExtra(EXTRA_SAFE_MONEY, 0);

        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size())
            currentQuestion = questions.get(currentQuestionIndex);

        updateView();
    }

    /** Update views to reflect member variables. */
    private void updateView() {
        txtQuestion.setText(getString(R.string.question_format, currentQuestion.getQuestion()));
        txtPlayingFor.setText(getString(R.string.question_playing_for_format, currentQuestion.getValue()));
        txtSafeMoney.setText(getString(R.string.safe_money_format, currentSafeMoney));

        // Update progress information.
        progressBar.setProgress(currentQuestionIndex);
        progressBar.setMax(questions.size());
        txtQuestionProgress.setText(getString(R.string.question_progress_format, currentQuestionIndex, questions.size()));

        // Update question answers.
        rgChoices.removeAllViews();
        for (int index = 0; index < currentQuestion.getChoices().length; index++) {
            RadioButton btn = new RadioButton(getApplicationContext());
            btn.setTag(index);
            btn.setText(getString(R.string.question_choice_format, currentQuestion.getChoices()[index]));
            rgChoices.addView(btn);
        }

    }

    public void btnConfirmClicked(View view) {
        RadioButton checkedAnswer = findViewById(rgChoices.getCheckedRadioButtonId());
        if (currentQuestion.getCorrectChoice() == (int) checkedAnswer.getTag()) { // Answer was correct.
            if (currentQuestion.isSafeMoney())
                currentSafeMoney = currentQuestion.getValue();

        } else { // Answer was incorrect.
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return;

        }

        Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
        intent.putExtra(EXTRA_CURRENT_QUESTION, currentQuestionIndex + 1);
        intent.putExtra(EXTRA_SAFE_MONEY, currentSafeMoney);
        intent.putParcelableArrayListExtra(EXTRA_QUESTIONS, questions);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        btnConfirm.setEnabled(true);

    }

}