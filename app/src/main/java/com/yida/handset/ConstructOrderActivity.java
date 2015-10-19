package com.yida.handset;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yida.handset.entity.OpticalRoute;
import com.yida.handset.workorder.WorkOrderFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ConstructOrderActivity extends AppCompatActivity {

    private static final List<OpticalRoute> data = new ArrayList<>();
    static {
        OpticalRoute route;
        route = new OpticalRoute();
        route.setaBoardNo(1);
        route.setaDeviceId("SBN-112");
        route.setaDeviceName("A端设备名称");
        route.setaFrameNo(3);
        route.setaPortNo(10);
        route.setzDeviceId("SCN-B-1306");
        route.setzDeviceName("Z端设备名称");
        route.setzFrameNo(5);
        route.setzBoardNo(6);
        route.setzPortNo(9);
        route.setOperateType(0);
        route.setRouteType(2);
        route.setSplittingRatio("2/3");
        data.add(route);

        route = new OpticalRoute();
        route.setaBoardNo(3);
        route.setaDeviceId("SBN-N-1330112");
        route.setaDeviceName("A端设备名称");
        route.setaFrameNo(5);
        route.setaPortNo(8);
//        route.setzDeviceId("SCN-B-1306yu");
//        route.setzDeviceName("Z端设备名称");
//        route.setzFrameNo(5);
//        route.setzBoardNo(6);
//        route.setzPortNo(9);
        route.setOperateType(0);
        route.setRouteType(1);
        route.setSplittingRatio("aaa");
        data.add(route);
    }

    @Bind(R.id.order_id)
    TextView mOrderId;
    @Bind(R.id.order_status)
    TextView mOrderStatus;
    @Bind(R.id.order_site)
    TextView mOrderSite;
    @Bind(R.id.order_who_transfer)
    TextView mOrderWhoTransfer;
    @Bind(R.id.order_remark)
    TextView mOrderRemark;
    @Bind(R.id.order_operate_list)
    ListView mOrderOperateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_construct_order);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent extraIntent = getIntent();
        if (extraIntent != null) {
            mOrderId.setText("工单ID : " + extraIntent.getStringExtra(WorkOrderFragment.TAG_ID));
            mOrderStatus.setText("工单状态 : " + extraIntent.getStringExtra(WorkOrderFragment.TAG_ORDER_STATUS));
            mOrderSite.setText("地址 : " + extraIntent.getStringExtra(WorkOrderFragment.TAG_SITE));
        }

        mOrderWhoTransfer.setText("被转派的人: 张三");
        mOrderRemark.setText("备注：这个问题需要B站的人协助");
        ConstructOrderAdapter adapter = new ConstructOrderAdapter(this, data);
        mOrderOperateList.setAdapter(adapter);
    }

    private class ConstructOrderAdapter extends BaseAdapter {

        private Context context;
        private List<OpticalRoute> mData;
        private LayoutInflater inflater;

        public ConstructOrderAdapter(Context context, List<OpticalRoute> data) {
            this.context = context;
            this.mData = data;
            this.inflater = LayoutInflater.from(this.context);
        }
        @Override
        public int getCount() {
            if (mData != null) {
                return mData.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (mData != null) {
                mData.get(i);
            }
            return null;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_construct_list, null);
                holder.title = (TextView) convertView.findViewById(R.id.con_item_title);
                holder.operateType = (TextView) convertView.findViewById(R.id.operate_type);
                holder.routeType = (TextView) convertView.findViewById(R.id.route_type);
                holder.portAInfo = (TextView) convertView.findViewById(R.id.portAInfo);
                holder.portZInfo = (TextView) convertView.findViewById(R.id.portZInfo);
                holder.splittingRatio = (TextView) convertView.findViewById(R.id.splitting_ratio);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OpticalRoute item = mData.get(i);
            holder.title.setText(String.valueOf(i));
            holder.operateType.setText("操作: " + OpticalRoute.OPERATE[item.getOperateType()]);
            holder.routeType.setText("跳接: " + OpticalRoute.ROUTE_TYPE[item.getRouteType()]);
            holder.splittingRatio.setText("分光比: " + item.getSplittingRatio());
            holder.portAInfo.setText("设备: " + item.getaDeviceName() + " > 机框: " + item.getaFrameNo() + " > 盘: " + item.getaBoardNo()
                + " > 端口: " + item.getaPortNo());
            holder.portZInfo.setText("设备: " + item.getzDeviceName() + " > 机框: " + item.getzFrameNo() + " > 盘: " + item.getzBoardNo()
                    + " > 端口: " + item.getzPortNo());
            return convertView;
        }

        class ViewHolder {
            TextView title;
            TextView operateType;
            TextView routeType;
            TextView splittingRatio;
            TextView portAInfo;
            TextView portZInfo;
        }
    }
}
