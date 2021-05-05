package au.edu.federation.itech3107.fedunimillionaire30339249.data.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Answer;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.GameQuestion;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.IQuestionSetCallback;
import au.edu.federation.itech3107.fedunimillionaire30339249.data.QuestionManager;
import au.edu.federation.itech3107.fedunimillionaire30339249.utility.IHttpResponse;
import au.edu.federation.itech3107.fedunimillionaire30339249.utility.JsonHttpRequestTask;

/**
 * A base class representing a question manager that uses a Http JSON API to fetch questions.
 */
public abstract class QuestionApiManager implements IHttpResponse {
    private static final String TAG = "QuestionApiManager";

    // Used by the callback for JsonHttpRequestTask
    private static final int REQUEST_TOKEN_ID = -1;
    private static final int RESET_TOKEN_ID = 0;
    private static final int QUESTION_SET_ID = 1;


    private final Context context;

    private final List<JSONObject> responses = new ArrayList<>();

    private String token;

    private QuestionManager questionManager;
    private IQuestionSetCallback callback;
    private int expectedResponseCount;


    public QuestionApiManager(Context context) {
        this.context = context;
    }

    /**
     * Asynchronously request for a session token. Method fails silently if no internet access is available, or implementation doesn't support tokens.
     */
    public void requestToken() {
        if (!hasInternetAccess())
            return;
        String url = getRequestTokenUrl();
        if (url != null)
            new JsonHttpRequestTask(REQUEST_TOKEN_ID, this).execute(url);
    }

    /**
     * Asynchronously request to reset the session token. Method fails silently if no internet access is available, or implementation doesn't support resetting tokens.
     */
    public void resetToken() {
        if (!hasInternetAccess())
            return;
        String url = getResetTokenUrl(token);
        if (url != null)
            new JsonHttpRequestTask(RESET_TOKEN_ID, this).execute(url);
    }

    protected abstract String getRequestTokenUrl();

    protected abstract String getResetTokenUrl(String token);

    protected abstract String buildUrl(String token, int questionCount, Difficulty difficulty);


    /**
     * Generate two new question sets asynchronously using a Http JSON API.
     */
    public void createNextQuestionSet(QuestionManager questionManager, int count, IQuestionSetCallback callback) {
        if (this.callback != null)
            throw new UnsupportedOperationException("Cannot fetch another question set while already waiting for another!");

        this.questionManager = questionManager;
        this.callback = callback;

        Log.d(TAG, "-----------------------");

        expectedResponseCount = questionManager.getDifficultySequences().size();
        responses.clear();

        int questionsNeeded = count;

        for (QuestionManager.DifficultyLevel level : questionManager.getDifficultySequences()) {
            int questionCount = level.getCount();

            String url = buildUrl(token, questionCount * 2, level.getDifficulty()); // questionCount * 2 , as two question sets are generated.
            new JsonHttpRequestTask(QUESTION_SET_ID, this).execute(url);

            questionsNeeded -= questionCount;
            if (questionsNeeded <= 0)
                break;
        }

        // If we need more questions than defined in the difficulty sequence map, use the last difficulty.
        if (questionsNeeded > 0) {
            Difficulty lastDifficulty = questionManager.getDifficultySequences().get(questionManager.getDifficultySequences().size() - 1).getDifficulty();

            String url = buildUrl(token, questionsNeeded * 2, lastDifficulty); // questionsNeeded * 2 , as two question sets are generated.
            new JsonHttpRequestTask(QUESTION_SET_ID, this).execute(url);
        }
    }

    @Override
    public void onResponse(int id, JSONObject obj) {
        switch (id) {
            case REQUEST_TOKEN_ID: // The response for requestToken()
                try {
                    token = obj.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case QUESTION_SET_ID: // The response for questions.
                responses.add(obj);

                if (responses.size() == expectedResponseCount) {

                    ArrayList<GameQuestion> questions1 = new ArrayList<>();
                    ArrayList<GameQuestion> questions2 = new ArrayList<>();

                    // TODO: Move parsing of JSONObject responses to derived class.
                    for (JSONObject jResponse : responses) {
                        try {
                            // Convert the response into a list of GameQuestions.
                            JSONArray jQuestions = jResponse.getJSONArray("results");
                            for (int i = 0; i < jQuestions.length(); i++) {
                                JSONObject jQuestion = jQuestions.getJSONObject(i);

                                Difficulty difficulty = Difficulty.valueOfIgnoreCase(jQuestion.getString("difficulty"));
                                String category = jQuestion.getString("category");
                                String questionText = jQuestion.getString("question");

                                boolean isQuestion1 = i < jQuestions.length() / 2;

                                // Determine question value and safe money by using question index.
                                double value = questionManager.getQuestionValues().get(Math.min(isQuestion1 ? questions1.size() : questions2.size(), questionManager.getQuestionValues().size() - 1));
                                boolean isSafeMoney = questionManager.getSafeMoneyIndices().contains(isQuestion1 ? questions1.size() : questions2.size());

                                List<Answer> answers = new ArrayList<>();

                                JSONArray incorrectAnswers = jQuestion.getJSONArray("incorrect_answers");
                                for (int y = 0; y < incorrectAnswers.length(); y++) {
                                    String incorrectAnswer = incorrectAnswers.getString(y);
                                    answers.add(new Answer(incorrectAnswer, false));
                                }

                                // Add correct answer.
                                String correctAnswer = jQuestion.getString("correct_answer");
                                answers.add(new Answer(correctAnswer, true));

                                Collections.shuffle(answers); // Shuffle answers, preventing the correct answer always being at the bottom of the list.


                                GameQuestion question = new GameQuestion(category, difficulty, questionText, answers, value, isSafeMoney);
                                if (isQuestion1) {
                                    questions1.add(question);
                                } else {
                                    questions2.add(question);
                                }
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "Failed to parse JSON response!", e);
                        }

                        Log.d(TAG, jResponse.toString());

                    }

                    callback.onQuestionSetCreated(questions1, questions2);
                    callback = null;
                    questionManager = null;

                    responses.clear();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Returns true if we have internet access.
     */
    public boolean hasInternetAccess() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}