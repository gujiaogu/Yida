package com.yida.handset;

import android.content.Context;
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

    public DrawerAdapter(Context context, List<ActionWrapper> mDataSet) {
        this.mDataSet = mDataSet;
        this.context = context;
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
            view = LayoutInflater.from(this.context).inflate(android.R.layout.simple_list_item_1, null);
            viewHolder.mTextView = (TextView) view.findViewById(android.R.id.text1);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.mTextView.setText(mDataSet.get(i).getName());
        return view;
    }

    class ViewHolder {
        TextView mTextView;
    }
}
