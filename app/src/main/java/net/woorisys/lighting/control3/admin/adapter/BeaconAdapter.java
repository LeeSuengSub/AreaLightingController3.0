package net.woorisys.lighting.control3.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.woorisys.lighting.control3.admin.R;
import net.woorisys.lighting.control3.admin.ble.BeaconDomain;

import java.util.ArrayList;
import java.util.List;

public class BeaconAdapter extends BaseAdapter {

    private final ArrayList<BeaconDomain> beaconArrayList;
    private final LayoutInflater inflater;

    public BeaconAdapter(Context context, List<BeaconDomain> beacon) {
        this.beaconArrayList = new ArrayList<>(beacon);
        this.inflater = LayoutInflater.from(context);
    }

    /** 데이터 갱신 — 어댑터 재생성 없이 리스트만 교체 */
    public void updateData(List<BeaconDomain> newList) {
        beaconArrayList.clear();
        beaconArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return beaconArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return beaconArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.scan_list_item, parent, false);
            holder = new ViewHolder();
            holder.serialNumber = convertView.findViewById(R.id.serialNumber);
            holder.macAddress   = convertView.findViewById(R.id.macAddress);
            holder.uuid         = convertView.findViewById(R.id.uuid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BeaconDomain item = beaconArrayList.get(position);
        holder.serialNumber.setText("SerialNumber : " + item.getSerialNumber());
        holder.macAddress.setText("MacAddress : "   + item.getMacAddress());
        holder.uuid.setText("UUID : "               + item.getUuid());

        return convertView;
    }

    static class ViewHolder {
        TextView serialNumber;
        TextView macAddress;
        TextView uuid;
    }
}
