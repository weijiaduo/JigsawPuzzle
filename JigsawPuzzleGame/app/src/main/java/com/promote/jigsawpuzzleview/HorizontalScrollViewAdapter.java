package com.promote.jigsawpuzzleview;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.promote.imagesplitter.ImagePiece;
import com.promote.jigsawpuzzlegame.R;

public class HorizontalScrollViewAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    //private List<Integer> mDatas;
    private List<ImagePiece> mImages;

    public HorizontalScrollViewAdapter(Context context, List<ImagePiece> mImages) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mImages = mImages;
    }

    public int getCount() {
        return mImages.size();
    }

    public Object getItem(int position) {
        return mImages.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.activity_index_gallery_item, parent, false);
            viewHolder.mImg = (ImageView) convertView
                    .findViewById(R.id.id_index_gallery_item_image);
            viewHolder.mText = (TextView) convertView
                    .findViewById(R.id.id_index_gallery_item_text);

            //设置id用于判断拼图完成
            convertView.setId(1000 + mImages.get(position).index);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Bitmap currentBitmap=mImages.get(position).bitmap;
        double relative = currentBitmap.getWidth()/(double)currentBitmap.getHeight();
        // 测试
        // Log.e("CURRENT_TAG", "" + relative);

        //设置viewholder数据
        viewHolder.mImg.setImageBitmap(currentBitmap);
        viewHolder.mText.setText("info " + (mImages.get(position).index + 1));
        viewHolder.mText.setTag(relative);

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
        TextView mText;
    }

}
