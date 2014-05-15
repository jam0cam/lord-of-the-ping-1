package com.lordoftheping.android.fragment;

import android.os.Bundle;
import android.util.Log;

import com.lordoftheping.android.PingPongApplication;
import com.lordoftheping.android.R;
import com.lordoftheping.android.model.Player;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GoogleSigninFragment extends BaseSignInFragment {
    private static final String TAG = GoogleSigninFragment.class.getName();

    private Player player;

    public GoogleSigninFragment() {
    }

    public GoogleSigninFragment(Player player) {
        this.player = player;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final PingPongApplication application = (PingPongApplication) getActivity().getApplication();
        application.getPingPongService().googleSignin(
                new Player(
                        player.getName(),
                        player.getEmail(),
                        "1",
                        player.getAvatarUrl()
                ),
                new Callback<Player>() {
                    @Override
                    public void success(Player player, Response response) {
                        Log.d(TAG, "Login successful!");

                        ((PingPongApplication)getActivity().getApplication()).setHasGoogleSignIn(true);

                        if (mCallbacks != null) {
                            mCallbacks.signInSuccessful(player);
                        }
                        detach();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e(TAG, "Login failed :(", retrofitError);
                        if (mCallbacks != null) {
                            mCallbacks.signInFailed(getString(R.string.sign_in_error));
                        }
                        detach();
                    }
                }
        );
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(GoogleSigninFragment.this)
                    .commitAllowingStateLoss();
        }
    }
}
