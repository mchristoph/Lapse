package at.mchristoph.lapse.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.design.widget.Snackbar;

import java.util.List;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.interfaces.AskForWlanCallback;
import at.mchristoph.lapse.app.interfaces.OnConnectionChangeListener;
import at.mchristoph.lapse.app.interfaces.WifiScanListener;
import butterknife.ButterKnife;

/**
 * Created by Xris on 29.03.2016.
 */
public class ConnectionManager{
    //region Fields
    public enum Status {CONNECTING, CONNECTED, DISCONNECTED };

    private Context mContext;
    private OnConnectionChangeListener mOnConnectionListener;
    private WifiScanListener mWifiScanListener;

    private WifiScanReceiver mScanResultReciever;
    private BroadcastReceiver mWifiStateChangedReciever;

    private Status mStatus;
    private WifiManager mWifiManager;
    //endregion

    //region Constructor
    public ConnectionManager(Context ctx) {
        mContext = ctx;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mStatus = Status.DISCONNECTED;
    }

    public ConnectionManager(Context ctx, OnConnectionChangeListener listener) {
        this(ctx);
        setConnectionListener(listener);
    }
    //endregion

    //region Setter

    public void setConnectionListener(OnConnectionChangeListener listener){
        mOnConnectionListener = listener;
    }

    public void setWifiScanListener(WifiScanListener listener){
        mWifiScanListener = listener;
        mScanResultReciever.setListener(mWifiScanListener);
    }

    //endregion

    //region Getter
    public Status getStatus(){
        return mStatus;
    }
    //endregion

    public void scanNetworks()
    {
        if (!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }

        mWifiManager.startScan();
    }

    public boolean connectToWifi(final ScanResult wifi, AskForWlanCallback cb){
        if (cb == null){ return false; }

        mStatus = Status.CONNECTING;

        //TODO Check if already connected to device

        int id = isKnownNetwork(wifi);
        if (id == -1){
            id = addNewNetwork(wifi, cb);
        }

        if (id >= 0)
        {
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(id, true);
            mWifiManager.reconnect();

            mStatus = Status.CONNECTED;
            return true;
        }

        mStatus = Status.DISCONNECTED;
        return false;
    }

    private int addNewNetwork(final ScanResult wifi, AskForWlanCallback cb){
        if (cb == null){ return -1; }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = wifi.SSID;
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.preSharedKey = cb.passphrase();

        if (conf.preSharedKey.isEmpty()){ return -1; }

        return mWifiManager.addNetwork(conf);
    }

    public int isKnownNetwork(final ScanResult wifi){
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        final int listSize = list.size();

        for (int i = 0; i < listSize; i++){
            WifiConfiguration conf = list.get(i);
            if (conf.SSID != null && conf.SSID.equals("\"" + wifi.SSID + "\"")){
                return conf.networkId;
            }
        }

        return -1;
    }

    //region Receiver
    public void registerReciever(){
        mScanResultReciever = new WifiScanReceiver(mWifiManager);
        mContext.registerReceiver(mScanResultReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiStateChangedReciever = new WifiStateChangeReceiver();
        mContext.registerReceiver(mWifiStateChangedReciever, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    }

    public void unregisterReciever(){
        mContext.unregisterReceiver(mScanResultReciever);
        mContext.unregisterReceiver(mWifiStateChangedReciever);

        mScanResultReciever = null;
        mWifiStateChangedReciever = null;
    }

    private static class WifiScanReceiver extends BroadcastReceiver
    {
        private WifiScanListener mListener;
        private WifiManager mWifiManager;

        public WifiScanReceiver(WifiManager wifiManager){
            mWifiManager = wifiManager;
        }

        public void setListener(WifiScanListener listener){
            mListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener != null) mListener.onReceived(mWifiManager.getScanResults());
        }
    }

    private static class WifiStateChangeReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
    //endregion
}
