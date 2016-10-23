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
import at.mchristoph.lapse.app.fragments.LapseFragment;
import at.mchristoph.lapse.app.interfaces.ApiJsonCallback;
import at.mchristoph.lapse.app.utils.CameraApiUtil;

public class LapseService extends Service {
    public static final String ARG_TIME = "arg_total_time";
    public static final String ARG_TIME_STRING = "arg_time_string";
    public static final String ARG_PROGRESS = "arg_progress";
    public static final String ARG_INTERVAL = "arg_intervall";
    public static final String ARG_RUNNING = "arg_running";
    public static final String ARG_ACTION = "arg_action";
    public static final int NOTIFICATION_ID = 1337;

    private CameraApiUtil mApi;
    private CountDownTimer mTimer;
    private long mTotalTime;
    private long mRemainingTime;
    private long mInterval;
    private NotificationManager mNotifyManager;

    public LapseService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(NOTIFICATION_ID, getNotification("Lapse starting.", "", 1));
    }

    private Notification getNotification(CharSequence message, String hms, int progress){
        Intent notificationIntent = new Intent(this, LapseActivity.class);
        if (progress > 0) {
            notificationIntent.putExtra(ARG_ACTION, LapseFragment.class.getSimpleName());
            notificationIntent.putExtra(ARG_TIME, mRemainingTime);
            notificationIntent.putExtra(ARG_TIME_STRING, hms);
            notificationIntent.putExtra(ARG_INTERVAL, mInterval);
            notificationIntent.putExtra(ARG_PROGRESS, 100 - progress);
            notificationIntent.putExtra(ARG_RUNNING, true);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        builder.setSmallIcon(R.drawable.ic_media_play);
        builder.setContentIntent(pendingIntent);

        if (progress > 0){
            builder.setOngoing(true);
            builder.setAutoCancel(false);
            builder.setContentTitle("Lapse in Progress");
            builder.setContentText(message);
            builder.setProgress(100, progress % 100, false);
        }else{
            builder.setOngoing(false);
            builder.setAutoCancel(true);
            builder.setContentTitle("Lapse finished");
            builder.setProgress(0, 0, false);
        }

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
                public void onTick(long millisUntilFinished) {
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
                    mRemainingTime = millisUntilFinished;
                    double progress = ((double) millisUntilFinished / (double) mTotalTime) * 100f;

                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

                    mNotifyManager.notify(NOTIFICATION_ID, getNotification("Remaining Time: " + hms, hms, 100 - ((int)progress)));
                    EventBus.getDefault().post(new LapseProgressEvent((int)progress, hms));
                }

                @Override
                public void onFinish() {
                    stopForeground(true);
                    mNotifyManager.cancel(NOTIFICATION_ID);

                    mNotifyManager.notify(NOTIFICATION_ID, getNotification("Lapsed finished", "", 0));
                    EventBus.getDefault().post(new LapseProgressEvent((int)0, null));

                    stopSelf();
                }
            }.start();

        }else{
            stopForeground(true);
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
