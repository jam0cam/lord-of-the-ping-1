package com.lordoftheping.android.fragment;

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

import com.squareup.picasso.Picasso;
import com.lordoftheping.android.PingPongApplication;
import com.zappos.android.lotp.R;
import com.lordoftheping.android.activity.ProfileActivity;
import com.lordoftheping.android.model.LeaderboardItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class LeaderboardFragment extends PullToRefreshFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = LeaderboardFragment.class.getName();
    private static final String STATE_LEADERBOARD = "leaderboard";

    private PingPongApplication mApplication;
    private ArrayList<LeaderboardItem> mLeaderboard;
    private LeaderboardAdapter mLeaderboardAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLeaderboard = (ArrayList<LeaderboardItem>) savedInstanceState.getSerializable(STATE_LEADERBOARD);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();
        setupListView();
        if (mLeaderboard == null) {
            refreshData(true);
        } else {
            bindLeaderboard();
        }
    }

    private void setupListView() {
        getListView().addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.header_leaderboard, null), null, false);
        getListView().setDrawSelectorOnTop(true);
        getListView().setOnItemClickListener(this);
        setTryAgainStringResId(R.string.leaderboard_error);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_LEADERBOARD, mLeaderboard);
    }

    public void refreshData(boolean setLoading) {
        super.refreshData(setLoading);
        mApplication.getPingPongService().getLeaderBoard(
                new Callback<List<LeaderboardItem>>() {
                    @Override
                    public void success(List<LeaderboardItem> leaderboard, Response response) {
                        Log.d(TAG, "Received a leaderboard!");
                        mLeaderboard = new ArrayList<LeaderboardItem>(leaderboard);
                        bindLeaderboard();
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.e(TAG, "An error occurred while loading leaderboard", retrofitError);
                        setLoadUnsuccessful();
                    }
                }
        );
    }

    private void bindLeaderboard() {
        mLeaderboardAdapter = new LeaderboardAdapter(getActivity(), mLeaderboard);
        setListAdapter(mLeaderboardAdapter);
        setLoadSuccessful();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getActivity(), ProfileActivity.class)
                .putExtra(ProfileActivity.EXTRA_PLAYER, mLeaderboardAdapter.getItem(position - getListView().getHeaderViewsCount()).getPlayer()));
    }

    private static class LeaderboardAdapter extends ArrayAdapter<LeaderboardItem> {

        private static final int LAYOUT_RES_ID = R.layout.item_leaderboard;
        private LayoutInflater mInflater;
        private int mOddBackgroundColor;

        public LeaderboardAdapter(Context context, List<LeaderboardItem> leaderboardItems) {
            super(context, LAYOUT_RES_ID, leaderboardItems);
            mInflater = LayoutInflater.from(context);
            mOddBackgroundColor = context.getResources().getColor(R.color.gray);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, null);
            }

            final LeaderboardItem item = getItem(position);

            TextView pos = (TextView) convertView.findViewById(R.id.leaderboard_pos);
            ImageView avatar = (ImageView) convertView.findViewById(R.id.leaderboard_avatar);
            TextView name = (TextView) convertView.findViewById(R.id.leaderboard_name);
            TextView wins = (TextView) convertView.findViewById(R.id.leaderboard_wins);
            TextView losses = (TextView) convertView.findViewById(R.id.leaderboard_losses);

            if (StringUtils.isNotEmpty(item.getPlayer().getAvatarUrl())) {
                Picasso.with(getContext()).load(item.getPlayer().getAvatarUrl()).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar_default);
            }
            pos.setText(String.valueOf(position + 1));
            name.setText(item.getPlayer().getName());
            wins.setText(String.valueOf(item.getMatchWins()));
            losses.setText(String.valueOf(item.getMatchLosses()));

            convertView.setBackgroundColor((position % 2 == 0 ? Color.TRANSPARENT : mOddBackgroundColor));

            return convertView;
        }
    }
}
