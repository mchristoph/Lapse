package at.mchristoph.lapse.app.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import at.mchristoph.lapse.app.models.ServerDevice;

/**
 * Created by Xris on 11.03.2016.
 */
public class CameraApiUtil {
    private static final String LOG_TAG = CameraApiUtil.class.getSimpleName();
    private final String SERVICE_CAM = "camera";

    private static CameraApiUtil mInstance;
    private RequestQueue mRequestQueue;
    private Context mCtx;
    // API server device you want to send requests.
    private ServerDevice mDevice;
    private long mRequestId;

    private final Set<String> mSupportedApiSet = new HashSet<String>();
    private final Set<String> mAvailableCameraApiSet = new HashSet<String>();

    private CameraApiUtil(ServerDevice device, Context ctx){
        mDevice = device;
        mCtx = ctx;
        mRequestQueue = Volley.newRequestQueue(mCtx);
        mRequestId = 1;
    }

    public static CameraApiUtil GetInstance(){
        if (mInstance != null)
            return mInstance;

        return null;
    }

    public static CameraApiUtil GetInstance(ServerDevice device, Context ctx){
        if (mInstance == null){
            mInstance = new CameraApiUtil(device, ctx);
        }
        return mInstance;
    }

    /**
     * Request ID. Counted up after calling.
     *
     * @return
     */
    private long requestID() {
        return mRequestId++;
    }

