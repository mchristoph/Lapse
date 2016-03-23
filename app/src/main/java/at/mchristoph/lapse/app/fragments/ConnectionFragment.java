package at.mchristoph.lapse.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.adapters.DeviceListAdapter;
import at.mchristoph.lapse.app.adapters.WifiListAdapter;
import at.mchristoph.lapse.app.asynctasks.DeviceLoader;
import at.mchristoph.lapse.app.models.ServerDevice;
import at.mchristoph.lapse.app.utils.ApiBooleanCallback;
import at.mchristoph.lapse.app.utils.CameraApiUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */

//TODO Check if already connected to device
//TODO Check if connection is lost
//TODO Check if already connected to device-wifi but openConnection not called
public class ConnectionFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ServerDevice>>{
    private DeviceListAdapter mDeviceList;

    @Bind(R.id.lv_devices) protected ListView mListView;
    @Bind(R.id.progress_bar) protected ProgressBar mProgressBar;
    @Bind(R.id.lv_refresh) protected SwipeRefreshLayout mLvRefresh;
    private WifiManager mWifiManager;
    private WifiListAdapter mWifiAdapter;
    private BroadcastReceiver mScanResultReciever;
    private BroadcastReceiver mWifiStateChangedReciever;

    private String mSSID;
    private boolean mConnecting;

    public ConnectionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connection, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDeviceList = new DeviceListAdapter(getActivity());
        mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mConnecting = false;

        mScanResultReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mWifiAdapter.clearDevices();
                mWifiAdapter.addDevice(mWifiManager.getScanResults());

                mLvRefresh.setEnabled(true);
                mProgressBar.setVisibility(View.GONE);
                mLvRefresh.setRefreshing(false);
                mLvRefresh.setVisibility(View.VISIBLE);

                //TODO If no result show info text
            }
        };

        mWifiStateChangedReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (netInfo.isConnected()) {
                    if (mWifiManager.getConnectionInfo().getSSID().equals("\"" + mSSID + "\"") && mConnecting == true) {
                        Log.d("IsConnected", mWifiManager.getConnectionInfo().getSSID());
                        mConnecting = false;
                        ConnectDevice();
                    }
                } else {
                    Log.d("NotConnected", "k");
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        mWifiAdapter = new WifiListAdapter(getContext());
        mListView.setAdapter(mWifiAdapter);

        if (mWifiManager.isWifiEnabled() == false)
        {
            // If wifi disabled then enable it
            //TODO Sinnvoller Text
            Toast.makeText(getActivity(), "wifi is disabled..making it enabled",
                    Toast.LENGTH_LONG).show();

            mWifiManager.setWifiEnabled(true);
        }

        getActivity().registerReceiver(mScanResultReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        getActivity().registerReceiver(mWifiStateChangedReciever, new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                final ScanResult device = (ScanResult) ((ListView) adapterView).getAdapter().getItem(pos);

                ConnectWifi(device);
            }
        });

        mLvRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SearchDevices();
            }
        });

        mWifiManager.startScan();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_lapse, menu);
    }

    private void ConnectWifi(final ScanResult device) {
        mConnecting = true;

        // TODO Add wifi from device, if not already in list
        WifiConfiguration conf = new WifiConfiguration();
        mSSID = conf.SSID = device.SSID;
        // TODO Ask for preSharedKey, if not already set
        conf.preSharedKey = "";

        mWifiManager.addNetwork(conf);
        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + device.SSID + "\"")) {
                mWifiManager.disconnect();
                mWifiManager.enableNetwork(i.networkId, true);
                mWifiManager.reconnect();

                break;
            }
        }

        //TODO Check if connected!
        //((LapseActivity)getActivity()).replaceFragment(new MenuFragment());
    }

    private void ConnectDevice(){
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(mScanResultReciever);
        getActivity().unregisterReceiver(mWifiStateChangedReciever);
    }

    public void SearchDevices(){
        mWifiManager.startScan();
    }

    @Override
    public Loader<List<ServerDevice>> onCreateLoader(int id, Bundle args) {
        return new DeviceLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ServerDevice>> loader, final List<ServerDevice> devices) {
        if (devices != null) {
            Toast.makeText(getActivity(), devices.size() + " Found", Toast.LENGTH_SHORT).show();

            if (devices.size() > 0){
                CameraApiUtil api = CameraApiUtil.GetInstance(devices.get(0), getActivity());
                api.setupConnection(new ApiBooleanCallback() {
                    @Override
                    public void onFinished(Boolean bool) {
                        if (bool){
                            ((LapseActivity)getActivity()).replaceFragment(new MenuFragment());
                        }else{
                            Toast.makeText(getContext(), "Something went horrible wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },  mSSID);
            }
        }else{
            Toast.makeText(getActivity(), "Nix gefunden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ServerDevice>> loader) {

    }
}
