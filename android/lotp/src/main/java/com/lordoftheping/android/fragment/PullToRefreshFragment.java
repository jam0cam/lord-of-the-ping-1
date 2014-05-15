package com.lordoftheping.android.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lordoftheping.android.R;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by mattkranzler on 12/7/13.
 */
public abstract class PullToRefreshFragment extends ListFragment implements OnRefreshListener {

    private PullToRefreshLayout mPullToRefreshLayout;
    private ViewGroup mProgressCont;
    private Button mTryAgainBtn;
    private TextView mTryAgainLbl;
    private ViewGroup mTryAgainCont;
    private ViewGroup mEmptyCont;
    private TextView mEmptyLbl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pull_to_refresh, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressCont = (ViewGroup) view.findViewById(R.id.progressContainer);
        mTryAgainBtn = (Button) view.findViewById(R.id.try_again_btn);
        mTryAgainLbl = (TextView) view.findViewById(R.id.try_again_lbl);
        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData(true);
            }
        });
        mTryAgainCont = (ViewGroup) view.findViewById(R.id.try_again);
        mEmptyCont = (ViewGroup) view.findViewById(R.id.empty);
        mEmptyLbl = (TextView) view.findViewById(R.id.empty_lbl);

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list, R.id.empty, R.id.try_again)
                .listener(this)
                .setup(mPullToRefreshLayout);
        setListAdapter(null);;
    }

    public void refreshData(boolean setLoading) {
        if (setLoading) {
            setLoading();
        }
    }

    public void setTryAgainStringResId(int stringResId) {
        mTryAgainLbl.setText(stringResId);
    }

    public void setEmptyLblStringResId(int stringResId) {
        mEmptyLbl.setText(stringResId);
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData(true);
    }

    protected void setLoading() {
        AnimatorSet set = new AnimatorSet();
        List<Animator> fadeOut = new ArrayList<Animator>();
        fadeOut.add(fadeOutTryAgainCont());
        fadeOut.add(fadeOutEmptyCont());
        fadeOut.add(fadeOutListView());
        set.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInTryProgressCont().start();
                    }
                }
        );
        set.playTogether(fadeOut);
        set.start();
    }

    protected void setLoadSuccessful() {
        mPullToRefreshLayout.setRefreshComplete();
        dismissLoading();
    }

    protected void dismissLoading() {
        AnimatorSet set = new AnimatorSet();
        List<Animator> fadeOut = new ArrayList<Animator>();
        fadeOut.add(fadeOutProgressCont());
        fadeOut.add(fadeOutTryAgainCont());
        fadeOut.add(fadeOutEmptyCont());
        set.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInListView().start();
                    }
                }
        );
        set.playTogether(fadeOut);
        set.start();
    }

    protected void setLoadUnsuccessful() {
        mPullToRefreshLayout.setRefreshComplete();
        AnimatorSet set = new AnimatorSet();
        List<Animator> fadeOut = new ArrayList<Animator>();
        fadeOut.add(fadeOutProgressCont());
        fadeOut.add(fadeOutListView());
        fadeOut.add(fadeOutEmptyCont());
        set.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInTryAgainCont().start();
                    }
                }
        );
        set.playTogether(fadeOut);
        set.start();
    }

    protected void setNoItems() {
        mPullToRefreshLayout.setRefreshComplete();
        AnimatorSet set = new AnimatorSet();
        List<Animator> fadeOut = new ArrayList<Animator>();
        fadeOut.add(fadeOutProgressCont());
        fadeOut.add(fadeOutListView());
        fadeOut.add(fadeOutEmptyCont());
        set.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeInEmptyCont().start();
                    }
                }
        );
        set.playTogether(fadeOut);
        set.start();
    }

    private Animator fadeOutTryAgainCont() {
        return fadeOutView(mTryAgainCont);
    }

    private Animator fadeInTryAgainCont() {
        return fadeInView(mTryAgainCont);
    }

    private Animator fadeOutEmptyCont() {
        return fadeOutView(mEmptyCont);
    }

    private Animator fadeInEmptyCont() {
        return fadeInView(mEmptyCont);
    }

    private Animator fadeOutListView() {
        return fadeOutView(getListView());
    }

    private Animator fadeInListView() {
        return fadeInView(getListView());
    }

    private Animator fadeOutProgressCont() {
        return fadeOutView(mProgressCont);
    }

    private Animator fadeInTryProgressCont() {
        return fadeInView(mProgressCont);
    }

    private Animator fadeOutView(final View view) {
        Animator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 0);
        fadeOut.addListener(
                new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(View.GONE);
                    }
                }
        );
        return fadeOut;
    }

    private Animator fadeInView(final View view) {
        Animator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 1);
        fadeIn.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }
        });
        return fadeIn;
    }
}
