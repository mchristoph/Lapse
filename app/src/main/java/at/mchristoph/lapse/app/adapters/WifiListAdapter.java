package at.mchristoph.lapse.app.adapters;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.mchristoph.lapse.app.R;
import butterknife.ButterKnife;

/**
* Created by Xris on 04.03.2016.
*/

/**
 * Adapter class for DeviceList
 */
public class WifiListAdapter extends BaseAdapter {

    private final List<ScanResult> mWifiList;

    private final LayoutInflater mInflater;

    public WifiListAdapter(Context context) {
        mWifiList = new ArrayList<ScanResult>();
        mInflater = LayoutInflater.from(context);
    }

    public void addDevice(ScanResult device) {
        if (device.SSID.contains("DIRECT")) {
            mWifiList.add(device);
        }
        notifyDataSetChanged();
    }

    public void addDevice(List<ScanResult> device) {
        for (ScanResult rslt : device) {
            if (rslt.SSID.contains("DIRECT")) {
                mWifiList.add(rslt);
            }
        }
        notifyDataSetChanged();
    }

    public void clearDevices() {
        mWifiList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mWifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return mWifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0; // not fine
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CardView textView = (CardView) convertView;
        if (textView == null) {
            textView = (CardView) mInflater.inflate(R.layout.list_item_device, parent, false);

        }
        ScanResult device = (ScanResult) getItem(position);

        ((TextView)ButterKnife.findById(textView, R.id.list_item_text)).setText(device.SSID);

        return textView;
    }
}