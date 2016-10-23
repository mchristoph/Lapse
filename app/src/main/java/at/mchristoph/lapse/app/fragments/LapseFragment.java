package at.mchristoph.lapse.app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.events.LapseProgressEvent;
import at.mchristoph.lapse.app.services.LapseService;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LapseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LapseFragment extends Fragment {
    private Long mTotalTime;
    private Long mInterval;
    private boolean mRunning;
    private String mTimeString;
    private int mProgress;

    @Bind(R.id.progress_timer) protected ProgressBar mProgressTimer;
    @Bind(R.id.progress_timer_text ) protected TextView mProgressTimerText;
    @Bind(R.id.progress_text) protected TextView mProgressText;
    @Bind(R.id.shimmer_view_container ) protected ShimmerFrameLayout mShimmerView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LapseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LapseFragment newInstance(Long totalTime, Long interval, String hms, int progress, boolean running) {
        LapseFragment fragment = new LapseFragment();
        Bundle args = new Bundle();
        args.putLong(LapseService.ARG_TIME, totalTime);
        args.putString(LapseService.ARG_TIME_STRING, hms);
        args.putLong(LapseService.ARG_INTERVAL, interval);
        args.putBoolean(LapseService.ARG_RUNNING, running);
        args.putInt(LapseService.ARG_PROGRESS, progress);
        fragment.setArguments(args);
        return fragment;
    }

    public LapseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTotalTime = getArguments().getLong(LapseService.ARG_TIME);
            mInterval = getArguments().getLong(LapseService.ARG_INTERVAL);
            mRunning = getArguments().getBoolean(LapseService.ARG_RUNNING, false);
            mTimeString = getArguments().getString(LapseService.ARG_TIME_STRING, "");
            mProgress = getArguments().getInt(LapseService.ARG_PROGRESS, 100);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressTimer.setMax(100);
        //mShimmerView.setBaseAlpha(0.65f);
        //mShimmerView.setIntensity(0.5f);
        mShimmerView.setDuration(3250);
        mShimmerView.setRepeatDelay(1750);
        mShimmerView.startShimmerAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mRunning == false) {
            Intent intent = new Intent(getContext(), LapseService.class);
            intent.putExtra(LapseService.ARG_TIME, mTotalTime);
            intent.putExtra(LapseService.ARG_INTERVAL, mInterval);
            getActivity().startService(intent);
        }else{
            onMessageEvent(new LapseProgressEvent(mProgress, mTimeString));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onMessageEvent(LapseProgressEvent event){
        if (event.progress > 0){
            mProgressTimer.setProgress(event.progress);
            mProgressTimerText.setText(event.timeUntilFinished);
        }else {
            mProgressTimer.setProgress(0);
            mProgressTimerText.setText(String.format("%02d:%02d:%02d", 0, 0, 0));
            mShimmerView.stopShimmerAnimation();
            mProgressText.setText("LAPSE FINISHED!");
        }
    }
}
