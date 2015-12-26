package com.rhodes.demo.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.rhodes.demo.R;
import com.rhodes.demo.Util.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by xiet on 2015/10/21.
 */
public class DeviceInfoActivity extends ActionBarActivity {
    ExpandableListView infoListView;
    DeviceInfoAdapter  deviceInfoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        infoListView = (ExpandableListView) findViewById(R.id.deviceInfoList);
        deviceInfoAdapter = new DeviceInfoAdapter(this);
        infoListView.setAdapter(deviceInfoAdapter);

        init();

        infoListView.expandGroup(0);
        infoListView.setGroupIndicator(null);
    }

    private void init() {
        fillScreenInfo();
    }

    private void fillScreenInfo() {
        String section = "Screen";
        clearSection(section);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        String dpiType = "";
        if (dm.densityDpi >= DisplayMetrics.DENSITY_XXXHIGH) {
            dpiType = "xxxhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_XXHIGH) {
            dpiType = "xxhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_XHIGH) {
            dpiType = "xhdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_HIGH) {
            dpiType = "hdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_TV) {
            dpiType = "tvdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_MEDIUM) {
            dpiType = "mdpi";
        } else if (dm.densityDpi >= DisplayMetrics.DENSITY_LOW) {
            dpiType = "ldpi";
        }
        List<String> infos = new ArrayList<String>();
        infos.add("density: " + dm.density);//The logical density of the display.
        infos.add("densityDpi: " + dm.densityDpi + "(" + dpiType + ")");//The screen density expressed as dots-per-inch.
        infos.add("scaledDensity: " + dm.scaledDensity);//A scaling factor for fonts displayed on the display.
        infos.add("widthPixels: " + dm.widthPixels);//The absolute width of the display in pixels.
        infos.add("heightPixels: " + dm.heightPixels);//The absolute height of the display in pixels.
        infos.add("xdpi: " + dm.xdpi);//The exact physical pixels per inch of the screen in the X dimension.
        infos.add("ydpi: " + dm.ydpi);//The exact physical pixels per inch of the screen in the Y dimension.
        //calc logical
        float x_inches = dm.widthPixels / dm.xdpi;
        infos.add("x-inches: " + x_inches);
        float y_inches = dm.heightPixels / dm.ydpi;
        infos.add("y-inches: " + y_inches);
        double diagonal = Math.sqrt(Math.pow(x_inches, 2) + Math.pow(y_inches, 2));
//        double diagonal = getScreenSizeOfDevice2();
        double diagonal_cm = new BigDecimal(diagonal * 2.54f).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        infos.add("diagonal(in): " + diagonal + "(≈" + diagonal_cm + "cm)");

        addSection(section, infos);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)//4.2.2
    private double getScreenSizeOfDevice2() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);

        return screenInches;
    }

    private void clearSection(String section) {
        if (deviceInfoAdapter.getSections().contains(section)) {
            deviceInfoAdapter.getInfos().remove(section);
            deviceInfoAdapter.getSections().remove(section);
        }
    }

    private void addSection(String section, List<String> infos) {
        clearSection(section);
        deviceInfoAdapter.getSections().add(section);
        deviceInfoAdapter.getInfos().put(section, infos);

        deviceInfoAdapter.notifyDataSetChanged();
    }

    class DeviceInfoAdapter extends BaseExpandableListAdapter {
        private final String TAG = getClass().getSimpleName();
        List<String>                    sections = null;
        Hashtable<String, List<String>> infos    = null;
        Context                         mContext = null;

        public DeviceInfoAdapter(Context mContext) {
            this.mContext = mContext;
            sections = new ArrayList<String>();
            infos = new Hashtable<String, List<String>>();
        }

        public LayoutInflater getLayoutInflater() {
            return (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public List<String> getSections() {
            if (sections == null) sections = new ArrayList<String>();
            return sections;
        }

        public Hashtable<String, List<String>> getInfos() {
            if (infos == null) infos = new Hashtable<String, List<String>>();
            return infos;
        }

        @Override
        public int getGroupCount() {
            return sections == null ? 0 : sections.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (getGroup(groupPosition) == null) return -1;
            return ((List) getGroup(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (groupPosition > (getSections().size() - 1)) return null;
            return getInfos().get(getSections().get(groupPosition));
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (getGroup(groupPosition) == null) return null;
            return ((List<String>) getGroup(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupViewHolder holder = null;
            try {
                if (convertView != null) {
                    holder = (GroupViewHolder) convertView.getTag();
                } else {
                    holder = new GroupViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.item_device_info_group, null);
                    holder.infoTitle = (TextView) convertView.findViewById(R.id.infoTitle);

                    convertView.setTag(holder);
                }

                String infoTitle = (String) sections.get(groupPosition);
                Logger.log(TAG, infoTitle);
                holder.infoTitle.setText("· " + infoTitle);
                holder.infoTitle.setTextColor(Color.parseColor("#4FC17F"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildViewHolder holder = null;
            try {
                if (convertView != null) {
                    holder = (ChildViewHolder) convertView.getTag();
                } else {
                    holder = new ChildViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.item_device_info, null);
                    holder.info = (TextView) convertView.findViewById(R.id.info);

                    convertView.setTag(holder);
                }
                String info = (String) getChild(groupPosition, childPosition);
                Logger.log(TAG, info);

                holder.info.setText("" + info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        class GroupViewHolder {

            public TextView infoTitle;
        }

        class ChildViewHolder {

            public TextView info;
        }
    }
}
