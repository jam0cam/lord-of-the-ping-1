package com.zappos.android.pingpong.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.model.Match;
import com.zappos.android.pingpong.model.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class NewMatchFragment extends Fragment implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener, SaveMatchFragment.SaveMatchCallbacks, AuthFragment.AuthCallbacks {

    private static final String TAG = NewMatchFragment.class.getName();
    private static final String STATE_OPPONENT = "opponent";
    private static final DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private static final String[] WINS_ARRAY = {
            "0",
            "1",
            "2",
            "3",
            "4",
            "5"
    };

    private static final String[][] LOSSES_ARRAYS = {

            // 0 WINS (loss)
            {
                    "3",
                    "4",
                    "5"
            },

            // 1 WIN (loss)
            {
                    "3",
                    "4"
            },

            // 2 WINS (loss)
            {
                    "3"
            },

            // 3 WINS (win)
            {
                    "0",
                    "1",
                    "2"
            },

            // 4 WINS (win)
            {
                    "0",
                    "1"
            },

            // 5 WINS (win)
            {
                    "0"
            },
    };

    private ViewGroup mNewMatchCont;
    private AutoCompleteTextView mOpponentField;
    private ImageButton mClearOpponentBtn;
    private Spinner mWinsSpinner;
    private Spinner mLossesSpinner;
    private Button mSubmitBtn;

    private ViewGroup mSubmittingCont;

    private ViewGroup mSubmissionResultCont;
    private TextView mSubmissionResult;
    private Button mSubmissionContinueBtn;

    private OpponentAutoCompleteAdapter mOpponentAutoCompleteAdapter;
    private List<Player> mOpponents;

    private PingPongApplication mApplication;
    private Player mOpponent;
    private Match mPendingMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_new_match, container, false);
        mNewMatchCont = (ViewGroup) root.findViewById(R.id.new_match_cont);
        mOpponentField = (AutoCompleteTextView) root.findViewById(R.id.new_match_opponent);
        mClearOpponentBtn = (ImageButton) root.findViewById(R.id.new_match_clear_opponent);
        mWinsSpinner = (Spinner) root.findViewById(R.id.new_match_wins);
        mLossesSpinner = (Spinner) root.findViewById(R.id.new_match_losses);
        mSubmitBtn = (Button) root.findViewById(R.id.new_match_submit_btn);

        mSubmittingCont = (ViewGroup) root.findViewById(R.id.new_match_submitting_cont);

        mSubmissionResultCont = (ViewGroup) root.findViewById(R.id.new_match_match_submission_result_cont);
        mSubmissionResult = (TextView) root.findViewById(R.id.new_match_submission_result);
        mSubmissionContinueBtn = (Button) root.findViewById(R.id.new_match_submission_continue_btn);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mOpponent = (Player) savedInstanceState.getSerializable(STATE_OPPONENT);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();

        // maybe re-attach to SaveMatchFragment
        SaveMatchFragment saveMatchFragment;
        if ((saveMatchFragment = (SaveMatchFragment) getFragmentManager().findFragmentByTag(SaveMatchFragment.class.getName())) != null) {
            saveMatchFragment.setSaveMatchCallbacks(this);
        }

        // maybe re-attach to AuthFragment
        AuthFragment authFragment;
        if ((authFragment = (AuthFragment) getFragmentManager().findFragmentByTag(AuthFragment.class.getName())) != null) {
            authFragment.setAuthCallbacks(this);
        }

        if (mOpponents == null) {
            mOpponents = new ArrayList<Player>();
            mApplication.getPingPongService().getAllPlayers(
                    new Callback<List<Player>>() {
                        @Override
                        public void success(List<Player> players, Response response) {
                            Log.d(TAG, "Received a list of opponents!");
                            mOpponents = players;

                            // remove current player if we have one
                            if (mApplication.getCurrentPlayer() != null) {
                                mOpponents.remove(mApplication.getCurrentPlayer());
                            }
                            mOpponentAutoCompleteAdapter.setOpponents(mOpponents);
                            mOpponentAutoCompleteAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            Log.d(TAG, "An error occurred while retrieving opponents :(", retrofitError);
                        }
                    }
            );
        }
        mOpponentAutoCompleteAdapter = new OpponentAutoCompleteAdapter(getActivity(), mOpponents);
        mOpponentField.setAdapter(mOpponentAutoCompleteAdapter);
        mOpponentField.setOnItemClickListener(this);

        mClearOpponentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOpponent = null;
                mOpponentField.setText(null);
                updateOpponentState();
                updateWinsSpinnerState();
            }
        });

        mWinsSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_wins_losses_dropdown_item, WINS_ARRAY));
        mWinsSpinner.setOnItemSelectedListener(this);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mApplication.getCurrentPlayer() == null) {

                    // build a pending match
                    mPendingMatch = buildMatchFromUi();

                    AuthFragment fragment = new AuthFragment();
                    fragment.setAuthCallbacks(NewMatchFragment.this);
                    getFragmentManager()
                            .beginTransaction()
                            .add(fragment, AuthFragment.class.getName())
                            .commit();

                } else {
                    submitMatch(buildMatchFromUi());
                }
            }
        });

        updateOpponentState();
        updateWinsSpinnerState();
    }

    private void submitMatch(final Match match) {
        mNewMatchCont
                .animate()
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mSubmittingCont.setAlpha(0);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mNewMatchCont.setVisibility(View.GONE);
                        mSubmittingCont.setVisibility(View.VISIBLE);
                        mSubmittingCont
                                .animate()
                                .alpha(1)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        saveMatch(match);
                                    }
                                })
                                .start();
                    }
                })
                .start();
    }

    private Match buildMatchFromUi() {
        Match match = new Match();
        match.setP1(mApplication.getCurrentPlayer());
        match.setP2(mOpponent);
        match.setP1Score(Integer.valueOf(((ArrayAdapter<String>) mWinsSpinner.getAdapter()).getItem(mWinsSpinner.getSelectedItemPosition())));
        match.setP2Score(Integer.valueOf(((ArrayAdapter<String>) mLossesSpinner.getAdapter()).getItem(mLossesSpinner.getSelectedItemPosition())));
        match.setDateString(MATCH_DATE_FORMAT.format(new Date()));
        return match;
    }

    private void saveMatch(Match match) {
        SaveMatchFragment fragment = SaveMatchFragment.newInstance(match);
        fragment.setSaveMatchCallbacks(NewMatchFragment.this);

        getFragmentManager()
                .beginTransaction()
                .add(fragment, SaveMatchFragment.class.getName())
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_OPPONENT, mOpponent);
    }

    private void updateWinsSpinnerState() {
        mWinsSpinner.setEnabled(isValidOpponent());
        updateLossesSpinnerState();
        updateSubmitBtnState();
    }

    private void updateOpponentState() {
        mOpponentField.setEnabled(!isValidOpponent());
        updateClearOpponentBtnState();
    }

    private void updateClearOpponentBtnState() {
        mClearOpponentBtn.setVisibility(mOpponentField.isEnabled() ? View.GONE : View.VISIBLE);
    }

    private void updateLossesSpinnerState() {
        mLossesSpinner.setEnabled(isValidOpponent());
    }

    private void updateSubmitBtnState() {
        mSubmitBtn.setEnabled(isValidOpponent());
    }

    private boolean isValidOpponent() {
        return mOpponent != null && !mOpponent.equals(mApplication.getCurrentPlayer());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mOpponent = mOpponentAutoCompleteAdapter.getItem(position);
        Log.d(TAG, "Selected player " + mOpponent + " as opponent");
        updateOpponentState();
        updateWinsSpinnerState();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mLossesSpinner.setAdapter(
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.spinner_wins_losses_dropdown_item,
                        LOSSES_ARRAYS[Integer.valueOf(WINS_ARRAY[position])]
                )
        );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {/* no-op */}

    @Override
    public void saveMatchSuccessful(String matchId) {
        showResultCont(getString(R.string.new_match_match_submitted_lbl, mOpponent.getName()), true);
    }

    @Override
    public void saveMatchFailed() {
        showResultCont(getString(R.string.new_match_match_submission_failed_lbl), false);
    }

    private void showResultCont(final String result, final boolean success) {
        mSubmittingCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                mSubmissionResultCont.setAlpha(0);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mSubmittingCont.setVisibility(View.GONE);
                                mSubmissionResultCont.setVisibility(View.VISIBLE);
                                mSubmissionResult.setText(result);
                                mSubmittingCont.animate().setListener(null);
                                mSubmissionResultCont
                                        .animate()
                                        .alpha(1)
                                        .setListener(
                                                new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        mSubmissionContinueBtn.setOnClickListener(
                                                                new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        mSubmissionResultCont
                                                                                .animate()
                                                                                .alpha(0)
                                                                                .setListener(
                                                                                        new AnimatorListenerAdapter() {

                                                                                            @Override
                                                                                            public void onAnimationEnd(Animator animation) {
                                                                                                mSubmissionResultCont.setVisibility(View.GONE);
                                                                                                mNewMatchCont.setVisibility(View.VISIBLE);
                                                                                                mNewMatchCont
                                                                                                        .animate()
                                                                                                        .alpha(1)
                                                                                                        .setListener(
                                                                                                                new AnimatorListenerAdapter() {
                                                                                                                    @Override
                                                                                                                    public void onAnimationEnd(Animator animation) {
                                                                                                                        if (success) {
                                                                                                                            continueWithSuccess();
                                                                                                                        } else {
                                                                                                                            continueWithFailure();
                                                                                                                        }
                                                                                                                    }
                                                                                                                }
                                                                                                        );
                                                                                            }
                                                                                        }
                                                                                );
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        )
                                        .start();
                            }
                        }
                );
    }

    private void continueWithSuccess() {
        mOpponent = null;
        mOpponentField.setText(null);
        updateOpponentState();
        updateWinsSpinnerState();
    }

    private void continueWithFailure() {
        // do nothing?
    }

    @Override
    public void playerSignedIn(Player player) {
        if (mPendingMatch != null) {
            mPendingMatch.setP1(player);
            submitMatch(mPendingMatch);
        }
    }

    @Override
    public void authCancelled() {
        // TODO
    }

    private static class OpponentAutoCompleteAdapter extends ArrayAdapter<Player> implements Filterable {

        private LayoutInflater mInflater;
        private List<Player> mOpponents;

        public OpponentAutoCompleteAdapter(Context context, List<Player> opponents) {
            super(context, android.R.layout.simple_dropdown_item_1line, opponents);
            mOpponents = opponents;
            mInflater = LayoutInflater.from(context);
        }

        public void setOpponents(List<Player> opponents) {
            mOpponents = opponents;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView tv;
            if (convertView != null) {
                tv = (TextView) convertView;
            } else {
                tv = (TextView) mInflater.inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
            }

            tv.setText(getItem(position).getName());
            return tv;
        }

        @Override
        public Filter getFilter() {
            Filter myFilter = new Filter() {
                @Override
                protected FilterResults performFiltering(final CharSequence constraint) {
                    final FilterResults filterResults = new FilterResults();
                    List<Player> filteredPlayers = new ArrayList<Player>();
                    for (Player player : mOpponents) {
                        if (constraint == null || player.getName().toUpperCase().startsWith(constraint.toString().toUpperCase())) {
                            filteredPlayers.add(player);
                        }
                    }
                    filterResults.values = filteredPlayers;
                    filterResults.count = filteredPlayers.size();

                    return filterResults;
                }

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(final CharSequence contraint, final FilterResults results) {
                    clear();
                    for (Player player : (List<Player>) results.values) {
                        add(player);
                    }
                    if (results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }

                @Override
                public CharSequence convertResultToString(final Object resultValue) {
                    return resultValue == null ? "" : ((Player) resultValue).getName();
                }
            };
            return myFilter;
        }
    }
}
