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
import android.support.v7.app.ActionBarActivity;
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
import com.yida.handset.entity.ConfigurationEntity;
import com.yida.handset.entity.ResourceVo;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.WorkOrderDao;
import com.yida.handset.util.UDPUtil;
import com.yida.handset.workorder.WorkOrderFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {

    public static ConfigurationEntity configurationEntity;

    private static final String TAG_ACCEPT_ORDER = "accept_configuration_order";
    private static final String TAG_REJECT_ORDER = "reject_configuration_order";
    private static final String TAG_COMPLETE_ORDER = "complete_configuration_order";
    static {
        configurationEntity = new ConfigurationEntity();
        configurationEntity.setDeviceName("设备");
        configurationEntity.setDeviceID("1");
        configurationEntity.setDeviceIPAddr("192.168.5.");
        configurationEntity.setDeviceIPAddrMask("4B:3C:6A:5F");
        configurationEntity.setDeviceIPGateway("192.110.123.2");
        configurationEntity.setDeviceType("SBN-");
        configurationEntity.setNMSIPAddr("192.122.6.3434343434");
        configurationEntity.setNMSTrapEnable(true);
        configurationEntity.setNMSTrapPort(String.valueOf(2));
        configurationEntity.setNMSTrapSecurityName("安全名");
        configurationEntity.setSNMPAuthority("SNMP授权");
        configurationEntity.setSNMPGroupName("组织");
        configurationEntity.setSNMPViewEnable(false);
        configurationEntity.setSNMPViewName("视图");
    }

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.order_id)
    TextView mOrderId;
    @Bind(R.id.order_status)
    TextView mOrderStatus;
    @Bind(R.id.complete_order)
    Button mCompleteOrder;
    @Bind(R.id.reject_order)
    Button mRejectOrder;
    @Bind(R.id.accept_order)
    Button mAcceptOrder;
//    @Bind(R.id.order_configuration_list)
//    ListView mListConfiguration;

    @Bind(R.id.device_name)
    TextView deviceName;
    @Bind(R.id.device_id)
    TextView deviceID;
    @Bind(R.id.device_type)
    TextView deviceType;
    @Bind(R.id.device_ip)
    TextView deviceIPAddr;
    @Bind(R.id.device_mac)
    TextView deviceIPAddrMask;
    @Bind(R.id.device_gateway)
    TextView deviceIPGateway;
    @Bind(R.id.NMSIPAddr)
    TextView NMSIPAddr;
    @Bind(R.id.NMSTrapPort)
    TextView NMSTrapPort;
    @Bind(R.id.NMSTrapEnable)
    TextView NMSTrapEnable;
    @Bind(R.id.NMSTrapSecurityName)
    TextView NMSTrapSecurityName;
    @Bind(R.id.SNMPGroupName)
    TextView SNMPGroupName;
    @Bind(R.id.SNMPAuthority)
    TextView SNMPAuthority;
    @Bind(R.id.SNMPViewEnable)
    TextView SNMPViewEnable;
    @Bind(R.id.SNMPViewName)
    TextView SNMPViewName;
    @Bind(R.id.btn_write_configuration)
    Button mBtnWriteConfiguration;

    private ProgressDialog pd;
    private User user;
    private int workId;
    private boolean isStatusChanged;
    private WorkOrderDao mWorkOrderDao;
    private UDPUtil udpUtil;
