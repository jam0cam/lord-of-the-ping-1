package com.lordoftheping.android.fragment;

import android.os.Bundle;
import android.util.Log;

import com.lordoftheping.android.PingPongApplication;
import com.lordoftheping.android.R;
import com.lordoftheping.android.model.Player;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/5/13.
 */
public class SignInFragment extends BaseSignInFragment {

    private static final String TAG = SignInFragment.class.getName();
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PASSWORD = "password";


    public static SignInFragment newInstance(String email, String password) {
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PASSWORD, password);
        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final PingPongApplication application = (PingPongApplication) getActivity().getApplication();
        application.getPingPongService().signIn(
                new Player(
                        getArguments().getString(ARG_EMAIL),
                        getArguments().getString(ARG_PASSWORD)
                ),
                new Callback<Player>() {
                    @Override
                    public void success(Player player, Response response) {
                        Log.d(TAG, "Login successful!");
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
                    .remove(SignInFragment.this)
                    .commitAllowingStateLoss();
        }
    }
}
