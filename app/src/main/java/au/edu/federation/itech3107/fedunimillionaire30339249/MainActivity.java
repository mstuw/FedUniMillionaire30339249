package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.IQuestionSetCallback;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.api.OpenTriviaQuestionApiManager;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.api.QuestionApiManager;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.QuestionManager;
import au.edu.federation.itech3107.fedunimillionaire30339249.utility.Util;

public class MainActivity extends AppCompatActivity implements IQuestionSetCallback {
    private static final boolean RESET_QUESTIONS = false; // If true, the original questions from assets will overwrite the user-modified questions. Used during development.

    public static final String QUESTIONS_EASY = "questions-easy.json";
    public static final String QUESTIONS_MEDIUM = "questions-medium.json";
    public static final String QUESTIONS_HARD = "questions-hard.json";

    private static final String TAG = "MainActivity";

    public static final int REQUEST_EDIT_QUESTIONS = 1; // Request code for QuestionLibraryActivity.

    /**
     * The number of questions in a game
     */
    public static final int GAME_QUESTION_COUNT = 11;
    public static final int GAME_HOT_SEAT_TIMER = 15000; // The number of milliseconds per question.

    private QuestionManager questionManager;

    private Intent gameIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuestionApiManager api = new OpenTriviaQuestionApiManager(this);
        api.requestToken(); // Request a session token from the OpenTrivia Database, preventing duplicate questions.

        questionManager = new QuestionManager(api);

        // Copy the question files from the read-only assets location to the writable internal storage.
        if (!Util.doesFileExist(this, QUESTIONS_EASY) || RESET_QUESTIONS)
            Util.copyAssetToStorage(this, QUESTIONS_EASY, QUESTIONS_EASY);

        if (!Util.doesFileExist(this, QUESTIONS_MEDIUM) || RESET_QUESTIONS)
            Util.copyAssetToStorage(this, QUESTIONS_MEDIUM, QUESTIONS_MEDIUM);

        if (!Util.doesFileExist(this, QUESTIONS_HARD) || RESET_QUESTIONS)
            Util.copyAssetToStorage(this, QUESTIONS_HARD, QUESTIONS_HARD);

        reloadQuestions();

        // Defines the difficulty level the question manager will generate questions at.
        questionManager.clearDifficultySeq();
        questionManager.appendDifficultySeq(5, Difficulty.EASY); // The first 5 questions will be easy.
        questionManager.appendDifficultySeq(4, Difficulty.MEDIUM); // The next 4 questions will be medium.
        questionManager.appendDifficultySeq(2, Difficulty.HARD); // The next 2 questions will be hard (including any additional questions over 11).

        // Make questions 1, 6, and 11 safe money.
        questionManager.setSafeMoneyQuestions(true, 0, 5, 10);

        // Make questions from 1-11 have money values.
        questionManager.setQuestionValues(1000, 2000, 4000, 8000, 16000, 32000, 64000, 125000, 250000, 500000, 1000000);
    }

    // Reloads all questions from internal storage into the question manager.
    private void reloadQuestions() {
        // Load questions for each difficulty level.
        questionManager.clearQuestions();
        try {
            questionManager.loadQuestions(Util.readAllText(this, QUESTIONS_EASY));
            questionManager.loadQuestions(Util.readAllText(this, QUESTIONS_MEDIUM));
            questionManager.loadQuestions(Util.readAllText(this, QUESTIONS_HARD));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // The return results for "Manage Questions" button (QuestionLibraryActivity).
        if (requestCode == REQUEST_EDIT_QUESTIONS && resultCode == QuestionLibraryActivity.RESULT_REFRESH_QUESTIONS) {

            // Questions that need to be removed/added from the question manager. (User created/deleted questions while in the question library).
            List<Question> deletedQuestions = data.getParcelableArrayListExtra(QuestionLibraryActivity.EXTRA_DELETED_QUESTIONS);
            List<Question> addedQuestions = data.getParcelableArrayListExtra(QuestionLibraryActivity.EXTRA_ADDED_QUESTIONS);

            questionManager.removeAll(deletedQuestions);
            questionManager.addAll(addedQuestions);

            // Save all the questions within the question manager to internal storage.
            try {
                Log.d(TAG, "Saving questions...");
                Util.writeAllText(this, QUESTIONS_EASY, questionManager.toJson(Difficulty.EASY));
                Util.writeAllText(this, QUESTIONS_MEDIUM, questionManager.toJson(Difficulty.MEDIUM));
                Util.writeAllText(this, QUESTIONS_HARD, questionManager.toJson(Difficulty.HARD));
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Failed to save questions!", e);
            }

        }

    }

    /**
     * Called when the "Start Game" button is clicked. Starts the {@link QuestionActivity}.
     */
    public void btnStartGameClicked(View view) {
        gameIntent = new Intent(this, QuestionActivity.class);

        Toast.makeText(this, "Loading game...", Toast.LENGTH_SHORT).show();
        questionManager.createNextQuestionSet(GAME_QUESTION_COUNT, this);
    }


    /**
     * Called when the "Hot Seat" button is clicked. Starts the {@link QuestionActivity} with a countdown timer.
     */
    public void btnHotSeatClicked(View view) {
        gameIntent = new Intent(this, QuestionActivity.class);
        gameIntent.putExtra(QuestionActivity.EXTRA_GAME_TIMER, GAME_HOT_SEAT_TIMER);

        Toast.makeText(this, "Loading game...", Toast.LENGTH_SHORT).show();
        questionManager.createNextQuestionSet(GAME_QUESTION_COUNT, this);
    }

    /**
     * Called when the "Highscores" button is clicked.
     */
    public void btnHighscoresClicked(View view) {
        Intent intent = new Intent(this, HighscoresActivity.class);
        startActivity(intent);
    }

    /**
     * Called when the "Manage Questions" button is clicked.
     */
    public void btnEditQuestionsClicked(View view) {
        Intent intent = new Intent(this, QuestionLibraryActivity.class);

        intent.putParcelableArrayListExtra(QuestionLibraryActivity.EXTRA_QUESTIONS, (ArrayList<Question>) questionManager.getAllQuestions());

        startActivityForResult(intent, REQUEST_EDIT_QUESTIONS);
    }

    @Override
    public void onQuestionSetCreated(List<GameQuestion> questions1, List<GameQuestion> questions2) {
        if (gameIntent == null)
            return;

        // TODO: Temp - Log info about the question set.
        for (int i = 0; i < questions1.size(); i++) {
            GameQuestion q = questions1.get(i);

            // Find the index for the correct answer.
            int ci = 0;
            for (ci = 0; ci < q.getAnswers().size(); ci++) {
                if (q.getAnswers().get(ci).isCorrect())
                    break;
            }

            Log.d(TAG, (i + 1) + ": " + q.getDifficulty() + " - " + q.isSafeMoney() + " - " + q.getValue() + " - " + (ci + 1) + " - " + q.getQuestionText());
        }


        gameIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        gameIntent.putParcelableArrayListExtra(QuestionActivity.EXTRA_QUESTIONS, (ArrayList<GameQuestion>) questions1);
        gameIntent.putParcelableArrayListExtra(QuestionActivity.EXTRA_LIFELINE_QUESTIONS, (ArrayList<GameQuestion>) questions2);
        gameIntent.putExtra(QuestionActivity.EXTRA_CURRENT_QUESTION, 0);

        startActivity(gameIntent);
        gameIntent = null;
    }

}