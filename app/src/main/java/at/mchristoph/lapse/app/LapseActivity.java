package at.mchristoph.lapse.app;

import android.net.wifi.WifiInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import org.greenrobot.eventbus.EventBus;

import at.mchristoph.lapse.app.events.ConnectionChangeEvent;
import at.mchristoph.lapse.app.fragments.MenuFragment;
import at.mchristoph.lapse.app.interfaces.OnConnectionChangeListener;

public class LapseActivity extends AppCompatActivity implements OnConnectionChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapse);

        replaceFragment(new MenuFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lapse, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count > 0){
            getSupportFragmentManager().popBackStack();
        }else{
            super.onBackPressed();
        }
    }

    public void replaceFragment(Fragment frgmt){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frgmt)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((LapseApplication)getApplication()).getConnectionManager().registerReciever();
        ((LapseApplication)getApplication()).getConnectionManager().setConnectionListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        ((LapseApplication)getApplication()).getConnectionManager().unregisterReciever();
    }

    @Override
    public void onConnect(WifiInfo connectionInfo) {
        EventBus.getDefault().post(new ConnectionChangeEvent(ConnectionChangeEvent.Status.CONNECTED, connectionInfo));
    }

    @Override
    public void onDisconnect(WifiInfo connectionInfo) {
        EventBus.getDefault().post(new ConnectionChangeEvent(ConnectionChangeEvent.Status.DISCONNECTED, connectionInfo));
    }
}
