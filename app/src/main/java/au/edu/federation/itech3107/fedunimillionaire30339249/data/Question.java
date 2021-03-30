package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.os.Parcelable;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a valued multiple-choice question. It implements {@link Parcelable} allowing it to be used as an extra for Intents.
 */
public class Question {

    protected final String category;
    protected final Difficulty difficulty;
    protected final String questionText;

    protected List<Answer> answers;

    public Question(String category, Difficulty difficulty, String questionText, List<Answer> answers) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.answers = answers;
    }

    public Question(JSONObject jQuestion) throws JSONException {
        category = jQuestion.getString("category");
        difficulty = Difficulty.valueOfIgnoreCase(jQuestion.getString("difficulty"));
        questionText = jQuestion.getString("question");

        List<Answer> answers = new ArrayList<>();

        // Add incorrect answers.
        JSONArray incorrectAnswers = jQuestion.getJSONArray("incorrect_answers");
        for (int i = 0; i < incorrectAnswers.length(); i++) {
            String incorrectAnswer = incorrectAnswers.getString(i);
            answers.add(new Answer(incorrectAnswer, false));
        }

        // Add correct answer.
        String correctAnswer = jQuestion.getString("correct_answer");
        answers.add(new Answer(correctAnswer, true));

        Collections.shuffle(answers); // Shuffle answers, preventing the correct answer always being at the bottom of the list.

        this.answers = answers;
    }


    public String getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<Answer> getAnswers() {
        return answers;
    }


    /**
     * @deprecated
     */
    public int getCorrectChoice() {
        return 0;
    }

    /**
     * @deprecated
     */
    public double getValue() {
        return 0;
    }

    /**
     * @deprecated
     */
    public String[] getChoices() {
        return new String[0];
    }

    /**
     * @deprecated
     */
    public String getQuestion() {
        return "";
    }
}
