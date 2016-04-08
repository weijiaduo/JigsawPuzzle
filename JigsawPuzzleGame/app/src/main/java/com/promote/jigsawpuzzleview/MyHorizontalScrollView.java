package com.promote.jigsawpuzzleview;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


import android.content.ClipData;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.promote.jigsawpuzzlegame.MainActivity;
import com.promote.jigsawpuzzlegame.R;
import com.promote.jigsawpuzzleview.HorizontalScrollViewAdapter;

public class MyHorizontalScrollView extends HorizontalScrollView {

    private static final String TAG = "MyHorizontalScrollView";

    /**
     * HorizontalListView中的LinearLayout
     */
    private LinearLayout mContainer;

    /**
     * 子元素的宽度
     */
    private int mChildWidth;
    /**
     * 子元素的高度
     */
    private int mChildHeight;
    /**
     * 数据适配器
     */
    private HorizontalScrollViewAdapter mAdapter;
    /**
     * 每屏幕最多显示的个数
     */
    private int mCountOneScreen;
    /**
     * 屏幕的宽度
     */
    private int mScreenWitdh;

    /**
     * 触屏事件对象
     */
    private GestureDetector mGestureDetector;

    /**
     * 当前拖拽的view
     */
    private View mDrapView;

    /**
     * 保存View与位置的键值对
     */
    private Map<View, Integer> mViewPos = new HashMap<View, Integer>();

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获得屏幕宽度
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWitdh = outMetrics.widthPixels;

        // 监听触屏事件
        mGestureDetector = new GestureDetector(context, new DrapGestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获取HorizontalScrollView里面的LinearLayout
        mContainer = (LinearLayout) getChildAt(0);
    }

    /**
     * 初始化数据，设置数据适配器
     *
     * @param mAdapter
     */
    public void initDatas(HorizontalScrollViewAdapter mAdapter) {
        this.mAdapter = mAdapter;
        mContainer = (LinearLayout) getChildAt(0);
        // 获得适配器中第一个View
        final View view = mAdapter.getView(0, null, mContainer);
        mContainer.addView(view);

        TextView mTextView=(TextView)((RelativeLayout)view).getChildAt(1);
        double relative = (double) mTextView.getTag();

        // 强制计算当前View的宽和高
        if (mChildWidth == 0 && mChildHeight == 0) {
            int w = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
            view.measure(w, h);
            mChildHeight = view.getMeasuredHeight();
            mChildWidth=(int)(mChildHeight * relative);

            //测试
            Log.e(TAG,"mChildWidth = " + getMeasuredWidth()
                    + " ,mChildHeight = " + getMeasuredHeight());
        }
        //初始化滚动条的子控件
        initView();
    }

    /**
     * 初始化数据，设置数据适配器
     */
    public void initView() {

        // 获取HorizontalScrollView里面的LinearLayout
        mContainer = (LinearLayout) getChildAt(0);
        mContainer.removeAllViews();

        // 设置插入子控件的参数
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mChildWidth,
                ViewGroup.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            final View view = mAdapter.getView(i, null, mContainer);
            view.setBackgroundColor(Color.TRANSPARENT);
            // 设置触屏和拖拽监听事件
            bindDrapListener(view);
            mContainer.addView(view, lp);
        }

