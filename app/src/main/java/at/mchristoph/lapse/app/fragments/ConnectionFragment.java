package at.mchristoph.lapse.app.fragments;

import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.LapseApplication;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.adapters.DeviceListAdapter;
import at.mchristoph.lapse.app.adapters.WifiListAdapter;
import at.mchristoph.lapse.app.asynctasks.DeviceLoader;
import at.mchristoph.lapse.app.events.ConnectionChangeEvent;
import at.mchristoph.lapse.app.events.LapseProgressEvent;
import at.mchristoph.lapse.app.interfaces.ApiBooleanCallback;
import at.mchristoph.lapse.app.interfaces.AskForWlanCallback;
import at.mchristoph.lapse.app.interfaces.ConnectionCallbacks;
import at.mchristoph.lapse.app.interfaces.WifiScanListener;
import at.mchristoph.lapse.app.models.ServerDevice;
import at.mchristoph.lapse.app.utils.CameraApiUtil;
import at.mchristoph.lapse.app.utils.ConnectionManager;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//TODO Check if already connected to device
//TODO Check if connection is lost
//TODO Check if already connected to device-wifi but openConnection not called
public class ConnectionFragment extends Fragment implements AdapterView.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<ServerDevice>>, WifiScanListener, SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.lv_devices) protected ListView mListView;
    @Bind(R.id.progress_bar) protected ProgressBar mProgressBar;
    @Bind(R.id.txt_search_info) protected TextView mTxtSearchInfo;
    @Bind(R.id.lv_refresh) protected SwipeRefreshLayout mLvRefresh;
    @Bind(R.id.txt_device_info) protected TextView mTxtDeviceInfo;
    @Bind(R.id.img_device_info) protected ImageView mImgDeviceInfo;

    private String mSSID;
    private boolean mConnecting;

    private WifiListAdapter mWifiAdapter;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mWifiAdapter = new WifiListAdapter(getContext());
        mListView.setAdapter(mWifiAdapter);

        mListView.setOnItemClickListener(this);
        mLvRefresh.setOnRefreshListener(this);
        ((LapseApplication)getActivity().getApplication()).getConnectionManager().setWifiScanListener(this);

        searchDevices();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_lapse, menu);
    }

    private void ConnectDevice(){
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void searchDevices(){
        ((LapseApplication)getActivity().getApplication()).getConnectionManager().scanNetworks();
    }

    @Override
    public Loader<List<ServerDevice>> onCreateLoader(int id, Bundle args) {
        Toast.makeText(getContext(), R.string.connection_info_connect_camera, Toast.LENGTH_SHORT).show();
        return new DeviceLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ServerDevice>> loader, final List<ServerDevice> devices) {
        if (devices != null) {
            Toast.makeText(getContext(), R.string.connection_info_setup_connection, Toast.LENGTH_SHORT).show();

            if (devices.size() > 0){
                CameraApiUtil api = ((LapseApplication)(getActivity().getApplicationContext())).getApi();
                api.setDevice(devices.get(0));
                api.setupConnection(new ApiBooleanCallback() {
                    @Override
                    public void onFinished(Boolean bool) {
                        if (bool){
                            Snackbar.make(ButterKnife.findById(getActivity(), android.R.id.content), R.string.connection_info_connection_successful, Snackbar.LENGTH_LONG).show();
                            ((LapseActivity)getActivity()).replaceFragment(new MenuFragment());
                        }else{
                            Toast.makeText(getContext(), R.string.connection_info_connection_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },  "TODO SSID");
            }
        }else{
            Toast.makeText(getContext(), R.string.connection_info_connection_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ServerDevice>> loader) {

    }

    @Override
    public void onReceived(List<ScanResult> scanResults) {
        mWifiAdapter.clearDevices();
        mWifiAdapter.addDevice(scanResults);

        if (mWifiAdapter.getCount() == 0){
            mTxtDeviceInfo.setVisibility(View.VISIBLE);
            mImgDeviceInfo.setVisibility(View.VISIBLE);

            mLvRefresh.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
            mTxtSearchInfo.setVisibility(View.GONE);
            mLvRefresh.setRefreshing(false);
            mLvRefresh.setVisibility(View.VISIBLE);
        }else{
            mTxtDeviceInfo.setVisibility(View.GONE);
            mImgDeviceInfo.setVisibility(View.GONE);

            mLvRefresh.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
            mTxtSearchInfo.setVisibility(View.GONE);
            mLvRefresh.setRefreshing(false);
            mLvRefresh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ProgressBar progress = ButterKnife.findById(view, R.id.progress_bar_connect);
        ImageView img = ButterKnife.findById(view, R.id.image_view_connect);

        img.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        final ScanResult device = (ScanResult) ((ListView) parent).getAdapter().getItem(position);
        mSSID = device.SSID;
        mConnecting = true;

        Toast.makeText(getContext(), R.string.connection_info_connect_wifi, Toast.LENGTH_SHORT).show();
        ((LapseApplication)getActivity().getApplication()).getConnectionManager().connectToWifi(device, new AskForWlanCallback() {
            @Override
            public String passphrase() {
                //TODO Open bottom sheet to ask for passphrase
                return "TODO";
            }
        });
    }

    @Override
    public void onRefresh() {
        searchDevices();
    }

    @OnClick({R.id.img_device_info, R.id.txt_device_info})
    public void onClickRefresh(){
        mTxtDeviceInfo.setVisibility(View.GONE);
        mImgDeviceInfo.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTxtSearchInfo.setVisibility(View.VISIBLE);

        searchDevices();
    }

    @Subscribe
    public void onEvent(ConnectionChangeEvent event) {
        if (event.status == ConnectionChangeEvent.Status.CONNECTED && event.wifiInfo.getSSID().equals("\"" + mSSID + "\"") && mConnecting == true) {
            //Log.d("IsConnected", mWifiManager.getConnectionInfo().getSSID());
            mConnecting = false;
            Toast.makeText(getContext(), String.format(getString(R.string.connection_info_connect_wifi), event.wifiInfo.getSSID()), Toast.LENGTH_SHORT).show();
            ConnectDevice();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

