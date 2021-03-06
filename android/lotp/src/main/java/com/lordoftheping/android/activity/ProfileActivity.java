package com.lordoftheping.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.lordoftheping.android.R;
import com.lordoftheping.android.fragment.ProfileFragment;
import com.lordoftheping.android.model.Player;

/**
 * Created by mattkranzler on 12/6/13.
 */
public class ProfileActivity extends Activity {

    public static final String EXTRA_PLAYER = "player";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setTitle(null);
        if (getFragmentManager().findFragmentById(R.id.fragment_profile) == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_profile, ProfileFragment.newInstance((Player) getIntent().getSerializableExtra(EXTRA_PLAYER)))
                    .commit();
        }
    }
}
