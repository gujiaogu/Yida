package com.yida.handset;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothprinter.BarcodeCreater;
import com.example.bluetoothprinter.BlueToothService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BluetoothActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    private static Bitmap btMapa = null;
    private static Bitmap barcode2D = null;

    private BlueToothService mService;
    private DeviceOtherAdapter mOtherAdapter;
    private DevicePairedAdapter mPairedAdapter;
    private ArrayList<BluetoothDevice> bondedDevice = new ArrayList<>();
    private boolean isPrintOp = false; //判断是打印连接操作，还是配对连接操作
    private boolean isConnectDone = true; // 判断连接是否完成，可能是连接失败，连接成功或者失去连接
    private volatile boolean isPrinting = false;
    private String mPrintInfo;
    private BluetoothAdapter mBtAdapter;
    private Bitmap newMap, btMap;
    private HashMap<String, Bitmap> bMapList;

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.list_paired)
    ListView mListPaired;
    @Bind(R.id.list_others)
    ListView mListOthers;
    @Bind(R.id.search_device)
    Button mSearch;

    private Handler blueToothHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BlueToothService.STATE_CONNECTED:
                            break;
                        case BlueToothService.STATE_CONNECTING:
                            break;
                        case BlueToothService.STATE_LISTEN:
                        case BlueToothService.STATE_NONE:
                            break;
                        case BlueToothService.SUCCESS_CONNECT:
                            Toast.makeText(BluetoothActivity.this, R.string.text_connect_success, Toast.LENGTH_SHORT).show();
                            if(!isPrintOp) {
                                refreshPaired();
                            } else if(isPrintOp) {
                                LogWrapper.d("test success connect");
//                                if (null != mPrintInfo && !"".equals(mPrintInfo)) {
                                isPrinting = true;
                                    new Thread(new PrintThread("03 previously on desperate housewives")).start();
//                                }
                            }
                            isConnectDone = true;
                            break;
                        case BlueToothService.FAILED_CONNECT:
                            Toast.makeText(BluetoothActivity.this, R.string.text_connect_failed, Toast.LENGTH_SHORT).show();
                            isPrinting = false;
                            LogWrapper.d("test failed connect");
                            isConnectDone = true;
                            break;
                        case BlueToothService.LOSE_CONNECT:
                            isPrinting = false;
                            LogWrapper.d("test lost connect");
                            isConnectDone = true;
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_WRITE:
                    break;

            }
            return true;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtAdapter.getState();
        mSearch.setOnClickListener(this);

        mService = new BlueToothService(this, blueToothHandler);
        mService.setOnReceive(new BlueToothService.OnReceiveDataHandleEvent() {
            @Override
            public void OnReceive(BluetoothDevice bluetoothDevice) {
                if (bluetoothDevice != null
                        && (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED)) {
                    mOtherAdapter.addDevice(bluetoothDevice);
                }
            }
        });


        mOtherAdapter = new DeviceOtherAdapter(this);
        mListOthers.setAdapter(mOtherAdapter);
        bondedDevice.clear();
        refreshPaired();
        mListOthers.setOnItemClickListener(this);

