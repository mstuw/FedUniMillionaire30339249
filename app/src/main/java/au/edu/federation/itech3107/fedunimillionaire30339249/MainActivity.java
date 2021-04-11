package au.edu.federation.itech3107.fedunimillionaire30339249;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Question;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.QuestionManager;

public class MainActivity extends AppCompatActivity {
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

    private final QuestionManager questionManager = new QuestionManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Copy the question files from the read-only assets location to the writable internal storage.
        if (!doesFileExist(QUESTIONS_EASY) || RESET_QUESTIONS)
            copyAssetToStorage(QUESTIONS_EASY, QUESTIONS_EASY);

        if (!doesFileExist(QUESTIONS_MEDIUM) || RESET_QUESTIONS)
            copyAssetToStorage(QUESTIONS_MEDIUM, QUESTIONS_MEDIUM);

        if (!doesFileExist(QUESTIONS_HARD) || RESET_QUESTIONS)
            copyAssetToStorage(QUESTIONS_HARD, QUESTIONS_HARD);

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
            questionManager.loadQuestions(readAllText(QUESTIONS_EASY));
            questionManager.loadQuestions(readAllText(QUESTIONS_MEDIUM));
            questionManager.loadQuestions(readAllText(QUESTIONS_HARD));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_QUESTIONS && resultCode == QuestionLibraryActivity.RESULT_REFRESH_QUESTIONS) {

            List<Question> deletedQuestions = data.getParcelableArrayListExtra(QuestionLibraryActivity.EXTRA_DELETED_QUESTIONS);

            questionManager.removeAll(deletedQuestions);

            try {
                Log.d(TAG,"Saving questions...");
                writeAllText(QUESTIONS_EASY, questionManager.toJson(Difficulty.EASY));
                writeAllText(QUESTIONS_MEDIUM, questionManager.toJson(Difficulty.MEDIUM));
                writeAllText(QUESTIONS_HARD, questionManager.toJson(Difficulty.HARD));
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Failed to save questions!", e);
            }

        }

    }

    private boolean doesFileExist(String name) {
        for (String file : fileList()) {
            if (file.equals(name))
                return true;
        }
        return false;
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

    public void btnHighscoresClicked(View view) {
        Intent intent = new Intent(this, HighscoresActivity.class);

        startActivity(intent);
    }

    public void btnEditQuestionsClicked(View view) {
        Intent intent = new Intent(this, QuestionLibraryActivity.class);

        intent.putParcelableArrayListExtra(QuestionLibraryActivity.EXTRA_QUESTIONS, (ArrayList<Question>) questionManager.getAllQuestions());

        startActivityForResult(intent, REQUEST_EDIT_QUESTIONS);
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
     * Returns all text from the specified internal storage file.
     *
     * @param filepath the internal storage filepath.
     * @return all text from the specified internal storage file.
     * @throws IOException an IO exception occurred.
     */
    private String readAllText(String filepath) throws IOException {
        return readAllText(openFileInput(filepath));
    }

    /**
     * Writes all the text specified into the specified internal storage file.
     *
     * @param filepath the filepath to save the text.
     * @param text     the string that will be written.
     * @throws IOException an IO exception occurred.
     */
    private void writeAllText(String filepath, String text) throws IOException {
        try (FileOutputStream fos = openFileOutput(filepath, Context.MODE_PRIVATE)) {
            fos.write(text.getBytes());
        }
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

    /**
     * Copies the specified src file from assets and writes it into the specified internal storage location.
     */
    private void copyAssetToStorage(String src, String dst) {
        try (InputStream is = getAssets().open(src)) {
            try (FileOutputStream fos = openFileOutput(dst, Context.MODE_PRIVATE)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1)
                    fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to copy asset to storage!", e);
        }
    }

}