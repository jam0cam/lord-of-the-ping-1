package com.zappos.android.pingpong.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.model.LeaderboardItem;
import com.zappos.android.pingpong.model.Player;

import org.apache.commons.lang.StringUtils;

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
public class LeaderboardFragment extends ListFragment implements OnRefreshListener, AdapterView.OnItemClickListener {

    public static interface OnPlayerSelectedListener {
        void onPlayerSelected(Player player);
    }

    private static final String TAG = LeaderboardFragment.class.getName();
    private static final String STATE_LEADERBOARD = "leaderboard";

    private PingPongApplication mApplication;
    private PullToRefreshLayout mPullToRefreshLayout;
    private ArrayList<LeaderboardItem> mLeaderboard;
    private LeaderboardAdapter mLeaderboardAdapter;
    private ViewGroup mProgressCont;
    private Button mTryAgainBtn;
    private TextView mTryAgainLbl;
    private ViewGroup mTryAgainCont;

    private OnPlayerSelectedListener mOnPlayerSelectedListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mOnPlayerSelectedListener = (OnPlayerSelectedListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        mProgressCont = (ViewGroup) root.findViewById(R.id.progressContainer);
        mTryAgainBtn = (Button) root.findViewById(R.id.try_again_btn);
        mTryAgainLbl = (TextView) root.findViewById(R.id.try_again_lbl);
        mTryAgainLbl.setText(R.string.leaderboard_error);
        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLeaderboard();
            }
        });
        mTryAgainCont = (ViewGroup) root.findViewById(R.id.empty);
        return root;
    }

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
                .theseChildrenArePullable(android.R.id.list, R.id.empty)
                .listener(this)
        .setup(mPullToRefreshLayout);
        setListAdapter(null);
        getListView().addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.header_leaderboard, null), null, false);
        getListView().setDrawSelectorOnTop(true);
        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLeaderboard = (ArrayList<LeaderboardItem>) savedInstanceState.getSerializable(STATE_LEADERBOARD);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mOnPlayerSelectedListener = null;
    }

    private void setLoading() {
        Log.d(TAG, "setLoading()");
        final View viewToFadeOut = getListView().getVisibility() == View.VISIBLE ? getListView() : mTryAgainCont;
        viewToFadeOut
                .animate()
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        viewToFadeOut.setVisibility(View.GONE);
                        mProgressCont.setVisibility(View.VISIBLE);
                        mProgressCont
                                .animate()
                                .alpha(1)
                                .setListener(null)
                                .start();
                    }
                })
                .start();
    }

    private void setLoadSuccessful() {
        Log.d(TAG, "setLoadSuccessful()");
        mPullToRefreshLayout.setRefreshComplete();
        mProgressCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mProgressCont.setVisibility(View.GONE);
                                getListView().setVisibility(View.VISIBLE);
                                getListView()
                                        .animate()
                                        .alpha(1)
                                        .setListener(null)
                                        .start();
                            }
                        }
                )
                .start();
    }

    private void setLoadUnsuccessful() {
        Log.d(TAG, "setLoadUnsuccessful()");
        mPullToRefreshLayout.setRefreshComplete();
        mProgressCont
                .animate()
                .alpha(0)
                .setListener(
                        new AnimatorListenerAdapter() {

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mProgressCont.setVisibility(View.GONE);
                                mTryAgainCont.setVisibility(View.VISIBLE);
                                mTryAgainCont
                                        .animate()
                                        .alpha(1)
                                        .setListener(null)
                                        .start();
                            }
                        }
                )
                .start();
    }

    private void refreshLeaderboard() {
        Log.d(TAG, "refreshLeaderboard()");
        setLoading();
//        new Handler().postDelayed(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        setLoadUnsuccessful();
//                    }
//                }
//        , 3000l);
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
        mPullToRefreshLayout.setRefreshComplete();
        setLoadSuccessful();
    }

    @Override
    public void onRefreshStarted(View view) {
        Log.d(TAG, "onRefreshStarted()");
        refreshLeaderboard();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnPlayerSelectedListener != null) {
            mOnPlayerSelectedListener.onPlayerSelected(mLeaderboardAdapter.getItem(position - getListView().getHeaderViewsCount()).getPlayer());
        }
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
