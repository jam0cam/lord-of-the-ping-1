package com.lordoftheping.android.fragment;

import android.app.Fragment;

import com.lordoftheping.android.model.Player;

public class BaseSignInFragment extends Fragment {
    protected SignInCallbacks mCallbacks;

    public static interface SignInCallbacks {
        void signInSuccessful(Player player);
        void signInFailed(String error);
    }


    public void setSignInCallbacks(SignInCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

}
