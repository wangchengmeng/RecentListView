package com.example.administrator.recentlistview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.Toast;

import com.example.administrator.recentlistview.R;
import com.example.administrator.recentlistview.model.Model;
import com.example.administrator.recentlistview.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangchengmeng
 * @desc 2015-11-22
 */

public class RecentListView extends FrameLayout {
    private Scroller        mScroller;
    private List<Model>     mTipModels;
    private OnClickListener mOnClickListener;
    private int             mScrollOffset;
    private int mSize = 210;
    private Context mContext;
    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // 手指不离开刷新界面
            mScrollOffset = (int) (mScrollOffset - distanceX * 3);
            postInvalidate();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            startScrolling(velocityX);//手指滑动速度
            return true;
        }
    });

    public RecentListView(Context context) {
        this(context, null);
    }

    public RecentListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecentListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext());
        setClipToPadding(false);
        mTipModels = new ArrayList<>();
    }

    public void fillData(List<Model> tipModels) {
        if (null == tipModels || tipModels.isEmpty()) {
            return;
        }
        mScrollOffset = getWidth() * (tipModels.size() - 1);// 每次刷新数据的时候就reset位置
        mTipModels.clear();
        mTipModels.addAll(tipModels);
        initChildren();
        layoutChildren();
        bindData();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    public void setSize(int size) {
        mSize = size;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int itemSize = DensityUtils.getScreenW() * mSize / DensityUtils.screenWidth;
        if (getChildCount() != mTipModels.size()) {
            removeAllViews();
            initChildren();
            bindData();
        }
        for (int i = 0; i < getChildCount(); i++) {
            View itemView = getChildAt(i);
            itemView.layout(getWidth() - itemSize, (getHeight() - itemSize) / 2, getWidth(), itemSize);
        }
    }

    private void initChildren() {
        removeAllViews();
        int size = mTipModels.size();
        for (int i = 0; i < size; i++) {
            CircleImageView itemView = (CircleImageView) View.inflate(getContext(), R.layout.view_tip_shop_logo, null);
//            CircleImageView itemView = new CircleImageView(mContext);
            itemView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            addView(itemView, i, generateDefaultLayoutParams());
            DensityUtils.measure(itemView, mSize, mSize);

        }
    }

    private void bindData() {
        int size = getChildCount();
        for (int i = 0; i < size; i++) {
            CircleImageView itemView = (CircleImageView) getChildAt(i);
            final Model model = mTipModels.get(i);
            itemView.setTag(model);
            final int finalI = i;
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnClickListener) {
                        mOnClickListener.onClick(v);
                    }
                    //点击事件 TODO
                    Toast.makeText(mContext, "点击事件" + finalI, Toast.LENGTH_LONG).show();
                }
            });
            //显示图片 TODO  显示图片并缓存
            Log.d("aaa", model.getImageId() + "");
            itemView.setImageResource(model.getImageId());
            //ImageLoader.getInstance().displayImage(model.getLogo(), itemView, new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565)
            //.cacheInMemory(false).cacheOnDisk(true).displayer(new SimpleBitmapDisplayer()).showImageOnLoading(R.mipmap.icon_default_head).showImageForEmptyUri(R.mipmap.icon_default_head)
            //  .build());
        }
    }

    private void layoutChildren() {
        int width = getWidth();
        for (int i = 0; i < getChildCount(); i++) {
            View item = getChildAt(i);
            int itemWidth = item.getWidth();
            int xOffset = (int) (itemWidth * Math.pow(2, (i - mScrollOffset / (float) width)));
            item.setTranslationX(-xOffset);
        }
    }

    private int getMaxScroll() {
        return (getChildCount() - 1) * getWidth();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        layoutChildren();
        super.dispatchDraw(canvas);
        doScrolling();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        gestureDetector.onTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    float mDownX = 0;
    float mDownY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        if (gestureDetector.onTouchEvent(event))
            return true;

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            forceFinished();
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                float distanceX = Math.abs(moveX - mDownX);
                float distanceY = Math.abs(moveY - mDownY);
                if (distanceX < distanceY) {
                    //竖直方向滑动 申请父类拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (distanceX > distanceY) {
                    //横向滑动 申请父类不拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            default:
                break;
        }
        return true;
    }

    void startScrolling(float initialVelocity) {
        mScroller.fling(0, mScrollOffset, 0, (int) initialVelocity, 0,
                0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        postInvalidate();
    }

    private void doScrolling() {
        if (mScroller.isFinished()) {
            return;
        }
        boolean more = mScroller.computeScrollOffset();
        int y = mScroller.getCurrY();
        mScrollOffset = Math.max(0, Math.min(y, getMaxScroll()));

        if (more)
            postInvalidate();
    }

    void forceFinished() {
        if (!mScroller.isFinished()) {
            mScroller.forceFinished(true);
        }
    }

}
