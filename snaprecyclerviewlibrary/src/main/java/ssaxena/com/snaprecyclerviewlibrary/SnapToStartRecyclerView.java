package ssaxena.com.snaprecyclerviewlibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class SnapToStartRecyclerView extends RecyclerView {

    public SnapToStartRecyclerView(Context context) {
        super(context);
        init();
    }

    public SnapToStartRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnapToStartRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private void init() {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        addOnScrollListener(new ScrollListener(smoothScroller));
    }

    public class ScrollListener extends RecyclerView.OnScrollListener {
        private boolean mIsForceScroll;
        private int mEffectiveScroll;
        private int mIncrementFactor = 1;
        private LinearLayoutManager mLinearLayoutManager;
        private LinearSmoothScroller mSmoothScroller;

        public ScrollListener(LinearSmoothScroller smoothScroller) {
            mSmoothScroller = smoothScroller;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mLinearLayoutManager == null) {
                mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (mLinearLayoutManager instanceof GridLayoutManager) {
                    mIncrementFactor = ((GridLayoutManager) mLinearLayoutManager).getSpanCount();
                }
            }

            switch (newState) {
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    mIsForceScroll = false;
                    break;

                case RecyclerView.SCROLL_STATE_SETTLING:
                    if (mIsForceScroll) {
                        mIsForceScroll = false;
                    } else {
                        int totalItemCount = mLinearLayoutManager.getItemCount();
                        int firstVisibleItem = mLinearLayoutManager
                                .findFirstVisibleItemPosition();
                        if (mEffectiveScroll > 0) {
                            firstVisibleItem += mIncrementFactor;
                        }
                        if (firstVisibleItem == totalItemCount) {
                            firstVisibleItem -= 1;
                        }
                        mIsForceScroll = true;
                        mEffectiveScroll = 0;
                        mSmoothScroller.setTargetPosition(firstVisibleItem);
                        mLinearLayoutManager.startSmoothScroll(mSmoothScroller);
                    }
                    break;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            switch (recyclerView.getScrollState()) {
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;

                default:
                    mEffectiveScroll = mEffectiveScroll + (recyclerView.getLayoutManager().canScrollHorizontally() ? dx : dy);
            }
        }
    }
}
