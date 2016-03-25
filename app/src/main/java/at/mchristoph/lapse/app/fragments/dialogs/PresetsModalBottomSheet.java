package at.mchristoph.lapse.app.fragments.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.interfaces.OnPresetBottomSheetListener;
import at.mchristoph.lapse.dao.model.LapseSetting;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Xris on 24.03.2016.
 */
public class PresetsModalBottomSheet extends BottomSheetDialogFragment implements NumberPicker.OnValueChangeListener, TextWatcher {
    private final static String ARG_KEY_ID = "arg_id";
    private final static String ARG_KEY_NAME = "arg_name";
    private final static String ARG_KEY_DESC = "arg_desc";
    private final static String ARG_KEY_FPS  = "arg_fps";
    private final static String ARG_KEY_INTVAL  = "arg_intval";
    private final static String ARG_KEY_MOVIETIME  = "arg_movie_time";
    private final static String ARG_KEY_MOVIETIME_H  = "arg_movie_time_h";
    private final static String ARG_KEY_MOVIETIME_M  = "arg_movie_time_m";
    private final static String ARG_KEY_MOVIETIME_S  = "arg_movie_time_s";
    private final static String ARG_KEY_CREATED  = "arg_created";

    @Bind(R.id.txt_preset_name)         TextView mEditName;
    @Bind(R.id.txt_preset_description)  TextView mEditDesc;
    @Bind(R.id.txt_target_framerate)    TextView mEditFps;
    @Bind(R.id.txt_interval)            TextView mEditInterval;
    @Bind(R.id.np_times_seconds)        NumberPicker mEditSeconds;
    @Bind(R.id.np_times_minutes)        NumberPicker mEditMinutes;
    @Bind(R.id.np_times_hours)          NumberPicker mEditHours;
    @Bind(R.id.textView5)               TextView mPicCount;
    @Bind(R.id.textView4)               TextView mShootingLength;

    private List<String> hours;
    private List<String> minAndSec;
    private LapseSetting mSettings;

    private static OnPresetBottomSheetListener mListener;

    public static PresetsModalBottomSheet newInstance(LapseSetting settings){
        return newInstance(settings, null);
    }

    public static PresetsModalBottomSheet newInstance(LapseSetting settings, OnPresetBottomSheetListener listener){
        Bundle args = new Bundle();
        args.putString(ARG_KEY_NAME, settings.getName());
        args.putString(ARG_KEY_DESC, settings.getDescription());
        args.putString(ARG_KEY_FPS, String.valueOf(settings.getFramerate()));
        args.putString(ARG_KEY_INTVAL, String.valueOf(settings.getInterval()));
        args.putString(ARG_KEY_MOVIETIME, String.valueOf(settings.getMovieTime()));
        args.putInt(ARG_KEY_MOVIETIME_H, settings.getMovieTimeHours());
        args.putInt(ARG_KEY_MOVIETIME_M, settings.getMovieTimeMinutes());
        args.putInt(ARG_KEY_MOVIETIME_S, settings.getMovieTimeSeconds());
        args.putLong(ARG_KEY_ID, settings.getId());
        args.putLong(ARG_KEY_CREATED, settings.getCreated().getTime());

        PresetsModalBottomSheet sheetFrgmnt = new PresetsModalBottomSheet();
        sheetFrgmnt.setArguments(args);

        mListener = listener;
        return sheetFrgmnt;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mSettings = new LapseSetting();
        if (args != null){
            mSettings.setId(args.getLong(ARG_KEY_ID));
            Date dt = new Date();
            dt.setTime(args.getLong(ARG_KEY_CREATED));
            mSettings.setCreated(dt);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_lapse_settings, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Prepare Numberpickervalues
        hours = new ArrayList<>();
        minAndSec = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            hours.add(String.format("%d", i));
        }

        for (int i = 0; i <= 59; i++) {
            minAndSec.add(String.format("%d", i));
        }

        mEditHours.setDisplayedValues(hours.toArray(new String[hours.size()]));
        mEditHours.setMinValue(0);
        mEditHours.setMaxValue(24);
        mEditHours.setValue(0);
        mEditHours.setOnValueChangedListener(this);

        mEditMinutes.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mEditMinutes.setMinValue(0);
        mEditMinutes.setMaxValue(59);
        mEditMinutes.setValue(0);
        mEditMinutes.setOnValueChangedListener(this);

        mEditSeconds.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mEditSeconds.setMinValue(0);
        mEditSeconds.setMaxValue(59);
        mEditSeconds.setValue(5);
        mEditSeconds.setOnValueChangedListener(this);

        mEditFps.addTextChangedListener(this);
        mEditInterval.addTextChangedListener(this);

        Bundle args = getArguments();
        if (args != null) {
            mEditName.setText(args.getString(ARG_KEY_NAME, ""));
            mEditDesc.setText(args.getString(ARG_KEY_DESC, ""));
            mEditFps.setText(args.getString(ARG_KEY_FPS, ""));
            mEditInterval.setText(args.getString(ARG_KEY_INTVAL, ""));

            mEditHours.setValue(args.getInt(ARG_KEY_MOVIETIME_H, 0));
            mEditMinutes.setValue(args.getInt(ARG_KEY_MOVIETIME_M, 0));
            mEditSeconds.setValue(args.getInt(ARG_KEY_MOVIETIME_S, 0));
        }

        Calculate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void Calculate() {
        long seconds = 0;

        int hour = mEditHours.getValue();
        seconds += (hour * 3600);
        int minute = mEditMinutes.getValue();
        seconds += (minute * 60);
        int second = mEditSeconds.getValue();
        seconds += second;

        long fps = 0;
        if (mEditFps.getText().length() > 0) {
            fps = Long.parseLong(mEditFps.getText().toString());
        }
        long interval = 0;
        if (mEditInterval.getText().length() > 0) {
            interval = Long.parseLong(mEditInterval.getText().toString());
        }

        long count = fps * seconds;
        mPicCount.setText(String.valueOf(count));

        long millis = count * interval * 1000L;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        mShootingLength.setText(hms);

        mSettings.setName(mEditName.getText().toString());
        mSettings.setDescription(mEditDesc.getText().toString());
        mSettings.setFramerate(fps);
        mSettings.setInterval(interval);
        mSettings.setMovieTime(seconds);
        mSettings.setMovieTimeHours(hour);
        mSettings.setMovieTimeMinutes(minute);
        mSettings.setMovieTimeSeconds(second);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Calculate();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Calculate();
    }

    @OnClick(R.id.btn_dismiss)
    public void dismiss(View v){
        this.dismiss();
    }

    @OnClick(R.id.btn_save)
    public void save(View v){
        if (mListener != null){
            Calculate();
            mListener.onPositive(mSettings);
        }
        this.dismiss();
    }
}
