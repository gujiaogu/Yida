package com.yida.handset;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yida.handset.entity.LogEntity;
import com.yida.handset.sqlite.LogDao;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LogActivity extends AppCompatActivity {

    public static List<LogEntity> data;

    @Bind(android.R.id.list)
    ListView mList;

    private LogDao dao;
    LogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
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
        if (data != null && data.size() > 0) {
            adapter = new LogAdapter(this, data);
            mList.setAdapter(adapter);
        }

        dao = new LogDao(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.clear_log) {
//            dao.clear();
//            if (adapter != null) {
//                adapter.clear();
//            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LogAdapter extends BaseAdapter {

        private Context context;
        private List<LogEntity> data;
        private LayoutInflater inflater;

        public LogAdapter(Context context, List<LogEntity> data) {
            this.context = context;
            this.data = data;
            this.inflater = LayoutInflater.from(this.context);
        }

        @Override
        public int getCount() {
            if (this.data != null) {
                this.data.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int i) {
            if (this.data != null) {
                this.data.get(i);
            }
            return 0;
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
                convertView = inflater.inflate(R.layout.item_log_list, null);
                viewHolder.username = (TextView) convertView.findViewById(R.id.user_name);
                viewHolder.type = (TextView) convertView.findViewById(R.id.log_type);
                viewHolder.time = (TextView) convertView.findViewById(R.id.log_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            LogEntity entity = data.get(position);
            viewHolder.username.setText(entity.getUsername());
            viewHolder.type.setText(entity.getType());
            viewHolder.time.setText(entity.getTime());
            return convertView;
        }

        public void clear() {
            if (this.data != null) {
                this.data.clear();
            }
            notifyDataSetChanged();
        }

        class ViewHolder {
            TextView username;
            TextView type;
            TextView time;
        }
    }

}
