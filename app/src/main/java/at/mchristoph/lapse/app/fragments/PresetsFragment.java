package at.mchristoph.lapse.app.fragments;


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
import at.mchristoph.lapse.app.fragments.dialogs.PresetsModalBottomSheet;
import at.mchristoph.lapse.app.interfaces.OnPresetBottomSheetListener;
import at.mchristoph.lapse.app.interfaces.OnItemClickListener;
import at.mchristoph.lapse.app.interfaces.OnLongItemClickListener;
import at.mchristoph.lapse.dao.model.LapseSetting;
import at.mchristoph.lapse.dao.model.LapseSettingDao;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PresetsFragment extends Fragment implements OnItemClickListener, OnLongItemClickListener, OnPresetBottomSheetListener {
    @Bind(R.id.list_presets) RecyclerView mRecyclerView;
    @Bind(R.id.txt_no_items) TextView mTxtNoItems;

    private LapseSetting mLastDeletedItem;
    private int mLastDeletedPos;
    private int mUpadatePos;
    private PresetRecyclerViewAdapter mAdapter;

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
            mAdapter = new PresetRecyclerViewAdapter(items);
            mAdapter.setOnClickListener(this);
            mAdapter.setOnLongClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    mLastDeletedPos = viewHolder.getAdapterPosition();
                    mLastDeletedItem = mAdapter.getItem(mLastDeletedPos);
                    dao.delete(mLastDeletedItem);
                    mAdapter.remove(mLastDeletedPos);

                    String message = String.format(getString(R.string.preset_deleted), mLastDeletedItem.getName());
                    Snackbar.make(getView(), message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.preset_deleted_undo_action, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dao.insert(mLastDeletedItem);
                                mAdapter.addItem(mLastDeletedPos, mLastDeletedItem);
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

    @Override
    public void onClick(int position, View v) {
        Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onLongClick(int position, View v) {
        mUpadatePos = position;

        PresetsModalBottomSheet sheet = PresetsModalBottomSheet.newInstance(mAdapter.getItem(position), this);
        sheet.show(getFragmentManager(), "bottom_sheet");

        return true;
    }


    @Override
    public void onPositive(LapseSetting setting) {
        final LapseSettingDao dao = ((LapseApplication) getActivity().getApplicationContext()).getSession().getLapseSettingDao();
        dao.update(setting);

        mAdapter.remove(mUpadatePos);
        mAdapter.addItem(mUpadatePos, setting);
        mRecyclerView.smoothScrollToPosition(mUpadatePos);
    }
}
