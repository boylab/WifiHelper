package com.microape.example;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.microape.wifihelper.R;

import java.util.List;

/**
 * Created by pengle on 2018-11-26.
 * email:pengle609@163.com
 */
public class WiFiListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<ScanResult> wifiList;

    public WiFiListAdapter(Context context, List<ScanResult> wifiList) {
        this.layoutInflater = LayoutInflater.from(context);
        this.wifiList = wifiList;
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int position) {
        return wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_wifi, null);
            viewHolder.tv_WiFi_Name = convertView.findViewById(R.id.tv_WiFi_Name);
            viewHolder.tv_WiFi_BSSID = convertView.findViewById(R.id.tv_WiFi_BSSID);
            viewHolder.tv_WiFi_Cap = convertView.findViewById(R.id.tv_WiFi_Cap);
            viewHolder.tv_WiFi_RSSI = convertView.findViewById(R.id.tv_WiFi_RSSI);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ScanResult result = wifiList.get(position);
        viewHolder.tv_WiFi_Name.setText(result.SSID);
        viewHolder.tv_WiFi_BSSID.setText(result.BSSID);
        viewHolder.tv_WiFi_Cap.setText(result.capabilities);
        viewHolder.tv_WiFi_RSSI.setText(String.valueOf(result.level));
        return convertView;
    }


    class ViewHolder{
        private TextView tv_WiFi_Name, tv_WiFi_BSSID, tv_WiFi_Cap, tv_WiFi_RSSI;
    }
}
