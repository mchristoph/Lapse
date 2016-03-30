package at.mchristoph.lapse.app.interfaces;

import android.net.wifi.WifiInfo;

/**
 * Created by Xris on 29.03.2016.
 */
public interface OnConnectionChangeListener {
    public void onConnect(WifiInfo connectionInfo);
    public void onDisconnect(WifiInfo connectionInfo);
}
