package com.yida.handset;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yida.handset.entity.ContainerVo;
import com.yida.handset.entity.FiberboxVo;
import com.yida.handset.entity.FrameVo;
import com.yida.handset.entity.NetUnitVo;
import com.yida.handset.entity.PortVo;
import com.yida.handset.entity.ResourceVo;
import com.yida.handset.entity.ResultVo;
import com.yida.handset.entity.User;
import com.yida.handset.entity.WorkList;
import com.yida.handset.slide.ExitAction;
import com.yida.handset.sqlite.WorkOrderDao;
import com.yida.handset.slide.AboutAction;
import com.yida.handset.slide.UpdateAction;
import com.yida.handset.slide.UpdatePwdAction;
import com.yida.handset.sqlite.DatabaseHelper;
import com.yida.handset.sqlite.Fiberbox;
import com.yida.handset.sqlite.NetUnitTask;
import com.yida.handset.sqlite.PortTask;
import com.yida.handset.sqlite.TableContainer;
import com.yida.handset.sqlite.TableFrame;
import com.yida.handset.sqlite.TableNetUnit;
import com.yida.handset.sqlite.TablePort;
import com.yida.handset.sqlite.TableWorkOrder;
import com.yida.handset.sqlite.TaskManager;
import com.yida.handset.workorder.WorkOrderFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HahaActivity extends AppCompatActivity implements View.OnClickListener{

    private static final ArrayList<ActionWrapper> mSlideActions = new ArrayList<>();
    static {
        mSlideActions.add(new ActionWrapper(0, "软件升级", new UpdateAction()));
        mSlideActions.add(new ActionWrapper(1, "修改密码", new UpdatePwdAction()));
        mSlideActions.add(new ActionWrapper(2, "关于", new AboutAction()));
        mSlideActions.add(new ActionWrapper(3, "退出登陆", new ExitAction()));
    }

    private static final String RESOURCE_TAG = "tag_resource";
    private static final String WORKORDER_TAG = "tag_workorder";

    @Bind(R.id.drawer_list)
    ListView mDrawerList;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.drawer_title)
    TextView mDrawerTitle;
    @Bind(R.id.drawer_phone)
    TextView mDrawerPhone;
    @Bind(R.id.drawer_company)
    TextView mDrawerCompany;
    @Bind(R.id.drawer)
    LinearLayout mDrawer;
    @Bind(R.id.main_action_resource)
    LinearLayout mActionResource;
    @Bind(R.id.main_action_log)
    LinearLayout mActionLog;
    @Bind(R.id.main_action_work_order)
    LinearLayout mActionWorkOder;
    @Bind(R.id.main_action_sync)
    LinearLayout mActionSync;

    private RequestQueueSingleton mRequestQueue;
    private ProgressDialog pd;
    private DatabaseHelper helper;
    private TaskManager mTaskManager;
    private WorkOrderDao mWorkOrderDao;

    private ActionBarDrawerToggle mDrawerToggle;
    private User user;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null || "".equals(intent.getAction())) {
                return;
            }
            switch (intent.getAction()) {
                case UpdatePwdActivity.PWD_UPDATED:
                    finish();
                    break;
                case PortTask.TASK_FINISHED:
                    if (mTaskManager.isFirst()) {
                        dismiss();
                        Intent intentResource = new Intent(HahaActivity.this, ResourceActivity.class);
                        startActivity(intentResource);
                        mTaskManager.setTaskStatus(false);
                        mTaskManager.setFirst(false);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haha);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        Gson gson = new Gson();
        user = gson.fromJson(userStr, new TypeToken<User>(){}.getType());
        mDrawerTitle.setText(user.getUsername());
        if (user.getPhone() == null || "".equals(user.getPhone())) {
            mDrawerPhone.setVisibility(View.GONE);
        } else {
            mDrawerPhone.setText(user.getPhone());
        }
        if (user.getCompany() == null || "".equals(user.getCompany())) {
            mDrawerCompany.setVisibility(View.GONE);
        } else {
            mDrawerCompany.setText(user.getCompany());
        }

        mDrawerList.setAdapter(new DrawerAdapter(this, mSlideActions));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mDrawerLayout.isDrawerOpen(mDrawer)) {
                    mDrawerLayout.closeDrawers();
                }
                mSlideActions.get(i).getAction().act(HahaActivity.this);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mActionResource.setOnClickListener(this);
        mActionLog.setOnClickListener(this);
        mActionWorkOder.setOnClickListener(this);
        mActionSync.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UpdatePwdActivity.PWD_UPDATED);
        filter.addAction(PortTask.TASK_FINISHED);
        registerReceiver(mReceiver, filter);

        if (helper == null) {
            helper = DatabaseHelper.getInstance(getApplicationContext());
        }
        mTaskManager = TaskManager.getInstance(getApplicationContext());
        mWorkOrderDao = new WorkOrderDao(this);

        new VersionTask(this, VersionTask.TAG_AUTO).execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_action_resource:
                startResource();
                break;
            case R.id.main_action_log:
                Intent intent1 = new Intent(this, LogActivity.class);
                startActivity(intent1);
                break;
            case R.id.main_action_work_order:
                startWorkOrder();
                break;
            case R.id.main_action_sync:
                refresh();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void startWorkOrder() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(WORKORDER_TAG);
            }
        });
        pd.show();

        if (mRequestQueue == null) {
            mRequestQueue = RequestQueueSingleton.getInstance(this);
        }

        SharedPreferences preferences = getSharedPreferences(LoginActivity.REFERENCE_NAME, Context.MODE_PRIVATE);
        String userStr = preferences.getString(LoginActivity.REFERENCE_USER, "");
        JSONObject user = null;
        String params = "?token=";
        try {
            user = new JSONObject(userStr);
            params += user.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
            dismiss();
        }

        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_WORKORDER + params;
        StringRequest request = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogWrapper.d(response);
                Gson gson = new Gson();
                WorkList result = null;
                try {
                    result = gson.fromJson(response, new TypeToken<WorkList>() {
                        }.getType());
                } catch (Exception e) {
                    dismiss();
                    e.printStackTrace();
                }
                if (result == null) {
                    dismiss();
                    return;
                }
                if(ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    mWorkOrderDao.insert(result.getWorkList());
                    WorkOrderFragment.orders = mWorkOrderDao.queryAll(TableWorkOrder.USERNAME + "=?", new String[]{HahaActivity.this.user.getUsername()}, null, null, null);
                    dismiss();
                    Intent intent = new Intent(HahaActivity.this, WorkOrderActivity.class);
                    startActivity(intent);
                } else if (ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(HahaActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dismiss();
            }
        });
        request.setTag(WORKORDER_TAG);
        mRequestQueue.addToRequestQueue(request);
    }

    private void startResource() {

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(RESOURCE_TAG);
            }
        });
        pd.show();

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + TableNetUnit.TABLE_NAME, null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                if (count <= 0) {
                    dismiss();
                    Toast.makeText(HahaActivity.this, R.string.please_sync_data, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        if (cursor != null) {
            try{
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        db.close();

        mTaskManager.setTaskStatus(true);
        mTaskManager.setFirst(true);
        new NetUnitTask(DatabaseHelper.getInstance(this), mTaskManager).execute();
    }

    private void refresh() {
        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
                RequestQueueSingleton.getInstance(getApplicationContext()).getRequestQueue().cancelAll(RESOURCE_TAG);
            }
        });
        pd.show();

        if (mRequestQueue == null) {
            mRequestQueue = RequestQueueSingleton.getInstance(this);
        }
        String mUrl = Constants.HTTP_HEAD + Constants.IP + ":" + Constants.PORT + Constants.SYSTEM_NAME + Constants.GET_RESOURCES;
        StringRequest request = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>() {
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
                final ResourceVo result2 = result;
                if (ResultVo.CODE_SUCCESS.equals(result.getCode())) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clearResource();
                            insertResource(result2);
                            dismiss();
                        }
                    }).start();
                } else if(ResultVo.CODE_FAILURE.equals(result.getCode())) {
                    Toast.makeText(HahaActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                dismiss();
            }
        });
        request.setTag(RESOURCE_TAG);
        mRequestQueue.addToRequestQueue(request);
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    private void clearResource() {
        helper.lock();
        if (helper == null) {
            helper = DatabaseHelper.getInstance(this);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(TableNetUnit.TABLE_NAME, null, null);
        db.delete(TableFrame.TABLE_NAME, null, null);
        db.delete(TableContainer.TABLE_NAME, null, null);
        db.delete(Fiberbox.TABLE_NAME, null, null);
        db.delete(TablePort.TABLE_NAME, null, null);
        db.close();
        helper.unlock();
    }

    private void insertResource(ResourceVo resources) {
        helper.lock();
        if (helper == null) {
            helper = DatabaseHelper.getInstance(this);
        }
        SQLiteDatabase db = helper.getWritableDatabase();
        List<NetUnitVo> netUnits = resources.getNetUnits();
        List<FrameVo> frames = resources.getFrames();
        List<ContainerVo> containers = resources.getContainers();
        List<FiberboxVo> fiberboxes = resources.getFiberboxes();
        List<PortVo> ports = resources.getPorts();
        insertNetUnits(db, netUnits);
        insertFrame(db, frames);
        insertContainer(db, containers);
        insertFiberbox(db, fiberboxes);
        insertPort(db, ports);
        db.close();
        helper.unlock();
    }

    private void insertNetUnits(SQLiteDatabase db, List<NetUnitVo> data) {
        ContentValues values;
        for (NetUnitVo netUnit : data) {
            values = new ContentValues();
            values.put(TableNetUnit.ADDRESS, netUnit.getAddress());
            values.put(TableNetUnit.CITY, netUnit.getCity());
            values.put(TableNetUnit.CREATE_TIME, netUnit.getCreateTime());
            values.put(TableNetUnit.CREATEBY, netUnit.getCreateBy());
            values.put(TableNetUnit.IP, netUnit.getIp());
            values.put(TableNetUnit.NAME, netUnit.getName());
            values.put(TableNetUnit.NETUNITID, netUnit.getNetunitId());
            values.put(TableNetUnit.PROVINCE, netUnit.getProvince());
            values.put(TableNetUnit.REMARK, netUnit.getRemark());
            values.put(TableNetUnit.STATUS, netUnit.getStatus());
            values.put(TableNetUnit.TYPE, netUnit.getType());
            values.put(TableNetUnit.UPDATE_TIME, netUnit.getUpdateTime());
            values.put(TableNetUnit.UPDATEBY, netUnit.getUpdateBy());
            values.put(TableNetUnit.URL, netUnit.getUrl());
            values.put(TableNetUnit.USERID, netUnit.getUserId());
            db.insert(TableNetUnit.TABLE_NAME, null, values);
        }
    }

    private void insertFrame(SQLiteDatabase db, List<FrameVo> data) {
        ContentValues values;
        for (FrameVo frame : data) {
            values = new ContentValues();
            values.put(TableFrame.CODE, frame.getCode());
            values.put(TableFrame.CREATEBY, frame.getCreateBy());
            values.put(TableFrame.CREATETIME, frame.getCreateTime());
            values.put(TableFrame.EFUID, frame.getEfUid());
            values.put(TableFrame.FP, frame.getFp());
            values.put(TableFrame.FRAME_ID, frame.getFrameId());
            values.put(TableFrame.MANUFACTURER, frame.getManufacturer());
            values.put(TableFrame.MODEL, frame.getModel());
            values.put(TableFrame.NETUNITID, frame.getNetunitId());
            values.put(TableFrame.POSITION, frame.getPosition());
            values.put(TableFrame.REMARK, frame.getRemark());
            values.put(TableFrame.TYPE, frame.getType());
            values.put(TableFrame.UPDATEBY, frame.getUpdateBy());
            values.put(TableFrame.UPDATETIME, frame.getUpdateTime());
            values.put(TableFrame.WORKSTATUS, frame.getWorkStatus());
            db.insert(TableFrame.TABLE_NAME, null, values);
        }
    }

    private void insertContainer(SQLiteDatabase db, List<ContainerVo> data) {
        ContentValues values;
        for (ContainerVo container : data) {
            values = new ContentValues();
            values.put(TableContainer.CODE, container.getCode());
            values.put(TableContainer.CONTAINERID, container.getContainerId());
            values.put(TableContainer.CREATEBY, container.getCreateBy());
            values.put(TableContainer.CREATETIME, container.getCreateTime());
            values.put(TableContainer.FRAMEID, container.getFrameId());
            values.put(TableContainer.REMARK, container.getRemark());
            values.put(TableContainer.TYPE, container.getType());
            values.put(TableContainer.UPDATEBY, container.getUpdateBy());
            values.put(TableContainer.UPDATETIME, container.getUpdateTime());
            db.insert(TableContainer.TABLE_NAME, null, values);
        }
    }

    private void insertFiberbox(SQLiteDatabase db, List<FiberboxVo> data) {
        ContentValues values;
        for(FiberboxVo fiberbox : data) {
            values = new ContentValues();
            values.put(Fiberbox.CODE, fiberbox.getCode());
            values.put(Fiberbox.CONTAINERID, fiberbox.getContainerId());
            values.put(Fiberbox.CREATEBY, fiberbox.getCreateBy());
            values.put(Fiberbox.CREATETIME, fiberbox.getCreateTime());
            values.put(Fiberbox.ERRINFO, fiberbox.getErrInfo());
            values.put(Fiberbox.FIBERBOXID, fiberbox.getFiberboxId());
            values.put(Fiberbox.HOLENUM, fiberbox.getHoleNum());
            values.put(Fiberbox.MANUFACTURE, fiberbox.getManufacture());
            values.put(Fiberbox.MSID, fiberbox.getMsId());
            values.put(Fiberbox.CONTAINERID, fiberbox.getContainerId());
            values.put(Fiberbox.PRODUCTDATE, fiberbox.getProductDate());
            values.put(Fiberbox.REMARK, fiberbox.getRemark());
            values.put(Fiberbox.TYPE, fiberbox.getType());
            values.put(Fiberbox.UPDATEBY, fiberbox.getUpdateBy());
            values.put(Fiberbox.UPDATETIME, fiberbox.getUpdateTime());
            values.put(Fiberbox.WORKSTATUS, fiberbox.getWorkStatus());
            db.insert(Fiberbox.TABLE_NAME, null, values);
        }
    }

    private void insertPort(SQLiteDatabase db, List<PortVo> data) {
        ContentValues values;
        for (PortVo port : data) {
            values = new ContentValues();
            values.put(TablePort.CREATEBY, port.getCreateBy());
            values.put(TablePort.CREATETIME, port.getCreateTime());
            values.put(TablePort.ETAG, port.getEtag());
            values.put(TablePort.FIBERBOXID, port.getFiberboxId());
            values.put(TablePort.INDICATOR, port.getIndicator());
            values.put(TablePort.PORTID, port.getPortId());
            values.put(TablePort.REMARK, port.getRemark());
            values.put(TablePort.SEQUENCE, port.getSequence());
            values.put(TablePort.UPDATEBY, port.getUpdateBy());
            values.put(TablePort.UPDATETIME, port.getUpdateTime());
            db.insert(TablePort.TABLE_NAME, null, values);
        }
    }
}
