package com.yida.handset;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.ConstructOrderRoute;
import com.yida.handset.entity.OpticalItem;
import com.yida.handset.entity.OpticalRoute;
import com.yida.handset.entity.ResourceVo;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.WorkOrderDao;
import com.yida.handset.workorder.WorkOrderFragment;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConstructOrderActivity extends AppCompatActivity implements View.OnClickListener{

    public static List<OpticalItem> mOpticalItems;

    private static final String TAG_ACCEPT_ORDER = "accept_order";
    private static final String TAG_REJECT_ORDER = "reject_order";
    private static final String TAG_COMPLETE_ORDER = "complete_order";

    private static final int TYPE_ROUTE = 0;
    private static final int TYPE_OPTICAL_ROUTE = 1;
    private static final int TYPE_COUNT = 2;

    @Bind(R.id.order_id)
    TextView mOrderId;
    @Bind(R.id.order_status)
    TextView mOrderStatus;
    @Bind(R.id.order_site)
    TextView mOrderSite;
    @Bind(R.id.order_remark)
    TextView mOrderRemark;
    @Bind(R.id.order_operate_list)
    ListView mOrderOperateList;
    @Bind(R.id.complete_order)
    Button mCompleteOrder;
    @Bind(R.id.reject_order)
    Button mRejectOrder;
    @Bind(R.id.accept_order)
    Button mAcceptOrder;

    private ProgressDialog pd;
    private User user;
    private int workId;
    private WorkOrderDao mWorkOrderDao;
    private boolean isStatusChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construct_order);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isStatusChanged) {
                    setResult(Activity.RESULT_OK);
                }
                finish();
            }
        });

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            workId = extraIntent.getIntExtra(WorkOrderFragment.TAG_ID, 0);
            String workStatus = extraIntent.getStringExtra(WorkOrderFragment.TAG_ORDER_STATUS);
            mOrderId.setText("工单ID : " + workId);
            mOrderStatus.setText("工单状态 : " + workStatus);
            mOrderSite.setText("地址 : " + extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE));
            if (workStatus != null && !"".equals(workStatus)) {
                if (workStatus.equals(WorkOrderFragment.STATUS_ACCEPTED)) {
                    mCompleteOrder.setVisibility(View.VISIBLE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                } else if (workStatus.equals(WorkOrderFragment.STATUS_COMPLETED)
                        || workStatus.equals(WorkOrderFragment.STATUS_NO_PUBLISHED)) {
                    mCompleteOrder.setVisibility(View.GONE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                }
            }
        }

        mOrderRemark.setText("备注：这个问题需要B站的人协助");
        ConstructOrderAdapter adapter = new ConstructOrderAdapter(this, mOpticalItems);
        mOrderOperateList.setAdapter(adapter);

        mWorkOrderDao = new WorkOrderDao(this);

        mCompleteOrder.setOnClickListener(this);
        mRejectOrder.setOnClickListener(this);
        mAcceptOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_order:
                acceptOrder();
                break;
            case R.id.reject_order:
                rejectOrder();
                break;
            case R.id.complete_order:
                completeOrder();
                break;
            default:
                break;
        }
    }

    private void completeOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.complete_dialog_text_hint);
        View view = LayoutInflater.from(this).inflate(R.layout.complete_dialog_view, null);
        final EditText remarkEdit = (EditText) view.findViewById(R.id.remark);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pd = new ProgressDialog(ConstructOrderActivity.this);
                pd.setMessage(getString(R.string.loading));
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dismiss();
                        RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(TAG_COMPLETE_ORDER);
                    }
                });
                pd.show();

                String params = "?workId=" + workId + "&token=" + user.getToken() + "&remark=" + remarkEdit.getText().toString().trim();
                String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.COMPLETE_CONSTRUCT_ORDER + params;
                LogWrapper.d(mUrl);
                StringRequest completeOrderRequest = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogWrapper.d(response);
                        Gson gson = new Gson();
                        ResourceVo result = null;
                        try {
                            result = gson.fromJson(response, new TypeToken<ResourceVo>() {
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
                            ContentValues values = new ContentValues();
                            values.put(TableWorkOrder.ORDER_STATUS, WorkOrderFragment.orderStatus.get(WorkOrderFragment.STATUS_COMPLETED));

                            String where = TableWorkOrder.WORKID + "='" + workId + "'";
                            int resultCodeDB = mWorkOrderDao.update(values, where);
                            if (resultCodeDB > 0) {
                                mRejectOrder.setVisibility(View.GONE);
                                mAcceptOrder.setVisibility(View.GONE);
                                mCompleteOrder.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_COMPLETED);
                                isStatusChanged = true;
                                Toast.makeText(ConstructOrderActivity.this, R.string.complete_dialog_text_hint_result, Toast.LENGTH_SHORT).show();
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ConstructOrderActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismiss();
                    }
                });

                completeOrderRequest.setTag(TAG_COMPLETE_ORDER);
                RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(completeOrderRequest);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void rejectOrder() {
        View view = LayoutInflater.from(this).inflate(R.layout.reject_dialog_view, null);
        final EditText editText = (EditText) view.findViewById(R.id.reject_reason_edit);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.order_user_operate_return);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String reason = editText.getText().toString().trim();
                if ("".equals(reason)) {
                    Toast.makeText(ConstructOrderActivity.this, R.string.reject_dialog_text_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                pd = new ProgressDialog(ConstructOrderActivity.this);
                pd.setMessage(getString(R.string.loading));
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dismiss();
                        RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(TAG_REJECT_ORDER);
                    }
                });
                pd.show();

                String params = "?workIds=" + workId + "&reason=" + reason + "&token=" + user.getToken();
                String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.REJECT_ORDER + params;
                StringRequest rejectRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogWrapper.d(response);
                        Gson gson = new Gson();
                        ResourceVo result = null;
                        try {
                            result = gson.fromJson(response, new TypeToken<ResourceVo>() {
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
                            ContentValues values = new ContentValues();
                            values.put(TableWorkOrder.ORDER_STATUS, WorkOrderFragment.orderStatus.get(WorkOrderFragment.STATUS_NO_PUBLISHED));

                            String where = TableWorkOrder.WORKID + "='" + workId + "'";
                            int resultCodeDB = mWorkOrderDao.update(values, where);
                            if (resultCodeDB > 0) {
                                mRejectOrder.setVisibility(View.GONE);
                                mAcceptOrder.setVisibility(View.GONE);
                                mCompleteOrder.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_NO_PUBLISHED);
                                isStatusChanged = true;
                            }
                        } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ConstructOrderActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        dismiss();
                    }
                });
                rejectRequest.setTag(TAG_REJECT_ORDER);
                RequestQueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(rejectRequest);

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ConstructOrderActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void acceptOrder() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(TAG_ACCEPT_ORDER);
            }
        });
        pd.show();

        String params = "?workIds=" + workId + "&status=" + WorkOrderFragment.orderStatus.get(WorkOrderFragment.STATUS_ACCEPTED);
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.ACCEPT_ORDER + params;
        LogWrapper.d(mUrl);
        StringRequest acceptOrderQueue = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                LogWrapper.d(s);
                Gson gson = new Gson();
                ResultVo result = null;
                try {
                    result = gson.fromJson(s, new TypeToken<ResultVo>() {
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
                    ContentValues values = new ContentValues();
                    values.put(TableWorkOrder.ORDER_STATUS, WorkOrderFragment.orderStatus.get(WorkOrderFragment.STATUS_ACCEPTED));

                    String where = TableWorkOrder.WORKID + "='" + workId + "'";
                    int resultCodeDB = mWorkOrderDao.update(values, where);
                    if (resultCodeDB > 0) {
                        mRejectOrder.setVisibility(View.GONE);
                        mAcceptOrder.setVisibility(View.GONE);
                        mCompleteOrder.setVisibility(View.VISIBLE);
                        mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_ACCEPTED);
                        isStatusChanged = true;
                    }

                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(ConstructOrderActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
        acceptOrderQueue.setTag(TAG_ACCEPT_ORDER);

        RequestQueueSingleton.getInstance(this).addToRequestQueue(acceptOrderQueue);
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (isStatusChanged) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
    }

    private class ConstructOrderAdapter extends BaseAdapter {

        private Context context;
        private List<OpticalItem> mData;
        private LayoutInflater inflater;

        public ConstructOrderAdapter(Context context, List<OpticalItem> data) {
            this.context = context;
            this.mData = data;
            this.inflater = LayoutInflater.from(this.context);
        }
        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (mData != null) {
                mData.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData.get(position) instanceof ConstructOrderRoute) {
                return TYPE_ROUTE;
            } else {
                return TYPE_OPTICAL_ROUTE;
            }
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            switch (getItemViewType(i)) {
                case TYPE_ROUTE:  // 通路标签名
                    ViewHolderRoute route;
                    if (convertView == null) {
                        route = new ViewHolderRoute();
                        convertView = inflater.inflate(R.layout.item_construct_order_route, null);
                        route.routeName = (TextView) convertView.findViewById(R.id.item_route_name);
                        convertView.setTag(route);
                    } else {
                        route = (ViewHolderRoute) convertView.getTag();
                    }

                    ConstructOrderRoute routeItem = (ConstructOrderRoute) mData.get(i);
                    route.routeName.setText(routeItem.getName());
                    break;
                case TYPE_OPTICAL_ROUTE: //单个通路
                    ViewHolder holder;
                    if (convertView == null) {
                        holder = new ViewHolder();
                        convertView = inflater.inflate(R.layout.item_construct_list, null);
                        holder.title = (TextView) convertView.findViewById(R.id.con_item_title);
                        holder.operateType = (TextView) convertView.findViewById(R.id.operate_type);
                        holder.routeType = (TextView) convertView.findViewById(R.id.route_type);
                        holder.portAInfo = (TextView) convertView.findViewById(R.id.portAInfo);
                        holder.portZInfo = (TextView) convertView.findViewById(R.id.portZInfo);
                        holder.splittingRatio = (TextView) convertView.findViewById(R.id.splitting_ratio);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }

                    OpticalRoute item = (OpticalRoute) mData.get(i);
                    holder.title.setText(String.valueOf(i));
                    holder.operateType.setText("操作: " + OpticalRoute.OPERATE[item.getOperateType()]);
                    holder.routeType.setText("跳接: " + OpticalRoute.ROUTE_TYPE[item.getRouteType()]);
                    holder.splittingRatio.setText("分光比: " + item.getSplittingRatio() == null ? "" : item.getSplittingRatio());
                    holder.portAInfo.setText("设备: " + item.getaDeviceName() + " > 机框: " + item.getaFrameNo() + " > 盘: " + item.getaBoardNo()
                            + " > 端口: " + item.getaPortNo());
                    holder.portZInfo.setText("设备: " + item.getzDeviceName() + " > 机框: " + item.getzFrameNo() + " > 盘: " + item.getzBoardNo()
                            + " > 端口: " + item.getzPortNo());
                    break;
                default:
                    break;
            }
            return convertView;
        }

        class ViewHolderRoute {
            TextView routeName;
        }

        class ViewHolder {
            TextView title;
            TextView operateType;
            TextView routeType;
            TextView splittingRatio;
            TextView portAInfo;
            TextView portZInfo;
        }
    }
}
