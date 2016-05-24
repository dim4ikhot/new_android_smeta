package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.ListOfOnlineCadBuilders;
import ua.com.expertsoft.android_smeta.R;

/*
 * Created by mityai on 25.12.2015.
 */
public class OcadProjectsAdapter extends BaseAdapter {

    public interface OnRefreshAfterDeleteListener{
        void onRefreshAfterDelete();
    }

    ArrayList<ListOfOnlineCadBuilders.JsonProjs> projectsOcadList;
    LayoutInflater inflater;
    View returnedView;
    ListOfOnlineCadBuilders.JsonProjs currentProj;
    int projectType;//0 - online, 1 - offline
    OnRefreshAfterDeleteListener deleteListener;


    public OcadProjectsAdapter(Context context, ArrayList<ListOfOnlineCadBuilders.JsonProjs> projList, int type){
        projectsOcadList = projList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        projectType = type;
        deleteListener = (OnRefreshAfterDeleteListener)context;
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
            String name = currentProj.getName().substring(0,49)+"...";
            ((TextView) returnedView.findViewById(R.id.buildName)).setText(name);
        }
        if(projectType == 0 || currentProj.getGuid().equals("")){
            returnedView.findViewById(R.id.delete_build_file).setVisibility(View.GONE);
        }else {
            returnedView.findViewById(R.id.delete_build_file).setVisibility(View.VISIBLE);
            returnedView.findViewById(R.id.delete_build_file).setTag(currentProj);
            returnedView.findViewById(R.id.delete_build_file).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File buildFile = new File(((ListOfOnlineCadBuilders.JsonProjs) v.getTag()).getGuid());
                            buildFile.delete();
                            new File(buildFile.getParent()).delete();
                            if(removeFileFromList(((ListOfOnlineCadBuilders.JsonProjs) v.getTag()).getGuid())) {
                                deleteListener.onRefreshAfterDelete();
                            }
                        }
                    });
        }
        returnedView.setTag(currentProj);
        return returnedView;
    }

    private boolean removeFileFromList(String guid){
        for(ListOfOnlineCadBuilders.JsonProjs currProj: projectsOcadList){
            if(guid.equals(currProj.getGuid())){
                projectsOcadList.remove(currProj);
                return true;
            }
        }
        return false;
    }
}
