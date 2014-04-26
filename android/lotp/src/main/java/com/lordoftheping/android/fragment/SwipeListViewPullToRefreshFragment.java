package com.lordoftheping.android.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.zappos.android.lotp.R;

/**
 * Created by mattkranzler on 12/7/13.
 */
public class SwipeListViewPullToRefreshFragment extends PullToRefreshFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slide_list_view_pull_to_refresh, container, false);
    }

    @Override
    public SwipeListView getListView() {
        return (SwipeListView) super.getListView();
    }
}
