package at.mchristoph.lapse.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import at.mchristoph.lapse.app.R;
import at.mchristoph.lapse.app.interfaces.OnItemClickListener;
import at.mchristoph.lapse.app.interfaces.OnLongItemClickListener;
import at.mchristoph.lapse.dao.model.LapseSetting;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Xris on 23.03.2016.
 */
public class PresetRecyclerViewAdapter extends RecyclerView.Adapter<PresetRecyclerViewAdapter.ViewHolder> {
    private List<LapseSetting> mItems;
    private static OnItemClickListener mClickListener;
    private static OnLongItemClickListener mLongClickListener;

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

        return new PresetRecyclerViewAdapter.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(PresetRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.name.setText(mItems.get(position).getName());
        holder.description.setText(mItems.get(position).getDescription());
        holder.created.setText(mItems.get(position).getCreated().toString());

        holder.fps.setText(holder.fpsLabel + String.valueOf(mItems.get(position).getFramerate()));
        holder.interval.setText(holder.intervalLabel + String.valueOf(mItems.get(position).getInterval()));

        long millis = mItems.get(position).getMovieTime() * 1000;
        String hms = String.format(holder.timeStampFormat, TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.time.setText(holder.movieTimeLabel + hms);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void addItem(LapseSetting set){
        mItems.add(set);
    }
    public void addItem(int pos, LapseSetting set){
        mItems.add(pos, set);
        notifyItemInserted(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public void remove(int pos){
        mItems.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }

    public LapseSetting getItem(int pos){
        return mItems.get(pos);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        @Bind(R.id.list_item_preset_name) TextView name;
        @Bind(R.id.list_item_preset_desc) TextView description;
        @Bind(R.id.list_item_preset_created) TextView created;
        @Bind(R.id.list_item_preset_fps) TextView fps;
        @Bind(R.id.list_item_preset_interval) TextView interval;
        @Bind(R.id.list_item_preset_time) TextView time;

        public final String fpsLabel;
        public final String intervalLabel;
        public final String movieTimeLabel;
        public final String timeStampFormat;


        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            fpsLabel = "FPS: ";
            intervalLabel = "Interval: ";
            movieTimeLabel = "Movie Time: ";
            timeStampFormat = "%02d:%02d:%02d";

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public ViewHolder(View view, Context ctx){
            super(view);
            ButterKnife.bind(this, view);
            fpsLabel = ctx.getString(R.string.generic_fps) + ": ";
            intervalLabel = ctx.getString(R.string.generic_interval) + ": ";
            movieTimeLabel = ctx.getString(R.string.generic_movie_time) + ": ";
            timeStampFormat = ctx.getString(R.string.generic_time_string_format);


            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (PresetRecyclerViewAdapter.mClickListener != null){
                PresetRecyclerViewAdapter.mClickListener.onClick(getAdapterPosition(), v);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (PresetRecyclerViewAdapter.mClickListener != null){
                return PresetRecyclerViewAdapter.mLongClickListener.onLongClick(getAdapterPosition(), v);
            }

            return false;
        }
    }

    public void setOnClickListener(OnItemClickListener l){
        PresetRecyclerViewAdapter.mClickListener = l;
    }

    public void setOnLongClickListener(OnLongItemClickListener l){
        PresetRecyclerViewAdapter.mLongClickListener = l;
    }
}