//    private ConfigurationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
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
        udpUtil = new UDPUtil();

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
//            mOrderSite.setText("地址 : " + (extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE) == null
//                    ? "" : extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE)));
//            mOrderRemark.setText("备注：这是一个巡检工单");
            if (workStatus != null && !"".equals(workStatus)) {
                if (workStatus.equals(WorkOrderFragment.STATUS_ACCEPTED)) {
                    mCompleteOrder.setVisibility(View.VISIBLE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                    mBtnWriteConfiguration.setVisibility(View.VISIBLE);
                } else if (workStatus.equals(WorkOrderFragment.STATUS_COMPLETED)
                        || workStatus.equals(WorkOrderFragment.STATUS_NO_PUBLISHED)) {
                    mCompleteOrder.setVisibility(View.GONE);
                    mRejectOrder.setVisibility(View.GONE);
                    mAcceptOrder.setVisibility(View.GONE);
                }
            }
        }

        setConfigurationView(configurationEntity);

        mCompleteOrder.setOnClickListener(this);
        mRejectOrder.setOnClickListener(this);
        mAcceptOrder.setOnClickListener(this);
        mBtnWriteConfiguration.setOnClickListener(this);
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
            case R.id.btn_write_configuration:
                writeConfiguration();
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

    private void writeConfiguration() {
        new WriteConfigurationTask().execute();
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
                        mBtnWriteConfiguration.setVisibility(View.VISIBLE);
                        mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_ACCEPTED);
                        isStatusChanged = true;
                    }

                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(ConfigurationActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ConfigurationActivity.this, R.string.reject_dialog_text_hint, Toast.LENGTH_SHORT).show();
                    return;
                }

                pd = new ProgressDialog(ConfigurationActivity.this);
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
                                mBtnWriteConfiguration.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_NO_PUBLISHED);
                                isStatusChanged = true;
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ConfigurationActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ConfigurationActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
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
                pd = new ProgressDialog(ConfigurationActivity.this);
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
                                mBtnWriteConfiguration.setVisibility(View.GONE);
                                mOrderStatus.setText("工单状态 : " + WorkOrderFragment.STATUS_COMPLETED);
                                isStatusChanged = true;
                                Toast.makeText(ConfigurationActivity.this, R.string.complete_dialog_text_hint_result, Toast.LENGTH_SHORT).show();
                            }
                        } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                            Toast.makeText(ConfigurationActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

//    private class ConfigurationAdapter extends BaseAdapter {
//
//        private List<ConfigurationEntity> data;
//        private Context context;
//        private LayoutInflater inflater;
//
//        public ConfigurationAdapter(Context context, List<ConfigurationEntity> data) {
//            this.data = data;
//            this.context = context;
//            this.inflater = LayoutInflater.from(this.context);
//        }
//        @Override
//        public int getCount() {
//            if (this.data != null) {
//                return this.data.size();
//            }
//            return 0;
//        }
//
//        @Override
//        public Object getItem(int i) {
//            if (this.data != null) {
//                return this.data.get(i);
//            }
//            return null;
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                viewHolder = new ViewHolder();
//                convertView = this.inflater.inflate(R.layout.item_configuration_list, null);
//
//                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
//                viewHolder.deviceID = (TextView) convertView.findViewById(R.id.device_id);
//                viewHolder.deviceType = (TextView) convertView.findViewById(R.id.device_type);
//                viewHolder.deviceIPAddr = (TextView) convertView.findViewById(R.id.device_ip);
//                viewHolder.deviceIPAddrMask = (TextView) convertView.findViewById(R.id.device_mac);
//                viewHolder.deviceIPGateway = (TextView) convertView.findViewById(R.id.device_gateway);
//                viewHolder.NMSIPAddr = (TextView) convertView.findViewById(R.id.NMSIPAddr);
//                viewHolder.NMSTrapPort = (TextView) convertView.findViewById(R.id.NMSTrapPort);
//                viewHolder.NMSTrapEnable = (TextView) convertView.findViewById(R.id.NMSTrapEnable);
//                viewHolder.NMSTrapSecurityName = (TextView) convertView.findViewById(R.id.NMSTrapSecurityName);
//                viewHolder.SNMPGroupName = (TextView) convertView.findViewById(R.id.SNMPGroupName);
//                viewHolder.SNMPAuthority = (TextView) convertView.findViewById(R.id.SNMPAuthority);
//                viewHolder.SNMPViewEnable = (TextView) convertView.findViewById(R.id.SNMPViewEnable);
//                viewHolder.SNMPViewName = (TextView) convertView.findViewById(R.id.SNMPViewName);
//
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            ConfigurationEntity entity = this.data.get(position);
//            viewHolder.deviceName.setText(getStringNotNull(entity.getDeviceName()));
//            viewHolder.deviceID.setText("设备ID: " + getStringNotNull(entity.getDeviceID()));
//            viewHolder.deviceType.setText("设备类型: " + getStringNotNull(entity.getDeviceType()));
//            viewHolder.deviceIPAddr.setText("设备IP: " + getStringNotNull(entity.getDeviceIPAddr()));
//            viewHolder.deviceIPAddrMask.setText("设备Mac: " + getStringNotNull(entity.getDeviceIPAddrMask()));
//            viewHolder.deviceIPGateway.setText("设备网关: " + getStringNotNull(entity.getDeviceIPGateway()));
//            viewHolder.NMSIPAddr.setText("网管IP: " + getStringNotNull(entity.getNMSIPAddr()));
//            viewHolder.NMSTrapPort.setText("网管Trap端口: " + getStringNotNull(entity.getNMSTrapPort()));
//            viewHolder.NMSTrapEnable.setText("网管Trap使能: " + (entity.isNMSTrapEnable() ? "使能" : "不使能"));
//            viewHolder.NMSTrapSecurityName.setText("网管Trap安全名: " + getStringNotNull(entity.getNMSTrapSecurityName()));
//            viewHolder.SNMPGroupName.setText("SNMP名: " + getStringNotNull(entity.getSNMPGroupName()));
//            viewHolder.SNMPAuthority.setText("SNMP权限: " + getStringNotNull(entity.getSNMPAuthority()));
//            viewHolder.SNMPViewEnable.setText("SNMP启用: " + (entity.isSNMPViewEnable() ? "配置" : "不配置"));
//            viewHolder.SNMPViewName.setText("SNMP名: " + getStringNotNull(entity.getSNMPViewName()));
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView deviceName;
//            TextView deviceID;
//            TextView deviceType;
//            TextView deviceIPAddr;
//            TextView deviceIPAddrMask;
//            TextView deviceIPGateway;
//            TextView NMSIPAddr;
//            TextView NMSTrapPort;
//            TextView NMSTrapEnable;
//            TextView NMSTrapSecurityName;
//            TextView SNMPGroupName;
//            TextView SNMPAuthority;
//            TextView SNMPViewEnable;
//            TextView SNMPViewName;
//        }
//
//    }

    private void setConfigurationView(ConfigurationEntity entity) {
        deviceName.setText(getStringNotNull(entity.getDeviceName()));
        deviceID.setText("设备ID: " + getStringNotNull(entity.getDeviceID()));
        deviceType.setText("设备类型: " + getStringNotNull(entity.getDeviceType()));
        deviceIPAddr.setText("设备IP: " + getStringNotNull(entity.getDeviceIPAddr()));
        deviceIPAddrMask.setText("设备Mac: " + getStringNotNull(entity.getDeviceIPAddrMask()));
        deviceIPGateway.setText("设备网关: " + getStringNotNull(entity.getDeviceIPGateway()));
        NMSIPAddr.setText("网管IP: " + getStringNotNull(entity.getNMSIPAddr()));
        NMSTrapPort.setText("网管Trap端口: " + getStringNotNull(entity.getNMSTrapPort()));
        NMSTrapEnable.setText("网管Trap使能: " + (entity.isNMSTrapEnable() ? "使能" : "不使能"));
        NMSTrapSecurityName.setText("网管Trap安全名: " + getStringNotNull(entity.getNMSTrapSecurityName()));
        SNMPGroupName.setText("SNMP名: " + getStringNotNull(entity.getSNMPGroupName()));
        SNMPAuthority.setText("SNMP权限: " + getStringNotNull(entity.getSNMPAuthority()));
        SNMPViewEnable.setText("SNMP启用: " + (entity.isSNMPViewEnable() ? "配置" : "不配置"));
        SNMPViewName.setText("SNMP名: " + getStringNotNull(entity.getSNMPViewName()));
    }

    private String getStringNotNull(String str) {
        return str == null ? "" : str;
    }

    private class WriteConfigurationTask extends AsyncTask<Void, Void, String> {
        public WriteConfigurationTask() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(ConfigurationActivity.this);
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
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if ("0".equals(aVoid)) {
                mBtnWriteConfiguration.setVisibility(View.GONE);
            }
            dismiss();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String resultStatus = "";
            byte[] configuration = constructCMD110B(configurationEntity);
            byte[] result = udpUtil.send(new byte[24], configuration);
            if (result.length == 24) {
                if (0 == (int) result[20]) {
                    resultStatus = "0";
                } else {
                    resultStatus = "1";
                }
            }
            return resultStatus;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            udpUtil.close();
        }
    }

    public byte[] constructCMD110B(ConfigurationEntity configurationEntity ) {
        byte[] cmd110b = new byte[740];
        cmd110b[0] = 0x7e;
        cmd110b[1] = 0x10;
        cmd110b[2] = 0x00;
        for (int i = 3; i < 17; i ++) {
            cmd110b[i] = 0x00;
        }
        cmd110b[17] = 0x11;
        cmd110b[18] = 0x0B;
        cmd110b[19] = (byte) 0xff;
        if (configurationEntity.getDeviceName() != null) {
            byte[] bDeviceName = configurationEntity.getDeviceName().getBytes();
            if (bDeviceName.length > 80) {
                Toast.makeText(this, "设备名称有错", Toast.LENGTH_SHORT).show();
                return new byte[0];
            }
            System.arraycopy(bDeviceName, 0, cmd110b, 20, bDeviceName.length);
        }
        if (configurationEntity.getDeviceID() != null) {
            byte[] bDeviceID = configurationEntity.getDeviceID().getBytes();
            if (bDeviceID.length > 3) {
                Toast.makeText(this, "设备ID有错", Toast.LENGTH_SHORT).show();
                return new byte[0];
            }
            System.arraycopy(bDeviceID, 0, cmd110b, 100, bDeviceID.length);
        }
        if (configurationEntity.getDeviceType() != null) {
            byte[] bDeviceType = configurationEntity.getDeviceType().getBytes();
            if (bDeviceType.length > 30) {
                Toast.makeText(this, "箱体标识有错", Toast.LENGTH_SHORT).show();
                return new byte[0];
            }
            System.arraycopy(bDeviceType, 0, cmd110b, 103, bDeviceType.length);
        }
        if (configurationEntity.getDeviceIPAddr() != null) {
            String[] IP = configurationEntity.getDeviceIPAddr().split("\\.");
            for (int i = 0; i < IP.length; i ++) {
                cmd110b[133 + i] = (byte) Integer.parseInt(IP[i]);
            }
        }
        if (configurationEntity.getDeviceIPAddrMask() != null) {
            String[] mask = configurationEntity.getDeviceIPAddrMask().split("\\.");
            for (int i = 0; i < mask.length; i ++) {
                cmd110b[137 + i] = (byte) Integer.parseInt(mask[i]);
            }
        }
        if (configurationEntity.getDeviceIPGateway() != null) {
            String[] gateway = configurationEntity.getDeviceIPGateway().split("\\.");
            for (int i = 0; i < gateway.length; i ++) {
                cmd110b[141 + i] = (byte) Integer.parseInt(gateway[i]);
            }
        }

        //这里组装SNMP信息


        byte[] location = "这是位置信息".getBytes();
        if(location.length > 128) {
            Toast.makeText(this, "位置信息有错。", Toast.LENGTH_SHORT).show();
            return new byte[0];
        }
        System.arraycopy(location, 0, cmd110b, 208, location.length);

        cmd110b[739] = 0x7e;
        return cmd110b;
    }

}
