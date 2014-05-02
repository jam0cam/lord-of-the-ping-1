package com.lordoftheping.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lordoftheping.android.PingPongApplication;
import com.lordoftheping.android.R;
import com.lordoftheping.android.event.SignedInEvent;
import com.lordoftheping.android.model.Player;
import com.lordoftheping.android.preference.PingPongPreferences;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import de.greenrobot.event.EventBus;

/**
 * Created by mattkranzler on 12/5/13.
 */
public class AuthFragment extends DialogFragment implements SignInFragment.SignInCallbacks, RegisterFragment.RegisterCallbacks {

    public static interface AuthCallbacks {
        void playerSignedIn(Player player);
        void authCancelled();
    }

    private static final String STATE_REGISTER_VISIBLE = "register_visible";

    private ViewGroup mSignInCont;
    private ViewGroup mRegisterCont;

    // sign in stuff
    private EditText mSignInEmail;
    private EditText mSignInPassword;
    private Button mSignInBtn;
    private Button mSignInRegisterBtn;

    // register stuff
    private EditText mRegisterName;
    private EditText mRegisterEmail;
    private EditText mRegisterPassword;
    private Button mRegisterBtn;
    private Button mRegisterSignInBtn;

    // doing stuff
    private ViewGroup mDoingStuffCont;
    private TextView mDoingStuffLbl;

    // failure
    private ViewGroup mFailureCont;
    private TextView mFailureLbl;
    private Button mTryAgainBtn;

