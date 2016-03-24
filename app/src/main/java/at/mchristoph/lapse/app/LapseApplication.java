package at.mchristoph.lapse.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import at.mchristoph.lapse.dao.model.DaoMaster;
import at.mchristoph.lapse.dao.model.DaoSession;

/**
 * Created by Xris on 23.03.2016.
 */
public class LapseApplication extends Application {
    private DaoSession mSession;

    @Override
    public void onCreate() {
        super.onCreate();

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lapse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        mSession = daoMaster.newSession();
    }

    public DaoSession getSession(){
        return mSession;
    }
}
