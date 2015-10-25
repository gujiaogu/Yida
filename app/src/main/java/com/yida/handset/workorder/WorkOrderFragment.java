package com.yida.handset.workorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.yida.handset.ConstructOrderActivity;
import com.yida.handset.ElectronicWriterActivity;
import com.yida.handset.LoginActivity;
import com.yida.handset.R;
import com.yida.handset.RequestQueueSingleton;
import com.yida.handset.entity.OrderItem;
import com.yida.handset.entity.User;
import com.yida.handset.entity.WorkOrder;
import com.yida.handset.entity.WorkOrderDao;
import com.yida.handset.sqlite.TableWorkOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class WorkOrderFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener{

    public static final String TAG_ID = "tag_id";
    public static final String TAG_ORDER_STATUS = "tag_order_status";
    public static final String TAG_SITE = "tag_site";
    public static List<WorkOrder> orders = new ArrayList<>();

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
    @Bind(R.id.order_mine)
    ImageView mOrderMine;
    @Bind(R.id.order_history)
    ImageView mOrderHistory;

    private WorkOrderDao mWorkOrderDao;
    private String username;
    private ProgressDialog pd;

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
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
        mOrderList.setAdapter(adapter);
        mOrderList.setOnItemClickListener(this);
        mOrderMine.setOnClickListener(this);
        mOrderHistory.setOnClickListener(this);
        mWorkOrderDao = new WorkOrderDao(getActivity());

        SharedPreferences preferences = getActivity().getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        User user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());
        this.username = user.getUsername();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderItem item = (OrderItem) parent.getAdapter().getItem(position);
        String orderType = item.getOrderType();
        Intent intent;
        switch (orderType) {
            case ORDER_CONSTRUCT:
                intent = new Intent(getActivity(), ConstructOrderActivity.class);
                intent.putExtra(TAG_ID, item.getId());
                intent.putExtra(TAG_ORDER_STATUS, item.getOrderStatus());
                intent.putExtra(TAG_SITE, item.getSiteName());
                startActivity(intent);
                break;
            case ORDER_CHECK:
                break;
            case ORDER_CONFIGURATION:
                break;
            case ORDER_COLLECT:
                break;
            case ORDER_WRITE_ELE_INFO:
                intent = new Intent(getActivity(), ElectronicWriterActivity.class);
                startActivity(intent);
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
            case R.id.order_mine:
                mOrderMine.setSelected(true);
                mOrderHistory.setSelected(false);
                setMineOrder();
                break;
            case R.id.order_history:
                mOrderMine.setSelected(false);
                mOrderHistory.setSelected(true);
                setHistoryOrder();
                break;
            default:
                break;
        }
    }

    public void setMineOrder() {
        new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
        mOrderList.setAdapter(adapter);
    }

    public void setHistoryOrder() {
        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_STATUS + "=?", new String[]{username, WorkOrder.STATUS_COMPLETED}).execute();
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
        mOrderList.setAdapter(adapter);
    }

    private void showTypeDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                switch (getSelectedIndex()) {
                    case 0:
                        new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
                        break;
                    case 1:
                        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_TYPE + "=?", new String[]{username, String.valueOf(getSelectedIndex())}).execute();
                        break;
                    case 2:
                        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_TYPE + "=?", new String[]{username, String.valueOf(getSelectedIndex())}).execute();
                        break;
                    case 3:
                        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_TYPE + "=?", new String[]{username, String.valueOf(getSelectedIndex())}).execute();
                        break;
                    default:
                        break;
                }
                OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
                mOrderList.setAdapter(adapter);
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
        final SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                switch (getSelectedIndex()) {
                    case 0:
                        new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
                        break;
                    case 1:
                        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_STATUS + "=?",  new String[]{username, getSelectedValue().toString()}).execute();
                        break;
                    case 2:
                        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_STATUS + "=?",  new String[]{username, getSelectedValue().toString()}).execute();
                        break;
                    default:
                        break;
                }
                OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
                mOrderList.setAdapter(adapter);
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
        private List<WorkOrder> data;
        private LayoutInflater inflater;

        public OrderListAdapter(Context context, List<WorkOrder> data) {
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

            WorkOrder item = data.get(position);
            holder.itemTitle.setText("ID : " + item.getWorkOrderId());
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

    private class OrderTask extends AsyncTask<Void, Void, Void> {
        private String selection;
        private String[] selections;
        public OrderTask(String selection, String[] selections) {
            super();
            this.selection = selection;
            this.selections = selections;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.loading));
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dismiss();
                    cancel(true);
                }
            });
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
            mOrderList.setAdapter(adapter);
            dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            orders = mWorkOrderDao.queryAll(this.selection, this.selections, null, null, null);
            return null;
        }
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }
}
