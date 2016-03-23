package at.mchristoph.lapse.app.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.adapters.PresetRecyclerViewAdapter;
import at.mchristoph.lapse.dao.model.DaoMaster;
import at.mchristoph.lapse.dao.model.DaoSession;
import at.mchristoph.lapse.dao.model.LapseSetting;
import at.mchristoph.lapse.dao.model.LapseSettingDao;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresetsFragment extends Fragment {
    @Bind(R.id.list_presets) RecyclerView mRecyclerView;

    public PresetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_presets, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getContext(), "lapse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();

        LapseSettingDao dao = daoSession.getLapseSettingDao();
        List<LapseSetting> items = dao.loadAll();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PresetRecyclerViewAdapter adapter = new PresetRecyclerViewAdapter(items);
        mRecyclerView.setAdapter(adapter);
    }
}
