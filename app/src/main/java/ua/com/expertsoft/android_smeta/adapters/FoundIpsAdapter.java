package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ua.com.expertsoft.android_smeta.ListOfOnlineCadBuilders;
import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 09.03.2016.
 */
public class FoundIpsAdapter extends BaseAdapter {

    ListOfOnlineCadBuilders.FoundComputersInLAN foundIps;
    Context context;
    LayoutInflater inflater;
    View view;
    public FoundIpsAdapter(Context ctx, ListOfOnlineCadBuilders.FoundComputersInLAN foundIps){
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        context = ctx;
        this.foundIps = foundIps;
    }

    @Override
    public int getCount() {
        return foundIps.getCount();
    }

    @Override
    public ListOfOnlineCadBuilders.IPs getItem(int position) {
        return foundIps.getIp(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView != null ? convertView :
                inflater.inflate(R.layout.ip_params_activity, parent, false);
        ListOfOnlineCadBuilders.IPs ip = foundIps.getIp(position);
        ((TextView)view.findViewById(R.id.computerNameValue)).setText(ip.getComputerName());
        ((TextView)view.findViewById(R.id.userNameValue)).setText(ip.getUserName());
        ((TextView)view.findViewById(R.id.ipAddressValue)).setText(ip.getIp());
        if (! ip.isConnectionBusy()) {
            return view;
        }else{
            return null;
        }

    }
}
