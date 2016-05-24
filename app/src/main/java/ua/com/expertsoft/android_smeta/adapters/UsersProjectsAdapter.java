package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.UserProjects;

/*
 * Created by mityai on 23.12.2015.
 */
public class UsersProjectsAdapter extends BaseAdapter {

    ArrayList<UserProjects> projList;
    LayoutInflater inflater;
    View customView;

    public UsersProjectsAdapter(ArrayList<UserProjects> projList, Context context){
        this.projList = projList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return projList.size();
    }

    @Override
    public UserProjects getItem(int position) {
        return projList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            customView = convertView;
        }else{
            customView = inflater.inflate(R.layout.all_group_tasks_layout, parent, false);
        }
        String name = projList.get(position).getUserProjName();
        if(name.length() >= 50) {
            name = name.substring(0,49) + "...";
        }
        ((TextView)customView.findViewById(R.id.shortTaskName)).setText(name);
        customView.setTag(projList.get(position));
        return customView;
    }
}
