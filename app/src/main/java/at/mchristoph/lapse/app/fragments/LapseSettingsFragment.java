package at.mchristoph.lapse.app.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.models.ServerDevice;
import butterknife.Bind;
import butterknife.ButterKnife;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LapseSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LapseSettingsFragment extends Fragment {
    private static final String ARG_DEVICE = "arg_connected_device";

    private ServerDevice mDevice;

    @Bind(R.id.numberPicker1) protected NumberPicker mPickerHours;
    @Bind(R.id.numberPicker2) protected NumberPicker mPickerMinutes;
    @Bind(R.id.numberPicker3) protected NumberPicker mPickerSeconds;
    @Bind(R.id.editText)      protected EditText     mFps;
    @Bind(R.id.editText2)     protected EditText     mIntervall;
    @Bind(R.id.textView5)     protected TextView     mPicCount;
    @Bind(R.id.textView4)     protected TextView     mShootingLength;
    @Bind(R.id.start_button)  protected ImageView    mStartButton;

    private List<String> hours;
    private List<String> minAndSec;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param device Parameter 1.
     * @return A new instance of fragment LapseFragment.
     */
    public static LapseSettingsFragment newInstance(ServerDevice device) {
        LapseSettingsFragment fragment = new LapseSettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DEVICE, device);
        fragment.setArguments(args);
        return fragment;
    }

    public LapseSettingsFragment() {
        hours = new ArrayList<>();
        minAndSec = new ArrayList<>();
        // Required empty public constructor
        for (int i = 0; i <= 24; i++){
            hours.add(String.format("%d", i));
        }

        for (int i = 0; i <= 59; i++){
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
        View view = inflater.inflate(R.layout.fragment_lapse_settings, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NumberPicker.OnValueChangeListener listener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                Calculate();
            }
        };

        mPickerHours.setDisplayedValues(hours.toArray(new String[hours.size()]));
        mPickerHours.setMinValue(0);
        mPickerHours.setMaxValue(24);
        mPickerHours.setValue(0);
        mPickerHours.setOnValueChangedListener(listener);

        mPickerMinutes.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerMinutes.setMinValue(0);
        mPickerMinutes.setMaxValue(59);
        mPickerMinutes.setValue(0);
        mPickerMinutes.setOnValueChangedListener(listener);

        mPickerSeconds.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerSeconds.setMinValue(0);
        mPickerSeconds.setMaxValue(59);
        mPickerSeconds.setValue(5);
        mPickerSeconds.setOnValueChangedListener(listener);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Calculate();
            }
        };
        mFps.addTextChangedListener(watcher);
        mIntervall.addTextChangedListener(watcher);

        Calculate();

        mStartButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                StartTimelapse();
                return false;
            }
        });
    }

    private void StartTimelapse(){
        long seconds = 0;

        int hour = mPickerHours.getValue();
        seconds += (hour * 3600);
        int minute = mPickerMinutes.getValue();
        seconds += (minute * 60);
        int second = mPickerSeconds.getValue();
        seconds += second;

        long interval = 0;
        if (mIntervall.getText().length() > 0) {
            interval = Long.parseLong(mIntervall.getText().toString());
        }

        ((LapseActivity)getActivity()).replaceFragment(LapseFragment.newInstance(seconds*1000L, interval*1000L));
    }

    private void Calculate(){
        long seconds = 0;

        int hour = mPickerHours.getValue();
        seconds += (hour * 3600);
        int minute = mPickerMinutes.getValue();
        seconds += (minute * 60);
        int second = mPickerSeconds.getValue();
        seconds += second;

        long fps = 0;
        if (mFps.getText().length() > 0) {
            fps = Long.parseLong(mFps.getText().toString());
        }
        long interval = 0;
        if (mIntervall.getText().length() > 0) {
            interval = Long.parseLong(mIntervall.getText().toString());
        }

        long count = fps * seconds;
        mPicCount.setText(String.valueOf(count));

        long millis = count * interval * 1000L;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        mShootingLength.setText(hms);
    }
}
