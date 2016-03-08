package at.mchristoph.lapse.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.adapters.DeviceListAdapter;
import at.mchristoph.lapse.app.asynctasks.DeviceLoader;
import at.mchristoph.lapse.app.models.ServerDevice;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jorgecastilloprz.FABProgressCircle;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ConnectionFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<ServerDevice>>{
    private DeviceListAdapter mDeviceList;

    @Bind(R.id.lv_devices) protected ListView mListView;
    @Bind(R.id.lv_refresh) protected SwipeRefreshLayout mLvRefresh;
    @Bind(R.id.fab_device_search) protected FloatingActionButton mBtnSearch;
    @Bind(R.id.fab_device_search_progress) protected FABProgressCircle mFabProgress;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        mListView.setAdapter(mDeviceList);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                ServerDevice device = (ServerDevice) ((ListView)adapterView).getAdapter().getItem(pos);
                ((LapseActivity)getActivity()).replaceFragment(LapseFragment.newInstance(device));
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
    }

    @OnClick(R.id.fab_device_search)
    public void BtnSearchDevices(View view){
        SearchDevices();
    }

    public void SearchDevices(){
        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public DeviceLoader onCreateLoader(int i, Bundle bundle) {
        mBtnSearch.setEnabled(false);
        mFabProgress.show();
        mLvRefresh.setEnabled(false);

        return new DeviceLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<ServerDevice>> loader, List<ServerDevice> devices) {
        mFabProgress.hide();
        mLvRefresh.setRefreshing(false);

        if (devices != null) {
            Toast.makeText(getActivity(), devices.size() + " Found", Toast.LENGTH_SHORT).show();
            mDeviceList.clearDevices();
            mDeviceList.addDevice(devices);

            mBtnSearch.setVisibility(View.GONE);
            mBtnSearch.setEnabled(false);
            mLvRefresh.setEnabled(true);
            mLvRefresh.setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(getActivity(), "Nix gefunden", Toast.LENGTH_SHORT).show();
            mBtnSearch.setVisibility(View.VISIBLE);
            mBtnSearch.setEnabled(true);
            mLvRefresh.setEnabled(false);
            mLvRefresh.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ServerDevice>> loader) {

    }
}
