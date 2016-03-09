package at.mchristoph.lapse.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import at.mchristoph.lapse.app.fragments.ConnectionFragment;
import at.mchristoph.lapse.app.fragments.LapseFragment;

public class LapseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapse);
    }

    @Override
    protected void onResume() {
        super.onResume();

        replaceFragment(new LapseFragment());
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0){
            super.onBackPressed();
        }else{
            getSupportFragmentManager().popBackStack();
        }

        super.onBackPressed();
    }

    public void replaceFragment(Fragment frgmt){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, frgmt)
                .addToBackStack(null)
                .commit();
    }
}
