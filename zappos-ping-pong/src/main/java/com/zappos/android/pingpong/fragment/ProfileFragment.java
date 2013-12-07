package com.zappos.android.pingpong.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.squareup.picasso.Picasso;
import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.event.SignedOutEvent;
import com.zappos.android.pingpong.model.Match;
import com.zappos.android.pingpong.model.Player;
import com.zappos.android.pingpong.model.Profile;
import com.zappos.android.pingpong.preference.PingPongPreferences;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class ProfileFragment extends ListFragment implements OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getName();
    private static final String ARG_PLAYER = "player";
    private static final String STATE_PROFILE = "profile";

    private PingPongApplication mApplication;

    private TextView mName;
    private ImageView mAvatar;
    private TextView mMatchesLbl;
    private PieGraph mMatchesGraph;
    private TextView mMatchWinPerc;
    private TextView mMatchWinsLbl;
    private TextView mGamesLbl;
    private PieGraph mGamesGraph;
    private TextView mGameWinPerc;
    private TextView mGameWinsLbl;
    private PullToRefreshLayout mPullToRefreshLayout;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPlayer = (Player) getArguments().getSerializable(ARG_PLAYER);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list, android.R.id.empty)
                .listener(this)
                .setup(mPullToRefreshLayout);

        getListView().setBackgroundColor(Color.WHITE);
        getListView().setDrawSelectorOnTop(true);
        getListView().setHeaderDividersEnabled(false);
        setupHeader(view.getContext());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mProfile = (Profile) savedInstanceState.getSerializable(STATE_PROFILE);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();
        mName.setText(mPlayer.getName());
        String avatarUrl;
        if (StringUtils.isNotEmpty(avatarUrl = mPlayer.getAvatarUrl())) {
            Picasso.with(getActivity()).load(avatarUrl).into(mAvatar);
        }

        if (mProfile == null) {
            refreshProfile();
        } else {
            bindProfile();
        }
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
        mMatchesLbl = (TextView) header.findViewById(R.id.profile_matches_lbl);
        mMatchesGraph = (PieGraph) header.findViewById(R.id.profile_matches_graph);
        mMatchWinPerc = (TextView) header.findViewById(R.id.profile_match_win_perc);
        mMatchWinsLbl = (TextView) header.findViewById(R.id.profile_match_wins);
        mGamesLbl = (TextView) header.findViewById(R.id.profile_games_lbl);
        mGamesGraph = (PieGraph) header.findViewById(R.id.profile_games_graph);
        mGameWinPerc = (TextView) header.findViewById(R.id.profile_game_win_perc);
        mGameWinsLbl = (TextView) header.findViewById(R.id.profile_game_wins);
        setListAdapter(null);
        getListView().addHeaderView(header, null, false);
    }

    private void refreshProfile() {
        setListShown(false);
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
        setListShown(true);
        mPullToRefreshLayout.setRefreshComplete();
    }

    private void bindMatchTotals() {
        final int wins = mProfile.getStats().getMatchWins();
        final int losses = mProfile.getStats().getMatchLosses();
        final int totalMatches = wins + losses;
        final int winPerc = (int)(((double) wins / (double) totalMatches * 100));

        final SpannableStringBuilder matchesBuilder = new SpannableStringBuilder(getResources().getQuantityString(R.plurals.matches, totalMatches, totalMatches));
        matchesBuilder.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, String.valueOf(totalMatches).length(), 0);
        mMatchesLbl.setText(matchesBuilder);
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

        final SpannableStringBuilder matchesBuilder = new SpannableStringBuilder(getResources().getQuantityString(R.plurals.games, totalGames, totalGames));
        matchesBuilder.setSpan(new TypefaceSpan("sans-serif-condensed"), 0, String.valueOf(totalGames).length(), 0);
        mGamesLbl.setText(matchesBuilder);
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
    public void onRefreshStarted(View view) {
        setListShown(false);
        refreshProfile();
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
