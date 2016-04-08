package com.promote.jigsawpuzzleview;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.promote.jigsawpuzzlegame.MainActivity;
import com.promote.jigsawpuzzlegame.R;
import com.promote.imagesplitter.ImagePiece;

/**
 *
 */
public class JigsawPuzzleLayout extends RelativeLayout{

    /**
     * 设置Item的数量n*n；默认为2
     */
    private static int mColumn = 3;
    /**
     * 布局的宽高比
     */
    private static double relative;
    /**
     * 布局的宽度
     */
    private int mWidth;
    /**
     * 布局的高度
     */
    private int mHeight;

    /**
     * 布局的padding
     */
    private int mPadding;
    /**
     * Item的宽度
     */
    private int mItemWidth;
    /**
     * Item的高度
     */
    private int mItemHeight;
    /**
     * Item横向与纵向的边距
     */
    private int mMargin = 0;

    /**
     * 暂时没有用，是后面的切换动画部分，视情况而定
     *
     * 存放切完以后的图片bean
     */
    private List<ImagePiece> mItemBitmaps;

    private boolean once;

    private Context mContext;

    public JigsawPuzzleLayout(Context context) {
        this(context, null);
        mContext=context;
    }

    public JigsawPuzzleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext=context;
    }

    public JigsawPuzzleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;

        // 把设置的margin值转化为dp
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMargin, getResources().getDisplayMetrics());
        // 设置Layout的内边距，四边一致，设置为四内边距中的最小值
        mPadding = min(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
                getPaddingBottom());
        // 测试
        //Log.e("mPADDING_TAG",String.valueOf(mPadding));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 获得拼图游戏布局的边长
        mWidth =Math.min(getMeasuredWidth(), getMeasuredHeight());
        // 留下四分之一放置下面的滚动条
        mHeight = getMeasuredHeight() / 4 * 3;

        if (!once) {
            initItem();
        }
        once = true;
        setMeasuredDimension(mWidth, mHeight);
    }

    /**
     * 初始化Item
     */
    private void initItem() {
        // 计算Item的高度和宽度
        mItemHeight = (mHeight - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;
        mItemWidth=(int) (mItemHeight * relative);
        // 测试
        // Log.e("ITEM_TAG","ItemWidth = " + mItemWidth + ", ItemHeight = " + mItemHeight);

        int length=mColumn * mColumn;
        // 放置Item
        for (int i = 0; i < length; i++) {

            //生成item
            LinearLayout newImageLin;
            newImageLin=generateImagePieceLayout(i);

            //设置布局item的id
            newImageLin.setId(i+1);
            //设置拖拽事件监听器
            newImageLin.setOnDragListener(mOnDragListener);

            //设置生成布局item的相关参数
            RelativeLayout.LayoutParams lp = new LayoutParams(mItemWidth, mItemHeight);
            // 设置横向边距,不是最后一列
            if ((i + 1) % mColumn != 0) {
                lp.rightMargin = mMargin;
            }
            // 如果不是第一列
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, i);
            }
            // 如果不是第一行，//设置纵向边距，非最后一行
            if ((i + 1) > mColumn) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, i + 1 - mColumn);
            }

            //添加item
            addView(newImageLin,lp);
        }

    }
    /**
     * 新建一个item
     * @param imageId
     * @return
     */
    private LinearLayout generateImagePieceLayout(int imageId){
        LinearLayout layoutLin=new LinearLayout(mContext);
        layoutLin.setOrientation(LinearLayout.VERTICAL);
        // 设置背景
        layoutLin.setBackgroundResource(R.drawable.puzzle_item_shape);

        return layoutLin;
    }

    /**
     * 拖拽事件监听器
     */
    private View.OnDragListener mOnDragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int enterShape = R.drawable.item_droptarget_shape;
            int normalShape = R.drawable.puzzle_item_shape;

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundResource(enterShape);
                    // Log.e("Drag_TAG", "Entered Action");
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    // Do nothing
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundResource(normalShape);
                    // Log.e("Drag_TAG", "Exit Action");
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) view.getParent();

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout container = (LinearLayout) v;

                    owner.removeView(view);

                    container.addView(view,lp);
                    //判断拼图完成
                    checkSuccess();
                    // Log.e("Drag_TAG", "Drop Action");
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundResource(normalShape);
                    // Log.e("Drag_TAG", "End Action");
                    break;
                default:
                    // Log.e("Drag_TAG","Other Action");
                    break;
            }
            return true;
        }
    };

    /**
     * 设置列数
     */
    public static void setColumn(int column){
        mColumn=column;
    }
    /**
     * 设置高宽比
     * @param re
     */
    public static void setRelative(double re){
        relative =  re;
    }

    /**
     * 得到多值中的最小值
     *
     * @param params
     * @return
     */
    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (min > param) {
                min = param;
            }
        }
        return min;
    }

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mColumn*mColumn; i++) {
            LinearLayout layoutLin=(LinearLayout)findViewById(i+1);
            View mView=layoutLin.getChildAt(0);
            if(mView==null||mView.getId()!=i+1000){
                isSuccess = false;
            }
            if(mView!=null)
            Log.e("SUCCESS_TAG", String.valueOf(mView.getId()));
        }

        if (isSuccess) {
            Toast.makeText(getContext(), "Success , Level Up !",
                    Toast.LENGTH_LONG).show();
            //nextLevel();
        }
    }

    /**
     * 提升游戏等级
     */
    public void nextLevel() {
        this.removeAllViews();
        mAnimLayout = null;
        mColumn++;
        //initBitmap();
        initItem();
    }


    /**
     * 以下是游戏图片交换动画部分,暂时还没有用到，先留着
     *
     *
     */

    private ImageView mFirst;
    private ImageView mSecond;

    /**
     * 动画运行的标志位
     */
    private boolean isAniming;
    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;

    /**
     * 创建动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }

    }

    /**
     *目前这两个函数和当前修改后的代码不一致
     *暂时不要使用
     *
     * 获得图片的真正索引
     *
     * @param tag
     * @return
     */
    private int getIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    private int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);

    }

    /**
     * 交换两个Item的图片
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);
        setUpAnimLayout();
        // 添加FirstView
        ImageView first = new ImageView(getContext());
        first.setImageBitmap(mItemBitmaps
                .get(getImageIndexByTag((String) mFirst.getTag())).bitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);
        // 添加SecondView
        ImageView second = new ImageView(getContext());
        second.setImageBitmap(mItemBitmaps
                .get(getImageIndexByTag((String) mSecond.getTag())).bitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        // 设置动画
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft()
                - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0,
                mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop()
                - mSecond.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);
        // 添加动画监听
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;
                mFirst.setVisibility(INVISIBLE);
                mSecond.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                String[] firstParams = firstTag.split("_");
                String[] secondParams = secondTag.split("_");

                mFirst.setImageBitmap(mItemBitmaps.get(Integer
                        .parseInt(secondParams[0])).bitmap);
                mSecond.setImageBitmap(mItemBitmaps.get(Integer
                        .parseInt(firstParams[0])).bitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);
                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                checkSuccess();
                isAniming = false;
            }
        });

    }

}
