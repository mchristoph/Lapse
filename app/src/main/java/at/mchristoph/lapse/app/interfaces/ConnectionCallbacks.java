package at.mchristoph.lapse.app.interfaces;

import at.mchristoph.lapse.app.utils.ConnectionManager;

/**
 * Created by Xris on 30.03.2016.
 */
public interface ConnectionCallbacks {
    public void onChange(ConnectionManager.Status status);
}
