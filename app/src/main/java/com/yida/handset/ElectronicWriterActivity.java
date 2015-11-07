package com.yida.handset;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.InspectItem;
import com.yida.handset.entity.InspectOrder;
import com.yida.handset.entity.InspectUploadEntity;
import com.yida.handset.entity.ResourceVo;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.WorkOrderDao;
import com.yida.handset.workorder.WorkOrderFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ElectronicWriterActivity extends AppCompatActivity implements View.OnClickListener {

    public static InspectOrder workOrder;

    private static final String TAG_ACCEPT_ORDER = "accept_write_order";
    private static final String TAG_REJECT_ORDER = "reject_write_order";
    private static final String TAG_COMPLETE_ORDER = "complete_write_order";

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.order_id)
    TextView mOrderId;
    @Bind(R.id.order_status)
    TextView mOrderStatus;
    @Bind(R.id.order_site)
    TextView mOrderSite;
    @Bind(R.id.order_remark)
    TextView mOrderRemark;
    @Bind(R.id.device_name)
    TextView mDeviceName;
    @Bind(R.id.device_type)
    TextView mDeviceType;
    @Bind(R.id.hardware_version)
    TextView mHardwareVersion;
    @Bind(R.id.software_version)
    TextView mSoftwareVersion;
    @Bind(R.id.write_port_count)
    TextView mWritePortCount;
    @Bind(R.id.complete_order)
    Button mCompleteOrder;
    @Bind(R.id.reject_order)
    Button mRejectOrder;
    @Bind(R.id.accept_order)
    Button mAcceptOrder;
    @Bind(R.id.write_ports)
    Button mWritePorts;

    private ProgressDialog pd;
    private User user;
    private int workId;
    private boolean isStatusChanged;
    private WorkOrderDao mWorkOrderDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_writer);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isStatusChanged) {
                    setResult(Activity.RESULT_OK);
                }
                finish();
            }
        });

        mWorkOrderDao = new WorkOrderDao(this);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());

        if (workOrder != null && workOrder.getDevices().size() > 0) {
            InspectItem device = workOrder.getDevices().get(0);
            mDeviceName.setText("设备名: " + (device.getDeviceName() == null ? "" : device.getDeviceName()));
            mDeviceType.setText("设备类型: " + (device.getDeviceType() == null ? "" : device.getDeviceType()));
            mHardwareVersion.setText("硬件版本: " + (device.getDeviceHardwareVersion() == null ? "" : device.getDeviceHardwareVersion()));
            mSoftwareVersion.setText("软件版本: " + (device.getDeviceSoftwareVersion() == null ? "" : device.getDeviceSoftwareVersion()));
            mWritePortCount.setText("待写入端口数: " + (device.getResourceData() == null ? 0 : device.getResourceData().size()));
        }

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            workId = extraIntent.getIntExtra(WorkOrderFragment.TAG_ID, 0);
            String workStatus = extraIntent.getStringExtra(WorkOrderFragment.TAG_ORDER_STATUS);
            mOrderId.setText("工单ID : " + workId);
            mOrderStatus.setText("工单状态 : " + workStatus);
            mOrderSite.setText("地址 : " + (extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE) == null
                    ? "" : extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE)));
            mOrderRemark.setText("备注：这是一个巡检工单");
            if (workStatus != null && !"".equals(workStatus)) {
                if (workStatus.equals(WorkOrderFragment.STATUS_ACCEPTED)) {
                    mCompleteOrder.setVisibility(View.GONE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                    mWritePorts.setVisibility(View.VISIBLE);
                } else if (workStatus.equals(WorkOrderFragment.STATUS_COMPLETED)
                        || workStatus.equals(WorkOrderFragment.STATUS_NO_PUBLISHED)) {
                    mCompleteOrder.setVisibility(View.GONE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                    mWritePorts.setVisibility(View.GONE);
                }
            }
        }

        mCompleteOrder.setOnClickListener(this);
        mRejectOrder.setOnClickListener(this);
        mAcceptOrder.setOnClickListener(this);
        mWritePorts.setOnClickListener(this);

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
            case R.id.write_ports:
                writeETags();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (isStatusChanged) {
            setResult(Activity.RESULT_OK);
        }
        super.onBackPressed();
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
                        mCompleteOrder.setVisibility(View.GONE);
                        mWritePorts.setVisibility(View.VISIBLE);
                        mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_ACCEPTED);
                        isStatusChanged = true;
                    }

                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(ElectronicWriterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ElectronicWriterActivity.this, R.string.reject_dialog_text_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                pd = new ProgressDialog(ElectronicWriterActivity.this);
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
                                mWritePorts.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_NO_PUBLISHED);
                                isStatusChanged = true;
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ElectronicWriterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ElectronicWriterActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
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
                pd = new ProgressDialog(ElectronicWriterActivity.this);
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
                String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.COMPLETE_ETAG_WRITE_ORDER + params;
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
                                Toast.makeText(ElectronicWriterActivity.this, R.string.complete_dialog_text_hint_result, Toast.LENGTH_SHORT).show();
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ElectronicWriterActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void writeETags () {
        new WriteETagsTask().execute();
    }

    private class WriteETagsTask extends AsyncTask<Void, Void, Void> {
        public WriteETagsTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ElectronicWriterActivity.this);
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
            dismiss();
            mCompleteOrder.setVisibility(View.VISIBLE);
            mWritePorts.setVisibility(View.GONE);
            Toast.makeText(ElectronicWriterActivity.this,"写入完成", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mCompleteOrder.setVisibility(View.GONE);
            mWritePorts.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
