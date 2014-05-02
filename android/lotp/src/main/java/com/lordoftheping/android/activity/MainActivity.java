package com.lordoftheping.android.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.lordoftheping.android.PingPongApplication;
import com.lordoftheping.android.R;
import com.lordoftheping.android.event.SignedInEvent;
import com.lordoftheping.android.event.SignedOutEvent;
import com.lordoftheping.android.fragment.AuthFragment;
import com.lordoftheping.android.fragment.InboxFragment;
import com.lordoftheping.android.fragment.LeaderboardFragment;
import com.lordoftheping.android.fragment.NewMatchFragment;
import com.lordoftheping.android.fragment.ProfileFragment;
import com.lordoftheping.android.preference.PingPongPreferences;

import java.util.Locale;

import de.greenrobot.event.EventBus;

public class MainActivity extends Activity implements ActionBar.TabListener {

    public static final int TAB_LEADERBOARD = 0;
    public static final int TAB_NEW_MATCH = 1;
    public static final int TAB_PROFILE = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private DrawerLayout mDrawerLayout;
    private InboxFragment mInboxFragment;
    private PingPongApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        mApplication = (PingPongApplication) getApplication();

        setContentView(R.layout.activity_main);

        mInboxFragment = (InboxFragment) getFragmentManager().findFragmentById(R.id.fragment_inbox);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setTitle(null);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(2);
        setupViewPager();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mDrawerLayout.setDrawerListener(mInboxFragment);
        mInboxFragment.setDrawerLayout(mDrawerLayout);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        refreshInboxDrawerState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDrawerLayout.setDrawerListener(null);
    }

    private void setupViewPager() {
        final int previousTab = mViewPager.getCurrentItem();
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        new Handler(Looper.getMainLooper())
                .post(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (previousTab > 0) {
                                    getActionBar().setSelectedNavigationItem(previousTab);
                                    mViewPager.setCurrentItem(previousTab, false);
                                }
                            }
                        }
                );
    }

    private void refreshInboxDrawerState() {
        if (mApplication.getCurrentPlayer() != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                PingPongPreferences.signOut(this);
                mApplication.setCurrentPlayer(null);
                EventBus.getDefault().post(new SignedOutEvent());
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logout = menu.findItem(R.id.action_sign_out);
        if (logout != null) {
            logout.setVisible(mApplication.getCurrentPlayer() != null);
        }
        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_LEADERBOARD:
                    return new LeaderboardFragment();
                case TAB_NEW_MATCH:
                    return new NewMatchFragment();
                case TAB_PROFILE:
                    if (mApplication.getCurrentPlayer() == null) {
                        return new AuthFragment();
                    } else {
                        return ProfileFragment.newInstance(mApplication.getCurrentPlayer());
                    }
            }
            return null;
        }

        @Override
        public int getItemPosition(Object object) {
            if ((object instanceof ProfileFragment
                    && mApplication.getCurrentPlayer() == null)
                    || (object instanceof AuthFragment
                    && mApplication.getCurrentPlayer() != null)) {
                getFragmentManager()
                        .beginTransaction()
                        .remove((Fragment) object)
                        .commit();
                return POSITION_NONE;
            }
            return POSITION_UNCHANGED;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAB_LEADERBOARD:
                    return getString(R.string.title_leaderboard).toUpperCase(l);
                case TAB_NEW_MATCH:
                    return getString(R.string.title_new_match).toUpperCase(l);
                case TAB_PROFILE:
                    return getString(R.string.title_profile).toUpperCase(l);
            }
            return null;
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedInEvent event) {
        if (event.getPlayer() != null) {
            mViewPager.getAdapter().notifyDataSetChanged();
        }
        invalidateOptionsMenu();
        refreshInboxDrawerState();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedOutEvent event) {
        mViewPager.getAdapter().notifyDataSetChanged();
        invalidateOptionsMenu();
        refreshInboxDrawerState();
    }
}
