package net.woorisys.lighting.control3.admin.common;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.io.Serializable;
import java.util.List;

public abstract class AbsractCommonAdapter<D extends Serializable> extends BaseAdapter {

    Activity activity;
    public LayoutInflater inflater;
    public List<D> data;

    public AbsractCommonAdapter(Activity activity, List<D> data) {
        this.activity = activity;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    protected abstract View getUserEditView(int position, View convertView, ViewGroup parent);

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public D getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        return getUserEditView(position, convertView, parent);
    }
}
