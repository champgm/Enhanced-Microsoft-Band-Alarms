package org.champgm.enhancedalarm;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity class, really just a holder for a {@link org.champgm.enhancedalarm.timerui.TimerAdapter}.
 */
public class MainActivity extends ActionBarActivity {

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    /**
     * auto-generated, not modified
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Sort of like a click-listener for all settings menu items.
     * Currently the only one we care about is {@link org.champgm.enhancedalarm.R.id#action_settings}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        final int id = item.getItemId();

        // Here is where we launch the settings activity if settings is clicked.
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public final void onRestoreInstanceState(final Bundle savedInstanceState, final PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new PageChangeListener());

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_timers).setTabListener(new TabListener()));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_alarms).setTabListener(new TabListener()));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            Log.i("MAIN", "got item: " + position);
            switch (position) {
                case 0:
                    return new TimersFragment();
                case 1:
                    return new AlarmsFragment();
                default:
                    return new TimersFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener{
        @Override
        public void onPageSelected(int position) {
            final ActionBar supportActionBar = getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setSelectedNavigationItem(position);
            }
        }
    }

    private class TabListener implements ActionBar.TabListener {

        @Override
        public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
            mPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {

        }

        @Override
        public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {

        }
    }

}
