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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.InspectItem;
import com.yida.handset.entity.InspectOrder;
import com.yida.handset.entity.InspectResultEntity;
import com.yida.handset.entity.InspectUploadEntity;
import com.yida.handset.entity.ResourceData;
import com.yida.handset.entity.ResourceVo;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.sqlite.InspectDao;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.WorkOrderDao;
import com.yida.handset.util.Cmd;
import com.yida.handset.util.UDPUtil;
import com.yida.handset.workorder.WorkOrderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InspectOrderActivity extends AppCompatActivity implements View.OnClickListener {

    public static InspectOrder workOrder;

    private static final String TAG_ACCEPT_ORDER = "accept_inspect_order";
    private static final String TAG_REJECT_ORDER = "reject_inspect_order";
    private static final String TAG_COMPLETE_ORDER = "complete_inspect_order";
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

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
    @Bind(R.id.inspect_port_count)
    TextView mPortCount;
    @Bind(R.id.already_inspected)
    TextView mAlreadyInspected;
    @Bind(R.id.complete_order)
    Button mCompleteOrder;
    @Bind(R.id.reject_order)
    Button mRejectOrder;
    @Bind(R.id.accept_order)
    Button mAcceptOrder;
    @Bind(R.id.inspect_device)
    Button mInspectDevice;

    private ProgressDialog pd;
    private User user;
    private int workId;
    private boolean isStatusChanged;
    private WorkOrderDao mWorkOrderDao;
    private InspectUploadEntity inspectResult;
    private InspectDao inspectDao;
    private UDPUtil udpUtil = new UDPUtil();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspect_order);
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
        inspectDao = new InspectDao(this);


        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>() {
        }.getType());
        if (workOrder != null && workOrder.getDevices().size() > 0) {
            InspectItem device = workOrder.getDevices().get(0);
            mDeviceName.setText("设备名: " + (device.getDeviceName() == null ? "" : device.getDeviceName()));
            mDeviceType.setText("设备类型: " + (device.getDeviceType() == null ? "" : device.getDeviceType()));
            mHardwareVersion.setText("硬件版本: " + (device.getDeviceHardwareVersion() == null ? "" : device.getDeviceHardwareVersion()));
            mSoftwareVersion.setText("软件版本: " + (device.getDeviceSoftwareVersion() == null ? "" : device.getDeviceSoftwareVersion()));
            mPortCount.setText("待巡检端口数: " + (device.getResourceData() == null ? 0 : device.getResourceData().size()));
        }

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            workId = extraIntent.getIntExtra(WorkOrderFragment.TAG_ID, 0);
            InspectResultEntity inspectResultEntity = inspectDao.query(workId);
            if (inspectResultEntity != null) {
                try {
                    inspectResult = gson.fromJson(inspectResultEntity.getData(), new TypeToken<InspectUploadEntity>() {
                    }.getType());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String workStatus = extraIntent.getStringExtra(WorkOrderFragment.TAG_ORDER_STATUS);
            mOrderId.setText("工单ID : " + workId);
            mOrderStatus.setText("工单状态 : " + workStatus);
            mOrderSite.setText("地址 : " + (extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE) == null
                    ? "" : extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE)));
            mOrderRemark.setText("备注：这是一个巡检工单");
            if (workStatus != null && !"".equals(workStatus)) {
                if (workStatus.equals(WorkOrderFragment.STATUS_ACCEPTED)) {
                    if (inspectResult != null && inspectResult.getResult().equals("true")) {
                        mCompleteOrder.setVisibility(View.VISIBLE);
                        mRejectOrder.setVisibility(View.GONE);
                        mAcceptOrder.setVisibility(View.GONE);
                        mInspectDevice.setVisibility(View.VISIBLE);
                        mAlreadyInspected.setVisibility(View.VISIBLE);
                    } else {
                        mCompleteOrder.setVisibility(View.GONE);
                        mRejectOrder.setVisibility(View.GONE);
                        mAcceptOrder.setVisibility(View.GONE);
                        mInspectDevice.setVisibility(View.VISIBLE);
                    }
                } else if (workStatus.equals(WorkOrderFragment.STATUS_COMPLETED)
                        || workStatus.equals(WorkOrderFragment.STATUS_NO_PUBLISHED)) {
                    mCompleteOrder.setVisibility(View.GONE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                    mInspectDevice.setVisibility(View.GONE);
                }
            }
        }

        mCompleteOrder.setOnClickListener(this);
        mRejectOrder.setOnClickListener(this);
        mAcceptOrder.setOnClickListener(this);
        mInspectDevice.setOnClickListener(this);
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
            case R.id.inspect_device:
                inspect();
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

    public void completeOrder() {
        if (inspectResult == null) {
            Toast.makeText(InspectOrderActivity.this, "请先巡检！", Toast.LENGTH_SHORT).show();
            mCompleteOrder.setVisibility(View.GONE);
            mInspectDevice.setVisibility(View.VISIBLE);
            return;
        }
        pd = new ProgressDialog(this);
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

        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.COMPLETE_INSPECT_ORDER;
        LogWrapper.d(mUrl);
        Gson gson = new Gson();

        String uploadStr = gson.toJson(inspectResult);
        JSONObject obj = null;
        try {
            obj = new JSONObject(uploadStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest uploadInspectResult = new JsonObjectRequest(Request.Method.POST, mUrl,  obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                LogWrapper.d(response.toString());
                try {
                    if (ResultVo.CODE_SUCCESS.equals(response.getString("code"))) {
                        ContentValues values = new ContentValues();
                        values.put(TableWorkOrder.ORDER_STATUS, WorkOrderFragment.orderStatus.get(WorkOrderFragment.STATUS_COMPLETED));

                        String where = TableWorkOrder.WORKID + "='" + workId + "'";
                        int resultCodeDB = mWorkOrderDao.update(values, where);
                        if (resultCodeDB > 0) {
                            mRejectOrder.setVisibility(View.GONE);
                            mAcceptOrder.setVisibility(View.GONE);
                            mCompleteOrder.setVisibility(View.GONE);
                            mInspectDevice.setVisibility(View.GONE);
                            mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_COMPLETED);
                            isStatusChanged = true;
                            Toast.makeText(InspectOrderActivity.this, R.string.complete_dialog_text_hint_result, Toast.LENGTH_SHORT).show();
                        }
                    } else if (ResultVo.CODE_FAILURE.equals(response.getString("code"))) {
                        Toast.makeText(InspectOrderActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismiss();
                error.printStackTrace();
            }
        });
        uploadInspectResult.setTag(TAG_COMPLETE_ORDER);

        RequestQueueSingleton.getInstance(this).addToRequestQueue(uploadInspectResult);
    }

    public void inspect() {
        new InspectTask().execute();
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
                        mInspectDevice.setVisibility(View.VISIBLE);
                        mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_ACCEPTED);
                        isStatusChanged = true;
                    }

                } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(InspectOrderActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(InspectOrderActivity.this, R.string.reject_dialog_text_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                pd = new ProgressDialog(InspectOrderActivity.this);
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
                                mInspectDevice.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_NO_PUBLISHED);
                                isStatusChanged = true;
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(InspectOrderActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(InspectOrderActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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

    private class InspectTask extends AsyncTask<Void, Void, Void> {
        public InspectTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(InspectOrderActivity.this);
            pd.setMessage(getString(R.string.loading));
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dismiss();
                    cancel(true);
                    udpUtil.close();
                }
            });
            pd.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dismiss();
            mCompleteOrder.setVisibility(View.VISIBLE);
            mInspectDevice.setVisibility(View.VISIBLE);
            mAlreadyInspected.setVisibility(View.VISIBLE);
            Toast.makeText(InspectOrderActivity.this, "完成巡检", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mCompleteOrder.setVisibility(View.GONE);
            mInspectDevice.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            inspectResult = new InspectUploadEntity();
            inspectResult.setWorkOrderId(workOrder.getWorkOrderId());
            inspectResult.setInspectTime(df.format(new Date()));
            List<InspectItem> devices = new ArrayList<>();
            for (InspectItem item : workOrder.getDevices()) {
                //在这里做巡检任务
                try {
                    byte[] resultNameLength = new byte[103];
                    byte[] resultDeviceName = udpUtil.send(resultNameLength, Cmd.CMD1101);
                    String efName = new String(Arrays.copyOfRange(resultDeviceName, 20, 100)).trim();
                    item.setDeviceName(efName);
                    resultNameLength = new byte[56];
                    byte[] resultTypeId = udpUtil.send(resultNameLength, Cmd.CMD1102);
                    String efOID = new String(Arrays.copyOfRange(resultTypeId, 20, 23)).trim();
                    String efType = new String(Arrays.copyOfRange(resultTypeId, 23, 53)).trim();
                    item.setDeviceId(Integer.parseInt(efOID));
                    item.setDeviceType(efType);
                    ResourceData portItem;
                    for (ResourceData port : item.getResourceData()) {
                        portItem = new ResourceData();
                        byte[] cmd = constructCMD1105(port);
                        byte[] result = udpUtil.send(new byte[53], cmd);
                        if (result.length > 20 && result[19] == 0x00) {

                        }
                    }
                    devices.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            inspectResult.setDevices(devices);
            inspectResult.setToken(user.getToken());
            if (devices.size() > 0) {
                inspectResult.setResult("true");
            } else {
                inspectResult.setResult("false");
            }
            Gson gson = new Gson();
            InspectResultEntity inspectResultEntity = new InspectResultEntity();
            inspectResultEntity.setWorkId(workId);
            inspectResultEntity.setData(gson.toJson(inspectResult));
            inspectDao.insert(inspectResultEntity);
            return null;
        }
    }

    public byte[] constructCMD1105(ResourceData port) {
        byte[] cmd1105 = new byte[36];
        cmd1105[0] = 0x7e;
        cmd1105[1] = 0x10;
        cmd1105[2] = 0x00;
        for (int i = 3; i < 17; i ++) {
            cmd1105[i] = 0x00;
        }
        cmd1105[17] = 0x11;
        cmd1105[18] = 0x05;
        cmd1105[19] = (byte) 0xff;
        try {
            int frameNo = Integer.parseInt(port.getFrameNo());
            cmd1105[20] = (byte) frameNo;
            int boardNo = Integer.parseInt(port.getBoardNo());
            cmd1105[21] = (byte) boardNo;
            int portNo = Integer.parseInt(port.getPortNo());
            cmd1105[22] = (byte) portNo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmd1105[34] = 0x00;
        cmd1105[35] = 0x7e;
        return cmd1105;
    }

}
