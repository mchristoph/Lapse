package at.mchristoph.lapse.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import at.mchristoph.lapse.app.utils.CameraApiUtil;
import at.mchristoph.lapse.app.utils.ConnectionManager;
import at.mchristoph.lapse.dao.model.DaoMaster;
import at.mchristoph.lapse.dao.model.DaoSession;

/**
 * Created by Xris on 23.03.2016.
 */
public class LapseApplication extends Application {
    private DaoSession mSession;
    private CameraApiUtil mApi;
    private ConnectionManager mConnectionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lapse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mSession = daoMaster.newSession();

        mApi = new CameraApiUtil(getApplicationContext());
        mConnectionManager = new ConnectionManager(getApplicationContext());
    }

    public DaoSession getSession(){
        return mSession;
    }

    public CameraApiUtil getApi() { return mApi; }

    public ConnectionManager getConnectionManager() { return mConnectionManager; }
}
