package at.mchristoph.lapse.app.interfaces;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import at.mchristoph.lapse.dao.model.LapseSetting;

/**
 * Created by Xris on 24.03.2016.
 */
public interface OnPresetBottomSheetListener {
    void onPositive(@Nullable LapseSetting settings);
}
