package at.mchristoph.lapse.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import at.mchristoph.lapse.app.R;
import butterknife.Bind;
import lecho.lib.hellocharts.model.ChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Xris on 23.06.2016.
 */
//https://github.com/lecho/hellocharts-android/blob/master/hellocharts-samples/src/lecho/lib/hellocharts/samples/LineChartActivity.java

public class LapseExtendedSettingsFragment extends Fragment {
    @Bind(R.id.chart)
    private LineChartView mChartView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 0));
        values.add(new PointValue(3, 4));
    }
}
