package com.zappos.android.pingpong.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zappos.android.pingpong.R;

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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        mProgressCont = (ViewGroup) root.findViewById(R.id.progressContainer);
        mTryAgainBtn = (Button) root.findViewById(R.id.try_again_btn);
        mTryAgainLbl = (TextView) root.findViewById(R.id.try_again_lbl);
        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
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
        setListAdapter(null);;
    }

    public void refreshData() {
        setLoading();
    }

    public void setTryAgainStringResId(int stringResId) {
        mTryAgainLbl.setText(stringResId);
    }

    @Override
    public void onRefreshStarted(View view) {
        refreshData();
    }

    protected void setLoading() {
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

    protected void setLoadSuccessful() {
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

    protected void setLoadUnsuccessful() {
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
}
