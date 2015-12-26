package com.rhodes.demo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.rhodes.demo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    /**
     * Called when the activity is first created.
     */


    enum Functions {
        ServerRequest(ServerRequestActivity.class),
        BluetoothDevice(BluetoothDeviceActivity.class),
        DeviceInfo(DeviceInfoActivity.class),
        VideoPlayer(VideoPlayerActivity.class);

        private Class clazz;

        Functions(Class cls) {
            this.clazz = cls;
        }

        public Class value() {
            return this.clazz;
        }
    }


    //adapter
    List<String>         titles;
    List<Class>          classes;
    ListView             listView;
    ArrayAdapter<String> adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //initialize
        titles = new ArrayList<String>();
        classes = new ArrayList<Class>();
        Functions[] functionses = Functions.values();
        for (int i = 0; i < functionses.length; i++) {
            Functions func = functionses[i];
            titles.add(func.name());
            classes.add(func.value());
        }

        listView = (ListView) findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, titles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(this, classes.get(position));

        intent.setComponent(cn);
        startActivity(intent);
    }
}
