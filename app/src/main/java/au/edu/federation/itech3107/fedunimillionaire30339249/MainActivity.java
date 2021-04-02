package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.QuestionManager;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    /**
     * The number of questions in a game
     */
    public static final int GAME_QUESTION_COUNT = 11;
    public static final int GAME_HOT_SEAT_TIMER = 15000;

    private final QuestionManager questionManager = new QuestionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load questions for each difficulty level.
        questionManager.clearQuestions();
        try {
            questionManager.loadQuestions(readAllText("questions-easy.json"));
            questionManager.loadQuestions(readAllText("questions-medium.json"));
            questionManager.loadQuestions(readAllText("questions-hard.json"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

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


    /**
     * Called when the "Start Game" button is clicked. Starts the {@link QuestionActivity}.
     */
    public void btnStartGameClicked(View view) {
        Intent intent = createGameActivity(GAME_QUESTION_COUNT);
        startActivity(intent);
    }

    /**
     * Called when the "Hot Seat" button is clicked. Starts the {@link QuestionActivity} with a countdown timer.
     */
    public void btnHotSeatClicked(View view) {
        Intent intent = createGameActivity(GAME_QUESTION_COUNT);

        intent.putExtra(QuestionActivity.EXTRA_GAME_TIMER, GAME_HOT_SEAT_TIMER);

        startActivity(intent);
    }

    public void btnHighscores(View view){
        Intent intent = new Intent(this, HighscoresActivity.class);

        startActivity(intent);
    }

    private Intent createGameActivity(int questionCount) {
        ArrayList<GameQuestion> questions = (ArrayList<GameQuestion>) questionManager.createNextQuestionSet(questionCount);


        // TODO: Temp - Log info about the question set.
        for (int i = 0; i < questions.size(); i++) {
            GameQuestion q = questions.get(i);

            // Find the index for the correct answer.
            int ci = 0;
            for (ci = 0; ci < q.getAnswers().size(); ci++) {
                if (q.getAnswers().get(ci).isCorrect())
                    break;
            }

            Log.d(TAG, (i + 1) + ": " + q.getDifficulty() + " - " + q.isSafeMoney() + " - " + q.getValue() + " - " + (ci + 1) + " - " + q.getQuestionText());
        }


        Intent intent = new Intent(this, QuestionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        intent.putParcelableArrayListExtra(QuestionActivity.EXTRA_QUESTIONS, questions);
        intent.putExtra(QuestionActivity.EXTRA_CURRENT_QUESTION, 0);

        return intent;
    }

    /**
     * Returns all text from the specified asset file.
     *
     * @param filepath the asset filepath. See {@link android.content.res.AssetManager#open(String) AssetManager.open(String)}
     * @return all text from the specified asset file.
     * @throws IOException an IO exception occurred.
     */
    public String readAllText(String filepath) throws IOException {
        return readAllText(getAssets().open(filepath));
    }

    /**
     * Reads all text from the specified {@link InputStream} and returns a String.
     *
     * @param inputStream the {@link InputStream} to read.
     * @return a string containing all text from the specified {@link InputStream}.
     * @throws IOException an IO exception occurred.
     */
    public static String readAllText(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null)
                sb.append(line).append('\n');
        }
        return sb.toString();
    }

}