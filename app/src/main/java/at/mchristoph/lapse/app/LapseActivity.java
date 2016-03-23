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
    private CameraApiUtil mApi;
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapse);
    }

    @Override
    protected void onResume() {
        super.onResume();
        replaceFragment(new MenuFragment());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lapse, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
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


}
