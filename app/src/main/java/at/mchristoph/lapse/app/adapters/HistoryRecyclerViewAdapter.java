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
import at.mchristoph.lapse.dao.model.LapseHistory;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Xris on 23.03.2016.
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder>{
    List<LapseHistory> mItems;

    public HistoryRecyclerViewAdapter(){
        mItems = new ArrayList<>();
    }

    public HistoryRecyclerViewAdapter(List<LapseHistory> items){
        mItems = items;
    }

    @Override
    public HistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_history, null);

        return new HistoryRecyclerViewAdapter.ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.created.setText(mItems.get(position).getCreated().toString());
        holder.fps.setText(holder.fpsLabel + String.valueOf(mItems.get(position).getFramerate()));
        holder.interval.setText(holder.intervalLabel + String.valueOf(mItems.get(position).getInterval()));

        long millis = mItems.get(position).getMovieTime() * 1000;
        String hms = String.format(holder.timeStampFormat, TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        holder.time.setText(holder.movieTimeLabel + hms);

        holder.location.setText(holder.locationLabel + mItems.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.list_item_history_created) TextView created;
        @Bind(R.id.list_item_history_fps) TextView fps;
        @Bind(R.id.list_item_history_interval) TextView interval;
        @Bind(R.id.list_item_history_time) TextView time;
        @Bind(R.id.list_item_history_location) TextView location;

        public final String fpsLabel;
        public final String intervalLabel;
        public final String movieTimeLabel;
        public final String locationLabel;
        public final String timeStampFormat;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            fpsLabel = "FPS: ";
            intervalLabel = "Interval: ";
            movieTimeLabel = "Movie Time: ";
            locationLabel = "Shot location: ";
            timeStampFormat = "%02d:%02d:%02d";
        }

        public ViewHolder(View view, Context ctx){
            super(view);
            ButterKnife.bind(this, view);
            fpsLabel = ctx.getString(R.string.generic_fps) + ": ";
            intervalLabel = ctx.getString(R.string.generic_interval) + ": ";
            movieTimeLabel = ctx.getString(R.string.generic_movie_time) + ": ";
            locationLabel = ctx.getString(R.string.generic_location) + ": ";
            timeStampFormat = ctx.getString(R.string.generic_time_string_format);
        }
    }

}
