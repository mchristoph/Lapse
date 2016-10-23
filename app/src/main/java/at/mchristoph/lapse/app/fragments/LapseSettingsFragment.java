package at.mchristoph.lapse.app.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.LapseActivity;
import at.mchristoph.lapse.app.LapseApplication;
import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.fragments.dialogs.AddPresetsModalBottomSheet;
import at.mchristoph.lapse.app.fragments.dialogs.PresetsModalBottomSheet;
import at.mchristoph.lapse.app.interfaces.OnPresetBottomSheetListener;
import at.mchristoph.lapse.app.models.ServerDevice;
import at.mchristoph.lapse.app.services.FetchAddressIntentService;
import at.mchristoph.lapse.app.utils.CameraApiUtil;
import at.mchristoph.lapse.dao.model.LapseHistory;
import at.mchristoph.lapse.dao.model.LapseHistoryDao;
import at.mchristoph.lapse.dao.model.LapseSetting;
import at.mchristoph.lapse.dao.model.LapseSettingDao;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.dao.DaoException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LapseSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LapseSettingsFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String ARG_DEVICE = "arg_connected_device";
    private static final String KEY_FPS = "key_fps";
    private static final String KEY_INTERVAL = "key_interval";
    private static final String KEY_MOVIETIME_H = "key_movie_time_h";
    private static final String KEY_MOVIETIME_M = "key_movie_time_m";
    private static final String KEY_MOVIETIME_S = "key_movie_time_s";

    private ServerDevice mDevice;
    private AddressResultReceiver mResultReceiver;
    private GoogleApiClient mGoogleApiClient;

    @Bind(R.id.np_times_hours)
    protected NumberPicker mPickerHours;
    @Bind(R.id.np_times_minutes)
    protected NumberPicker mPickerMinutes;
    @Bind(R.id.np_times_seconds)
    protected NumberPicker mPickerSeconds;
    @Bind(R.id.txt_target_framerate)
    protected EditText mFps;
    @Bind(R.id.txt_interval)
    protected EditText mIntervall;
    @Bind(R.id.textView5)
    protected TextView mPicCount;
    @Bind(R.id.textView4)
    protected TextView mShootingLength;
    @Bind(R.id.start_button)
    protected ImageView mStartButton;

    private List<String> hours;
    private List<String> minAndSec;
    private Location mLastLocation;
    private String mCurrentLocation;

    private Bundle mSavedInstanceState;

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
        for (int i = 0; i <= 24; i++) {
            hours.add(String.format("%d", i));
        }

        for (int i = 0; i <= 59; i++) {
            minAndSec.add(String.format("%d", i));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDevice = getArguments().getParcelable(ARG_DEVICE);
        }
        setHasOptionsMenu(true);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mSavedInstanceState = savedInstanceState;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        mPickerMinutes.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerMinutes.setMinValue(0);
        mPickerMinutes.setMaxValue(59);

        mPickerSeconds.setDisplayedValues(minAndSec.toArray(new String[minAndSec.size()]));
        mPickerSeconds.setMinValue(0);
        mPickerSeconds.setMaxValue(59);

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

        mPickerHours.setOnValueChangedListener(listener);
        mPickerMinutes.setOnValueChangedListener(listener);
        mPickerSeconds.setOnValueChangedListener(listener);
        mStartButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                StartTimelapse();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mSavedInstanceState != null){
            mFps.setText(mSavedInstanceState.getString(KEY_FPS));
            mIntervall.setText(mSavedInstanceState.getString(KEY_INTERVAL));
            mPickerHours.setValue(mSavedInstanceState.getInt(KEY_MOVIETIME_H));
            mPickerMinutes.setValue(mSavedInstanceState.getInt(KEY_MOVIETIME_M));
            mPickerSeconds.setValue(mSavedInstanceState.getInt(KEY_MOVIETIME_S));
        }else{
            /*mFps.setText("24");
            mIntervall.setText("5");
            mPickerHours.setValue(0);
            mPickerMinutes.setValue(0);
            mPickerSeconds.setValue(5);*/
        }

        Calculate();
    }

    private void StartTimelapse() {
        if (!((LapseApplication)getActivity().getApplication()).getApi().isConnected()){
            Snackbar.make(getView(), "Cannot start, no device connected", Snackbar.LENGTH_LONG).show();

            ((LapseActivity) getActivity()).replaceFragment(new LapseExtendedSettingsFragment());
            return;
        }

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

        long count = (fps * seconds) + 1;

        LapseHistoryDao dao = ((LapseApplication) getActivity().getApplicationContext()).getSession().getLapseHistoryDao();

        long daoCount = dao.count();
        if (daoCount == 50) {
            dao.queryBuilder().orderAsc(LapseHistoryDao.Properties.Created).limit(1).buildDelete().executeDeleteWithoutDetachingEntities();
        }

        LapseHistory history = new LapseHistory();
        history.setFramerate(fps);
        history.setInterval(interval);
        history.setMovieTime(seconds);
        history.setMovieTimeHours(hour);
        history.setMovieTimeMinutes(minute);
        history.setMovieTimeSeconds(second);
        history.setCreated(new Date());
        history.setLocation(mCurrentLocation == null ? "" : mCurrentLocation);

        dao.insert(history);

        ((LapseActivity) getActivity()).replaceFragment(LapseFragment.newInstance(count * interval * 1000L, interval * 1000L, "", 100, false));
    }

    private void Calculate() {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lapse_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_preset) {
            showSaveDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_FPS, mFps.getText().toString());
        outState.putString(KEY_INTERVAL, mIntervall.getText().toString());
        outState.putInt(KEY_MOVIETIME_H, mPickerHours.getValue());
        outState.putInt(KEY_MOVIETIME_M, mPickerMinutes.getValue());
        outState.putInt(KEY_MOVIETIME_S, mPickerSeconds.getValue());

        super.onSaveInstanceState(outState);
    }

    private void showSaveDialog() {
        AddPresetsModalBottomSheet sheet = AddPresetsModalBottomSheet.newInstance(new OnPresetBottomSheetListener() {
            @Override
            public void onPositive(@Nullable LapseSetting settings) {
                addPreset(settings.getName(), settings.getDescription());
            }
        });
        sheet.show(getFragmentManager(), "bottom_sheet");
    }

    private void addPreset(String name, String description) {
        if (name.isEmpty()) {
            Snackbar.make(ButterKnife.findById(getActivity(), android.R.id.content), R.string.add_preset_no_name_error, Snackbar.LENGTH_LONG).show();
            return;
        }

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

        LapseSettingDao dao = ((LapseApplication) getActivity().getApplicationContext()).getSession().getLapseSettingDao();

        LapseSetting set = new LapseSetting();
        set.setName(name);
        set.setDescription(description);
        set.setFramerate(fps);
        set.setInterval(interval);
        set.setMovieTime(seconds);
        set.setMovieTimeHours(hour);
        set.setMovieTimeMinutes(minute);
        set.setMovieTimeSeconds(second);
        set.setCreated(new Date());

        int message = R.string.add_preset_success;
        try {
            if (dao.insert(set) <= 0){
                message = R.string.add_preset_safe_error;
            }
        }catch (DaoException e){
            message = R.string.add_preset_safe_error;
        }catch (Exception e){
            message = R.string.add_preset_safe_error;
        }

        Snackbar.make(ButterKnife.findById(getActivity(), android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null){
            if (!Geocoder.isPresent()) {
                mCurrentLocation = "";
                return;
            }


            Intent intent = new Intent(getContext(), FetchAddressIntentService.class);
            mResultReceiver = new AddressResultReceiver(new Handler());
            intent.putExtra(FetchAddressIntentService.RECEIVER, mResultReceiver);
            intent.putExtra(FetchAddressIntentService.LOCATION_DATA_EXTRA, mLastLocation);
            getActivity().startService(intent);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(FetchAddressIntentService.RESULT_DATA_KEY);

            // Show a toast message if an address was found.
            if (resultCode == FetchAddressIntentService.SUCCESS_RESULT) {
                mCurrentLocation = mAddressOutput;
            }
        }
    }
}
