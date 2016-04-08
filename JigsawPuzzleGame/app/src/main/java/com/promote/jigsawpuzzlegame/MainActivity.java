package com.promote.jigsawpuzzlegame;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.promote.jigsawpuzzleview.HorizontalScrollViewAdapter;
import com.promote.imagesplitter.ImagePiece;
import com.promote.imagesplitter.ImageSplitter;
import com.promote.jigsawpuzzleview.MyHorizontalScrollView;
import com.promote.jigsawpuzzleview.JigsawPuzzleLayout;
import com.promote.jigsawpuzzleview.PuzzleGridView;
import com.promote.jigsawpuzzleview.PuzzleGridViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 *@version 1.1
 *
 */
public class MainActivity extends Activity {

    /**
     * 拼图用的主框图
     */
    JigsawPuzzleLayout mGameView;
    /**
     * 主框图
     */
    private PuzzleGridView mGameGridView;
    /**
     * 主框图的适配器
     */
    private PuzzleGridViewAdapter mPuzzleAdapter;
    /**
     * 拼图用的滑动框
     */
    private MyHorizontalScrollView mHorizontalScrollView;
    /**
     * 滑动框的适配器
     */
    private HorizontalScrollViewAdapter mAdapter;
    /**
     * 当前拼图的图片
     */
    private Bitmap mBitmap;
    /**
     * 切分图后的宽高比
     */
    public double relative;
    /**
     * 当前拼图的列数
     */
    public int mColumn = 4;
    /**
     * 切分好的图片
     */
    private  List<ImagePiece> mItemBitmaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化图片及获取宽高比
        relative = initBitmap();
        Log.e("RELATIVE_TAG", String.valueOf(relative));
        JigsawPuzzleLayout.setColumn(mColumn);
        JigsawPuzzleLayout.setRelative(relative);

        setContentView(R.layout.activity_main);

        mGameView = (JigsawPuzzleLayout) findViewById(R.id.id_gameview);
        // mGameGridView = (PuzzleGridView)findViewById(R.id.id_gameview);
        // mPuzzleAdapter=new PuzzleGridViewAdapter(this,mColumn);
        // mGameGridView.setAdapter(mPuzzleAdapter);

        //relative=mBitmap.getWidth()/(double)mBitmap.getHeight();
        //Log.e("RELATIVE_TAG", String.valueOf(relative));

        mHorizontalScrollView = (MyHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
        mAdapter = new HorizontalScrollViewAdapter(this, mItemBitmaps);
        // 为滚动条设置适配器
        mHorizontalScrollView.initDatas(mAdapter);
    }

    /**
     * 初始化图片
     */
    private double initBitmap() {
        if (mBitmap == null)
            mBitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.a);

        // 将图片切成mColumn*mColumn份
        mItemBitmaps = ImageSplitter.split(mBitmap, mColumn);

        //对图片进行随机排序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece lhs, ImagePiece rhs) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });

        // 获取图片的宽高比
        Bitmap mItemBitmap = (Bitmap)mItemBitmaps.get(0).bitmap;
        double rela = mItemBitmap.getWidth() / (double)mItemBitmap.getHeight();
        return rela;
    }
}
