package au.edu.federation.itech3107.fedunimillionaire30339249.utility;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonHttpRequestTask extends AsyncTask<String, Void, JSONObject> {
    private static final String TAG = "JsonHttpRequestTask";

    private final int id;
    private final IHttpResponse response;

    /**
     * Create a new Async JSON HTTP Request Task.
     *
     * @param id       A unique number used to identify this task, when using the same IHttpResponse handler for multiple request tasks.
     * @param response A callback for responses.
     */
    public JsonHttpRequestTask(int id, IHttpResponse response) {
        this.id = id;
        this.response = response;
    }


    @Override
    protected JSONObject doInBackground(String... urls) {
        Log.d(TAG, "Get: " + urls[0]);

        try {
            URL url = new URL(urls[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            try (InputStream is = conn.getInputStream()) {
                String response = Util.readAllText(is);
                return new JSONObject(response);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Failed to read response!", e);
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to open connection!", e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject obj) {
        response.onResponse(id, obj);
    }

}