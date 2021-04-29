package au.edu.federation.itech3107.fedunimillionaire30339249.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

/**
 * This class represents a multiple-choice question.
 */
public class Question implements Parcelable {
    private static final String TAG = "Question";

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    /**
     * The question category (e.g. General Knowledge) (Note: Currently unused).
     */
    protected String category;

    /**
     * The question difficulty, allows for question sets that increase in difficulty. See {@link QuestionManager#appendDifficultySeq(int, Difficulty)}.
     */
    protected Difficulty difficulty;

    /**
     * The actual question.
     */
    protected String questionText;

    /**
     * The  answers to this question, both correct and incorrect.
     */
    protected List<Answer> answers;

    public Question(String category, Difficulty difficulty, String questionText, List<Answer> answers) {
        this.category = category;
        this.difficulty = difficulty;
        this.questionText = questionText;
        this.answers = answers;
    }

    protected Question(Parcel in) {
        category = in.readString();
        difficulty = Difficulty.values()[in.readInt()];
        questionText = in.readString();

        answers = new ArrayList<>();
        in.readTypedList(answers, Answer.CREATOR);
    }

    /**
     * Create a new question from parsing the provided {@link JSONObject}.
     *
     * @throws JSONException if an exception occurs when parsing the {@link JSONObject}.
     */
    public Question(JSONObject jQuestion) throws JSONException {
        category = jQuestion.getString("category");
        difficulty = Difficulty.valueOfIgnoreCase(jQuestion.getString("difficulty"));
        questionText = jQuestion.getString("question");

        answers = new ArrayList<>();

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

    }

    /**
     * Returns a set of indices containing one correct and one incorrect answer.
     */
    public Set<Integer> trimmedAnswers() {
        Set<Integer> trimmedAnswers = new HashSet<>();

        boolean hasIncorrect = false;
        boolean hasCorrect = false;

        for (int index = 0; index < answers.size(); index++) {
            Answer answer = answers.get(index);

            if (answer.isCorrect()) {
                if (!hasCorrect)
                    trimmedAnswers.add(index);
                hasCorrect = true;
            } else {
                if (!hasIncorrect)
                    trimmedAnswers.add(index);
                hasIncorrect = true;
            }

            if (hasIncorrect && hasCorrect)
                break;
        }

        return trimmedAnswers;
    }

    /**
     * Returns a list of random percentages using dynamic weighting depending on question difficulty. Provide set of blacklisted answer indices to ignore, or null.
     */
    public List<Float> generatePercentages(Set<Integer> trimmedAnswers) {
        List<Float> percentages = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        int sum = 0;

        Random r = new Random();
        for (int index = 0; index < answers.size(); index++) {
            if (trimmedAnswers != null && !trimmedAnswers.contains(index))
                continue;

            Answer answer = answers.get(index);

            // Most likely not correct...
            float weighting = answer.isCorrect() ? difficulty.getCorrectAnswerWeighting() : 1f - difficulty.getCorrectAnswerWeighting();

            int max = (int) (100f * weighting);
            int min = (int) (max * (1f - difficulty.getSpread()));

            int value = r.nextInt((max - min) + 1) + min;

            sum += value;
            values.add(value);
        }

        for (int index = 0; index < values.size(); index++) {
            float value = ((float) values.get(index) / sum) * 100f;
            percentages.add(value);
        }

        return percentages;
    }


    /**
     * Returns a {@link JSONObject} representing this question. The object is compatible with the constructor {@link #Question(JSONObject)}.
     *
     * @return a {@link JSONObject} representing this question.
     */
    public JSONObject asJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("category", category);
        obj.put("difficulty", difficulty.toString().toLowerCase());
        obj.put("question", questionText);

        JSONArray incorrectAnswers = new JSONArray();
        for (Answer answer : answers) {
            if (answer.isCorrect()) {
                obj.put("correct_answer", answer.getText());
            } else {
                incorrectAnswers.put(answer.getText());
            }
        }

        obj.put("incorrect_answers", incorrectAnswers);

        return obj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(category);
        dest.writeInt(difficulty.ordinal());
        dest.writeString(questionText);
        dest.writeTypedList(answers);
    }

    public String getCategory() {
        return category;
    }

    /**
     * The question difficulty, allows for question sets that increase in difficulty. See {@link QuestionManager#appendDifficultySeq(int, Difficulty)}.
     *
     * @return the question difficulty, allows for question sets that increase in difficulty.
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * The actual question.
     *
     * @return the actual question.
     */
    public String getQuestionText() {
        return questionText;
    }

    /**
     * The answers to this question, both correct and incorrect.
     *
     * @return the answers to this question, both correct and incorrect.
     */
    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return getDifficulty() == question.getDifficulty() &&
                Objects.equals(getQuestionText(), question.getQuestionText()) &&
                Objects.equals(getAnswers(), question.getAnswers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDifficulty(), getQuestionText(), getAnswers());
    }

}