        // 防止布局内的元素全部移走后的特殊情况
        mContainer.setOnDragListener(mOnDragListener);
    }

    /**
     * 设置触屏和拖拽事件监听器
     *
     * @param v
     */
    private void bindDrapListener(View v) {
        v.setOnTouchListener(mOnTouchListener);
        v.setOnDragListener(mOnDragListener);
    }

    /**
     * 定义触屏事件监听器
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mDrapView = v;
            // 将触屏事件交给GestureDetector处理
            if (mGestureDetector.onTouchEvent(event))
                return true;
            return false;
        }
    };

    /**
     * 定义拖拽事件监听器
     */
    private View.OnDragListener mOnDragListener = new View.OnDragListener() {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {

                // 开始拖拽控件，获得一个拖动阴影
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do something
                    break;

                // 拖动阴影进入了某View的屏幕边界内时
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (!(v instanceof LinearLayout))
                        v.setAlpha(0.5F);
                    // Log.e("IMAGE_TAG","ENTERED");
                    break;

                // 拖动阴影移到了View的边界之外时
                case DragEvent.ACTION_DRAG_EXITED:
                    if (!(v instanceof LinearLayout))
                        v.setAlpha(1F);
                    // Log.e("IMAGE_TAG","EXITED");
                    break;

                // 在View对象上面释放拖动阴影时
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    // 获取该拖动组件的父组件
                    ViewGroup owner = (ViewGroup) view.getParent();
                    // 设置要加入的子view的参数
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mChildWidth,
                            LinearLayout.LayoutParams.MATCH_PARENT);

                    // 当前布局内没有任何元素时
                    if (v instanceof LinearLayout) {
                        // 从其父组件中移除
                        owner.removeView(view);
                        ((LinearLayout) v).addView(view, lp);
                        // Log.e("LAYOUT_TAG","instanof Layout");
                        break;
                    }

                    // 查找view的插入位置
                    for (int i = 0, j = mContainer.getChildCount(); i < j; i++) {
                        if (mContainer.getChildAt(i) == v) {
                            // 从其父组件中移除
                            owner.removeView(view);
                            // 插入当前位置
                            mContainer.addView(view, i, lp);

                            // 测试
                            // Log.e("Id_TAG", String.valueOf(v.getId()));
                            // Log.e("Id_TAG", String.valueOf(i));
                            break;
                        }
                    }
                    // Log.e("IMAGE_TAG","DROP");
                    break;

                // 系统停止拖动操作时
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1F);
                    // Log.e("IMAGE_TAG","END");
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 定义触屏动作事件监听器
     */
    private class DrapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            // 拖拽view控件的相关数据，无需携带数据可为空
            ClipData data = ClipData.newPlainText("", "");
            // 设置拖拽阴影
            MyDragShadowBuilder shadowBuilder = new MyDragShadowBuilder(
                    mDrapView);
            // 开始拖拽view控件
            mDrapView.startDrag(data, shadowBuilder, mDrapView, 0);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    /**
     * 定义拖拽事件阴影生成器
     */
    private class MyDragShadowBuilder extends View.DragShadowBuilder {

        private final WeakReference<View> mView;

        /**
         * 不必扩展 View.DragShadowBuilder
         * 构造器 View.DragShadowBuilder(View) 创建一个默认与传入的参数View大小相同的拖动阴影，
         * 其中触摸点位于拖动阴影的中心。
         *
         * @param view
         */
        public MyDragShadowBuilder(View view) {
            // 保存传给myDragShadowBuilder的View参数
            super(view);
            // 创建对view的弱引用
            mView = new WeakReference<View>(view);
        }

        /**
         * 定义回调方法，用于在Canvas上绘制拖动阴影，
         * Canvas由系统根据onProvideShadowMetrics()传入的尺寸参数创建。
         *
         * @param canvas
         */
        @Override
        public void onDrawShadow(Canvas canvas) {
            // 此处设置拖拽阴影和控件大小一致
            canvas.scale(1.0F, 1.0F);
            super.onDrawShadow(canvas);
        }

        /**
         * 定义回调方法，用于把拖动阴影的大小和触摸点位置返回给系统
         *
         * @param shadowSize
         * @param shadowTouchPoint
         */
        @Override
        public void onProvideShadowMetrics(Point shadowSize,
                                           Point shadowTouchPoint) {
            // super.onProvideShadowMetrics(shadowSize, shadowTouchPoint);

            final View view = mView.get();
            if (view != null) {
                // 阴影的宽和高
                shadowSize.set((int) (view.getWidth()), (int) (view.getHeight()));
                // 用户手指在拖动过程中所触及的点在拖动阴影中的相对位置
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            } else {
                // Log.e(View.VIEW_LOG_TAG,
                // "Asked for drag thumb metrics but no view");
            }
        }
    }

}
