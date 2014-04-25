package com.zappos.android.lotp.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.zappos.android.lotp.PingPongApplication;
import com.zappos.android.lotp.R;
import com.zappos.android.lotp.event.SignedInEvent;
import com.zappos.android.lotp.event.SignedOutEvent;
import com.zappos.android.lotp.model.Match;
import com.zappos.android.lotp.model.MatchConfirmationResponse;
import com.zappos.android.lotp.service.PingPongService;
import com.zappos.android.lotp.view.InboxActionView;

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
public class InboxFragment extends SwipeListViewPullToRefreshFragment implements AdapterView.OnItemClickListener, SwipeListViewListener,
        DrawerLayout.DrawerListener{

    private static final String STATE_MATCHES_TO_CONFIRM = "matchesToConfirm";

    private PingPongApplication mApplication;
    private ArrayList<Match> mMatchesToConfirm;
    private InboxAdapter mInboxAdapter;
    private InboxActionView mInboxActionView;
    private DrawerLayout mDrawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

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
        } else {
            refreshData(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_inbox, menu);
        mInboxActionView = (InboxActionView) menu.findItem(R.id.action_inbox).getActionView();
        mInboxActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                        mDrawerLayout.closeDrawer(Gravity.RIGHT);
                        mInboxActionView.setUnPressed();
                    } else {
                        mDrawerLayout.openDrawer(Gravity.RIGHT);
                        mInboxActionView.setPressed();
                    }
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem inbox = menu.findItem(R.id.action_inbox);
        if (inbox != null) {
            inbox.setVisible(mApplication.getCurrentPlayer() != null);
        }
        if (mInboxActionView != null) {
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mInboxActionView.setPressed();
            } else {
                mInboxActionView.setUnPressed();
            }
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

    public void setDrawerLayout(DrawerLayout drawerLayout) {
        mDrawerLayout = drawerLayout;
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
                            EventBus.getDefault().postSticky(new InboxActionView.InboxItemUpdatedEvent(matches.size()));
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            setLoadUnsuccessful();
                            EventBus.getDefault().postSticky(new InboxActionView.InboxItemUpdatedEvent(0));
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
    public void onOpened(int position, boolean toRight) {/* no-op */}

    @Override
    public void onClosed(int position, boolean fromRight) {/* no-op */}

    @Override
    public void onListChanged() {/* no-op */}

    @Override
    public void onMove(int position, float x) {/* no-op */}

    @Override
    public void onStartOpen(int position, int action, boolean right) {/* no-op */}

    @Override
    public void onStartClose(int position, boolean right) {/* no-op */}

    @Override
    public void onClickFrontView(int position) {
        getListView().openAnimate(position);
    }

    @Override
    public void onClickBackView(int position) {/* no-op */}

    @Override
    public void onDismiss(int[] reverseSortedPositions) {/* no-op */}

    @Override
    public int onChangeSwipeMode(int position) {
        return 0;
    }

    @Override
    public void onChoiceChanged(int position, boolean selected) {/* no-op */}

    @Override
    public void onChoiceStarted() {/* no-op */}

    @Override
    public void onChoiceEnded() {/* no-op */}

    @Override
    public void onFirstListItem() {/* no-op */}

    @Override
    public void onLastListItem() {/* no-op */}

    public void inboxClosed() {
        getListView().closeOpenedItems();
    }

    public void inboxOpened() {
        refreshData(false);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {/* no-op */}

    @Override
    public void onDrawerOpened(View drawerView) {
        inboxOpened();
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        inboxClosed();
        if (getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onDrawerStateChanged(int newState) {/* no-op */}

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
        EventBus.getDefault().postSticky(new InboxActionView.InboxItemUpdatedEvent(0));
    }

    public static class RefreshInboxEvent {}
}
