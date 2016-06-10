package ua.com.expertsoft.android_smeta;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.admob.DynamicAdMob;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.sheet.SheetActivity;
import ua.com.expertsoft.android_smeta.static_data.SelectedLocal;
import ua.com.expertsoft.android_smeta.static_data.SelectedObjectEstimate;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.adapters.TotalWorksAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.FilterDialog;
import ua.com.expertsoft.android_smeta.static_data.StaticAsyncTasks;


public class ShowWorksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        TotalWorksAdapter.OnGetChangedWorkListener,TotalWorksAdapter.OnWorksItemsClickListener,
        FilterDialog.OnGetFilterListener {

    private static final int SHOW_PARAMS = 1;
    private static final int SHOW_FACTS = 2;

    LS selectedLs;
    OS selectedOs;
    CharSequence mTitle;
    ListView worksTotal;
    Works selectedWork;
    TotalWorksAdapter worksAdapter;
    DBORM database;
    ActionBar bar;
    LoadingWorks loadWorks;
    TextView filterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_show_works);
        new DynamicAdMob(this, (LinearLayout)findViewById(R.id.worksScreen)).showAdMob();
        selectedLs = SelectedLocal.localEstimate; //(LS)getIntent().getSerializableExtra("worksListIn");
        selectedOs = SelectedObjectEstimate.objectEstimate;
        mTitle = getResources().getString(R.string.title_activity_show_works);
        /*if(selectedLs.getLsNameRus().length() > 15) {
            mTitle = mTitle + "(" + selectedLs.getLsNameRus().substring(0, 15) + ")";
        }else{
            mTitle = mTitle + "(" + selectedLs.getLsNameRus() + ")";
        }*/
        mTitle = mTitle + "(" + (selectedLs != null ? selectedLs.getLsNameRus() :
                                   selectedOs!= null ? selectedOs.getOsNameRus():"") + ")";
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        if(bar != null) {
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(mTitle);
        }
        worksTotal = (ListView)findViewById(R.id.listViewTotalWorks);
        filterName = (TextView)findViewById(R.id.showCurrentFilter);
        worksTotal.setOnItemClickListener(this);
        database = new DBORM(this);
        if(selectedLs != null) {
            if (selectedLs.getAllWorks().size() == 0) {
                if (StaticAsyncTasks.staticLoadWorks == null) {
                    loadWorks = new LoadingWorks(this);
                    loadWorks.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    loadWorks = (LoadingWorks) getLastCustomNonConfigurationInstance(); //StaticAsyncTasks.staticLoadWorks;
                    //loadWorks.setContext(this);
                }
            } else {
                showWorks(selectedLs.cloneWorks());
            }
        }else{
            if(selectedOs != null){
                if(checkForEmptyLs()){
                    loadWorks = new LoadingWorks(this);
                    loadWorks.execute();
                }else{
                    showWorksByOs();
                }
            }
        }
    }

    private boolean checkForEmptyLs(){
        for (LS ls : selectedOs.getAllLocalEstimates())
        {
            if(ls.getAllWorks().size() == 0){
                return true;
            }
        }
        return false;
    }

    public Object onRetainCustomNonConfigurationInstance(){
        super.onRetainCustomNonConfigurationInstance();
        return loadWorks;
    }

    @Override
    public void onGetFilter(String filter,int what, int which) {
        if(selectedLs != null) {
            switch(what) {
                case 1:
                    showWorks(database.getWorksByFilter(selectedLs.getLsProjects(), Works.TW_FIELD_PART_TAG, filter));
                    break;
                case 2:
                    showWorks(database.getWorksByFilter(selectedLs.getLsProjects(), Works.TW_FIELD_LAYER_TAG, filter));
                    break;
                case 3:
                    showWorks(database.getWorksByFilter(selectedLs.getLsProjects(), Works.TW_FIELD_RES_GROUP_TAG, filter));
                    break;
            }
        }else{
            showWorks(selectedOs.getCurrentEstimate(which).cloneWorks());
        }
        filterName.setVisibility(View.VISIBLE);
        filterName.setText(filter);
    }

    public class LoadingWorks extends AsyncTask<Void,Void,Void>{

        AsyncProgressDialog dialog;
        Context context;

        public void setContext(Context ctx){
            context = ctx;
            dialog = new AsyncProgressDialog(context,
                    ctx.getResources().getString(R.string.dialogLoadingTitle),
                    ctx.getResources().getString(R.string.dialog_works_load));
            dialog.createDialog();
        }
        public LoadingWorks(Context ctx){
            context = ctx;
            dialog = new AsyncProgressDialog(context,
                    ctx.getResources().getString(R.string.dialogLoadingTitle),
                    ctx.getResources().getString(R.string.dialog_works_load));
        }

        private void lockScreenOrientation() {
            int currentOrientation = getResources().getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        private void unlockScreenOrientation() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        protected void onPreExecute(){
            dialog.createDialog();
            StaticAsyncTasks.staticLoadWorks = this;
            lockScreenOrientation();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(selectedLs != null) {
                ArrayList<Works> tmpArray = database.getWorks(selectedLs.getLsProjects(), selectedLs.getLsOs(), selectedLs, false);
                selectedLs.setAllWorks(tmpArray);
            }else{
                for(LS ls: selectedOs.getAllLocalEstimates()){
                    ls.setAllWorks(database.getWorks(ls.getLsProjects(), ls.getLsOs(), ls, false));
                }
            }
            return null;
        }
        protected void onPostExecute(Void result){
            dialog.freeDialog();
            StaticAsyncTasks.staticLoadWorks = null;
            unlockScreenOrientation();
            if(selectedLs != null) {
                ((ShowWorksActivity) context).showWorks(selectedLs.cloneWorks());
            }else{
                ((ShowWorksActivity) context).showWorksByOs();
            }
        }
    }

    private void showWorks(ArrayList<Works> w){
        if(!MainActivity.isShowHidden(this)){
            int i = 0;
            while(i < w.size()){
                if(! w.get(i).getWOnOff()){
                    w.remove(i);
                }else{
                    i++;
                }
            }
        }
        worksAdapter = new TotalWorksAdapter(this, w, database);
        worksTotal.setAdapter(worksAdapter);
        worksAdapter.notifyDataSetChanged();
    }

    private void showWorksByOs(){
        ArrayList<Works> toShow = new ArrayList<>();
        for (LS ls : selectedOs.getAllLocalEstimates())
        {
            toShow.addAll(ls.cloneWorks());
        }
        showWorks(toShow);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent();
        SelectedLocal.localEstimate = selectedLs;
        SelectedObjectEstimate.objectEstimate = selectedOs;
        //intent.putExtra("changedLs",selectedLs);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        if(menu.getClass().getSimpleName().equals("MenuBuilder")){
            try{
                Method m = menu.getClass().getDeclaredMethod(
                        "setOptionalIconsVisible", Boolean.TYPE);
                m.setAccessible(true);
                m.invoke(menu, true);
            }
            catch(NoSuchMethodException e){
                e.printStackTrace();
            }
            catch(Exception e){
                throw new RuntimeException(e);
            }
            MenuItem item = menu.findItem(R.id.filterWorksByLS);
            if(selectedLs == null){
                if(item != null){
                    item.setVisible(true);
                }
            }else{
                if(item != null){
                    item.setVisible(false);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.works_activity_main_menu, menu);
        return true;
    }

    private Bundle getFilters(String columnName, int filter){
        ArrayList<Works> works = new ArrayList<>();
        String[] filters;
        String tag;
        Bundle dialogParams = new Bundle();
        LS ls;
        if(selectedLs != null) {
            works = database.getWorksFilter(selectedLs.getLsProjects(),selectedLs.getLsId(), columnName);
            filters = new String[works.size()];
        }else{
            if(filter == 4) {
                filters = new String[selectedOs.getAllLocalEstimates().size()];
            }else{
                //filters = new String[0];
                works = database.getWorksFilter(selectedOs.getOsProjects(),selectedOs.getOsId(), columnName);
                filters = new String[works.size()];
            }
        }
        for (int i = 0; i < filters.length; i++) {
            switch (filter) {
                case 1:
                    tag = works.get(i).getWPartTag();
                    if (!tag.equals("")) {
                        filters[i] = tag;
                    }
                    break;
                case 2:
                    tag = works.get(i).getWLayerTag();
                    if (!tag.equals("")) {
                        filters[i] = tag;
                    }
                    break;
                case 3:
                    tag = works.get(i).getWGroupTag();
                    if (!tag.equals("")) {
                        filters[i] = tag;
                    }
                    break;
                case 4:
                    ls = selectedOs.getCurrentEstimate(i);
                    tag = ls.getLsNameRus();
                    if (!tag.equals("")) {
                        filters[i] = tag;
                    }
                    break;
            }
        }
        dialogParams.putInt("filter", filter);
        dialogParams.putStringArray("filters", filters);
        return dialogParams;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        FilterDialog filter = new FilterDialog();
        Intent intent;
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.filterWorks:
                filter.setArguments(getFilters("work_razdel_tag", 1));
                filter.show(getSupportFragmentManager(), "filterDialog");
                break;
            case R.id.filterWorksByLayer:
                filter.setArguments(getFilters("work_layer_tag", 2));
                filter.show(getSupportFragmentManager(), "filterDialog");
                break;
            case R.id.filterWorksByGroup:
                filter.setArguments(getFilters("work_res_group_tag", 3));
                filter.show(getSupportFragmentManager(), "filterDialog");
                break;
            case R.id.filterWorksByLS:
                filter.setArguments(getFilters(Works.TW_FIELD_LS_ID, 4));
                filter.show(getSupportFragmentManager(), "filterDialog");
                break;
            case R.id.cancelFilter:
                if(selectedLs != null) {
                    showWorks(selectedLs.cloneWorks());
                }else{
                    showWorksByOs();
                }
                filterName.setVisibility(View.GONE);
                break;
            case R.id.shown_works_sheet:
                SelectedWork.listOfShownWorks = worksAdapter.getAddedList();
                intent = new Intent(this, SheetActivity.class);
                intent.putExtra("isNormsSheet",1);
                intent.putExtra("isBySelected",1);
                intent.putExtra("sheet_title",getResources().getString(R.string.standard_norms_sheet));
                startActivityForResult(intent, MainActivity.NORMS_SHEET);
                break;
            case R.id.shown_works_resources_sheet:
                SelectedWork.listOfShownWorks = worksAdapter.getAddedList();
                intent = new Intent(this, SheetActivity.class);
                intent.putExtra("isNormsSheet",0);
                intent.putExtra("isBySelected",1);
                intent.putExtra("sheet_title",getResources().getString(R.string.standard_resources_sheet));
                startActivityForResult(intent, MainActivity.RESOURCES_SHEET);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onGetChangedWork(int position,Works changedWork) {
        if(selectedLs != null) {
            selectedLs.getAllWorks().set(position, changedWork);
        }else{
            for(LS ls : selectedOs.getAllLocalEstimates()){
                int index = ls.findWorkPositionByGuid(changedWork);
                if (index != -1){
                    ls.setCurrentWork(index, changedWork);
                    break;
                }
            }
        }
    }

    @Override
    public void onFactsClick(View v) {
        selectedWork = (Works)v.getTag();
        Intent intent = new Intent(this, ShowFacts.class);
        //intent.putExtra("facts",selectedWork);
        selectedWork.reCalculateExecuting();
        SelectedWork.work = selectedWork;
        startActivityForResult(intent, SHOW_FACTS);
    }

    @Override
    public void onMoreClick(View v) {
        selectedWork = (Works)v.getTag();
        Intent intent = new Intent(this, ShowWorksParam.class);
        //intent.putExtra("selectedWork", selectedWork);
        SelectedWork.work = selectedWork;
        startActivityForResult(intent, SHOW_PARAMS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == SHOW_FACTS) | (requestCode == SHOW_PARAMS)) {
                if (resultCode == RESULT_OK) {
                    Works changedWork = SelectedWork.work;
                    /*if(changedWork.getAllFacts().size() == 0){
                        changedWork.setAllFactss(database.getWorksFacts(changedWork));
                    }*/
                    if(requestCode == SHOW_FACTS) {
                        changedWork.reCalculateExecuting();
                    }
                    //changedWork = recalcWork(changedWork);
                    if((selectedWork != null)&(changedWork != null)) {
                        try {
                            database.getHelper().getWorksDao().update(changedWork);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        if(selectedLs != null) {
                            selectedLs.replaceWorks(selectedWork, changedWork);
                        }else{
                            for(LS ls : selectedOs.getAllLocalEstimates()){
                                int index = ls.findWorkPositionByGuid(changedWork);
                                if (index != -1){
                                    ls.replaceWorks(selectedWork, changedWork);
                                    break;
                                }
                            }
                        }
                        worksAdapter.notifyDataSetChanged();
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowWorksActivity: onActivityResult", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }
}
