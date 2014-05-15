package com.lordoftheping.android.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;
import com.lordoftheping.android.PingPongApplication;

public abstract class PlusBaseFragment extends DialogFragment
        implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String TAG = PlusBaseFragment.class.getSimpleName();

    // A magic number we will use to know that our sign-in error resolution activity has completed
    public static final int OUR_REQUEST_CODE = 49404;

    // A flag to stop multiple dialogues appearing for the user
    private boolean mAutoResolveOnFail;

    // This is the helper object that connects to Google Play Services.
    private PlusClient mPlusClient;

    // The saved result from {@link #onConnectionFailed(ConnectionResult)}.  If a connection
    // attempt has been made, this is non-null.
    // If this IS null, then the connect method is still running.
    private ConnectionResult mConnectionResult;

     /**
     * Called when the PlusClient is successfully connected.
     */
    protected abstract void onPlusClientSignIn();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlusClient =
                new PlusClient.Builder(getActivity(), this, this).setScopes(Scopes.PLUS_LOGIN,
                        Scopes.PROFILE, Scopes.PLUS_ME, "https://www.googleapis.com/auth/userinfo.email").build();
    }

    /**
     * Try to sign in the user.
     */
    public void signIn() {
        Log.d(TAG, "signIn");
        if (!mPlusClient.isConnected()) {
            // Show the dialog as we are now signing in.
            // Make sure that we will start the resolution (e.g. fire the intent and pop up a
            // dialog for the user) for any errors that come in.
            mAutoResolveOnFail = true;
            // We should always have a connection result ready to resolve,
            // so we can start that process.
            if (mConnectionResult != null) {
                startResolution();
            } else {
                // If we don't have one though, we can start connect in
                // order to retrieve one.
                initiatePlusClientConnect();
            }
        }
    }

    /**
     * Connect the {@link PlusClient} only if a connection isn't already in progress.  This will
     * call back to {@link #onConnected(android.os.Bundle)} or
     * {@link #onConnectionFailed(com.google.android.gms.common.ConnectionResult)}.
     */
    private void initiatePlusClientConnect() {
        Log.d(TAG, "initiatePlusClientConnect");
        if (!mPlusClient.isConnected() && !mPlusClient.isConnecting()) {
            mPlusClient.connect();
        }
    }

    /**
     * A helper method to flip the mResolveOnFail flag and start the resolution
     * of the ConnectionResult from the failed connect() call.
     */
    private void startResolution() {
        Log.d(TAG, "startResolution");
        try {
            // Don't start another resolution now until we have a result from the activity we're
            // about to start.
            mAutoResolveOnFail = false;
            // If we can resolve the error, then call start resolution and pass it an integer tag
            // we can use to track.
            // This means that when we get the onActivityResult callback we'll know it's from
            // being started here.
            mConnectionResult.startResolutionForResult(getActivity(), OUR_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            // Any problems, just try to connect() again so we get a new ConnectionResult.
            mConnectionResult = null;
            initiatePlusClientConnect();
        }
    }

    /**
     * Successfully connected (called by PlusClient)
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected");

        if (!((PingPongApplication)getActivity().getApplication()).isManuallySignedOut()) {
            //we don't want to auto-sign in if the user manually signed out.
            onPlusClientSignIn();
        }
    }

    /**
     * Successfully disconnected (called by PlusClient)
     */
    @Override
    public void onDisconnected() {
    }

    /**
     * Connection failed for some reason (called by PlusClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed");

        // Most of the time, the connection will fail with a user resolvable result. We can store
        // that in our mConnectionResult property ready to be used when the user clicks the
        // sign-in button.
        if (result.hasResolution()) {
            mConnectionResult = result;
            if (mAutoResolveOnFail) {
                // This is a local helper function that starts the resolution of the problem,
                // which may be showing the user an account chooser or similar.
                startResolution();
            }
        }
    }

    public void successfullyResolved(int requestCode, int responseCode) {
//        updateConnectButtonState();
        if (requestCode == PlusBaseFragment.OUR_REQUEST_CODE && responseCode == Activity.RESULT_OK) {
            // If we have a successful result, we will want to be able to resolve any further
            // errors, so turn on resolution with our flag.
            mAutoResolveOnFail = true;
            // If we have a successful result, let's call connect() again. If there are any more
            // errors to resolve we'll get our onConnectionFailed, but if not,
            // we'll get onConnected.
            initiatePlusClientConnect();
        }
    }

    public PlusClient getPlusClient() {
        return mPlusClient;
    }
}
