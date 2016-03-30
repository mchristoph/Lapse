package at.mchristoph.lapse.app.events;

import android.net.wifi.WifiInfo;

/**
 * Created by Xris on 24.03.2016.
 */
public class ConnectionChangeEvent {
    public enum Status {CONNECTED, DISCONNECTED};
    public final Status status;
    public final WifiInfo wifiInfo;

    public ConnectionChangeEvent(Status status, WifiInfo wifiInfo) {
        this.status = status;
        this.wifiInfo = wifiInfo;
    }
}
