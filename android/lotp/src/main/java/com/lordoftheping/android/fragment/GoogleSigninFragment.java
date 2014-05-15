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
    private static final String PLAYER = "player";

    private Player mPlayer;


    public static GoogleSigninFragment newInstance(Player player) {
        Bundle args = new Bundle();
        args.putSerializable(PLAYER, player);
        GoogleSigninFragment fragment = new GoogleSigninFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPlayer = (Player)getArguments().getSerializable(PLAYER);

        setRetainInstance(true);
        final PingPongApplication application = (PingPongApplication) getActivity().getApplication();
        application.getPingPongService().googleSignin(
                new Player(
                        mPlayer.getName(),
                        mPlayer.getEmail(),
                        "1",
                        mPlayer.getAvatarUrl()
                ),
                new Callback<Player>() {
                    @Override
                    public void success(Player player, Response response) {
                        Log.d(TAG, "Login successful!");

                        ((PingPongApplication) getActivity().getApplication()).setHasGoogleSignIn(true);

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
