package at.mchristoph.lapse.app.interfaces;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Xris on 29.03.2016.
 */
public interface WifiScanListener {
    public void onReceived(List<ScanResult> scanResults);
}

