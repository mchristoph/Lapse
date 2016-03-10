package at.mchristoph.lapse.app.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;
import at.mchristoph.lapse.app.R;
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

    @Bind(R.id.progress_timer) protected ProgressBar mProgressTimer;

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
        ButterKnife.bind(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        CountDownTimer countDownTimer = new CountDownTimer(mTotalTime, mIntervall) {
            private boolean warned = false;
            @Override
            public void onTick(long millisUntilFinished_) {
                mProgressTimer.setProgress((int)((mTotalTime-millisUntilFinished_)/mTotalTime*100));
            }

            @Override
            public void onFinish() {
                // do whatever when the bar is full
            }
        }.start();
    }
}
