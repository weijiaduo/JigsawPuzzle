package com.promote.jigsawpuzzleview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.promote.jigsawpuzzlegame.R;

/**
 *
 *
 *这个类同样暂时没有用，不用管
 *
 *
 */
public class PuzzleGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private int mColumn;

    public PuzzleGridViewAdapter(Context context,int mColumn) {
        this.mContext = context;
        this.mColumn=mColumn;
    }

    public int getCount() {
        return mColumn*mColumn;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = new ImageView(mContext);
            convertView.setBackgroundResource(R.drawable.puzzle_item_shape);
            viewHolder.mImg = (ImageView) convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    private class ViewHolder {
        ImageView mImg;
    }
}
