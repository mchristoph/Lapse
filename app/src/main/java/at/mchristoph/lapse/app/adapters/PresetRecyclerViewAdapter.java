package at.mchristoph.lapse.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.dao.model.LapseSetting;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Xris on 23.03.2016.
 */
public class PresetRecyclerViewAdapter extends RecyclerView.Adapter<PresetRecyclerViewAdapter.ViewHolder> {
    private List<LapseSetting> mItems;

    public PresetRecyclerViewAdapter(){
        mItems = new ArrayList<>();
    }

    public PresetRecyclerViewAdapter(List<LapseSetting> items){
        mItems = items;
    }

    @Override
    public PresetRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_preset, null);

        return new PresetRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PresetRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(mItems.get(position).getName());
        holder.description.setText(mItems.get(position).getDescription());
        holder.created.setText(mItems.get(position).getCreated().toString());

        holder.fps.setText("FPS: " + String.valueOf(mItems.get(position).getFramerate()));
        holder.interval.setText("Interval: " + String.valueOf(mItems.get(position).getInterval()));

        long millis = mItems.get(position).getMovie_time() * 1000;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.time.setText("Movie Time: " + hms);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.list_item_preset_name) TextView name;
        @Bind(R.id.list_item_preset_desc) TextView description;
        @Bind(R.id.list_item_preset_created) TextView created;
        @Bind(R.id.list_item_preset_fps) TextView fps;
        @Bind(R.id.list_item_preset_interval) TextView interval;
        @Bind(R.id.list_item_preset_time) TextView time;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

