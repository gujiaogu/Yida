package com.yida.handset.workorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.yida.handset.ConstructOrderActivity;
import com.yida.handset.R;
import com.yida.handset.entity.OrderItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkOrderFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    private static List<OrderItem> orderItems = new ArrayList<>();
    private static String[] status = {"未开始", "进行中", "已完成"};
    private static final String ORDER_CONSTRUCT = "施工工单";
    private static final String ORDER_CHECK = "巡检工单";
    private static final String ORDER_CONFIGURATION = "配置工单";
    private static final String ORDER_COLLECT = "数据采集工单";
    private static final String ORDER_WRITE_ELE_INFO = "电子标签写入工单";
    public static Map<Integer, String> orderTypes = new HashMap<>();
    static {
        orderTypes.put(0, ORDER_CONSTRUCT);
        orderTypes.put(1, ORDER_CHECK);
        orderTypes.put(2, ORDER_CONFIGURATION);
        orderTypes.put(3, ORDER_COLLECT);
        orderTypes.put(4, ORDER_WRITE_ELE_INFO);
    }

    static {
        OrderItem item;
        for (int i = 0; i < 15; i ++) {
            item = new OrderItem();
            item.setId(String.valueOf(i));
            item.setOrderStatus(status[i % 3]);
            item.setOrderType(orderTypes.get(i % 5));
            item.setSiteName("地址 " + i);
            item.setRemark("备注" + i);
            orderItems.add(item);
        }
    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Bind(R.id.order_type_text_top)
    TextView mOrderTypeText;
    @Bind(R.id.order_status_text_top)
    TextView mOrderStatusText;
    @Bind(R.id.order_list)
    ListView mOrderList;

    public static WorkOrderFragment newInstance(String param1, String param2) {
        WorkOrderFragment fragment = new WorkOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public WorkOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_third, container, false);
        ButterKnife.bind(this, rootView);
        mOrderTypeText.setOnClickListener(this);
        mOrderStatusText.setOnClickListener(this);
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orderItems);
        mOrderList.setAdapter(adapter);
        mOrderList.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (((OrderItem) parent.getAdapter().getItem(position)).getOrderType()) {
            case ORDER_CONSTRUCT:
                Intent intent = new Intent(getActivity(), ConstructOrderActivity.class);
                startActivity(intent);
                break;
            case ORDER_CHECK:
                break;
            case ORDER_CONFIGURATION:
                break;
            case ORDER_COLLECT:
                break;
            case ORDER_WRITE_ELE_INFO:
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_type_text_top:
                showTypeDialog();
                break;
            case R.id.order_status_text_top:
                showStatusDialog();
                break;
            default:
                break;
        }
    }

    private void showTypeDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                Toast.makeText(getActivity(), "You have selected " + getSelectedValue() + " as phone ringtone.", Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(getActivity(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(getResources().getStringArray(R.array.order_type), 0)
            .title(getString(R.string.order_types))
                .positiveAction("确定")
                .negativeAction("取消");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    private void showStatusDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                Toast.makeText(getActivity(), "You have selected " + getSelectedValue() + " as phone ringtone.", Toast.LENGTH_SHORT).show();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                Toast.makeText(getActivity(), "Cancelled" , Toast.LENGTH_SHORT).show();
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.items(getResources().getStringArray(R.array.order_status), 0)
                .title(getString(R.string.order_status))
                .positiveAction("确定")
                .negativeAction("取消");
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    private class OrderListAdapter extends BaseAdapter {

        private Context context;
        private List<OrderItem> data;
        private LayoutInflater inflater;

        public OrderListAdapter(Context context, List<OrderItem> data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(this.context);
        }
        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (data != null) {
                return data.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_order_list, null);
                holder.itemTitle = (TextView) convertView.findViewById(R.id.item_title);
                holder.itemType = (TextView) convertView.findViewById(R.id.item_type);
                holder.itemStatus = (TextView) convertView.findViewById(R.id.item_status);
                holder.itemSite = (TextView) convertView.findViewById(R.id.site);
                holder.remark = (TextView) convertView.findViewById(R.id.remark);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OrderItem item = data.get(position);
            holder.itemTitle.setText("ID : " + item.getId());
            holder.itemStatus.setText("状态 : " + item.getOrderStatus());
            holder.itemType.setText("类型 : " + item.getOrderType());
            holder.itemSite.setText("地址 : " + item.getSiteName());
            holder.remark.setText("备注 : " + item.getRemark());
            return convertView;
        }

        class ViewHolder {
            TextView itemTitle;
            TextView itemType;
            TextView itemStatus;
            TextView itemSite;
            TextView remark;
        }
    }
}
