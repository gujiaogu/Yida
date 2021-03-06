package com.yida.handset;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gujiao on 15-8-19.
 */
public class DrawerAdapter extends BaseAdapter {

    private List<ActionWrapper> mDataSet;
    private Context context;
    private Resources res;

    public DrawerAdapter(Context context, List<ActionWrapper> mDataSet) {
        this.mDataSet = mDataSet;
        this.context = context;
        this.res = this.context.getResources();
    }

    @Override
    public int getCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return mDataSet != null ? mDataSet.get(i) : null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(this.context).inflate(R.layout.item_drawer, null);
            viewHolder.mTextView = (TextView) view.findViewById(R.id.text1);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mTextView.setText(mDataSet.get(i).getName());
        if(i == 0) {
            Drawable drawable = this.res.getDrawable(R.mipmap.ic_drawable_update);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            viewHolder.mTextView.setCompoundDrawables(drawable, null, null, null);
        }
        if(i == 1) {
            Drawable drawable = res.getDrawable(R.mipmap.ic_drawable_modify_pwd);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            viewHolder.mTextView.setCompoundDrawables(drawable, null, null, null);
        }
        if(i == 2) {
            Drawable drawable = res.getDrawable(R.mipmap.ic_drawable_about);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            viewHolder.mTextView.setCompoundDrawables(drawable, null, null, null);
        }
        if (i == 3) {
            Drawable drawable = res.getDrawable(R.mipmap.ic_exit);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            viewHolder.mTextView.setCompoundDrawables(drawable, null, null, null);
        }
        return view;
    }

    class ViewHolder {
        TextView mTextView;
    }
}
