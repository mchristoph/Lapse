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
public class AddPresetsModalBottomSheet extends BottomSheetDialogFragment {
    @Bind(R.id.txt_preset_name)         TextView mEditName;
    @Bind(R.id.txt_preset_description)  TextView mEditDesc;

    private static OnPresetBottomSheetListener mListener;

    public static AddPresetsModalBottomSheet newInstance(){
        return newInstance(null);
    }

    public static AddPresetsModalBottomSheet newInstance(OnPresetBottomSheetListener listener){
        AddPresetsModalBottomSheet sheetFrgmnt = new AddPresetsModalBottomSheet();
        mListener = listener;
        return sheetFrgmnt;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_preset, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @OnClick(R.id.btn_dismiss)
    public void dismiss(View v){
        this.dismiss();
    }

    @OnClick(R.id.btn_save)
    public void save(View v){
        if (mListener != null){
            LapseSetting setting = new LapseSetting();
            setting.setName(mEditName.getText().toString());
            setting.setDescription(mEditDesc.getText().toString());
            mListener.onPositive(setting);
        }
        this.dismiss();
    }
}
