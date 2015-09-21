package com.yida.handset;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private BlueToothService mService;
    private DeviceOtherAdapter mOtherAdapter;
    private DevicePairedAdapter mPairedAdapter;
    private List<BluetoothDevice> bondedDevice = new ArrayList<>();
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
                            refreshPaired();
                            break;
                        case BlueToothService.FAILED_CONNECT:
                            Toast.makeText(BluetoothActivity.this, R.string.text_connect_failed, Toast.LENGTH_SHORT).show();
                            break;
                        case BlueToothService.LOSE_CONNECT:
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
        refreshPaired();
        mListPaired.setOnItemClickListener(this);
        mListOthers.setOnItemClickListener(this);

        newMap= BarcodeCreater.encode2dAsBitmap("03 previously on desperate housewives", 120, 120,
                2);
        btMap = Bitmap.createBitmap(384, 150, Bitmap.Config.ALPHA_8);
        bMapList = new HashMap<String,Bitmap>();
        bMapList.put("TheTag", newMap);
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
        if (mService.GetScanState() == BlueToothService.STATE_SCANING) {
            mService.StopScan();
        }
        if (adapterView == mListOthers) {
            mService.connect((BluetoothDevice) mOtherAdapter.getItem(i));
        }
//        if (adapterView == mListPaired) {
//            mService.connect((BluetoothDevice) mPairedAdapter.getItem(i));
//        }
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
        private List<BluetoothDevice> mmDevices;
        private Context mmContext;

        public DevicePairedAdapter(Context context, List<BluetoothDevice> devices) {
            this.mmContext = context;
            this.mmDevices = devices;
            this.mmInflater = LayoutInflater.from(this.mmContext);
        }

        public void addDevice(BluetoothDevice device) {
            if (mmDevices != null) {
                mmDevices.add(device);
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
            ViewHolder viewHolder;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = mmInflater.inflate(R.layout.item_paired_list, null);
                viewHolder.textView = (TextView) view.findViewById(R.id.item_paired_name);
                viewHolder.button = (Button) view.findViewById(R.id.print_button);
                viewHolder.buttonConnect = (Button) view.findViewById(R.id.connect_button);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            final BluetoothDevice itemDevice = mmDevices.get(i);
            viewHolder.textView.setText(itemDevice.getName() + "\n" + itemDevice.getAddress());
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mService.getState() == BlueToothService.STATE_CONNECTED) {
                        mService.PrintImage(
                                addWatermark(btMap, bMapList, newMap.getWidth(), newMap.getHeight()));
                    } else {
                        Toast.makeText(BluetoothActivity.this, R.string.text_connect_please, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            viewHolder.buttonConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mService.getState() == BlueToothService.STATE_CONNECTED) {
                        mService.DisConnected();
                    }
                    mService.connect(itemDevice);
                }
            });
            return view;
        }

        class ViewHolder {
            TextView textView;
            Button button;
            Button buttonConnect;
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

    private void refreshPaired() {
        Set<BluetoothDevice> deviceSet = mService.GetBondedDevice();
        if (deviceSet != null) {
            for(BluetoothDevice device : deviceSet) {
                bondedDevice.add(device);
            }
        }
        mPairedAdapter = new DevicePairedAdapter(this, bondedDevice);
        mListPaired.setAdapter(mPairedAdapter);
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
                Paint p = new Paint(); String familyName ="宋体";
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
}
