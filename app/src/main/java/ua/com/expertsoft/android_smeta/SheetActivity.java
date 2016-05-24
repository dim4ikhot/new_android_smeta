package ua.com.expertsoft.android_smeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.adapters.SheetAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.sheet.SheetBody;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;
public class SheetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayList<SheetBody> sheet;
    SheetAdapter adapter;
    ListView sheetShower;
    ActionBar bar;
    boolean isNormsSheet;
    int globalItemPosition;
    static SheetBody selectedSheetItem;
    DBORM database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet);
        bar = getSupportActionBar();
        String title = getIntent().getStringExtra("sheet_title");
        sheetShower = (ListView)findViewById(R.id.sheet);
        if(sheetShower != null) {
            sheetShower.setOnItemClickListener(this);
        }
        if(bar != null){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            if(title != null){
                bar.setTitle(title);
            }
        }
        database = new DBORM(this);
        isNormsSheet = (getIntent().getIntExtra("isNormsSheet", 0) == 1);
        sheet = new ArrayList<>();
        fillTheSheet();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillTheSheet(){
        new CollectSheet(this).execute(isNormsSheet);
    }

    private SheetBody findSheetByWork(Works work){
        for(SheetBody body : sheet){
            boolean namesEquals = body.getName().equals(work.getWName());
            boolean measuresEquals = body.getMeasure().equals(work.getWMeasuredRus());
            boolean costsEquals = body.getCost() == work.getWItogo();
            boolean salariesEquals = body.getSalary() == work.getWZP();
            if(namesEquals & measuresEquals & costsEquals & salariesEquals){
                return body;
            }
        }
        return null;
    }

    private SheetBody findSheetByREsource(WorksResources resource){
        for(SheetBody body : sheet){
            boolean namesEquals = body.getName().equals(resource.getWrNameRus());
            boolean measuresEquals = body.getMeasure().equals(resource.getWrMeasuredRus());
            boolean costsEquals = body.getCost() == resource.getWrCost();
            if(namesEquals & measuresEquals & costsEquals){
                return body;
            }
        }
        return null;
    }

    private void addNewSheet(Works work){
        SheetBody sheetBody = new SheetBody();
        sheetBody.setName(work.getWName());
        sheetBody.setMeasure(work.getWMeasuredRus());
        sheetBody.setCost(work.getWItogo());
        sheetBody.setCount(work.getWCount());
        sheetBody.setTotalCost(work.getWTotal());
        sheetBody.setSalary(work.getWZP());
        if(sheetBody.getCanEditSalary()) {
            sheetBody.setCanEditSalary(work.getAllWorksResources().size() == 0);
        }
        sheetBody.addWork(work);
        sheet.add(sheetBody);
    }

    private void addNewSheet(WorksResources resource){
        SheetBody sheetBody = new SheetBody();
        sheetBody.setName(resource.getWrNameRus());
        sheetBody.setMeasure(resource.getWrMeasuredRus());
        sheetBody.setCost(resource.getWrCost());
        sheetBody.setCount(resource.getWrCount());
        sheetBody.setTotalCost(resource.getWrTotalCost());
        sheetBody.setSalary(0);
        if(sheetBody.getCanEditSalary()) {
            sheetBody.setCanEditSalary(false);
        }
        sheetBody.addResource(resource);
        sheet.add(sheetBody);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        globalItemPosition = position;
        selectedSheetItem = (SheetBody)view.getTag();
        new EditSheetItem().show(getSupportFragmentManager(), "edit_sheet_item");
    }

    public void OnApplyChanges() {
        sheet.set(globalItemPosition, selectedSheetItem);
        adapter.notifyDataSetChanged();
    }

    public class CollectSheet extends AsyncTask<Boolean,Void,Void>{
        AsyncProgressDialog waitDialog;
        Context context;

        public CollectSheet(Context ctx){
            context = ctx;
            waitDialog = new AsyncProgressDialog(ctx, R.string.sheet_wait_dialog_title,
                    R.string.sheet_wait_dialog_message);
        }

        public void onPreExecute(){
            waitDialog.createDialog();
        }

        private void getProject(){
            MainActivity.fillObjectsBeforeUpdate(ProjectInfo.project, database);
        }

        @Override
        protected Void doInBackground(Boolean... params) {
            getProject();
            SheetBody sheetBody;
            boolean isNeedPosition;
            boolean isNormsSheet = params[0];
            for (OS os : ProjectInfo.project.getAllObjectEstimates()) {
                for (LS ls : os.getAllLocalEstimates()) {
                    for (Works work : ls.getAllWorks()) {
                        if(isNormsSheet) {
                            isNeedPosition = work.getWRec().equals("record") || work.getWRec().equals("work");
                        }
                        else{
                            isNeedPosition = work.getWRec().equals("machine") ||
                                             work.getWRec().equals("resource");
                        }
                        if(isNormsSheet && isNeedPosition) {
                            if (sheet.size() == 0) {
                                addNewSheet(work);
                            } else {
                                sheetBody = findSheetByWork(work);
                                if (sheetBody != null) {
                                    sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + work.getWCount(),4));
                                    sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(),4));
                                    if(sheetBody.getCanEditSalary()) {
                                        sheetBody.setCanEditSalary(work.getAllWorksResources().size() == 0);
                                    }
                                    sheetBody.addWork(work);
                                } else {
                                    addNewSheet(work);
                                }
                            }
                        }
                        else if(!isNormsSheet){
                            if(isNeedPosition){
                                if (sheet.size() == 0) {
                                    addNewSheet(work);
                                } else {
                                    sheetBody = findSheetByWork(work);
                                    if (sheetBody != null) {
                                        sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + work.getWCount(),4));
                                        sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(),4));
                                        sheetBody.addWork(work);
                                    } else {
                                        addNewSheet(work);
                                    }
                                }
                            }
                            else{
                                for(WorksResources res : work.getAllWorksResources()){
                                    if (sheet.size() == 0) {
                                        addNewSheet(res);
                                    } else {
                                        sheetBody = findSheetByREsource(res);
                                        if (sheetBody != null) {
                                            sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + res.getWrCount(),4));
                                            sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(),4));
                                            sheetBody.addResource(res);
                                        } else {
                                            addNewSheet(res);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        public void onPostExecute(Void result){
            adapter = new SheetAdapter(context,sheet);
            if(sheetShower != null) {
                sheetShower.setAdapter(adapter);
            }
            waitDialog.freeDialog();

        }
    }

    public static class EditSheetItem extends DialogFragment{

        EditText name;
        EditText measure;
        EditText count;
        EditText cost;
        EditText totalCost;
        EditText salary;
        LinearLayout salaryLayout;

        public EditSheetItem(){}

        public Dialog onCreateDialog(Bundle params){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.edit_sheet_item, null, false);
            dialog.setCancelable(false);
            dialog.setView(v);
            initControls(v);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editSelectedItem();
                    dismiss();
                }
            });
            dialog.setNegativeButton(R.string.user_edit_dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            return dialog.create();
        }

        private void initControls(View v){
            name = (EditText)v.findViewById(R.id.sheet_item_name);
            measure = (EditText)v.findViewById(R.id.sheet_item_measure);
            count = (EditText)v.findViewById(R.id.sheet_item_count);
            cost = (EditText)v.findViewById(R.id.sheet_item_cost);
            totalCost = (EditText)v.findViewById(R.id.sheet_item_total_cost);
            salary = (EditText)v.findViewById(R.id.sheet_item_salary);
            salaryLayout = (LinearLayout)v.findViewById(R.id.parent_salary);
            if(selectedSheetItem.getCanEditSalary()){
                salaryLayout.setVisibility(View.VISIBLE);
            }
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!s.toString().equals("")){
                        double currentCost = Double.parseDouble(s.toString().replace(",","."));
                        double total = ZmlParser.roundTo(selectedSheetItem.getCount() * currentCost,4);
                        totalCost.setText(String.valueOf(total));
                    }
                    else{
                        totalCost.setText("0");
                    }
                }
            });
            fillControls();
        }

        private void fillControls(){
            DecimalFormat df = new DecimalFormat("#.###", new DecimalFormatSymbols(Locale.US));
            name.setText(selectedSheetItem.getName());
            measure.setText(selectedSheetItem.getMeasure());
            count.setText(df.format(selectedSheetItem.getCount()).replace(",","."));
            cost.setText(df.format(selectedSheetItem.getCost()).replace(",","."));
            totalCost.setText(df.format(selectedSheetItem.getTotalCost()).replace(",","."));
            salary.setText(df.format(selectedSheetItem.getSalary()).replace(",","."));
        }

        private void editSelectedItem(){
            selectedSheetItem.setName(name.getText().toString());
            selectedSheetItem.setMeasure(measure.getText().toString());
            selectedSheetItem.setCount(Double.parseDouble(count.getText().toString().replace(",",".")));
            selectedSheetItem.setCost(Double.parseDouble(cost.getText().toString().replace(",",".")));
            selectedSheetItem.setTotalCost(Double.parseDouble(totalCost.getText().toString().replace(",",".")));
            selectedSheetItem.setSalary(Double.parseDouble(salary.getText().toString().replace(",",".")));
            if(((SheetActivity)getActivity()).isNormsSheet) {
                for(Works w : selectedSheetItem.getAllWorks()){
                    w.setWName(selectedSheetItem.getName());
                    w.setWMeasuredRus(selectedSheetItem.getMeasure());
                    w.setWItogo((float)selectedSheetItem.getCost());
                    w.setWTotal(w.getWCount() * w.getWItogo());
                    if(selectedSheetItem.getCanEditSalary()){
                        w.setWZP((float)selectedSheetItem.getSalary());
                    }
                    try{
                        ((SheetActivity)getActivity()).database.getHelper().getWorksDao().update(w);
                    }catch(SQLException e){
                        e.printStackTrace();
                    }
                }
            }
            else{
                for(WorksResources res: selectedSheetItem.getAllResources()){
                    res.setWrNameRus(selectedSheetItem.getName());
                    res.setWrMeasuredRus(selectedSheetItem.getMeasure());
                    res.setWrCost((float)selectedSheetItem.getCost());
                    res.setWrTotalCost(res.getWrCount() * res.getWrCost());
                    try{
                        ((SheetActivity)getActivity()).database.getHelper().getWorksResDao().update(res);
                    }catch(SQLException e){
                        e.printStackTrace();
                    }
                }
            }
            ((SheetActivity)getActivity()).OnApplyChanges();
        }
    }
}

