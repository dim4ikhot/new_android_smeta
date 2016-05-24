package ua.com.expertsoft.android_smeta;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.UUID;

import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.static_data.SelectedFact;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.adapters.FactsAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.NewFactDialog;

public class ShowFacts extends AppCompatActivity implements NewFactDialog.OnGetFacts,
        AdapterView.OnItemClickListener, FactsAdapter.OnDeleteFactListener {

    Works selectedWork;
    FactsAdapter factsAdapter;
    ListView factsList;
    ActionBar bar;
    DBORM database;
    Facts selectedFact;
    CharSequence title;

    @Override
    public void getFacts(Facts currentFact) {
        try {
            if (selectedFact != null) {
                selectedWork.replaceFacts(selectedFact, currentFact);
            } else {
                currentFact.setFactsGuid(UUID.randomUUID().toString());
                selectedWork.setCurrentFact(currentFact);
            }
            currentFact.setFactsWorkId(selectedWork.getWorkId());
            updateFacts(currentFact);
            setWorksExecution();
            selectedWork.reCalculateExecuting();
            selectedWork.sortFacts();
            factsAdapter.notifyDataSetChanged();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts: getFacts", Toast.LENGTH_SHORT).show();
        }
    }

    private void setWorksExecution(){
        try {
            if (getSumPercent(selectedWork)) {
                selectedWork.setWPercentDone(100);
                selectedWork.setWCountDone(selectedWork.getWCount());
            } else {
                float total = getTotalPercent(selectedWork);
                selectedWork.setWPercentDone(total);
                selectedWork.setWCountDone((selectedWork.getWCount() * total) / 100);
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts: setWorksExecution", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFacts(Facts newFact){
        try {
            database.getHelper().getFactsDao().createOrUpdate(newFact);
            selectedWork.reCalculateExecuting();
        }catch(SQLException e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts: updateFacts", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        try {
            setContentView(R.layout.activity_show_facts);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            title = getResources().getString(R.string.title_activity_show_facts);
            bar = getSupportActionBar();
            if(bar != null) {
                bar.setHomeButtonEnabled(true);
                bar.setDisplayHomeAsUpEnabled(true);
            }
            database = new DBORM(this);

            selectedWork = SelectedWork.work;//(Works) getIntent().getSerializableExtra("facts");
            factsList = (ListView) findViewById(R.id.listFactsShower);
            if (factsList != null) {
                factsList.setOnItemClickListener(this);
            }
            if(selectedWork.getAllFacts().size() == 0){
                new LoadingWorksFacts(this).execute();
            }else{
                showFacts();
            }
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!getSumPercent(selectedWork)) {
                            NewFactDialog dialog = new NewFactDialog();
                            Bundle bundle = new Bundle();
                            SelectedWork.work = selectedWork;
                            SelectedFact.fact = null;
                            selectedFact = null;
                            //bundle.putSerializable("work", selectedWork);
                            dialog.setArguments(bundle);
                            dialog.show(getSupportFragmentManager(), "factsdialog");
                        } else {
                            Snackbar.make(view, R.string.all_facts_done, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts: onCreate", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
    }

    private class LoadingWorksFacts extends AsyncTask<Void,Void,Void> {

        AsyncProgressDialog dialog;
        public LoadingWorksFacts(Context ctx){
            dialog = new AsyncProgressDialog(ctx,
                    ctx.getResources().getString(R.string.dialogLoadingTitle),
                    ctx.getResources().getString(R.string.dialog_works_load));
        }

        protected void onPreExecute(){
            dialog.createDialog();
        }

        @Override
        protected Void doInBackground(Void... params) {
            selectedWork.setAllFactss(database.getWorksFacts(selectedWork));
            return null;
        }
        protected void onPostExecute(Void result){
            dialog.freeDialog();
            ShowFacts.this.showFacts();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.facts_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showFacts(){
        selectedWork.reCalculateExecuting();
        factsAdapter = new FactsAdapter(this, selectedWork.getAllFacts());
        factsList.setAdapter(factsAdapter);
    }

    private boolean getSumPercent(Works w){
        boolean res;
        float sum = 0;
        for(Facts f : w.getAllFacts()){
            sum += f.getFactsMakesPercent();
        }
        res = (sum == 100);
        return res;
    }

    private float getTotalPercent(Works w){
        float sum = 0;
        for(Facts f : w.getAllFacts()){
            sum += f.getFactsMakesPercent();
        }
        return sum;
    }

    @Override
    public void onBackPressed(){
        try {
            Intent intent = new Intent();
            //intent.putExtra("changedWork", selectedWork);
            SelectedWork.work = selectedWork;
            setResult(RESULT_OK, intent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts: onBackPressed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.show_facts_info:
                InfoCommonDialog dialog = new InfoCommonDialog();
                dialog.setMessage(getResources().getString(R.string.fact_information));
                dialog.show(getSupportFragmentManager(),"facts_info");
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedFact = (Facts)view.getTag();
        if(selectedFact != null){
            NewFactDialog dialog = new NewFactDialog();
            Bundle bundle = new Bundle();
            SelectedWork.work = selectedWork;
            SelectedFact.fact = selectedFact;
            //bundle.putSerializable("work", selectedWork);
            //bundle.putSerializable("fact", selectedFact);
            dialog.setArguments(bundle);
            dialog.show(getSupportFragmentManager(), "factsdialog");
        }
    }

    @Override
    public void onDeleteFact(Facts deletingFact) {
        try {
            selectedWork.removeFact(deletingFact);
            setWorksExecution();
            factsAdapter.notifyDataSetChanged();
            try {
                database.getHelper().getFactsDao().delete(deletingFact);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "ShowFacts onDeleteFact", Toast.LENGTH_SHORT).show();
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
