package nl.vinsev.workoutapp.service;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

public class NetworkManager {
    private static NetworkManager networkManager;

    public RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (null == networkManager)
            networkManager = new NetworkManager(context);
        return networkManager;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == networkManager) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(Context context) first");
        }
        return networkManager;
    }

    public void getRequestJsonArray(String url, final JsonListener listener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        listener.getResult(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Log.d("NetworkManager", "Something went wrong");
                    error.printStackTrace();
                    try {
                        listener.getResult(null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        });
        requestQueue.add(request);
    }
}
