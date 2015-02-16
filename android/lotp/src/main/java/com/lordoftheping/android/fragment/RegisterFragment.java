package com.lordoftheping.android.fragment;

import android.app.Fragment;
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
public class RegisterFragment extends Fragment {

    private static final String TAG = RegisterFragment.class.getName();
    private static final String ARG_NAME = "name";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PASSWORD = "password";

    public static interface RegisterCallbacks {
        void registrationSuccessful(Player player);
        void registrationFailed(String error);
    }

    private RegisterCallbacks mCallbacks;

    public static RegisterFragment newInstance(String name, String email, String password) {
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_EMAIL, email);
        args.putString(ARG_PASSWORD, password);
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        final PingPongApplication application = (PingPongApplication) getActivity().getApplication();
        application.getPingPongService().register(
                new Player(
                        getArguments().getString(ARG_NAME),
                        getArguments().getString(ARG_EMAIL),
                        getArguments().getString(ARG_PASSWORD)
                ),
                new Callback<Player>() {
                    @Override
                    public void success(Player player, Response response) {
                        Log.d(TAG, "Registration successful!");
                        if (mCallbacks != null) {
                            mCallbacks.registrationSuccessful(player);
                        }
                        detach();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e(TAG, "Registration failed :(", retrofitError);
                        if (mCallbacks != null) {
                            mCallbacks.registrationFailed(getString(R.string.registration_error));
                        }
                        detach();
                    }
                }
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setRegisterCallbacks(RegisterCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    private void detach() {
        if (isAdded()) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(RegisterFragment.this)
                    .commitAllowingStateLoss();
        }
    }
}
