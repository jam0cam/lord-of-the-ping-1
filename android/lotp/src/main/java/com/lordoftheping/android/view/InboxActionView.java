package com.lordoftheping.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zappos.android.lotp.R;

import de.greenrobot.event.EventBus;

public class InboxActionView extends FrameLayout {

    private static final String TAG = InboxActionView.class.getName();

    private TextView mNumItems;
    private TextView mText;
    private Drawable mPressedBackground;
    private Drawable mNormalBackground;

    public InboxActionView(Context context) {
        this(context, null);
    }

    public InboxActionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InboxActionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        TypedArray ta = context.obtainStyledAttributes(R.styleable.Theme_Lotp);
        mPressedBackground = ta.getDrawable(R.styleable.Theme_Lotp_listSelectorPressed);
        mNormalBackground = ta.getDrawable(R.styleable.Theme_Lotp_listSelector);
        ta.recycle();
        LayoutInflater.from(context).inflate(R.layout.inbox_action_view, this);
        mNumItems = (TextView) findViewById(R.id.num_items);
        mText = (TextView) findViewById(R.id.text);
        mText.setVisibility(View.GONE);
        setClickable(true);
        setLongClickable(true);
        setUnPressed();
    }

    @SuppressWarnings("deprecation")
    public void setPressed() {
        setBackgroundDrawable(mPressedBackground);
    }

    @SuppressWarnings("deprecation")
    public void setUnPressed() {
        setBackgroundDrawable(mNormalBackground);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    public boolean isTextVisible() {
        return mText != null && View.VISIBLE == mText.getVisibility();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(InboxItemUpdatedEvent event) {
        if (event.getTotalItems() > 0) {
            mNumItems.setVisibility(View.VISIBLE);
        } else {
            mNumItems.setVisibility(View.GONE);
        }
        mNumItems.setText(String.valueOf(event.getTotalItems()));
    }

    public static class InboxItemUpdatedEvent {
        private int totalItems;

        public InboxItemUpdatedEvent(int totalItems) {
            this.totalItems = totalItems;
        }

        public int getTotalItems() {
            return totalItems;
        }
    }
}
