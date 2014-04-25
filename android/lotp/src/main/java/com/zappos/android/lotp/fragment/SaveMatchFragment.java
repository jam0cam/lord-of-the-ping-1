package com.zappos.android.lotp.fragment;

import android.app.Fragment;
import android.os.Bundle;

import com.zappos.android.lotp.PingPongApplication;
import com.zappos.android.lotp.model.Match;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/5/13.
 */
public class SaveMatchFragment extends Fragment {

    private static final String ARG_MATCH = "match";

    public static interface SaveMatchCallbacks {
        void saveMatchSuccessful(String matchId);
        void saveMatchFailed();
    }

    private SaveMatchCallbacks mCallbacks;

    public static SaveMatchFragment newInstance(Match match) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MATCH, match);
        SaveMatchFragment fragment = new SaveMatchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PingPongApplication application = (PingPongApplication)getActivity().getApplication();
        application.getPingPongService()
                .saveMatch(
                        (Match) getArguments().getSerializable(ARG_MATCH),
                        new Callback<String>() {
                            @Override
                            public void success(String s, Response response) {
                                if (mCallbacks != null) {
                                    mCallbacks.saveMatchSuccessful(s);
                                }
                                detach();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                if (mCallbacks != null) {
                                    mCallbacks.saveMatchFailed();
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
                    .remove(SaveMatchFragment.this)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public void setSaveMatchCallbacks(SaveMatchCallbacks callbacks) {
        mCallbacks = callbacks;
    }
}
