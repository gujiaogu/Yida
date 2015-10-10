package com.yida.handset;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import android.widget.Button;
import com.rey.material.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResourceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResourceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResourceFragment extends Fragment implements View.OnClickListener,
        Spinner.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG_STATION = "station";
    public static final String TAG_RACK = "rack";
    public static final String TAG_FRAME = "frame";
    public static final String TAG_DISK = "disk";
    public static final String TAG_INFO = "info";

    private static final int QR_WIDTH = 600;
    private static final int QR_HEIGHT = 600;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> stationAdapter;
    private ArrayAdapter<String> rackAdapter;
    private ArrayAdapter<String> frameAdapter;
    private ArrayAdapter<String> diskAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    @Bind(R.id.generate_code)
    Button mGenerator;
    @Bind(R.id.check_detail)
    Button mCheckDetail;
    @Bind(R.id.btn_warning)
    Button mBtnWarning;
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

    public static ResourceFragment newInstance(String param1, String param2) {
        ResourceFragment fragment = new ResourceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ResourceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        stationAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.stations));
        stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        rackAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.racks));
        rackAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        frameAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.frames));
        frameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        diskAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.disks));
        diskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        ButterKnife.bind(this, view);
        mGenerator.setOnClickListener(this);
        mBtnWarning.setOnClickListener(this);
        mCheckDetail.setOnClickListener(this);

        mSpinnerStation.setAdapter(stationAdapter);
        mSpinnerRack.setAdapter(rackAdapter);
        mSpinnerFrame.setAdapter(frameAdapter);
        mSpinnerDisk.setAdapter(diskAdapter);

        mSpinnerStation.setOnItemSelectedListener(this);
        mSpinnerRack.setOnItemSelectedListener(this);
        mSpinnerFrame.setOnItemSelectedListener(this);
        mSpinnerDisk.setOnItemSelectedListener(this);

        getInfo();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.generate_code:
                if (checkBluetooth()) {
                    startBluetoothActivity();
                }
                break;
            case R.id.btn_warning:
                break;
            case R.id.check_detail:
                detail();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_station:
                getInfo();
                break;
            case R.id.spinner_rack:
                getInfo();
                break;
            case R.id.spinner_frame:
                getInfo();
                break;
            case R.id.spinner_disk:
                getInfo();
                break;
            default:
                break;
        }
    }

    public void detail() {
        String info = getInfo();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
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
        Toast.makeText(getActivity(), obj.toString(), Toast.LENGTH_SHORT).show();
        generateQRCode(obj.toString());
        return obj.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
            Toast.makeText(getActivity(), R.string.text_cannot_support_bluetooth, Toast.LENGTH_SHORT).show();
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
                    startBluetoothActivity();
                }
                break;
            default:
                break;
        }
    }

    private void startBluetoothActivity() {
        Intent intent = new Intent(getActivity(), BluetoothActivity.class);
        startActivity(intent);
    }
}