    public void takePicture(){
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actTakePicture").put("params", new JSONArray()) //
                            .put("id", requestID()).put("version", "1.0");

            submitRequest(requestJson);
        }catch (JSONException e){

        }
    }

    public void getCameraMethodTypes(final ApiJsonCallback cb){
        Log.d(LOG_TAG, "getCameraMethodTypes() called.");

        String url = null;
        try {
            JSONObject requestJson = new JSONObject().put("method", "getMethodTypes")
            .put("params", new JSONArray().put("")).put("id", requestID()).put("version", "1.0");

            submitRequest(requestJson, cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupConnection(final ApiBooleanCallback cb){
        Log.d(LOG_TAG, "SetupConection called.");

        getCameraMethodTypes(new ApiJsonCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    loadSupportedApiList(response);

                    getAvcontentMethodTypes(new ApiJsonCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            loadSupportedApiList(response);

                            if (!isApiSupported("setCameraFunction")) {
                                openConnection(cb);
                            } else if (!isApiSupported("getEvent")) {
                                openConnection(cb);
                            } else {
                                getEvent(new ApiJsonCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        try {
                                            // confirm current camera status
                                            String cameraStatus = null;
                                            JSONArray resultsObj = response.getJSONArray("result");
                                            JSONObject cameraStatusObj = resultsObj.getJSONObject(1);
                                            String type = cameraStatusObj.getString("type");
                                            if ("cameraStatus".equals(type)) {
                                                cameraStatus = cameraStatusObj.getString("cameraStatus");
                                            } else {
                                                throw new IOException();
                                            }

                                            if (isShootingStatus(cameraStatus)) {
                                                Log.d(LOG_TAG, "camera function is Remote Shooting.");
                                                openConnection(cb);
                                            } else {
                                                // set Listener
                                                //startOpenConnectionAfterChangeCameraState();

                                                // set Camera function to Remote Shooting
                                                //replyJson = mRemoteApi.setCameraFunction("Remote Shooting");
                                            }
                                        } catch (Exception e) {

                                        }
                                    }

                                    @Override
                                    public void onFailure(VolleyError error) {
                                        cb.onFinished(false);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(VolleyError error) {

                        }
                    });
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(VolleyError error) {
                cb.onFinished(false);
            }
        });
    }

    private void openConnection(final ApiBooleanCallback cb){
        Log.d(LOG_TAG, "openConnection() called.");

        getAvailableApiList(new ApiJsonCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                loadAvailableCameraApiList(response);

                // check version of the server device
                /*if (isCameraApiAvailable("getApplicationInfo")) {
                    Log.d(LOG_TAG, "openConnection(): getApplicationInfo()");
                    replyJson = getApplicationInfo();
                    if (!isSupportedServerVersion(replyJson)) {
                        return;
                    }
                } else {
                    // never happens;
                    return;
                }*/

                // startRecMode if necessary.
                if (isCameraApiAvailable("startRecMode")) {
                    Log.d(LOG_TAG, "openConnection(): startRecMode()");

                    startRecMode(new ApiJsonCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            loadAvailableCameraApiList(response);
                        }

                        @Override
                        public void onFailure(VolleyError error) {

                        }
                    });
                }

                Log.d(LOG_TAG, "openConnection() successful.");
                cb.onFinished(true);
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.d(LOG_TAG, "openConnection() not successful.");
                cb.onFinished(false);
            }
        });
    }

    private void startRecMode(final ApiJsonCallback cb){
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startRecMode").put("params", new JSONArray()) //
                            .put("id", requestID()).put("version", "1.0");

            submitRequest(requestJson, cb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void CloseConnection(){

    }

    public void submitRequest(JSONObject data){
        submitRequest(data, null);
    }

    public void submitRequest(JSONObject data, final ApiJsonCallback cb){
        try {
            String url = findActionListUrl(SERVICE_CAM);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, data,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (cb != null) cb.onSuccess(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (cb != null) cb.onFailure(error);
                        }
                    }
            );

            mRequestQueue.add(req);
        }catch (Exception e){

        }
    }

    /**
     * Retrieves Action List URL from Server information.
     *
     * @param service
     * @return
     * @throws IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ServerDevice.ApiService> services = mDevice.getApiServices();
        for (ServerDevice.ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl() + "/" + service;
            }
        }
        throw new IOException("actionUrl not found. service : " + service);
    }

    /**
     * Retrieve a list of APIs that are supported by the target device.
     *
     * @param replyJson
     */
    private void loadSupportedApiList(JSONObject replyJson) {
        Log.d(LOG_TAG, "loadSupportedApiList() called.");
        try {
            JSONArray resultArrayJson = replyJson.getJSONArray("results");
            for (int i = 0; i < resultArrayJson.length(); i++) {
                mSupportedApiSet.add(resultArrayJson.getJSONArray(i).getString(0));
            }
        } catch (JSONException e) {
            Log.w(LOG_TAG, "loadSupportedApiList: JSON format error.");
        }
    }

    /**
     * Retrieve a list of APIs that are available at present.
     *
     * @param replyJson
     */
    private void loadAvailableCameraApiList(JSONObject replyJson) {
        mAvailableCameraApiSet.clear();
        try {
            JSONArray resultArrayJson = replyJson.getJSONArray("result");
            JSONArray apiListJson = resultArrayJson.getJSONArray(0);
            for (int i = 0; i < apiListJson.length(); i++) {
                mAvailableCameraApiSet.add(apiListJson.getString(i));
            }
        } catch (JSONException e) {
            Log.w(LOG_TAG, "loadAvailableCameraApiList: JSON format error.");
        }
    }

    private void getAvailableApiList(final ApiJsonCallback cb){
        Log.d(LOG_TAG, "getAvailableApiList() called.");
        try {
            String url = findActionListUrl(SERVICE_CAM);

            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableApiList")
                            .put("params", new JSONArray()).put("id", requestID())
                            .put("version", "1.0");

            submitRequest(requestJson, cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAvcontentMethodTypes(final ApiJsonCallback cb){
        try {
            JSONObject requestJson = new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", requestID()).put("version", "1.0"); //

            submitRequest(requestJson, cb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check if the specified API is supported. This is for camera and avContent
     * service API. The result of this method does not change dynamically.
     *
     * @param apiName
     * @return
     */
    private boolean isApiSupported(String apiName) {
        boolean isAvailable = false;
        isAvailable = mSupportedApiSet.contains(apiName);
        return isAvailable;
    }

    /**
     * Check if the specified API is available at present. This works correctly
     * only for Camera API.
     *
     * @param apiName
     * @return
     */
    private boolean isCameraApiAvailable(String apiName) {
        boolean isAvailable = false;
        isAvailable = mAvailableCameraApiSet.contains(apiName);
        return isAvailable;
    }

    /**
     * Calls getEvent API to the target server. Request JSON data is such like
     * as below.
     *
     * <pre>
     * {
     *   "method": "getEvent",
     *   "params": [true],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public void getEvent(final ApiJsonCallback cb) {
        Log.d(LOG_TAG, "getEvent() called.");
        try {
            JSONObject requestJson = new JSONObject().put("method", "getEvent")
                    .put("params", new JSONArray().put(false)).put("id", requestID()).put("version", "1.0");

            submitRequest(requestJson, cb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isShootingStatus(String currentStatus) {
        Set<String> shootingStatus = new HashSet<String>();
        shootingStatus.add("IDLE");
        shootingStatus.add("NotReady");
        shootingStatus.add("StillCapturing");
        shootingStatus.add("StillSaving");
        shootingStatus.add("MovieWaitRecStart");
        shootingStatus.add("MovieRecording");
        shootingStatus.add("MovieWaitRecStop");
        shootingStatus.add("MovieSaving");
        shootingStatus.add("IntervalWaitRecStart");
        shootingStatus.add("IntervalRecording");
        shootingStatus.add("IntervalWaitRecStop");
        shootingStatus.add("AudioWaitRecStart");
        shootingStatus.add("AudioRecording");
        shootingStatus.add("AudioWaitRecStop");
        shootingStatus.add("AudioSaving");

        return shootingStatus.contains(currentStatus);
    }

}
