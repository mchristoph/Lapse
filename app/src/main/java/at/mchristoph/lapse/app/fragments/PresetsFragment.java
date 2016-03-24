package at.mchristoph.lapse.app.fragments;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import at.mchristoph.lapse.app.LapseApplication;
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
public class PresetsFragment extends Fragment{
    @Bind(R.id.list_presets) RecyclerView mRecyclerView;
    @Bind(R.id.txt_no_items) TextView mTxtNoItems;

    LapseSetting mLastDeletedItem;
    int mLastDeletedPos;

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
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final LapseSettingDao dao = ((LapseApplication) getActivity().getApplicationContext()).getSession().getLapseSettingDao();
        List<LapseSetting> items = dao.loadAll();

        if (items.size() > 0) {
            mTxtNoItems.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            final PresetRecyclerViewAdapter adapter = new PresetRecyclerViewAdapter(items);
            mRecyclerView.setAdapter(adapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    mLastDeletedPos = viewHolder.getAdapterPosition();
                    mLastDeletedItem = adapter.getItem(mLastDeletedPos);
                    dao.delete(mLastDeletedItem);
                    adapter.remove(mLastDeletedPos);

                    Snackbar.make(ButterKnife.findById(getActivity(), android.R.id.content), "Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dao.insert(mLastDeletedItem);
                                adapter.addItem(mLastDeletedPos, mLastDeletedItem);
                                mRecyclerView.smoothScrollToPosition(mLastDeletedPos);
                            }
                        }).show();
                }
            });

            itemTouchHelper.attachToRecyclerView(mRecyclerView);
        }else{
            mTxtNoItems.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }
    }
}
