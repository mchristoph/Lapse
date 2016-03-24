package at.mchristoph.lapse.app.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import at.mchristoph.lapse.app.LapseApplication;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.adapters.HistoryRecyclerViewAdapter;
import at.mchristoph.lapse.app.adapters.PresetRecyclerViewAdapter;
import at.mchristoph.lapse.dao.model.DaoMaster;
import at.mchristoph.lapse.dao.model.DaoSession;
import at.mchristoph.lapse.dao.model.LapseHistory;
import at.mchristoph.lapse.dao.model.LapseHistoryDao;
import at.mchristoph.lapse.dao.model.LapseSetting;
import at.mchristoph.lapse.dao.model.LapseSettingDao;
import butterknife.Bind;
import butterknife.ButterKnife;

//TODO Clear history
public class HistoryFragment extends Fragment {
    @Bind(R.id.list_presets) RecyclerView mRecyclerView;
    @Bind(R.id.txt_no_items) TextView mTxtNoItems;

    public HistoryFragment() {
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

        LapseHistoryDao dao = ((LapseApplication) getActivity().getApplicationContext()).getSession().getLapseHistoryDao();
        List<LapseHistory> items = dao.queryBuilder().orderDesc(LapseHistoryDao.Properties.Created).build().list();

        if (items.size() > 0) {
            mTxtNoItems.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(items);
            mRecyclerView.setAdapter(adapter);
        }else{
            mTxtNoItems.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}
