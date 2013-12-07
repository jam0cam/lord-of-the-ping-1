package com.zappos.android.pingpong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zappos.android.pingpong.PingPongApplication;
import com.zappos.android.pingpong.R;
import com.zappos.android.pingpong.event.SignedInEvent;
import com.zappos.android.pingpong.event.SignedOutEvent;
import com.zappos.android.pingpong.model.Match;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by mattkranzler on 12/7/13.
 */
public class InboxFragment extends PullToRefreshFragment {

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
        setTryAgainStringResId(R.string.inbox_error_txt);
        if (mMatchesToConfirm == null) {
            refreshData();
        } else {
            bindMatchesToConfirm();
        }
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
    public void refreshData() {
        super.refreshData();
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
        mInboxAdapter = new InboxAdapter(getActivity(), mMatchesToConfirm);
        setListAdapter(mInboxAdapter);
        setLoadSuccessful();
    }

    private static final class InboxAdapter extends ArrayAdapter<Match> {

        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("E, MMM dd");
        private static final int LAYOUT_RES_ID = R.layout.item_inbox_match_confirmation;
        private LayoutInflater mInflater;

        public InboxAdapter(Context context, List<Match> matchesToConfirm) {
            super(context, LAYOUT_RES_ID, matchesToConfirm);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(LAYOUT_RES_ID, null);
            }

            ImageView avatar = (ImageView) convertView.findViewById(R.id.inbox_match_confirmation_avatar);
            TextView label = (TextView) convertView.findViewById(R.id.inbox_match_confirmation_lbl);
            TextView result = (TextView) convertView.findViewById(R.id.inbox_match_confirmation_result_lbl);

            Match item = getItem(position);
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
                    "(" + wins + "-" + losses + ")",
                    DATE_FORMAT.format(new Date(item.getDate()))));
            result.setText(win ? "W" : "L");
            result.setBackgroundResource(win ? R.drawable.win_background : R.drawable.loss_background);

            return convertView;
        }
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedInEvent event) {
        refreshData();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(SignedOutEvent event) {
        mMatchesToConfirm = null;
        setListAdapter(null);
    }
}
