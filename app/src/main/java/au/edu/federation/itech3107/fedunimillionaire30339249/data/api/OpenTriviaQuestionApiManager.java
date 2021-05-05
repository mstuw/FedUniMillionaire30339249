package au.edu.federation.itech3107.fedunimillionaire30339249.data.api;

import android.content.Context;
import android.net.Uri;

import au.edu.federation.itech3107.fedunimillionaire30339249.data.Difficulty;

public class OpenTriviaQuestionApiManager extends QuestionApiManager {
    private static final String TAG = "OpenTriviaQuestionApiManager";

    private static final String URL_SCHEME = "https";
    private static final String URL_HOST = "opentdb.com";
    private static final String URL_PATH = "api.php";
    private static final String URL_PATH_TOKEN = "api_token.php";

    private static final int QUESTION_CATEGORY = 9; // General Knowledge

    public OpenTriviaQuestionApiManager(Context context) {
        super(context);
    }


    @Override
    public String getRequestTokenUrl() {
        Uri.Builder builder = new Uri.Builder()
                .scheme(URL_SCHEME)
                .authority(URL_HOST)
                .appendPath(URL_PATH_TOKEN)
                .appendQueryParameter("command", "request");

        return builder.build().toString(); // https://opentdb.com/api_token.php?command=request
    }

    @Override
    public String getResetTokenUrl(String token) {
        if (token == null)
            return null;

        Uri.Builder builder = new Uri.Builder()
                .scheme(URL_SCHEME)
                .authority(URL_HOST)
                .appendPath(URL_PATH_TOKEN)
                .appendQueryParameter("command", "reset")
                .appendQueryParameter("token", token);

        return builder.build().toString(); // https://opentdb.com/api_token.php?command=reset&token=YOURTOKENHERE
    }

    @Override
    protected String buildUrl(String token, int questionCount, Difficulty difficulty) {
        Uri.Builder builder = new Uri.Builder()
                .scheme(URL_SCHEME)
                .authority(URL_HOST)
                .appendPath(URL_PATH)
                .appendQueryParameter("amount", Integer.toString(questionCount))
                .appendQueryParameter("difficulty", difficulty.toString().toLowerCase())
                .appendQueryParameter("category", Integer.toString(QUESTION_CATEGORY))
                .appendQueryParameter("type", "multiple");

        if (token != null)
            builder.appendQueryParameter("token", token);

        return builder.build().toString(); // https://opentdb.com/api.php?amount=11&category=9&difficulty=medium&type=multiple
    }

}
