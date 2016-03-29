package at.mchristoph.lapse.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import at.mchristoph.lapse.app.fragments.ConnectionFragment;
import at.mchristoph.lapse.app.fragments.LapseSettingsFragment;
import at.mchristoph.lapse.app.fragments.MenuFragment;
import at.mchristoph.lapse.app.utils.CameraApiUtil;

public class LapseActivity extends AppCompatActivity {
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
    }

    @Override
    protected void onPause() {
        super.onPause();

        ((LapseApplication)getApplication()).getConnectionManager().unregisterReciever();
    }
}
