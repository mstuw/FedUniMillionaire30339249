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
import java.util.List;
import java.util.Set;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Answer;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;

public class QuestionActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "QuestionActivity";

    public static final String EXTRA_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS";
    public static final String EXTRA_CURRENT_QUESTION = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_CURRENT_QUESTION";
    public static final String EXTRA_SAFE_MONEY = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_SAFE_MONEY";
    public static final String EXTRA_QUESTIONS_ANSWERED_CORRECTLY = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_QUESTIONS_ANSWERED_CORRECTLY";
    public static final String EXTRA_GAME_TIMER = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_GAME_TIMER";
    public static final String EXTRA_LIFELINE_USED_5050 = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_LIFELINE_USED_5050";
    public static final String EXTRA_LIFELINE_USED_ATA = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_LIFELINE_USED_ATA";
    public static final String EXTRA_LIFELINE_USED_SWITCH = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_LIFELINE_USED_SWITCH";
    public static final String EXTRA_LIFELINE_QUESTIONS = "au.edu.federation.itech3107.fedunimillionaire30339249.EXTRA_LIFELINE_QUESTIONS";

    private CountDownTimer timer;

    private ArrayList<GameQuestion> questions;
    private ArrayList<GameQuestion> lifelineQuestions;
    private GameQuestion currentQuestion;

    private int currentQuestionIndex; // The current question index (zero-based).
    private int questionsAnsweredCorrectly; // The number of questions answered correctly.
    private int initialCountdownTime; // The number of milliseconds until the game will end. Zero if disabled.
    private double currentSafeMoney; // The amount of money the user has already won.

    // Views
    private Button btnConfirm;
    private Button btnLifeline5050;
    private Button btnLifelineAskTheAudience;
    private Button btnLifelineSwitch;

    private TextView txtQuestion;
    private TextView txtPlayingFor;
    private TextView txtSafeMoney;
    private TextView txtQuestionProgress;
    private ProgressBar progressBar;
    private RadioGroup rgChoices;

    // True if the lifeline 50:50 was used during the game session.
    private boolean isLifelineUsed5050;

    // True if the lifeline ask the audience was used during the game session.
    private boolean isLifelineUsedAskTheAudience;

    // True if the lifeline switch was used during the game session.
    private boolean isLifelineUsedSwitch;

    private List<Float> percentages = null;
    private Set<Integer> trimmedAnswers = null;

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

        btnLifeline5050 = findViewById(R.id.btnLifeline5050);
        btnLifelineAskTheAudience = findViewById(R.id.btnLifelineAskTheAudience);
        btnLifelineSwitch = findViewById(R.id.btnLifelineSwitch);

        rgChoices = findViewById(R.id.rgChoices);
        rgChoices.setOnCheckedChangeListener(this);

        // Get extras.
        Intent intent = getIntent();
        questions = intent.getParcelableArrayListExtra(EXTRA_QUESTIONS);
        currentQuestionIndex = intent.getIntExtra(EXTRA_CURRENT_QUESTION, 0);
        currentSafeMoney = intent.getDoubleExtra(EXTRA_SAFE_MONEY, 0);
        questionsAnsweredCorrectly = intent.getIntExtra(EXTRA_QUESTIONS_ANSWERED_CORRECTLY, 0);

        isLifelineUsed5050 = intent.getBooleanExtra(EXTRA_LIFELINE_USED_5050, false);
        isLifelineUsedAskTheAudience = intent.getBooleanExtra(EXTRA_LIFELINE_USED_ATA, false);
        isLifelineUsedSwitch = intent.getBooleanExtra(EXTRA_LIFELINE_USED_SWITCH, false);
        lifelineQuestions = intent.getParcelableArrayListExtra(EXTRA_LIFELINE_QUESTIONS);

        currentQuestion = questions.get(currentQuestionIndex);

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
            countdownProgressBar.setVisibility(View.GONE);
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
        txtQuestion.setText(getString(R.string.question_format, currentQuestion.getQuestionText()));
        txtPlayingFor.setText(getString(R.string.question_playing_for_format, currentQuestion.getValue()));
        txtSafeMoney.setText(getString(R.string.safe_money_format, currentSafeMoney));

        // Update progress information.
        progressBar.setProgress(currentQuestionIndex + 1);
        progressBar.setMax(questions.size());
        txtQuestionProgress.setText(getString(R.string.question_progress_format, currentQuestionIndex + 1, questions.size()));

        // Update question answers.
        rgChoices.removeAllViews();


        // Disable the lifeline 50:50 button if used.
        btnLifeline5050.setEnabled(!isLifelineUsed5050);

        // Disable the lifeline Ask the Audience button if used.
        btnLifelineAskTheAudience.setEnabled(!isLifelineUsedAskTheAudience);

        // Disable the lifeline Switch button if used.
        btnLifelineSwitch.setEnabled(!isLifelineUsedSwitch);

        for (int index = 0; index < currentQuestion.getAnswers().size(); index++) {
            if (trimmedAnswers != null && !trimmedAnswers.contains(index))
                continue;

            Answer answer = currentQuestion.getAnswers().get(index);

            RadioButton btn = new RadioButton(getApplicationContext());

            btn.setTag(index);
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            btn.setTextColor(getResources().getColor(R.color.black, getTheme()));

            // Get the answer text with audience percentages or just the text.
            String text = (percentages != null) ?
                    getString(R.string.question_choice_audience_format, answer.getText(), percentages.get(index))
                    :
                    getString(R.string.question_choice_format, answer.getText());

            btn.setText(getString(R.string.question_choice_format, text));

            rgChoices.addView(btn);
        }

    }


    public void btnLifeline(View view) {
        switch (view.getId()) {
            case R.id.btnLifeline5050:
                isLifelineUsed5050 = true;

                trimmedAnswers = currentQuestion.trimmedAnswers();

                updateView();
                break;
            case R.id.btnLifelineAskTheAudience:
                isLifelineUsedAskTheAudience = true;

                percentages = currentQuestion.generatePercentages(trimmedAnswers);

                updateView();
                break;
            case R.id.btnLifelineSwitch:
                if (lifelineQuestions != null) {
                    isLifelineUsedSwitch = true;
                    currentQuestion = lifelineQuestions.get(currentQuestionIndex);
                    updateView();
                }
                break;
            default:
                break;
        }
    }

    public void btnConfirmClicked(View view) {
        if (timer != null)
            timer.cancel();

        RadioButton checkedAnswer = findViewById(rgChoices.getCheckedRadioButtonId());
        int checkedIndex = (int) checkedAnswer.getTag();

        if (currentQuestion.getAnswers().get(checkedIndex).isCorrect()) { // Answer was correct.
            questionsAnsweredCorrectly++;

            if (currentQuestion.isSafeMoney())
                currentSafeMoney = currentQuestion.getValue();

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
                intent.putExtra(EXTRA_LIFELINE_USED_5050, isLifelineUsed5050);
                intent.putExtra(EXTRA_LIFELINE_USED_ATA, isLifelineUsedAskTheAudience);
                //    intent.putExtra(EXTRA_LIFELINE_USED_SWITCH, isLifelineUsedSwitch);

                if (lifelineQuestions != null)
                    intent.putExtra(EXTRA_LIFELINE_QUESTIONS, lifelineQuestions);

                if (initialCountdownTime > 0)
                    intent.putExtra(EXTRA_GAME_TIMER, initialCountdownTime);

                startActivity(intent);

            }

        } else { // Answer was incorrect.
            endGame();
        }

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