    private PingPongApplication mApplication;
    private AuthCallbacks mCallbacks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auth, container, false);
        mSignInCont = (ViewGroup) root.findViewById(R.id.auth_sign_in_cont);
        mRegisterCont = (ViewGroup) root.findViewById(R.id.auth_register_cont);

        // sign in stuff
        mSignInEmail = (EditText) root.findViewById(R.id.sign_in_email);
        mSignInPassword = (EditText) root.findViewById(R.id.sign_in_password);
        mSignInBtn = (Button) root.findViewById(R.id.sign_in_btn);
        mSignInRegisterBtn = (Button) root.findViewById(R.id.sign_in_register_btn);

        // register stuff
        mRegisterName = (EditText) root.findViewById(R.id.register_name);
        mRegisterEmail = (EditText) root.findViewById(R.id.register_email);
        mRegisterPassword = (EditText) root.findViewById(R.id.register_password);
        mRegisterBtn = (Button) root.findViewById(R.id.register_btn);
        mRegisterSignInBtn = (Button) root.findViewById(R.id.register_sign_in_btn);

        // doing stuff
        mDoingStuffCont = (ViewGroup) root.findViewById(R.id.auth_doing_stuff_cont);
        mDoingStuffLbl = (TextView) root.findViewById(R.id.auth_doing_stuff_lbl);

        // failure
        mFailureCont = (ViewGroup) root.findViewById(R.id.auth_failure_cont);
        mFailureLbl = (TextView) root.findViewById(R.id.auth_failure_lbl);
        mTryAgainBtn = (Button) root.findViewById(R.id.auth_try_again_btn);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mApplication = (PingPongApplication) getActivity().getApplication();
        if (savedInstanceState != null) {
            boolean registerVisible = savedInstanceState.getBoolean(STATE_REGISTER_VISIBLE, false);
            if (registerVisible) {
                mSignInCont.setVisibility(View.GONE);
                mRegisterCont.setVisibility(View.VISIBLE);
            }
        }
        addListeners();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_REGISTER_VISIBLE, mRegisterCont != null && View.VISIBLE == mRegisterCont.getVisibility());
    }

    public void setAuthCallbacks(AuthCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    private void addListeners() {
        mSignInRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSignInCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mRegisterCont.setAlpha(0);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mSignInCont.setVisibility(View.GONE);
                                        mRegisterCont.setVisibility(View.VISIBLE);
                                        mRegisterCont.animate()
                                                .alpha(1)
                                                .setListener(null)
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });

        mRegisterSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegisterCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        mSignInCont.setAlpha(0);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mRegisterCont.setVisibility(View.GONE);
                                        mSignInCont.setVisibility(View.VISIBLE);
                                        mSignInCont.animate()
                                                .alpha(1)
                                                .setListener(null)
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });

        mSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard();
                final String email = mSignInEmail.getText().toString();
                final String password = mSignInPassword.getText().toString();
                if (StringUtils.isEmpty(email)) {
                    mSignInEmail.setError(getString(R.string.auth_email_blank));
                    return;
                } else {
                    mSignInEmail.setError(null);
                }
                if (!EmailValidator.getInstance().isValid(email)) {
                    mSignInEmail.setError(getString(R.string.auth_email_invalid));
                    return;
                } else {
                    mSignInEmail.setError(null);
                }
                if (StringUtils.isEmpty(password)) {
                    mSignInPassword.setError(getString(R.string.auth_password_invalid));
                    return;
                } else {
                    mSignInPassword.setError(null);
                }
                mSignInCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mSignInCont.setVisibility(View.GONE);
                                        mDoingStuffLbl.setText(R.string.auth_signing_in_lbl);
                                        mDoingStuffCont
                                                .animate()
                                                .alpha(1)
                                                .setListener(
                                                        new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationStart(Animator animation) {
                                                                mDoingStuffCont.setAlpha(0);
                                                                mDoingStuffCont.setVisibility(View.VISIBLE);
                                                            }

                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {
                                                                SignInFragment fragment = SignInFragment.newInstance(email, password);
                                                                fragment.setSignInCallbacks(AuthFragment.this);
                                                                getFragmentManager()
                                                                        .beginTransaction()
                                                                        .add(fragment, SignInFragment.class.getName())
                                                                        .commit();
                                                            }
                                                        }

                                                )
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard();
                final String name = mRegisterName.getText().toString();
                final String email = mRegisterEmail.getText().toString();
                final String password = mRegisterPassword.getText().toString();
                if (StringUtils.isEmpty(name)) {
                    mRegisterName.setError(getString(R.string.auth_name_invalid));
                    return;
                } else {
                    mRegisterName.setError(null);
                }
                if (StringUtils.isEmpty(email)) {
                    mRegisterEmail.setError(getString(R.string.auth_email_blank));
                    return;
                } else {
                    mRegisterEmail.setError(null);
                }
                if (!EmailValidator.getInstance().isValid(email)) {
                    mRegisterEmail.setError(getString(R.string.auth_email_invalid));
                    return;
                } else {
                    mRegisterEmail.setError(null);
                }
                if (StringUtils.isEmpty(password)) {
                    mRegisterPassword.setError(getString(R.string.auth_password_invalid));
                    return;
                } else {
                    mRegisterPassword.setError(null);
                }
                mRegisterCont
                        .animate()
                        .alpha(0)
                        .setListener(
                                new AnimatorListenerAdapter() {

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mRegisterCont.setVisibility(View.GONE);
                                        mDoingStuffLbl.setText(R.string.auth_registering_lbl);
                                        mDoingStuffCont
                                                .animate()
                                                .alpha(1)
                                                .setListener(
                                                        new AnimatorListenerAdapter() {
                                                            @Override
                                                            public void onAnimationStart(Animator animation) {
                                                                mDoingStuffCont.setAlpha(0);
                                                                mDoingStuffCont.setVisibility(View.VISIBLE);
                                                            }

                                                            @Override
                                                            public void onAnimationEnd(Animator animation) {
                                                                RegisterFragment fragment = RegisterFragment.newInstance(
                                                                        mRegisterName.getText().toString(),
                                                                        mRegisterEmail.getText().toString(),
                                                                        mRegisterPassword.getText().toString()
                                                                );
                                                                fragment.setRegisterCallbacks(AuthFragment.this);
                                                                getFragmentManager()
                                                                        .beginTransaction()
                                                                        .add(fragment, RegisterFragment.class.getName())
                                                                        .commit();
                                                            }
                                                        }

                                                )
                                                .start();
                                    }
                                }
                        )
                        .start();
            }
        });
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSignInEmail.getWindowToken(), 0);
    }

    @Override
    public void signInSuccessful(final Player player) {
        playerSignedIn(player);
    }

    @Override
    public void signInFailed(final String error) {
        authFailed(false, error);
    }

    @Override
    public void registrationSuccessful(final Player player) {
        playerSignedIn(player);
    }

    @Override
    public void registrationFailed(String error) {
        authFailed(true, error);
    }

    private void playerSignedIn(final Player player) {
        mDoingStuffCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                getFragmentManager()
                                        .beginTransaction()
                                        .remove(AuthFragment.this)
                                        .commitAllowingStateLoss();
                                PingPongPreferences.setCurrentPlayer(player, getActivity());
                                mApplication.setCurrentPlayer(player);
                                EventBus.getDefault().post(new SignedInEvent(player));
                                if (mCallbacks != null) {
                                    mCallbacks.playerSignedIn(player);
                                }
                            }
                        }
                )
                .start();
    }

    private void authFailed(final boolean isRegistration, final String error) {
        final ViewGroup authCont = (isRegistration ? mRegisterCont : mSignInCont);
        mDoingStuffCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationStart(Animator animation) {
                                mFailureCont.setAlpha(0);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mDoingStuffCont.setVisibility(View.GONE);
                                mFailureLbl.setText(error);
                                mFailureCont.setVisibility(View.VISIBLE);
                                mFailureCont
                                        .animate()
                                        .alpha(1)
                                        .setListener(
                                                new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        mTryAgainBtn.setOnClickListener(
                                                                new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        mFailureCont
                                                                                .animate()
                                                                                .alpha(0)
                                                                                .setListener(
                                                                                        new AnimatorListenerAdapter() {

                                                                                            @Override
                                                                                            public void onAnimationEnd(Animator animation) {
                                                                                                mFailureCont.setVisibility(View.GONE);
                                                                                                authCont.setVisibility(View.VISIBLE);
                                                                                                authCont
                                                                                                        .animate()
                                                                                                        .setListener(null)
                                                                                                        .alpha(1)
                                                                                                        .start();
                                                                                            }
                                                                                        }
                                                                                )
                                                                                .start();
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        )
                                        .start();
                            }
                        }
                )
                .start();
    }
}
