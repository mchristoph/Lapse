package at.mchristoph.lapse.app.events;

/**
 * Created by Xris on 24.03.2016.
 */
public class LapseProgressEvent {
    public final int progress;
    public final String timeUntilFinished;

    public LapseProgressEvent(int progress, String timeUntilFinished) {
        this.progress = progress;
        this.timeUntilFinished = timeUntilFinished;
    }
}
