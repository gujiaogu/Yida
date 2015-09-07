package com.yida.handset;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SecondFragment extends ActionFragment{

    private static final int REQUEST_ENABLE_BT = 1;

    private Activity context;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 10000;

    public static SecondFragment newInstance() {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Toast.makeText(context, R.string.ble_support_text, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    break;
                default:
                    break;
            }
        }
    }

}
