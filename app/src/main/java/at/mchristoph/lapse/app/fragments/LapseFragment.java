package at.mchristoph.lapse.app.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.NumberPicker;
import android.widget.TextView;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.models.ServerDevice;
import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LapseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LapseFragment extends Fragment {
    private static final String ARG_DEVICE = "arg_connected_device";

    private ServerDevice mDevice;

    @Bind(R.id.numberPicker1) protected NumberPicker mPickerHours;
    @Bind(R.id.numberPicker2) protected NumberPicker mPickerMinutes;
    @Bind(R.id.numberPicker3) protected NumberPicker mPickerSeconds;
    @Bind(R.id.editText)      protected TextView     mFps;
    @Bind(R.id.editText2)     protected TextView     mIntervall;
    @Bind(R.id.editText3)     protected TextView     mPicCount;

    private List<String> hours;
    private List<String> minAndSec;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param device Parameter 1.
     * @return A new instance of fragment LapseFragment.
     */
    public static LapseFragment newInstance(ServerDevice device) {
        LapseFragment fragment = new LapseFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DEVICE, device);
        fragment.setArguments(args);
        return fragment;
    }

    public LapseFragment() {
        // Required empty public constructor
        for (int i = 0; i <= 24; i++){
            hours.add(String.format("%d", i));
        }

        for (int i = 0; i <= 60; i++){
            minAndSec.add(String.format("%d", i));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDevice = getArguments().getParcelable(ARG_DEVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lapse, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPickerHours.setDisplayedValues(hours.toArray(new String[hours.size()]));
        mPickerHours.setMinValue(0);
        mPickerHours.setMaxValue(24);
        mPickerHours.setValue(0);

        mPickerMinutes.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerMinutes.setMinValue(0);
        mPickerMinutes.setMaxValue(60);
        mPickerMinutes.setValue(0);

        mPickerSeconds.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerMinutes.setMinValue(0);
        mPickerMinutes.setMaxValue(60);
        mPickerMinutes.setValue(5);

        int count
    }
}
