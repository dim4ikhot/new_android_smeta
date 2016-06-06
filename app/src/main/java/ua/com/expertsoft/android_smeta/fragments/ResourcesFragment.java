package ua.com.expertsoft.android_smeta.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import java.sql.SQLException;

import ua.com.expertsoft.android_smeta.EditResourceActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.ShowWorksParam;
import ua.com.expertsoft.android_smeta.static_data.SelectedResource;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.adapters.ResourcesAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/*
 * Created by mityai on 04.01.2016.
 */
public class ResourcesFragment extends Fragment{

    public void OnCheckedConsists() {
        currWork.recalculateWorkByResources();
        ShowWorksParam params = (ShowWorksParam)getActivity();
        DetailFragment detail = (DetailFragment) params.adapter.getItem(0);
        detail.fillTheWorksDetail(currWork);
    }

    ListView resList;
    View resources;
    ResourcesAdapter listAdapter;
    Works currWork;
    DBORM database;

    public ResourcesFragment(){
        database = new DBORM(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle params){
        super.onCreateView(inflater,group,params);
        resources = inflater.inflate(R.layout.resources_layout, group, false);
        resList = (ListView)resources.findViewById(R.id.listResources);
        if (resList != null){
            resList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SelectedResource.resource = (WorksResources) view.getTag();
                    startActivityForResult(
                            new Intent(ResourcesFragment.this.getContext(),
                                    EditResourceActivity.class), 0);
                }
            });
        }
        //currWork = (Works)getArguments().getSerializable("adapterWork");
        currWork = SelectedWork.work;
        if(currWork != null) {
            if(currWork.getAllWorksResources().size() == 0){
                new LoadingWorksRes(getActivity()).execute();
            }else {
                showParams();
            }
        }
        return resources;
    }

    private class LoadingWorksRes extends AsyncTask<Void,Void,Void> {

        AsyncProgressDialog dialog;
        public LoadingWorksRes(Context ctx){
            dialog = new AsyncProgressDialog(ctx,
                    ctx.getResources().getString(R.string.dialogLoadingTitle),
                    ctx.getResources().getString(R.string.dialog_works_load));
        }

        protected void onPreExecute(){
            dialog.createDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            currWork.setAllResources(database.getWorksResource(currWork));
            return null;
        }
        protected void onPostExecute(Void result){
            dialog.freeDialog();
            ResourcesFragment.this.showParams();
        }
    }

    private void showParams(){
        listAdapter = new ResourcesAdapter(getActivity(), currWork.getAllWorksResources(), new DBORM(getActivity()), this);
        resList.setAdapter(listAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 0){
                if (currWork.replaceResources(SelectedResource.resource)){
                    try {
                        new DBORM(getActivity()).getHelper().getWorksResDao().update(SelectedResource.resource);
                    }catch(SQLException e){
                        e.printStackTrace();
                    }
                    OnCheckedConsists();
                    showParams();
                }
            }
        }
    }
}
