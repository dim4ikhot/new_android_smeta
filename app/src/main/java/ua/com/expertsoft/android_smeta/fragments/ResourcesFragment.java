package ua.com.expertsoft.android_smeta.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.ShowWorksParam;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.adapters.ResourcesAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/**
 * Created by mityai on 04.01.2016.
 */
public class ResourcesFragment extends Fragment implements ResourcesAdapter.OnCheckedConsistsListener {

    @Override
    public void OnCheckedConsists() {
        currWork = recalcWork(currWork);
        ShowWorksParam params = (ShowWorksParam)getActivity();
        DetailFragment detail = (DetailFragment) params.adapter.getItem(0);
        detail.fillTheWorksDetail(currWork);
    }

    private Works recalcWork(Works work){
        float zp = 0;
        float mach = 0;
        float itogo = 0;
        float count = work.getWCount();
        for(WorksResources wr : work.getAllWorksResources()){
            if (wr.getWrOnOff() == 1) {
                itogo += wr.getWrTotalCost();
                switch (wr.getWrPart()) {
                    case 1:
                        zp += wr.getWrTotalCost();
                        break;
                    case 2:
                        mach += wr.getWrTotalCost();
                        break;
                }
            }
        }
        work.setWItogo(itogo);
        work.setWZP(zp);
        work.setWMach(mach);
        work.setWZPTotal(zp * count);
        work.setWMachTotal(mach * count);
        work.setWTotal(itogo * count);
        return work;
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
}
