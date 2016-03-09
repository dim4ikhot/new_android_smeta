package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.ListOfOnlineCadBuilders;
import ua.com.expertsoft.android_smeta.R;

/**
 * Created by mityai on 25.12.2015.
 */
public class OcadProjectsAdapter extends BaseAdapter {

    ArrayList<ListOfOnlineCadBuilders.JsonProjs> projectsOcadList;
    LayoutInflater inflater;
    View returnedView;
    ListOfOnlineCadBuilders.JsonProjs currentProj;

    public OcadProjectsAdapter(){}

    public OcadProjectsAdapter(Context context, ArrayList<ListOfOnlineCadBuilders.JsonProjs> projList){
        projectsOcadList = projList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return projectsOcadList.size();
    }

    @Override
    public ListOfOnlineCadBuilders.JsonProjs getItem(int position) {
        return projectsOcadList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView != null){
            returnedView = convertView;
        }else{
            returnedView = inflater.inflate(R.layout.list_item_of_online_builders, parent, false);
        }
        currentProj = projectsOcadList.get(position);
        if(currentProj.getName().length() <= 50) {
            ((TextView) returnedView.findViewById(R.id.buildName)).setText(currentProj.getName());
        }else{
            ((TextView) returnedView.findViewById(R.id.buildName)).setText(currentProj.getName().substring(0,49)+"...");
        }
        returnedView.setTag(currentProj);
        return returnedView;
    }
}
