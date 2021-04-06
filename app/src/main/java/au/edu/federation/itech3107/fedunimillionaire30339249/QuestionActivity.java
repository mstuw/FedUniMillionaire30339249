package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Answer;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;

public class QuestionActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "QuestionActivity";

    public static final String EXTRA_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS";
    public static final String EXTRA_CURRENT_QUESTION = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_CURRENT_QUESTION";
    public static final String EXTRA_SAFE_MONEY = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_SAFE_MONEY";
    public static final String EXTRA_QUESTIONS_ANSWERED_CORRECTLY = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS_ANSWERED_CORRECTLY";
    public static final String EXTRA_GAME_TIMER = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_GAME_TIMER";

    private CountDownTimer timer;

    private ArrayList<GameQuestion> questions;
    private int currentQuestionIndex; // The current question index (zero-based).
    private int questionsAnsweredCorrectly; // The number of questions answered correctly.
    private int initialCountdownTime; // The number of milliseconds until the game will end. Zero if disabled.
    private double currentSafeMoney; // The amount of money the user has already won.

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

        // Get extras.
        Intent intent = getIntent();
        questions = intent.getParcelableArrayListExtra(EXTRA_QUESTIONS);
        currentQuestionIndex = intent.getIntExtra(EXTRA_CURRENT_QUESTION, 0);
        currentSafeMoney = intent.getDoubleExtra(EXTRA_SAFE_MONEY, 0);
        questionsAnsweredCorrectly = intent.getIntExtra(EXTRA_QUESTIONS_ANSWERED_CORRECTLY, 0);


        ProgressBar countdownProgressBar = findViewById(R.id.progressBarCountdown);

        // Hot seat mode enabled, init count down timer.
        if (intent.hasExtra(EXTRA_GAME_TIMER)) {
            initialCountdownTime = intent.getIntExtra(EXTRA_GAME_TIMER, 15000);

            countdownProgressBar.setVisibility(View.VISIBLE);
            countdownProgressBar.setMax(initialCountdownTime);

            timer = new CountDownTimer(initialCountdownTime, 40) {
                public void onTick(long millisUntilFinished) {
                    countdownProgressBar.setProgress((int) (initialCountdownTime - millisUntilFinished));
                }

                public void onFinish() {
                    Log.d(TAG, "Countdown finished - endGame()");
                    endGame();
                }

            };
            timer.start();

        } else { // Hot seat mode disabled.
            initialCountdownTime = 0; // Explicitly set zero, ensuring value doesn't get passed as an extra.
            countdownProgressBar.setVisibility(View.INVISIBLE);
        }

        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            Log.d(TAG, "Stopping countdown timer...");
        }
    }

    /**
     * Update views to reflect member variables.
     */
    private void updateView() {
        GameQuestion question = getCurrentQuestion();

        txtQuestion.setText(getString(R.string.question_format, question.getQuestionText()));
        txtPlayingFor.setText(getString(R.string.question_playing_for_format, question.getValue()));
        txtSafeMoney.setText(getString(R.string.safe_money_format, currentSafeMoney));

        // Update progress information.
        progressBar.setProgress(currentQuestionIndex + 1);
        progressBar.setMax(questions.size());
        txtQuestionProgress.setText(getString(R.string.question_progress_format, currentQuestionIndex + 1, questions.size()));

        // Update question answers.
        rgChoices.removeAllViews();
        for (int index = 0; index < question.getAnswers().size(); index++) {
            Answer answer = question.getAnswers().get(index);

            RadioButton btn = new RadioButton(getApplicationContext());

            btn.setTag(index);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btn.setTextColor(getResources().getColor(R.color.black, getTheme()));
            btn.setText(getString(R.string.question_choice_format, answer.getText()));

            rgChoices.addView(btn);
        }

    }

    public void btnConfirmClicked(View view) {
        if (timer != null)
            timer.cancel();

        RadioButton checkedAnswer = findViewById(rgChoices.getCheckedRadioButtonId());
        int checkedIndex = (int) checkedAnswer.getTag();

        GameQuestion question = getCurrentQuestion();

        if (question.getAnswers().get(checkedIndex).isCorrect()) { // Answer was correct.
            questionsAnsweredCorrectly++;

            if (question.isSafeMoney())
                currentSafeMoney = question.getValue();

            if (currentQuestionIndex == questions.size() - 1) { // Reached the end of the questions. User has won.
                endGame();
            } else {
                // Next question...
                Intent intent = new Intent(this, QuestionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                intent.putParcelableArrayListExtra(EXTRA_QUESTIONS, questions);
                intent.putExtra(EXTRA_CURRENT_QUESTION, currentQuestionIndex + 1);
                intent.putExtra(EXTRA_SAFE_MONEY, currentSafeMoney);
                intent.putExtra(EXTRA_QUESTIONS_ANSWERED_CORRECTLY, questionsAnsweredCorrectly);

                if (initialCountdownTime > 0)
                    intent.putExtra(EXTRA_GAME_TIMER, initialCountdownTime);

                startActivity(intent);

            }

        } else { // Answer was incorrect.
            endGame();
        }

    }

    /**
     * @return The current question this activity should be displaying / handling.
     */
    private GameQuestion getCurrentQuestion() {
        return questions.get(currentQuestionIndex);
    }

    /**
     * Starts the {@link GameEndActivity} with the required extras needed. Can be used for both winning and losing states.
     */
    private void endGame() {
        Intent intent = new Intent(this, GameEndActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        intent.putExtra(GameEndActivity.EXTRA_WINNINGS_AMOUNT, currentSafeMoney);
        intent.putExtra(GameEndActivity.EXTRA_TOTAL_QUESTIONS, questions.size());
        intent.putExtra(GameEndActivity.EXTRA_TOTAL_CORRECT, questionsAnsweredCorrectly);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        btnConfirm.setEnabled(true);
    }

}