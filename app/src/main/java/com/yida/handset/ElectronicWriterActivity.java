package com.yida.handset;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yida.handset.entity.Electronic;
import com.yida.handset.entity.ElectronicInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ElectronicWriterActivity extends ActionBarActivity {

    private static List<ElectronicInfo> electronicInfoList = new ArrayList<>();
    static {
        ElectronicInfo element;
        Electronic ele;
        element = new ElectronicInfo();
        element.setFrameNo(3);
        element.setBoardNo(5);
        element.setPortNo(11);
        ele =  new Electronic();
        ele.setVersion("123456");
        ele.setSerialNumber("0978755768");
        ele.setOid("AS");
        ele.setOperator("中国电信");
        ele.setProductType("设备类型A");
        element.setElectronicIdInfo(ele);
        electronicInfoList.add(element);

        element = new ElectronicInfo();
        element.setFrameNo(2);
        element.setBoardNo(6);
        element.setPortNo(7);
        ele =  new Electronic();
        ele.setVersion("1.0.2");
        ele.setSerialNumber("0978755768");
        ele.setOid("SKII");
        ele.setOperator("中国移动");
        ele.setProductType("设备类型B");
        element.setElectronicIdInfo(ele);
        electronicInfoList.add(element);
    }

    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.electronic_list)
    ListView mList;

    private ElectronicAdapter adapter;

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
                finish();
            }
        });

        adapter = new ElectronicAdapter(this, electronicInfoList);
        mList.setAdapter(adapter);
    }

    private class ElectronicAdapter extends BaseAdapter {

        private Context context;
        private List<ElectronicInfo> data;
        private LayoutInflater inflater;

        public ElectronicAdapter(Context context, List<ElectronicInfo> data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            if (data != null) {
                return data.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (data != null) {
                return data.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_electtronic_order, null);
                viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
                viewHolder.elePosition = (TextView) convertView.findViewById(R.id.ele_position);
                viewHolder.operator = (TextView) convertView.findViewById(R.id.operator);
                viewHolder.oid = (TextView) convertView.findViewById(R.id.oid);
                viewHolder.productType = (TextView) convertView.findViewById(R.id.product_type);
                viewHolder.serialNum = (TextView) convertView.findViewById(R.id.serial_number);
                viewHolder.extraInfo = (TextView) convertView.findViewById(R.id.extra_info);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            ElectronicInfo item = data.get(position);
            Electronic ele = item.getElectronicIdInfo();
            viewHolder.title.setText(String.valueOf(position));
            viewHolder.elePosition.setText("机框: " + item.getFrameNo() + " > 盘: " + item.getBoardNo() + " > 端口: " + item.getPortNo());
            viewHolder.operator.setText("运营商: " + ele.getOperator());
            viewHolder.oid.setText("供应商: " + ele.getOid());
            viewHolder.productType.setText("产品类型: " + ele.getProductType());
            viewHolder.serialNum.setText("序列号: " + ele.getSerialNumber());
            viewHolder.extraInfo.setText("其他信息" + ele.getExtraInfo());
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView elePosition;
            TextView operator;
            TextView oid;
            TextView productType;
            TextView serialNum;
            TextView extraInfo;
        }
    }

}
