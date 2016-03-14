package at.mchristoph.lapse.app.utils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by Xris on 11.03.2016.
 */
public interface ApiJsonCallback {
    public void onSuccess(JSONObject response);
    public void onFailure(VolleyError error);
}
