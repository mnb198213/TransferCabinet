package com.jintoufs.zj.transfercabinet.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jintoufs.zj.transfercabinet.R;

import java.util.List;

/**
 * Created by zj on 2017/9/19.
 */

public class CredentialListAdapter extends BaseAdapter {

    private Context context;
    private List<String> infos;

    public CredentialListAdapter(Context context, List<String> infos) {
        this.context = context;
        this.infos = infos;
    }

    @Override
    public int getCount() {
        return infos.size();
    }

    @Override
    public Object getItem(int position) {
        return infos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.credential_item_view, null);
        TextView info = (TextView) view.findViewById(R.id.tv_info);
        info.setText(infos.get(position));
        return view;
    }
}