//        newMap = BarcodeCreater.encode2dAsBitmap("03 previously on desperate housewives", 120, 120,
//                2);
//        btMap = Bitmap.createBitmap(384, 150, Bitmap.Config.ALPHA_8);
//        bMapList = new HashMap<String,Bitmap>();
//        bMapList.put("TheTag", newMap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mService != null) {
            mService.stop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mService != null) {
            mService.start();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.search_device:
                if (mService.GetScanState() == BlueToothService.STATE_SCANING) {
                    mService.StopScan();
                }
                mOtherAdapter.clear();
                mService.ScanDevice();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        isPrintOp = false;
        if (isConnectDone && !isPrinting) {
            isConnectDone = false;
            if (mService.GetScanState() == BlueToothService.STATE_SCANING) {
                mService.StopScan();
            }
            if (adapterView == mListOthers) {
                mService.connect((BluetoothDevice) mOtherAdapter.getItem(i));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DevicePairedAdapter extends BaseAdapter {

        private LayoutInflater mmInflater;
        private Context mmContext;

        public DevicePairedAdapter(Context context) {
            this.mmContext = context;
            this.mmInflater = LayoutInflater.from(this.mmContext);
        }

        public void addDevice(BluetoothDevice device) {
            if (bondedDevice != null) {
                bondedDevice.add(device);
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (bondedDevice != null) {
                return bondedDevice.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (bondedDevice != null) {
                return bondedDevice.get(i);
            }
            return null;
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
                view = mmInflater.inflate(R.layout.item_paired_list, null);
                viewHolder.textView = (TextView) view.findViewById(R.id.item_paired_name);
                viewHolder.button = (Button) view.findViewById(R.id.print_button);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final BluetoothDevice itemDevice = bondedDevice.get(i);
            viewHolder.textView.setText(itemDevice.getName() + "\n" + itemDevice.getAddress());
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFastDoubleClick()) {
                        return;
                    }
                    LogWrapper.d("test onclick connect before " + isPrinting);
                    if (isPrinting) {
                        Toast.makeText(BluetoothActivity.this, R.string.text_connect_please, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isConnectDone) {
                        isPrintOp = true;
                        isConnectDone = false;
                        isPrinting = true;
                        LogWrapper.d("test onclick connect after " + isPrinting);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                if (mService.getState() == BlueToothService.STATE_CONNECTED) {
//                                    mService.DisConnected();
//                                }
                                mService.connect(itemDevice);
                            }
                        }).start();

                    }
                }
            });
            return view;
        }

        class ViewHolder {
            TextView textView;
            Button button;
        }
    }

    private class DeviceOtherAdapter extends BaseAdapter {

        private LayoutInflater mmInflater;
        private List<BluetoothDevice> mmDevices = new ArrayList<>();
        private Context mmContext;

        public DeviceOtherAdapter(Context context) {
            this.mmContext = context;
            this.mmInflater = LayoutInflater.from(this.mmContext);
        }

        public void addDevice(BluetoothDevice device) {
            if (mmDevices != null) {
                mmDevices.add(device);
                notifyDataSetChanged();
            }
        }

        public void clear() {
            if (mmDevices != null) {
                mmDevices.clear();
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (mmDevices != null) {
                return mmDevices.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (mmDevices != null) {
                return mmDevices.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = mmInflater.inflate(android.R.layout.simple_list_item_1, null);
                holder.textView = (TextView) view;
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            holder.textView.setText(mmDevices.get(i).getName() + "\n" + mmDevices.get(i).getAddress());
            return view;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private synchronized void refreshPaired() {
        Set<BluetoothDevice> deviceSet = mService.GetBondedDevice();

        if (deviceSet != null && deviceSet.size() > 0) {
            //if there is no element, we add all devices to paired adapter
            if (bondedDevice.size() <= 0) {
                for(BluetoothDevice device : deviceSet) {
                    bondedDevice.add(device);
                }
                mPairedAdapter = new DevicePairedAdapter(this);
                mListPaired.setAdapter(mPairedAdapter);
                return;
            }

            ArrayList<BluetoothDevice> tmp = bondedDevice;
            for(BluetoothDevice device : deviceSet) {
                for(int i = 0; i < tmp.size(); i ++) {
                    if (device.getAddress().equals(tmp.get(i).getAddress())) {
                        break;
                    }
                    if (i == tmp.size() - 1) {
                        mPairedAdapter.addDevice(device);
                    }
                }
            }
        }
    }

    private class PrintThread implements Runnable {

        private String str;

        private PrintThread(String info) {
            this.str = info;
        }

        @Override
        public void run() {

            if (mService.getState() == BlueToothService.STATE_CONNECTED) {
                generate2DCodes(this.str);
                Bitmap bitmapOrg = btMapa; //BitmapFactory.decodeFile(picPath);
                int w = bitmapOrg.getWidth();
                int h = bitmapOrg.getHeight();
                mService.PrintImage(resizeImage(addWatermark(bitmapOrg, barcode2D), w, h));
                isPrinting = false;
                LogWrapper.d("======over====== " + isPrinting);
            }
        }
    }

    //批量二维码生成bitMap
    private Bitmap addWatermark(Bitmap src, HashMap<String,Bitmap> bList,int w,int h) {
        if (src == null || bList  == null) {

            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();

        int wWid = w;
        int wHei = h;
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }
        int tempHei=sHei;
        //假设有几个二维码
        int _2dNum=bList.size();
        if (_2dNum>3) {

            sHei=sHei*(_2dNum/3+1);

        }


        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawColor(Color.WHITE);
			/*for (int i = 0; i < _2dNum; i++) {
				String temp="第"+(i+1)+"个";
				Paint p = new Paint(); String familyName ="宋体";
				Typeface font = Typeface.create(familyName,Typeface.BOLD);
				p.setColor(Color.BLACK);
				p.setTypeface(font); p.setTextSize(20);

				float p_x=sWid-(3-i%3)*(wWid+5);

				float p_y=tempHei*(i/3+1)-wHei-10;
				cv.drawText(temp,p_x,p_y-5,p);
				cv.drawBitmap(bList.get(i), p_x, p_y, null);
			}*/
            Iterator iter = bMapList.entrySet().iterator();
            int i=0;
            while (iter.hasNext()) {

                Map.Entry entry = (Map.Entry) iter.next();
                String temp=String.valueOf(entry.getKey());
                Paint p = new Paint();
                String familyName ="宋体";
                Typeface font = Typeface.create(familyName,Typeface.BOLD);
                p.setColor(Color.BLACK);
                p.setTypeface(font); p.setTextSize(17);
                float p_x=sWid-(3-i%3)*(wWid+5);

                float p_y=tempHei*(i/3+1)-wHei-10;
                //字符串换行
                int charCount=0;
                charCount=temp.length();
                if (charCount>7) {
                    String oneCell=temp.substring(0, 7);
                    String twoCell=temp.substring(7);
                    cv.drawText(oneCell,p_x,p_y,p);
                    cv.drawText(twoCell,p_x,p_y+18,p);
                    cv.drawBitmap((Bitmap)entry.getValue(), p_x, p_y+20, null);
                }else {
                    cv.drawText(temp,p_x,p_y,p);
                    cv.drawBitmap((Bitmap)entry.getValue(), p_x, p_y+10, null);
                }

                //cv.drawText(temp,p_x,p_y-5,p);
                //cv.drawBitmap((Bitmap)entry.getValue(), p_x, p_y, null);
                i++;
            }


            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    private Bitmap addWatermark(Bitmap src, Bitmap watermark) {
        if (src == null || watermark  == null) {
            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = watermark.getWidth();
        int wHei = watermark.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }

        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_8888);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawColor(Color.WHITE);
            cv.drawBitmap(watermark, sWid - wWid - 42, sHei - wHei - 5, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        Bitmap BitmapOrg = bitmap;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, w,
                h, matrix, true);
        return resizedBitmap;
    }

    // 生成二维码和背景
    public void generate2DCodes(String message) {
        if (message.length() > 0) {
            try {
                message = new String(message.getBytes("utf8"));
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            btMapa = Bitmap.createBitmap(384, 310, Bitmap.Config.ALPHA_8);
            barcode2D = BarcodeCreater.encode2dAsBitmap(message,
                    300, 300, 2);// 230依照现在设备勉强能行(34位),300很容易
        }
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        lastClickTime = time;
        if (0 < timeD && timeD < 3000) {
            return true;
        }
        return false;
    }
}
