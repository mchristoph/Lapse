package at.mchristoph.lapse.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.utils.CameraApiUtil;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    @Bind(R.id.btn_connect_device) protected CardView mBtnConnect;
    @Bind(R.id.btn_quick)          protected CardView mBtnQuick;
    @Bind(R.id.btn_lapse_settings) protected CardView mBtnLapseSettings;
    @Bind(R.id.btn_presets)        protected CardView mBtnPresets;
    @Bind(R.id.btn_history)        protected CardView mBtnHistory;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean connected = true;
        if (CameraApiUtil.GetInstance() != null){
            //connected = CameraApiUtil.GetInstance().isConnected();
        }

        int color = 0;
        if (connected == false) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                color = getResources().getColor(R.color.background_material_light, null);
            } else {
                color = getResources().getColor(R.color.background_material_light);
            }
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                color = getResources().getColor(R.color.white, null);
            } else {
                color = getResources().getColor(R.color.white);
            }
        }

        mBtnQuick.setCardBackgroundColor(color);
        mBtnQuick.setClickable(connected);
        mBtnQuick.setEnabled(connected);
        mBtnLapseSettings.setCardBackgroundColor(color);
        mBtnLapseSettings.setClickable(connected);
        mBtnLapseSettings.setEnabled(connected);
        mBtnPresets.setCardBackgroundColor(color);
        mBtnPresets.setClickable(connected);
        mBtnPresets.setEnabled(connected);
        mBtnHistory.setCardBackgroundColor(color);
        mBtnHistory.setClickable(connected);
        mBtnHistory.setEnabled(connected);
    }

    @OnClick(R.id.btn_connect_device)
    public void OpenConnectionFragment(View view){
        ((LapseActivity)getActivity()).replaceFragment(new ConnectionFragment());
    }

    @OnClick(R.id.btn_lapse_settings)
    public void OpenLapseSettingsFragment(View view){
        ((LapseActivity)getActivity()).replaceFragment(new LapseSettingsFragment());
    }

    @OnClick(R.id.btn_presets)
    public void OpenPresetsFragment(View view){
        ((LapseActivity)getActivity()).replaceFragment(new PresetsFragment());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_lapse, menu);
    }
}
