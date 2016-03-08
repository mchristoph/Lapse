package at.mchristoph.lapse.app.fragments;

import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.ServerDevice;
import at.mchristoph.lapse.app.SimpleSsdpClient;
import at.mchristoph.lapse.app.adapters.DeviceListAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jorgecastilloprz.FABProgressCircle;

/**
 * A placeholder fragment containing a simple view.
 */
public class LapseActivityFragment extends Fragment {
    private SimpleSsdpClient mSsdpClient;
    private DeviceListAdapter mDeviceList;

    @Bind(R.id.lv_devices) protected ListView mListView;
    @Bind(R.id.lv_refresh) protected SwipeRefreshLayout mLvRefresh;
    @Bind(R.id.fab_device_search) protected FloatingActionButton mBtnSearch;
    @Bind(R.id.fab_device_search_progress) protected FABProgressCircle mFabProgress;

    public LapseActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lapse, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSsdpClient = new SimpleSsdpClient();
        mDeviceList = new DeviceListAdapter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.setAdapter(mDeviceList);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                ServerDevice device = (ServerDevice) ((ListView)adapterView).getAdapter().getItem(pos);
            }
        });

        mLvRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SearchDevices();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSsdpClient != null && mSsdpClient.isSearching()) {
            mSsdpClient.cancelSearching();
        }
    }

    @OnClick(R.id.fab_device_search)
    public void BtnSearchDevices(View view){
        mBtnSearch.setEnabled(false);
        mFabProgress.show();
        SearchDevices();
    }

    public void SearchDevices(){
        mDeviceList.clearDevices();

        if (!mSsdpClient.isSearching()) {
            mSsdpClient.search(new SimpleSsdpClient.SearchResultHandler() {

                @Override
                public void onDeviceFound(final ServerDevice device) {
                    // Called by non-UI thread.
                    Log.d("Temp", ">> Search device found: " + device.getFriendlyName());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceList.addDevice(device);
                            mFabProgress.hide();
                            mFabProgress.setVisibility(View.GONE);
                            mLvRefresh.setVisibility(View.VISIBLE);
                            mLvRefresh.setRefreshing(false);
                        }
                    });
                }

                @Override
                public void onFinished() {
                    // Called by non-UI thread.
                    Log.d("temp", ">> Search finished.");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFabProgress.hide();
                            mBtnSearch.setEnabled(true);
                            mLvRefresh.setRefreshing(false);
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), //
                                        "Suche abgeschlossen", //
                                        Toast.LENGTH_SHORT).show(); //
                            }
                        }
                    });
                }

                @Override
                public void onErrorFinished() {
                    // Called by non-UI thread.
                    Log.d("temp", ">> Search Error finished.");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBtnSearch.setEnabled(true);
                            mFabProgress.hide();
                            mLvRefresh.setRefreshing(false);
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), //
                                        "Fehler bei der Suche", //
                                        Toast.LENGTH_SHORT).show(); //
                            }
                        }
                    });
                }
            });
        }
    }
}
