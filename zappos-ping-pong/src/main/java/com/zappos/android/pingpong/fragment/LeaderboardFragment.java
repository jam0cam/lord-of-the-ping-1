package com.zappos.android.pingpong.fragment;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.model.LeaderboardItem;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by mattkranzler on 12/4/13.
 */
public class LeaderboardFragment extends ListFragment implements OnRefreshListener {

    private static final String TAG = LeaderboardFragment.class.getName();
    private static final String STATE_LEADERBOARD = "leaderboard";

    private PingPongApplication mApplication;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ArrayList<LeaderboardItem> mLeaderboard;
    private LeaderboardAdapter mLeaderboardAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLeaderboard = (ArrayList<LeaderboardItem>) savedInstanceState.getSerializable(STATE_LEADERBOARD);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();
        setupListView();
        if (mLeaderboard == null) {
            refreshLeaderboard();
        } else {
            bindLeaderboard();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_LEADERBOARD, mLeaderboard);
    }

    private void setupListView() {
        mLeaderboardAdapter = new LeaderboardAdapter(getActivity());
        setListAdapter(null);
        getListView().addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.header_leaderboard, null), null, false);
        setListAdapter(mLeaderboardAdapter);
        setListShown(false);
    }

    private void refreshLeaderboard() {
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
                        // TODO handle
                    }
                }
        );
    }

    private void bindLeaderboard() {
        mLeaderboardAdapter.clear();
        mLeaderboardAdapter.addAll(mLeaderboard);
        setListShown(true);
        mPullToRefreshLayout.setRefreshComplete();
    }

    @Override
    public void onRefreshStarted(View view) {
        setListShown(false);
        refreshLeaderboard();
    }

    private static class LeaderboardAdapter extends ArrayAdapter<LeaderboardItem> {

        private static final int LAYOUT_RES_ID = R.layout.item_leaderboard;
        private LayoutInflater mInflater;

        public LeaderboardAdapter(Context context) {
            super(context, LAYOUT_RES_ID);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, null);
            }

            final LeaderboardItem item = getItem(position);

            TextView mPos = (TextView) convertView.findViewById(R.id.leaderboard_pos);
            TextView mName = (TextView) convertView.findViewById(R.id.leaderboard_name);
            TextView mWins = (TextView) convertView.findViewById(R.id.leaderboard_wins);
            TextView mLosses = (TextView) convertView.findViewById(R.id.leaderboard_losses);

            mPos.setText(String.valueOf(position + 1));
            mName.setText(item.getPlayer().getName());
            mWins.setText(String.valueOf(item.getMatchWins()));
            mLosses.setText(String.valueOf(item.getMatchLosses()));

            return convertView;
        }
    }
}
