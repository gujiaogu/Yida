package com.yida.handset.workorder;

import android.app.Activity;
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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.yida.handset.CollectActivity;
import com.yida.handset.ConfigurationActivity;
import com.yida.handset.Constants;
import com.yida.handset.ConstructOrderActivity;
import com.yida.handset.ElectronicWriterActivity;
import com.yida.handset.InspectOrderActivity;
import com.yida.handset.LogWrapper;
import com.yida.handset.LoginActivity;
import com.yida.handset.R;
import com.yida.handset.RequestQueueSingleton;
import com.yida.handset.entity.ConfigurationResult;
import com.yida.handset.entity.ConstructOrderResult;
import com.yida.handset.entity.ConstructOrderRoute;
import com.yida.handset.entity.InspectResult;
import com.yida.handset.entity.OpticalItem;
import com.yida.handset.entity.OpticalRoute;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.entity.WorkOrder;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.WorkOrderDao;

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
    public static final int REQUEST_CODE_CONSTRUCT_ORDER = 1;
    public static final int REQUEST_CODE_INSPECT_ORDER = 2;
    public static List<WorkOrder> orders = new ArrayList<>();

    private static final String ORDER_CONSTRUCT = "施工工单";
    private static final String ORDER_CHECK = "巡检工单";
    private static final String ORDER_WRITE_ELE_INFO = "电子标签写入工单";
    private static final String ORDER_CONFIGURATION = "配置工单";
    private static final String ORDER_COLLECT = "数据采集工单";
    public static Map<String, String> orderTypes = new HashMap<>();
    static {
        orderTypes.put("1", ORDER_CONSTRUCT);
        orderTypes.put("2", ORDER_CHECK);
        orderTypes.put("3", ORDER_WRITE_ELE_INFO);
        orderTypes.put("4", ORDER_CONFIGURATION);
        orderTypes.put("5", ORDER_COLLECT);

        orderTypes.put(ORDER_CONSTRUCT, "1");
        orderTypes.put(ORDER_CHECK, "2");
        orderTypes.put(ORDER_WRITE_ELE_INFO, "3");
        orderTypes.put(ORDER_CONFIGURATION, "4");
        orderTypes.put(ORDER_COLLECT, "5");
    }

    public static final String STATUS_COMPLETED = "已回单";
    public static final String STATUS_NO_ACCEPT = "未接收";
    public static final String STATUS_ACCEPTED = "已接收";
    public static final String STATUS_NO_PUBLISHED = "已驳回"; //驳回后为未发布状态
    public static Map<String, String> orderStatus = new HashMap<>();
    static {
        orderStatus.put(STATUS_NO_PUBLISHED, "10");
        orderStatus.put(STATUS_NO_ACCEPT, "20");
        orderStatus.put(STATUS_ACCEPTED, "30");
        orderStatus.put(STATUS_COMPLETED, "40");

        orderStatus.put("10", STATUS_NO_PUBLISHED);
        orderStatus.put("20", STATUS_NO_ACCEPT);
        orderStatus.put("30", STATUS_ACCEPTED);
        orderStatus.put("40", STATUS_COMPLETED);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG_CONSTRUCT_ORDER = "tag_construct_order";
    private static final String TAG_INSPECT_ORDER = "tag_inspect_order";
    private static final String TAG_ETAG_WRITE_ORDER = "tag_etag_write_order";
    private static final String TAG_CONFIGURATION_ORDER = "tag_configuration_order";

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
        WorkOrder item = (WorkOrder) parent.getAdapter().getItem(position);
        String orderType = item.getOrderType();
        Intent intent;
        switch (orderType) {
            case "1": //施工工单
                startConstructOrder(item);
                break;
            case "2": //巡检工单
                startInspectOrder(item);
                break;
            case "3": //电子标签写入工单
                startEtagWriterOrder(item);
                break;
            case "4":
                intent = new Intent(getActivity(), ConfigurationActivity.class);
                startActivity(intent);
//                startConfigurationOrder(item);
                break;
            case "5":
                intent = new Intent(getActivity(), CollectActivity.class);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogWrapper.d("=======================");
        switch (requestCode) {
            case REQUEST_CODE_CONSTRUCT_ORDER:
                if (resultCode == Activity.RESULT_OK) {
                    new OrderTask(null, null).execute();
                }
                break;
            case REQUEST_CODE_INSPECT_ORDER:
                if (resultCode == Activity.RESULT_OK) {
                    new OrderTask(null, null).execute();
                }
                break;
            default:
                break;
        }
    }

    private void startConfigurationOrder(WorkOrder item) {
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(TAG_CONFIGURATION_ORDER);
            }
        });
        pd.show();

        final WorkOrder order = item;
        String params = "?workId=" + item.getWorkId();
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_CONFIGURATION_ORDER + params;
        LogWrapper.d(mUrl);
        StringRequest configurationOrderRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                ConfigurationResult result = null;
                try {
                    result = gson.fromJson(s, new TypeToken<ConfigurationResult>() {
                    }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }

                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    ConfigurationActivity.configurationEntities = result.getDevices();
                    if (ConfigurationActivity.configurationEntities != null) {
                        Intent intent = new Intent(getActivity(), ConfigurationActivity.class);
                        intent.putExtra(TAG_ID, order.getWorkId());
                        intent.putExtra(TAG_ORDER_STATUS, orderStatus.get(order.getStatus()));
                        intent.putExtra(TAG_SITE, order.getSiteName());
                        startActivityForResult(intent, REQUEST_CODE_INSPECT_ORDER);
                    }
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        configurationOrderRequest.setTag(TAG_CONFIGURATION_ORDER);

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(configurationOrderRequest);
    }

    private void startEtagWriterOrder(WorkOrder item) {
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(TAG_ETAG_WRITE_ORDER);
            }
        });
        pd.show();

        final WorkOrder order = item;
        String params = "?workId=" + item.getWorkId();
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_ETAG_WRITE_ORDER + params;
        LogWrapper.d(mUrl);
        StringRequest etagWriteOrderRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                InspectResult result = null;
                try {
                    result = gson.fromJson(s, new TypeToken<InspectResult>() {
                    }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }

                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    ElectronicWriterActivity.workOrder = result.getWorkOrder();
                    if (ElectronicWriterActivity.workOrder != null) {
                        Intent intent = new Intent(getActivity(), ElectronicWriterActivity.class);
                        intent.putExtra(TAG_ID, order.getWorkId());
                        intent.putExtra(TAG_ORDER_STATUS, orderStatus.get(order.getStatus()));
                        intent.putExtra(TAG_SITE, order.getSiteName());
                        startActivityForResult(intent, REQUEST_CODE_INSPECT_ORDER);
                    }
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        etagWriteOrderRequest.setTag(TAG_ETAG_WRITE_ORDER);

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(etagWriteOrderRequest);
    }

    private void startInspectOrder(WorkOrder item) {
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(TAG_INSPECT_ORDER);
            }
        });
        pd.show();

        final WorkOrder order = item;
        String params = "?workId=" + item.getWorkId();
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_INSPECT_ORDER + params;
        LogWrapper.d(mUrl);
        StringRequest inspectOrderRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                InspectResult result = null;
                try {
                    result = gson.fromJson(s, new TypeToken<InspectResult>() {
                    }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }

                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    InspectOrderActivity.workOrder = result.getWorkOrder();
                    if (InspectOrderActivity.workOrder != null) {
                        Intent intent = new Intent(getActivity(), InspectOrderActivity.class);
                        intent.putExtra(TAG_ID, order.getWorkId());
                        intent.putExtra(TAG_ORDER_STATUS, orderStatus.get(order.getStatus()));
                        intent.putExtra(TAG_SITE, order.getSiteName());
                        startActivityForResult(intent, REQUEST_CODE_INSPECT_ORDER);
                    }
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        inspectOrderRequest.setTag(TAG_INSPECT_ORDER);

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(inspectOrderRequest);
    }

    private void startConstructOrder(WorkOrder item) {
        pd = new ProgressDialog(getActivity());
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getActivity().getApplicationContext()).getRequestQueue().cancelAll(TAG_CONSTRUCT_ORDER);
            }
        });
        pd.show();

        final WorkOrder order = item;
        String params = "?workId=" + item.getWorkId();
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_CONSTRUCT_ORDER + params;
        StringRequest constrcutOrderRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                ConstructOrderResult result = null;
                try {
                    result = gson.fromJson(s, new TypeToken<ConstructOrderResult>() {
                        }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }

                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    List<OpticalItem> data = new ArrayList<>();
                    List<ConstructOrderRoute> opticalRouteList = result.getOrder().getRouteList();
                    for (ConstructOrderRoute route : opticalRouteList) {
                        data.add(route);
                        List<OpticalRoute> opticalRoutes = route.getOpticalRoute();
                        if (opticalRoutes != null && opticalRoutes.size() > 0) {
                            for(OpticalRoute item : opticalRoutes) {
                                data.add(item);
                            }
                        }
                    }
                    ConstructOrderActivity.mOpticalItems = data;

                    Intent intent = new Intent(getActivity(), ConstructOrderActivity.class);
                    intent.putExtra(TAG_ID, order.getWorkId());
                    intent.putExtra(TAG_ORDER_STATUS, orderStatus.get(order.getStatus()));
                    intent.putExtra(TAG_SITE, order.getSiteName());
                    dismiss();
                    startActivityForResult(intent, REQUEST_CODE_CONSTRUCT_ORDER);
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    dismiss();
                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dismiss();
                volleyError.printStackTrace();
            }
        });
        constrcutOrderRequest.setTag(TAG_CONSTRUCT_ORDER);

        RequestQueueSingleton.getInstance(getActivity()).addToRequestQueue(constrcutOrderRequest);
    }

    public void setMineOrder() {
        new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
        mOrderList.setAdapter(adapter);
        resetTopText();
    }

    public void setHistoryOrder() {
        new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_STATUS + " in(?,?)", new String[]{username, orderStatus.get(STATUS_COMPLETED), orderStatus.get(STATUS_NO_PUBLISHED)}).execute();
        OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
        mOrderList.setAdapter(adapter);
        resetTopText();
    }

    private void resetTopText() {
        mOrderTypeText.setText(getResources().getStringArray(R.array.order_type)[0]);
        mOrderStatusText.setText(getResources().getStringArray(R.array.order_status)[0]);
    }

    private void showTypeDialog() {
        SimpleDialog.Builder builder = new SimpleDialog.Builder(R.style.SimpleDialogLight) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                if (getSelectedIndex() == 0) {
                    new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
                } else {
                    new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_TYPE + "=?", new String[]{username, String.valueOf(getSelectedIndex())}).execute();
                }
                OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
                mOrderList.setAdapter(adapter);
                mOrderTypeText.setText(getSelectedValue());
                mOrderStatusText.setText(getResources().getStringArray(R.array.order_status)[0]);
                Toast.makeText(getActivity(), "You have selected " + getSelectedValue(), Toast.LENGTH_SHORT).show();
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
                if (getSelectedIndex() == 0) {
                    new OrderTask(TableWorkOrder.USERNAME + "=?", new String[]{username}).execute();
                } else {
                    new OrderTask(TableWorkOrder.USERNAME + "=? and " + TableWorkOrder.ORDER_STATUS + "=?",  new String[]{username, orderStatus.get(getSelectedValue().toString())}).execute();
                }
                OrderListAdapter adapter = new OrderListAdapter(getActivity(), orders);
                mOrderList.setAdapter(adapter);
                mOrderStatusText.setText(getSelectedValue());
                mOrderTypeText.setText(getResources().getStringArray(R.array.order_type)[0]);
                Toast.makeText(getActivity(), "You have selected " + getSelectedValue(), Toast.LENGTH_SHORT).show();
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
                holder.assignerName = (TextView) convertView.findViewById(R.id.assigner_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            WorkOrder item = data.get(position);
            holder.itemTitle.setText("ID : " + item.getWorkId());
            holder.itemStatus.setText("状态 : " + orderStatus.get(item.getStatus()));
            holder.itemType.setText("类型 : " + orderTypes.get(item.getOrderType()));
            holder.itemSite.setText("地址 : " + (item.getSiteName() == null ? "" : item.getSiteName()));
            holder.assignerName.setText("责任人 : " + item.getAssignerName());
            return convertView;
        }

        class ViewHolder {
            TextView itemTitle;
            TextView itemType;
            TextView itemStatus;
            TextView itemSite;
            TextView assignerName;
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
