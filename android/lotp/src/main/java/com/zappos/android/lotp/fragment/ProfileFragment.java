package com.zappos.android.lotp.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.squareup.picasso.Picasso;
import com.zappos.android.lotp.PingPongApplication;
import com.zappos.android.lotp.R;
import com.zappos.android.lotp.activity.ProfileActivity;
import com.zappos.android.lotp.model.Match;
import com.zappos.android.lotp.model.Player;
import com.zappos.android.lotp.model.Profile;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class ProfileFragment extends PullToRefreshFragment implements OnRefreshListener, AdapterView.OnItemClickListener {

    private static final String TAG = ProfileFragment.class.getName();
    private static final String ARG_PLAYER = "player";
    private static final String STATE_PROFILE = "profile";

    private PingPongApplication mApplication;

    private TextView mName;
    private ImageView mAvatar;
    private TextView mTotalMatches;
    private PieGraph mMatchesGraph;
    private TextView mMatchWinPerc;
    private TextView mMatchWinsLbl;
    private TextView mTotalGames;
    private PieGraph mGamesGraph;
    private TextView mGameWinPerc;
    private TextView mGameWinsLbl;

    private MatchHistoryAdapter mMatchHistoryAdapter;

    private Profile mProfile;
    private Player mPlayer;

    public static ProfileFragment newInstance(Player player) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLAYER, player);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mProfile = (Profile) savedInstanceState.getSerializable(STATE_PROFILE);
        }
        mPlayer = (Player) getArguments().getSerializable(ARG_PLAYER);
        mApplication = (PingPongApplication) getActivity().getApplication();
        setupListView();
        mName.setText(mPlayer.getName());
        String avatarUrl;
        if (StringUtils.isNotEmpty(avatarUrl = mPlayer.getAvatarUrl())) {
            Picasso.with(getActivity()).load(avatarUrl).into(mAvatar);
        }

        if (mProfile == null) {
            refreshData(true);
        } else {
            bindProfile();
        }
    }

    private void setupListView() {
        getListView().setBackgroundColor(Color.WHITE);
        getListView().setDrawSelectorOnTop(true);
        getListView().setHeaderDividersEnabled(false);
        getListView().setOnItemClickListener(this);
        setupHeader(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_PROFILE, mProfile);
    }

    private void setupHeader(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View header = inflater.inflate(R.layout.header_profile, null);
        mName = (TextView) header.findViewById(R.id.profile_name);
        mAvatar = (ImageView) header.findViewById(R.id.profile_avatar);
        mTotalMatches = (TextView) header.findViewById(R.id.profile_total_matches);
        mMatchesGraph = (PieGraph) header.findViewById(R.id.profile_matches_graph);
        mMatchesGraph.setThickness(20);
        mMatchWinPerc = (TextView) header.findViewById(R.id.profile_match_win_perc);
        mMatchWinsLbl = (TextView) header.findViewById(R.id.profile_match_wins);
        mTotalGames = (TextView) header.findViewById(R.id.profile_total_games);
        mGamesGraph = (PieGraph) header.findViewById(R.id.profile_games_graph);
        mGamesGraph.setThickness(20);
        mGameWinPerc = (TextView) header.findViewById(R.id.profile_game_win_perc);
        mGameWinsLbl = (TextView) header.findViewById(R.id.profile_game_wins);
        setListAdapter(null);
        getListView().addHeaderView(header, null, false);
    }

    public void refreshData(boolean setLoading) {
        super.refreshData(setLoading);
        mApplication.getPingPongService().getProfile(
                Long.valueOf(mPlayer.getId()),
                new Callback<Profile>() {
                    @Override
                    public void success(Profile profile, Response response) {
                        Log.d(TAG, "Successfully received profile!");
                        mProfile = profile;
                        bindProfile();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "An error occurred while retrieving profile :(", retrofitError);
                    }
                }
        );
    }

    private void bindProfile() {
        bindMatchTotals();
        bindGameTotals();
        bindMatchHistory();
        setLoadSuccessful();
    }

    private void bindMatchTotals() {
        final int wins = mProfile.getStats().getMatchWins();
        final int losses = mProfile.getStats().getMatchLosses();
        final int totalMatches = wins + losses;
        final int winPerc = (int)(((double) wins / (double) totalMatches * 100));

        mTotalMatches.setText(String.valueOf(totalMatches));
        mMatchWinsLbl.setText(getResources().getQuantityString(R.plurals.wins, wins, wins));

        // remove all slices
        mMatchesGraph.removeSlices();

        PieSlice winSlice = new PieSlice();
        winSlice.setColor(getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        mMatchesGraph.addSlice(winSlice);
        mMatchesGraph.addSlice(lossSlice);

        mMatchWinPerc.setText(winPerc + "%");
    }

    private void bindGameTotals() {
        final int wins = mProfile.getStats().getGameWins();
        final int losses = mProfile.getStats().getGameLosses();
        final int totalGames = wins + losses;
        final int winPerc = (int)(((double) wins / (double) totalGames * 100));

        mTotalGames.setText(String.valueOf(totalGames));
        mGameWinsLbl.setText(getResources().getQuantityString(R.plurals.wins, wins, wins));

        // remove all slices
        mGamesGraph.removeSlices();

        PieSlice winSlice = new PieSlice();
        winSlice.setColor(getResources().getColor(R.color.green));
        winSlice.setValue(wins);
        PieSlice lossSlice = new PieSlice();
        lossSlice.setColor(getResources().getColor(R.color.red));
        lossSlice.setValue(losses);
        mGamesGraph.addSlice(winSlice);
        mGamesGraph.addSlice(lossSlice);

        mGameWinPerc.setText(winPerc + "%");
    }

    private void bindMatchHistory() {
        mMatchHistoryAdapter = new MatchHistoryAdapter(getActivity(), mProfile.getMatches());
        setListAdapter(mMatchHistoryAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getActivity(), ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_PLAYER, mMatchHistoryAdapter.getItem(position - getListView().getHeaderViewsCount()).getP2()));
    }

    private static class MatchHistoryAdapter extends ArrayAdapter<Match> {

        private static final int LAYOUT_RES_ID = R.layout.item_match_history;
        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd");

        private LayoutInflater mInflater;
        private int mOddBackgroundColor;

        public MatchHistoryAdapter(Context context, List<Match> matches) {
            super(context, LAYOUT_RES_ID, matches);
            mInflater = LayoutInflater.from(context);
            mOddBackgroundColor = context.getResources().getColor(R.color.gray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, null);
            }
            TextView date = (TextView) convertView.findViewById(R.id.match_history_date);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.match_history_avatar);
            TextView opponent = (TextView) convertView.findViewById(R.id.match_history_opponent);
            TextView result = (TextView) convertView.findViewById(R.id.match_history_result);
            TextView score = (TextView) convertView.findViewById(R.id.match_history_score);

            Match item = getItem(position);
            if (StringUtils.isNotEmpty(item.getP2().getAvatarUrl())) {
                Picasso.with(getContext()).load(item.getP2().getAvatarUrl()).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar_default);
            }
            date.setText(DATE_FORMAT.format(new Date(item.getDate())));
            opponent.setText(item.getP2().getName());
            final boolean win = item.getP1Score() > item.getP2Score();
            result.setText(win ? "W" : "L");
            result.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);
            score.setText(item.getP1Score() + "-" + item.getP2Score());

            convertView.setBackgroundColor((position % 2 == 0 ? Color.TRANSPARENT : mOddBackgroundColor));

            return convertView;
        }
    }
}
