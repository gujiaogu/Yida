package com.yida.handset;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.rey.material.widget.Spinner;
import com.yida.handset.entity.ContainerVo;
import com.yida.handset.entity.FiberboxVo;
import com.yida.handset.entity.FrameVo;
import com.yida.handset.entity.NetUnitVo;
import com.yida.handset.entity.PortVo;
import com.yida.handset.sqlite.DatabaseHelper;
import com.yida.handset.sqlite.PortTask;
import com.yida.handset.sqlite.TaskManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResourceActivity extends AppCompatActivity implements View.OnClickListener,
        Spinner.OnItemSelectedListener{

    private static final int TAG_NET_SELECTED = 4;
    private static final int TAG_FRAME_SELECTED = 3;
    private static final int TAG_CONTAINER_SELECTED = 2;
    private static final int TAG_FIBERBOX_SELECTED = 1;

    private static final int QR_Frame = 1;
    private static final int QR_Container = 2;
    private static final int QR_Fiberbox = 3;
    private static final int QR_Port = 4;
    private static int QR_TAG = 0;

    public static List<NetUnitVo> netUnits = new ArrayList<>();
    public static List<String> netUnitsSpinner = new ArrayList<>();
    public static List<FrameVo> frames = new ArrayList<>();
    public static List<String> framesSpinner = new ArrayList<>();
    public static List<ContainerVo> containers = new ArrayList<>();
    public static List<String> containersSpinner = new ArrayList<>();
    public static List<FiberboxVo> fiberboxes = new ArrayList<>();
    public static List<String> fiberboxesSpinner = new ArrayList<>();
    public static List<PortVo> ports = new ArrayList<>();
    public static List<String> portsSpinner = new ArrayList<>();

    public static final String TAG_STATION = "station";
    public static final String TAG_RACK = "rack";
    public static final String TAG_FRAME = "frame";
    public static final String TAG_DISK = "disk";
    public static final String TAG_INFO = "info";
    public static final String TAG_BLUETOOTH = "info_for_bluetooth";
    public static final int REQUEST_CODE = 1;

    private static final int QR_WIDTH = 600;
    private static final int QR_HEIGHT = 600;
    private static final int REQUEST_ENABLE_BLUETOOTH = 2;

    private ArrayAdapter<String> stationAdapter;
    private ArrayAdapter<String> rackAdapter;
    private ArrayAdapter<String> frameAdapter;
    private ArrayAdapter<String> diskAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    @Bind(R.id.generate_code)
    RelativeLayout mGenerator;
    @Bind(R.id.check_detail)
    RelativeLayout mCheckDetail;
    @Bind(R.id.btn_warning)
    LinearLayout mBtnWarning;
    @Bind(R.id.img)
    ImageView mImg;
    @Bind(R.id.spinner_station)
    Spinner mSpinnerStation;
    @Bind(R.id.spinner_rack)
    Spinner mSpinnerRack;
    @Bind(R.id.spinner_frame)
    Spinner mSpinnerFrame;
    @Bind(R.id.spinner_disk)
    Spinner mSpinnerDisk;

    private DatabaseHelper helper;
    private TaskManager mTaskManager;
    private ProgressDialog pd;
    private int tag = 0;
    private JSONObject resultObj;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null || "".equals(intent.getAction())) {
                return;
            }
            switch (intent.getAction()) {
                case PortTask.TASK_FINISHED:
                    dismiss();
                    mTaskManager.setTaskStatus(false);
                    if (tag == TAG_NET_SELECTED) {
                        setRackAdapter();
                        setFrameAdapter();
                        setDiskAdapter();
                    }
                    if (tag == TAG_FRAME_SELECTED) {
                        setFrameAdapter();
                        setDiskAdapter();
                    }
                    if (tag == TAG_CONTAINER_SELECTED) {
                        setDiskAdapter();
                    }

                    if (QR_TAG == QR_Frame) {
                        if (resultObj != null) {
                            try {
                                String frame = resultObj.getString(TAG_RACK);
                                if (frame != null && !"".equals(frame)) {
                                    int index = framesSpinner.indexOf(frame);
                                    if (index != -1) {
                                        QR_TAG = QR_Container;
                                        mSpinnerRack.setSelection(index);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (QR_TAG == QR_Container) {
                        if (resultObj != null) {
                            try {
                                String container = resultObj.getString(TAG_FRAME);
                                if (container != null && !"".equals(container)) {
                                    int index = containersSpinner.indexOf(container);
                                    if (index != -1) {
                                        QR_TAG = QR_Fiberbox;
                                        mSpinnerFrame.setSelection(index);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (QR_TAG == QR_Fiberbox) {
                        if (resultObj != null) {
                            try {
                                String fiberbox = resultObj.getString(TAG_DISK);
                                if (fiberbox != null && !"".equals(fiberbox)) {
                                    int index = fiberboxesSpinner.indexOf(fiberbox);
                                    if (index != -1) {
                                        QR_TAG = QR_Port;
                                        mSpinnerDisk.setSelection(index);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

//    public static final int REQUEST_CODE = 1;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ButterKnife.bind(this);
        mGenerator.setOnClickListener(this);
        mBtnWarning.setOnClickListener(this);
        mCheckDetail.setOnClickListener(this);
        setStationAdapter();
        setRackAdapter();
        setFrameAdapter();
        setDiskAdapter();

        mSpinnerStation.setOnItemSelectedListener(this);
        mSpinnerRack.setOnItemSelectedListener(this);
        mSpinnerFrame.setOnItemSelectedListener(this);
        mSpinnerDisk.setOnItemSelectedListener(this);

        if (helper == null) {
            helper = DatabaseHelper.getInstance(this);
        }
        if (mTaskManager == null) {
            mTaskManager = TaskManager.getInstance(getApplicationContext());
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(PortTask.TASK_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.scanner) {
            Intent intent = new Intent(this, ScannerActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.generate_code:
                if (checkBluetooth()) {
                    String info = getInfo();
                    if ("".equals(info)) {
                        Toast.makeText(this, R.string.toast_bad_info, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startBluetoothActivity(info);
                }
                break;
            case R.id.btn_warning:
                break;
            case R.id.check_detail:
                detail();
                break;
            case R.id.spinner_station:

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        if (position < 0) {
            return;
        }
        switch (parent.getId()) {
            case R.id.spinner_station:
                selectedStation(position);
                break;
            case R.id.spinner_rack:
                selectedRack(position);
                break;
            case R.id.spinner_frame:
                selectedFrame(position);
                break;
            case R.id.spinner_disk:
                selectedDisk(position);
                break;
            default:
                break;
        }
    }

    private void setStationAdapter() {
        stationAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, netUnitsSpinner);
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerStation.setAdapter(stationAdapter);
    }

    private void setRackAdapter() {
        rackAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, framesSpinner);
        rackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (rackAdapter.getCount() <= 0) {
            mSpinnerRack.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]{""}));
        } else {
            mSpinnerRack.setAdapter(rackAdapter);
        }
    }

    private void setFrameAdapter() {
        frameAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, containersSpinner);
        frameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (frameAdapter.getCount() <= 0) {
            mSpinnerFrame.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]{""}));
        } else {
            mSpinnerFrame.setAdapter(frameAdapter);
        }
    }

    private void setDiskAdapter() {
        diskAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, fiberboxesSpinner);
        diskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (diskAdapter.getCount() <= 0) {
            mSpinnerDisk.setAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new String[]{""}));
        } else {
            mSpinnerDisk.setAdapter(diskAdapter);
        }
    }

    private void selectedStation(int position) {
        if(mTaskManager.isRunning()) {
            Toast.makeText(this, R.string.resource_edit_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
        pd.show();

        mTaskManager.setTaskStatus(true);
        tag = TAG_NET_SELECTED;
        mTaskManager.startFrameTask(helper, position);
    }

    private void selectedRack(int position) {
        if(mTaskManager.isRunning()) {
            Toast.makeText(this, R.string.resource_edit_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
        pd.show();

        mTaskManager.setTaskStatus(true);
        tag = TAG_FRAME_SELECTED;
        mTaskManager.startContainer(helper, position);
    }

    private void selectedFrame(int position) {
        if(mTaskManager.isRunning()) {
            Toast.makeText(this, R.string.resource_edit_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
        pd.show();

        mTaskManager.setTaskStatus(true);
        tag = TAG_CONTAINER_SELECTED;
        mTaskManager.startFiberbox(helper, position);
    }

    private void selectedDisk(int position) {
        if(mTaskManager.isRunning()) {
            Toast.makeText(this, R.string.resource_edit_wait, Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setCanceledOnTouchOutside(false);
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dismiss();
            }
        });
        pd.show();

        mTaskManager.setTaskStatus(true);
        tag = TAG_FIBERBOX_SELECTED;
        mTaskManager.startPortTask(helper, position);
    }

    private void dismiss() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public void detail() {
        String info = getInfo();
        if ("".equals(info)) {
            Toast.makeText(this, R.string.toast_bad_info, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(TAG_INFO, info);
        startActivity(intent);
    }

    public String getInfo() {
        JsonObject obj = new JsonObject();
        String station = mSpinnerStation.getSelectedItem().toString();
        String rack = mSpinnerRack.getSelectedItem().toString();
        String frame = mSpinnerFrame.getSelectedItem().toString();
        String disk = mSpinnerDisk.getSelectedItem().toString();

        obj.addProperty(TAG_STATION, station);
        obj.addProperty(TAG_RACK, rack);
        obj.addProperty(TAG_FRAME, frame);
        obj.addProperty(TAG_DISK, disk);
        if ("".equals(station) || "".equals(rack)
                || "".equals(frame) || "".equals(disk)) {
            Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show();
            return "";
        }
        Toast.makeText(this, obj.toString(), Toast.LENGTH_SHORT).show();
//        generateQRCode(obj.toString());
        return obj.toString();
    }

    public void generateQRCode(String info) {
        if (info == null || info.equals("")) {
            return;
        }
        QRCodeWriter writer = new QRCodeWriter();
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        try {
            BitMatrix bitMatrix = writer.encode(info, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }

                }
            }

            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
                    Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            mImg.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public boolean checkBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.text_cannot_support_bluetooth, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(mIntent, REQUEST_ENABLE_BLUETOOTH);
            return false;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    String info = getInfo();
                    if ("".equals(info)) {
                        Toast.makeText(this, R.string.toast_bad_info, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startBluetoothActivity(info);
                }
                break;
            case REQUEST_CODE:
                if (resultCode == AppCompatActivity.RESULT_OK) {
                    if (data != null) {
                        String result = data.getStringExtra(ScannerActivity.SCANNER_TAG_RESULT);
//                        String result = "{\"station\":\"网元1\",\"rack\":\"10\",\"frame\":\"9\",\"disk\":\"7\"}";
                        if (result == null || "".equals(result)) {
                            Toast.makeText(this, R.string.resource_wrong_data, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String station = "";
                        try {
                            resultObj = new JSONObject(result);
                            station = resultObj.getString(TAG_STATION);
                            if (station != null && !"".equals(station)) {
                                if (mSpinnerStation.getSelectedItem().toString().equals(station)) {
                                    String frame = resultObj.getString(TAG_RACK);
                                    if (frame != null && !"".equals(frame)) {
                                        if (frame.equals(mSpinnerRack.getSelectedItem().toString())) {
                                            String container = resultObj.getString(TAG_FRAME);
                                            if (container.equals(mSpinnerFrame.getSelectedItem().toString())) {
                                                String fiberbox = resultObj.getString(TAG_DISK);
                                                QR_TAG = QR_Port;
                                                mSpinnerDisk.setSelection(fiberboxesSpinner.indexOf(fiberbox));
                                            }
                                            QR_TAG = QR_Fiberbox;
                                            mSpinnerFrame.setSelection(containersSpinner.indexOf(container));
                                        }
                                        QR_TAG = QR_Container;
                                        mSpinnerRack.setSelection(framesSpinner.indexOf(frame));
                                    }
                                }
                                QR_TAG = QR_Frame;
                                mSpinnerStation.setSelection(netUnitsSpinner.indexOf(station));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void startBluetoothActivity(String info) {
        Intent intent = new Intent(this, BluetoothActivity.class);
        intent.putExtra(TAG_BLUETOOTH, info);
        startActivity(intent);
    }
}
