package at.mchristoph.lapse.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

import at.mchristoph.lapse.app.interfaces.AskForWlanCallback;
import at.mchristoph.lapse.app.interfaces.ConnectionCallbacks;
import at.mchristoph.lapse.app.interfaces.OnConnectionChangeListener;
import at.mchristoph.lapse.app.interfaces.WifiScanListener;

/**
 * Created by Xris on 29.03.2016.
 */
public class ConnectionManager{
    //region Fields
    private final static String LOG_TAG = ConnectionManager.class.getSimpleName();
    public enum Status {CONNECTING, CONNECTED, DISCONNECTED };

    private Context mContext;
    private OnConnectionChangeListener mOnConnectionListener;
    private WifiScanListener mWifiScanListener;

    private WifiScanReceiver mScanResultReciever;
    private WifiStateChangeReceiver mWifiStateChangedReciever;

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
        mWifiStateChangedReciever.setListener(listener);
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
        Log.i(LOG_TAG, "scanNetworks called.");
        if (!mWifiManager.isWifiEnabled()){
            Log.i(LOG_TAG, "scanNetworks - wifi not enabled -> enabling it.");
            mWifiManager.setWifiEnabled(true);
        }

        mWifiManager.startScan();
    }

    public boolean connectToWifi(final ScanResult wifi, AskForWlanCallback cb){
        Log.i(LOG_TAG, "connectToWifi called.");
        if (cb == null){ Log.i(LOG_TAG, "connectToWifi - AskForWlanCallback null"); return false; }

        mStatus = Status.CONNECTING;

        //TODO Check if already connected to device

        int id = isKnownNetwork(wifi);
        if (id == -1){
            Log.i(LOG_TAG, "connectToWifi - Network not know -> add it");
            id = addNewNetwork(wifi, cb);
        }

        if (id >= 0)
        {
            Log.i(LOG_TAG, "connectToWifi - Connect to Network with id " + String.valueOf(id));
            mWifiManager.disconnect();
            mWifiManager.enableNetwork(id, true);
            mWifiManager.reconnect();

            mStatus = Status.CONNECTED;
            return true;
        }

        Log.i(LOG_TAG, "connectToWifi - Connect failed");
        mStatus = Status.DISCONNECTED;

        return false;
    }

    private int addNewNetwork(final ScanResult wifi, AskForWlanCallback cb){
        Log.i(LOG_TAG, "addNewNetwork called.");
        if (cb == null) { Log.i(LOG_TAG, "addNewNetwork - AskForWlanCallback is null"); return -1; }

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

        if (conf.preSharedKey.isEmpty()){ Log.i(LOG_TAG, "addNewNetwork - preSharedKey is empty."); return -1; }

        return mWifiManager.addNetwork(conf);
    }

    public int isKnownNetwork(final ScanResult wifi){
        Log.i(LOG_TAG, "isKnownNetwork called.");
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        final int listSize = list.size();

        for (int i = 0; i < listSize; i++){
            WifiConfiguration conf = list.get(i);
            if (conf.SSID != null && conf.SSID.equals("\"" + wifi.SSID + "\"")){
                Log.i(LOG_TAG, "isKnownNetwork - Network is known.");
                return conf.networkId;
            }
        }

        Log.i(LOG_TAG, "isKnownNetwork - Network not known.");
        return -1;
    }

    //region Receiver
    public void registerReciever(){
        mScanResultReciever = new WifiScanReceiver(mWifiManager);
        mContext.registerReceiver(mScanResultReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mWifiStateChangedReciever = new WifiStateChangeReceiver(mWifiManager);
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
        private OnConnectionChangeListener mListener;
        private WifiManager mWifiManager;

        public WifiStateChangeReceiver(WifiManager wifiManager){
            mWifiManager = wifiManager;
        }
        public void setListener(OnConnectionChangeListener listener){
            mListener = listener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (netInfo.isConnected()) {
                if (mListener != null){
                    mListener.onConnect(mWifiManager.getConnectionInfo());
                }
            }else{
                if (mListener != null){
                    mListener.onDisconnect(mWifiManager.getConnectionInfo());
                }
            }
        }
    }
    //endregion
}
