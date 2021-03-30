package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class QuestionManager {
    public static final String TAG = "QuestionManager";

    private static class DifficultyLevel {
        private final int count;
        private final Difficulty difficulty;

        public DifficultyLevel(int count, Difficulty difficulty) {
            this.count = count;
            this.difficulty = difficulty;
        }

    }

    // A map containing questions of each difficulty level.
    private final Map<Difficulty, List<Question>> questions = new HashMap<>();

    private final List<DifficultyLevel> difficultySequence = new ArrayList<>();
    private final Set<Integer> safeMoneyIndices = new HashSet<>();

    public QuestionManager() {
    }

    public void appendDifficultySeq(int count, Difficulty difficulty) {
        difficultySequence.add(new DifficultyLevel(count, difficulty));
    }

    public void addQuestion(Question question) {
        Log.d(TAG, "  - Adding question for difficulty " + question.getDifficulty() + ": " + question.getQuestionText());

        if (questions.containsKey(question.getDifficulty())) {
            questions.get(question.getDifficulty()).add(question); // add into existing list.
        } else {
            List<Question> dQuestions = new ArrayList<>();
            dQuestions.add(question);
            questions.put(question.getDifficulty(), dQuestions);
        }
    }

    public void loadQuestions(String json) throws JSONException {
        Log.d(TAG, "Loading questions from JSON...");

        JSONObject jRoot = new JSONObject(json);

        JSONArray jQuestions = jRoot.getJSONArray("questions");
        for (int i = 0; i < jQuestions.length(); i++) {
            Question question = new Question(jQuestions.getJSONObject(i));
            addQuestion(question);
        }
    }

    public void setSafeMoneyQuestions(boolean isSafe, int... indices) {
        for (int index : indices) {
            if (isSafe) {
                safeMoneyIndices.add(index);
            } else {
                safeMoneyIndices.remove(index);
            }
        }
    }

    /**
     * Returns a list of questions (limited to the specified count) and that follows the difficulty sequence defined in this {@link QuestionManager}
     *
     * @param count The number of questions to return.
     * @returns an {@link ArrayList} of questions.
     */
    public List<GameQuestion> createNextQuestionSet(int count) {
        int difficultyIndex = 0; // The current difficulty level index.
        int questionsOfCurrentDifficulty = 0; // The number of questions added while at the current difficulty.

        List<GameQuestion> questionList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            DifficultyLevel level = difficultySequence.get(difficultyIndex);

            // Get all the questions of a current difficulty.
            List<Question> questionsOfDifficulty = questions.get(level.difficulty);

            // Add a random question of the current difficulty to the question list.
            int index = (int) (Math.random() * questionsOfDifficulty.size());

            Question q = questionsOfDifficulty.get(index);
            boolean isSafe = safeMoneyIndices.contains(i);

            questionList.add(new GameQuestion(q, isSafe));

            questionsOfCurrentDifficulty++;

            // We have reached the required number of questions for this difficulty level.
            if (questionsOfCurrentDifficulty == level.count) {
                questionsOfCurrentDifficulty = 0;

                // Goto the next difficulty level in the sequence.
                difficultyIndex = Math.min(difficultyIndex + 1, difficultySequence.size() - 1);
            }

        }
        return questionList;
    }


    public void clearDifficultySeq() {
        difficultySequence.clear();
    }

    public void clearQuestions() {
        questions.clear();
    }

    public void clearQuestions(Difficulty difficulty) {
        List<Question> dQuestions = questions.get(difficulty);
        if (dQuestions != null)
            dQuestions.clear();
    }

}
