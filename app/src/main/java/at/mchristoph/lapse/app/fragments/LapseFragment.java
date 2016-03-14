package at.mchristoph.lapse.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.utils.CameraApiUtil;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LapseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LapseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TIME = "arg_total_time";
    private static final String ARG_INTERVALL = "arg_intervall";

    // TODO: Rename and change types of parameters
    private Long mTotalTime;
    private Long mIntervall;
    private CameraApiUtil mApi;

    @Bind(R.id.progress_timer) protected ProgressBar mProgressTimer;
    @Bind(R.id.progress_text ) protected TextView    mProgressText;
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
    public static LapseFragment newInstance(Long totalTime, Long intervall) {
        LapseFragment fragment = new LapseFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TIME, totalTime);
        args.putLong(ARG_INTERVALL, intervall);
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
            mTotalTime = getArguments().getLong(ARG_TIME);
            mIntervall = getArguments().getLong(ARG_INTERVALL);
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

        mApi = CameraApiUtil.GetInstance();

        if (mApi != null) {
            new CountDownTimer(mTotalTime, mIntervall) {
                @Override
                public void onTick(long millisUntilFinished_) {
                    mApi.takePicture();
                    double test = ((double) millisUntilFinished_ / (double) mTotalTime) * 100f;
                    mProgressTimer.setProgress((int) test);

                    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished_),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished_) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished_)),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished_) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished_)));

                    mProgressText.setText(hms);
                }

                @Override
                public void onFinish() {
                    mProgressTimer.setProgress(0);
                    mProgressText.setText(0);
                }
            }.start();
        }
    }
}
