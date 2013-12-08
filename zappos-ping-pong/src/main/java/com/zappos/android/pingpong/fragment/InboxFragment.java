package com.zappos.android.pingpong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.fortysevendeg.swipelistview.SwipeListViewListener;
import com.squareup.picasso.Picasso;
import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.event.SignedInEvent;
import com.zappos.android.pingpong.event.SignedOutEvent;
import com.zappos.android.pingpong.model.Match;
import com.zappos.android.pingpong.model.MatchConfirmationResponse;
import com.zappos.android.pingpong.service.PingPongService;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/7/13.
 */
public class InboxFragment extends SwipeListViewPullToRefreshFragment implements AdapterView.OnItemClickListener, SwipeListViewListener {

    private static final String STATE_MATCHES_TO_CONFIRM = "matchesToConfirm";

    private PingPongApplication mApplication;
    private ArrayList<Match> mMatchesToConfirm;
    private InboxAdapter mInboxAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mMatchesToConfirm = (ArrayList<Match>) savedInstanceState.getSerializable(STATE_MATCHES_TO_CONFIRM);
        }
        mApplication = (PingPongApplication) getActivity().getApplication();
        setupListView();
        if (mMatchesToConfirm != null) {
            bindMatchesToConfirm();
        }
    }

    private void setupListView() {
        setTryAgainStringResId(R.string.inbox_error_txt);
        setEmptyLblStringResId(R.string.inbox_empty_txt);
        getListView().setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
        getListView().setSwipeOpenOnLongPress(false);
        getListView().setSwipeListViewListener(this);
        getListView().setOffsetLeft(getResources().getDimension(R.dimen.inbox_back_view_offset));
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_MATCHES_TO_CONFIRM, mMatchesToConfirm);
    }

    @Override
    public void refreshData(boolean setLoading) {
        super.refreshData(setLoading);
        if (mApplication.getCurrentPlayer() != null) {
            mApplication.getPingPongService().getPendingMatches(
                    Long.valueOf(mApplication.getCurrentPlayer().getId()),
                    new Callback<List<Match>>() {
                        @Override
                        public void success(List<Match> matches, Response response) {
                            mMatchesToConfirm = new ArrayList<Match>(matches);
                            bindMatchesToConfirm();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            setLoadUnsuccessful();
                        }
                    }
            );
        }
    }

    private void bindMatchesToConfirm() {
        mInboxAdapter = new InboxAdapter(getActivity(), mApplication.getPingPongService(), mMatchesToConfirm);
        setListAdapter(mInboxAdapter);
        if (mInboxAdapter.getCount() == 0) {
            setNoItems();
        } else {
            setLoadSuccessful();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        getListView().openAnimate(position);
    }

    @Override
    public void onOpened(int position, boolean toRight) {

    }

    @Override
    public void onClosed(int position, boolean fromRight) {

    }

    @Override
    public void onListChanged() {

    }

    @Override
    public void onMove(int position, float x) {

    }

    @Override
    public void onStartOpen(int position, int action, boolean right) {

    }

    @Override
    public void onStartClose(int position, boolean right) {

    }

    @Override
    public void onClickFrontView(int position) {
        getListView().openAnimate(position);
    }

    @Override
    public void onClickBackView(int position) {

    }

    @Override
    public void onDismiss(int[] reverseSortedPositions) {

    }

    @Override
    public int onChangeSwipeMode(int position) {
        return 0;
    }

    @Override
    public void onChoiceChanged(int position, boolean selected) {

    }

    @Override
    public void onChoiceStarted() {

    }

    @Override
    public void onChoiceEnded() {

    }

    @Override
    public void onFirstListItem() {

    }

    @Override
    public void onLastListItem() {

    }

    public void inboxClosed() {
        getListView().closeOpenedItems();
    }

    public void inboxOpened() {
        refreshData(mInboxAdapter == null || mInboxAdapter.getCount() == 0);
    }

    private static final class InboxAdapter extends ArrayAdapter<Match> {

        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("E, MMM dd");
        private static final int LAYOUT_RES_ID = R.layout.item_inbox_match_confirmation;
        private LayoutInflater mInflater;
        private PingPongService mService;

        public InboxAdapter(Context context, PingPongService service, List<Match> matchesToConfirm) {
            super(context, LAYOUT_RES_ID, matchesToConfirm);
            mInflater = LayoutInflater.from(context);
            mService = service;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, null);
            }
            final SwipeListView swipeListView = (SwipeListView)parent;
            ImageView avatar = (ImageView) convertView.findViewById(R.id.inbox_match_confirmation_avatar);
            TextView label = (TextView) convertView.findViewById(R.id.inbox_match_confirmation_lbl);
            TextView result = (TextView) convertView.findViewById(R.id.inbox_match_confirmation_result_lbl);
            ImageButton moreBtn = (ImageButton) convertView.findViewById(R.id.inbox_match_confirmation_more_btn);
            Button confirmBtn = (Button) convertView.findViewById(R.id.inbox_match_confirmation_confirm_btn);
            Button declineBtn = (Button) convertView.findViewById(R.id.inbox_match_confirmation_decline_btn);

            final Match item = getItem(position);
            if (StringUtils.isNotEmpty(item.getP1().getAvatarUrl())) {
                Picasso.with(getContext()).load(item.getP1().getAvatarUrl()).into(avatar);
            } else {
                avatar.setImageResource(R.drawable.avatar_default);
            }
            final int wins = item.getP2Score();
            final int losses = item.getP1Score();
            final boolean win = wins > losses;
            label.setText(getContext().getString((win ? R.string.win_lbl_txt : R.string.loss_lbl_txt),
                    item.getP1().getName(),
                    "(" + wins + "-" + losses + ")"));
            result.setText(win ? "W" : "L");
            result.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (swipeListView.isOpen(position)) {
                        swipeListView.closeAnimate(position);
                    } else {
                        swipeListView.openAnimate(position);
                    }
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeListView.closeAnimate(position);
                    mService.confirmMatch(
                            new MatchConfirmationResponse(item.getId(), true),
                            new Callback<Void>() {
                                @Override
                                public void success(Void aVoid, Response response) {
                                    EventBus.getDefault().post(new RefreshInboxEvent());
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    EventBus.getDefault().post(new RefreshInboxEvent());
                                }
                            }
                    );
                }
            });
            declineBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    swipeListView.closeAnimate(position);
                    mService.confirmMatch(
                            new MatchConfirmationResponse(item.getId(), false),
                            new Callback<Void>() {
                                @Override
                                public void success(Void aVoid, Response response) {
                                    EventBus.getDefault().post(new RefreshInboxEvent());
                                }

                                @Override
                                public void failure(RetrofitError retrofitError) {
                                    EventBus.getDefault().post(new RefreshInboxEvent());
                                }
                            }
                    );
                }
            });

            return convertView;
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(RefreshInboxEvent event) {
        refreshData(false);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedInEvent event) {
        refreshData(true);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedOutEvent event) {
        mMatchesToConfirm = null;
        setListAdapter(null);
    }

    public static class RefreshInboxEvent {}
}
