package at.mchristoph.lapse.app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.VolleyError;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.LapseApplication;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.events.LapseProgressEvent;
import at.mchristoph.lapse.app.interfaces.ApiJsonCallback;
import at.mchristoph.lapse.app.utils.CameraApiUtil;

public class LapseService extends Service {
    public static final String ARG_TIME = "arg_total_time";
    public static final String ARG_INTERVAL = "arg_intervall";
    public static final int NOTIFICATION_ID = 1337;

    private CameraApiUtil mApi;
    private CountDownTimer mTimer;
    private long mTotalTime;
    private long mInterval;
    private NotificationManager mNotifyManager;

    public LapseService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(NOTIFICATION_ID,getNotification("Lapse starting."));
    }

    private Notification getNotification(CharSequence message){
        Intent notificationIntent = new Intent(this, LapseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, notificationIntent, 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setAutoCancel(false);
        builder.setContentTitle("Lapse in Progress");
        builder.setContentText(message);
        builder.setSmallIcon(R.drawable.ic_media_play);
        builder.setOngoing(true);
        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mApi = ((LapseApplication)getApplication()).getApi();

        mTotalTime = intent.getLongExtra(ARG_TIME, 0);
        mInterval = intent.getLongExtra(ARG_INTERVAL, 0);

        if (mApi != null && mTotalTime > 0 && mInterval > 0) {
            mTimer = new CountDownTimer(mTotalTime, mInterval) {
                @Override
                public void onTick(long millisUntilFinished_) {
                    Log.d("Lapse_Timer", "Tick");
                    mApi.takePicture(new ApiJsonCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d("Lapse_Timer", "Sent");
                        }

                        @Override
                        public void onFailure(VolleyError error) {
                            Log.d("Lapse_Timer", error.toString());
                        }
                    });
                    double progress = ((double) millisUntilFinished_ / (double) mTotalTime) * 100f;

                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished_),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished_) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished_)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished_) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished_)));

                    mNotifyManager.notify(NOTIFICATION_ID, getNotification("Remaining Time: " + hms));
                    EventBus.getDefault().post(new LapseProgressEvent((int)progress, hms));
                }

                @Override
                public void onFinish() {
                    EventBus.getDefault().post(new LapseProgressEvent((int)0, null));
                    stopSelf();
                }
            }.start();
        }else{
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mTimer != null){
            mTimer.cancel();
            mTimer.onFinish();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }
}
