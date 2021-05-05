package au.edu.federation.itech3107.fedunimillionaire30339249.utility;

import org.json.JSONObject;

public interface IHttpResponse {
    void onResponse(int id, JSONObject obj);
}