package com.promote.jigsawpuzzleview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.promote.jigsawpuzzlegame.R;

/**
 *
 *
 *这个类还没有实现得，没成功，暂时没有用
 *
 *
 *
 */
public class PuzzleGridView extends GridView {

    /**
     * 布局的宽度
     */
    private int mWidth;
    /**
     * 布局的高度
     */
    private int mheight;

    private PuzzleGridViewAdapter mPuzzleGridter;
    /**
     * 布局的列数
     */
    private int mNumColumns;
    /**
     * 列的宽度
     */
    private int mColumnWidth;
    /**
     * 行的高度
     */
    private int mRowHeight;
    private boolean mNumColumnsSet;
    /**
     * 各元素的水平间距
     */
    private int mHorizontalSpacing;
    /**
     * 各元素的垂直间距
     */
    private int mVerticalSpacing;

    public PuzzleGridView(Context context) {
        this(context, null);
    }

    public PuzzleGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PuzzleGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        //mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //mStatusHeight = getStatusHeight(context);

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得游戏布局的边长
        mWidth = getMeasuredWidth();
        mheight = getMeasuredHeight();
        if (mNumColumns > 0) {
            int gridWidth = Math.max(mWidth - getPaddingLeft()
                    - getPaddingRight() - mNumColumns * mHorizontalSpacing, 0);
            mColumnWidth = gridWidth / mNumColumns;
            gridWidth = Math.max(mWidth - getPaddingTop()
                    - getPaddingBottom() - mNumColumns * mVerticalSpacing, 0);
            mRowHeight = gridWidth / mNumColumns;
        }

        Log.e("WIDTH_HEIGHT", String.valueOf(mWidth) + "," + String.valueOf(mheight));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof PuzzleGridViewAdapter) {
            mPuzzleGridter = (PuzzleGridViewAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be implements PuzzleGridViewAdapter");
        }
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
    }


    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }


    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        this.mHorizontalSpacing = horizontalSpacing;
    }

    /**
     * 拖拽事件监听器
     */
    private View.OnDragListener mOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int enterShape = R.drawable.item_droptarget_shape;
            int normalShape = R.drawable.puzzle_item_shape;
            // 测试
            //Log.e("PADDING_TAG",String.valueOf(v.getPaddingBottom()));
            //Log.e("MARGIN_TAG",String.valueOf(v.getHeight()));
            //Log.e("MARGIN_TAG",String.valueOf(getPaddingBottom()));

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();
                    owner.removeView(view);

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);

                    LinearLayout container = (LinearLayout) v;
                    container.addView(view, lp);
                    // 判断拼图完成
                    // checkSuccess();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(normalShape);
                default:
                    break;
            }
            return true;
        }
    };

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mNumColumns * mNumColumns; i++) {
            LinearLayout layoutLin = (LinearLayout) findViewById(i + 1);
            View mView = layoutLin.getChildAt(0);
            if (mView == null || mView.getId() != i + 1000) {
                isSuccess = false;
            }
            if (mView != null)
                Log.e("SUCCESS_TAG", String.valueOf(mView.getId()));
        }

        if (isSuccess) {
            Toast.makeText(getContext(), "Success , Level Up !",
                    Toast.LENGTH_LONG).show();
            //nextLevel();
        }
    }
}
