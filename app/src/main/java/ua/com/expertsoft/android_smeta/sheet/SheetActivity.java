package ua.com.expertsoft.android_smeta.sheet;

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
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.adapters.SheetAdapter;
import ua.com.expertsoft.android_smeta.adapters.TotalWorksAdapter;
import ua.com.expertsoft.android_smeta.admob.DynamicAdMob;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;

public class SheetActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener{

    ArrayList<SheetBody> sheet;
    SheetAdapter adapter;
    ListView sheetShower;
    ActionBar bar;
    boolean isNormsSheet,isBySelected;
    int globalItemPosition;
    static SheetBody selectedSheetItem;
    DBORM database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_sheet);
        bar = getSupportActionBar();
        String title = getIntent().getStringExtra("sheet_title");
        sheetShower = (ListView)findViewById(R.id.sheet);
        if(sheetShower != null) {
            sheetShower.setOnItemClickListener(this);
            sheetShower.setOnItemLongClickListener(this);
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
        isBySelected = (getIntent().getIntExtra("isBySelected", 0) == 1);
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
        new CollectSheet(this).execute(isNormsSheet, isBySelected);
    }

    private SheetBody findSheetByWork(Works work){
        for(SheetBody body : sheet){
            boolean namesEquals;
            boolean measuresEquals;
            if(FragmentSettings.isDataLanguageRus(this)) {
                namesEquals = body.getName().equals(work.getWName());
                measuresEquals = body.getMeasure().equals(work.getWMeasuredRus());
            }
            else{
                namesEquals = body.getName().equals(work.getWNameUkr());
                measuresEquals = body.getMeasure().equals(work.getWMeasuredUkr());
            }
            boolean costsEquals = body.getCost() == work.getWItogo();
            boolean salariesEquals = body.getSalary() == work.getWZP();
            boolean included = body.getIsIncluded() == work.getWOnOff();
            if(namesEquals && measuresEquals && costsEquals && salariesEquals && included){
                return body;
            }
        }
        return null;
    }

    private SheetBody findSheetByREsource(WorksResources resource){
        for(SheetBody body : sheet){
            boolean namesEquals;
            boolean measuresEquals;
            if(FragmentSettings.isDataLanguageRus(this)) {
                namesEquals = body.getName().equals(resource.getWrNameRus());
                measuresEquals = body.getMeasure().equals(resource.getWrMeasuredRus());
            }
            else{
                namesEquals = body.getName().equals(resource.getWrNameUkr());
                measuresEquals = body.getMeasure().equals(resource.getWrMeasuredUkr());
            }
            boolean costsEquals = body.getCost() == resource.getWrCost();
            if(namesEquals & measuresEquals & costsEquals){
                return body;
            }
        }
        return null;
    }

    private void addNewSheet(Works work){
        SheetBody sheetBody = new SheetBody();
        if(FragmentSettings.isDataLanguageRus(this)) {
            sheetBody.setName(work.getWName());
            sheetBody.setMeasure(work.getWMeasuredRus());
        }
        else{
            sheetBody.setName(work.getWNameUkr());
            sheetBody.setMeasure(work.getWMeasuredUkr());
        }
        sheetBody.setCost(work.getWItogo());
        sheetBody.setCount(work.getWCount());
        sheetBody.setTotalCost(work.getWTotal());
        sheetBody.setSalary(work.getWZP());
        sheetBody.setCipher(work.getWCipher());
        sheetBody.setIsDone(work.getWPercentDone() == 100);
        sheetBody.setIsIncluded(work.getWOnOff());
        if(sheetBody.getCanEditSalary()) {
            sheetBody.setCanEditSalary(work.getAllWorksResources().size() == 0);
        }
        sheetBody.addWork(work);
        sheet.add(sheetBody);
    }

    private void addNewSheet(WorksResources resource){
        SheetBody sheetBody = new SheetBody();
        if(FragmentSettings.isDataLanguageRus(this)) {
            sheetBody.setName(resource.getWrNameRus());
            sheetBody.setMeasure(resource.getWrMeasuredRus());
        }
        else{
            sheetBody.setName(resource.getWrNameUkr());
            sheetBody.setMeasure(resource.getWrMeasuredUkr());
        }
        sheetBody.setCost(resource.getWrCost());
        sheetBody.setCount(resource.getWrCount());
        sheetBody.setTotalCost(resource.getWrTotalCost());
        sheetBody.setCipher(resource.getWrCipher());
        sheetBody.setIsIncluded(resource.getWrOnOff() == 1);
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
        EditSheetItem dlg = new EditSheetItem();
        dlg.show(getSupportFragmentManager(), "edit_sheet_item");
    }

    public void OnApplyChanges() {
        sheet.set(globalItemPosition, selectedSheetItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public static void viewGroup(SheetActivity activity, SheetBody body){
        selectedSheetItem = body;
        ConsistViewer dlg = new ConsistViewer();
        dlg.show(activity.getSupportFragmentManager(), "view_sheet_item_consist");
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
            boolean isBySelected = params[1];
            SheetBody sheetBody;
            boolean isNeedPosition;
            boolean isNormsSheet = params[0];
            ArrayList<Works> commonWorksList = new ArrayList<>();

            if(!isBySelected) {
                getProject();
                for (OS os : ProjectInfo.project.getAllObjectEstimates()) {
                    for (LS ls : os.getAllLocalEstimates()) {
                        commonWorksList.addAll(ls.cloneWorks());
                    }
                }
            }
            else{
                //only shown works
                if(SelectedWork.listOfShownWorks != null && SelectedWork.listOfShownWorks.size() > 0){
                    commonWorksList = SelectedWork.listOfShownWorks;
                    //TODO: Load resources for works only if building resources sheet
                    if(! isNormsSheet) {
                        for (Works w : commonWorksList) {
                            if (w.getAllWorksResources().size() == 0) {
                                w.setAllResources(database.getWorksResource(w));
                            }
                        }
                    }
                }
            }

            for (Works work : commonWorksList) {
                if (isNormsSheet) {
                    isNeedPosition = work.getWRec().equals("record") || work.getWRec().equals("work");
                } else {
                    isNeedPosition = work.getWRec().equals("machine") ||
                            work.getWRec().equals("resource") ||
                            //OCAD builds
                            work.getWRec().equals("material") ||
                            work.getWRec().equals("mach");
                }
                if (isNormsSheet && isNeedPosition) {
                    if (sheet.size() == 0) {
                        addNewSheet(work);
                    } else {
                        sheetBody = findSheetByWork(work);
                        if (sheetBody != null) {
                            sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + work.getWCount(), 4));
                            sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(), 4));
                            if (sheetBody.getCanEditSalary()) {
                                sheetBody.setCanEditSalary(work.getAllWorksResources().size() == 0);
                            }
                            if(sheetBody.getIsDone()) {
                                sheetBody.setIsDone(work.getWPercentDone() == 100);
                            }
                            sheetBody.addWork(work);
                        } else {
                            addNewSheet(work);
                        }
                    }
                } else if (!isNormsSheet) {
                    if (isNeedPosition) {
                        if (sheet.size() == 0) {
                            addNewSheet(work);
                        } else {
                            sheetBody = findSheetByWork(work);
                            if (sheetBody != null) {
                                sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + work.getWCount(), 4));
                                sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(), 4));
                                sheetBody.addWork(work);
                            } else {
                                addNewSheet(work);
                            }
                        }
                    } else {
                        for (WorksResources res : work.getAllWorksResources()) {
                            if (sheet.size() == 0) {
                                addNewSheet(res);
                            } else {
                                sheetBody = findSheetByREsource(res);
                                if (sheetBody != null) {
                                    sheetBody.setCount(ZmlParser.roundTo(sheetBody.getCount() + res.getWrCount(), 4));
                                    sheetBody.setTotalCost(ZmlParser.roundTo(sheetBody.getCount() * sheetBody.getCost(), 4));
                                    sheetBody.addResource(res);
                                } else {
                                    addNewSheet(res);
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
        ToggleButton toggleIsDone,toggleIncluded;
        AlertDialog dialog;

        public EditSheetItem(){}

        public Dialog onCreateDialog(Bundle params){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.edit_sheet_item, null, false);
            dialogBuilder.setCancelable(false);
            dialogBuilder.setView(v);
            initControls(v);
            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editSelectedItem();
                    dismiss();
                }
            });
            dialogBuilder.setNegativeButton(R.string.user_edit_dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            dialog = dialogBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            });
            return dialog;
        }

        private void initControls(View v){
            name = (EditText)v.findViewById(R.id.sheet_item_name);
            measure = (EditText)v.findViewById(R.id.sheet_item_measure);
            count = (EditText)v.findViewById(R.id.sheet_item_count);
            cost = (EditText)v.findViewById(R.id.sheet_item_cost);
            cost.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (selectedSheetItem.getCanEditSalary()){
                        if(! s.toString().equals("") && !salary.getText().toString().equals("")) {
                            if ((Double.parseDouble(s.toString().replace(",", ".")) <
                                    Double.parseDouble(salary.getText().toString().replace(",", ".")))) {
                                salary.setText(s.toString());
                            }
                        }
                        else{
                            salary.setText("0");
                        }
                    }
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
            totalCost = (EditText)v.findViewById(R.id.sheet_item_total_cost);
            salary = (EditText)v.findViewById(R.id.sheet_item_salary);
            salary.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(! s.toString().equals("") && !cost.getText().toString().equals("")) {
                        if ((Double.parseDouble(s.toString().replace(",", ".")) >
                                Double.parseDouble(cost.getText().toString().replace(",", ".")))) {
                            cost.setText(s.toString());
                        }
                    }
                }
            });
            salaryLayout = (LinearLayout)v.findViewById(R.id.parent_salary);
           /*
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
            */
            toggleIsDone = (ToggleButton)v.findViewById(R.id.toggle_done);
            toggleIncluded = (ToggleButton)v.findViewById(R.id.toggle_included);

            if(selectedSheetItem.getCanEditSalary()){
                salaryLayout.setVisibility(View.VISIBLE);
            }
            if(!((SheetActivity)getActivity()).isNormsSheet) {
                v.findViewById(R.id.parent_is_done).setVisibility(View.GONE);
            }

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
            toggleIsDone.setChecked(selectedSheetItem.getIsDone());
            toggleIncluded.setChecked(selectedSheetItem.getIsIncluded());
        }

        private void editSelectedItem(){
            selectedSheetItem.setName(name.getText().toString());
            selectedSheetItem.setMeasure(measure.getText().toString());
            selectedSheetItem.setCount(Double.parseDouble(count.getText().toString().replace(",",".")));
            selectedSheetItem.setCost(Double.parseDouble(cost.getText().toString().replace(",",".")));
            selectedSheetItem.setTotalCost(Double.parseDouble(totalCost.getText().toString().replace(",",".")));
            selectedSheetItem.setSalary(Double.parseDouble(salary.getText().toString().replace(",",".")));
            selectedSheetItem.setIsDone(toggleIsDone.isChecked());
            selectedSheetItem.setIsIncluded(toggleIncluded.isChecked());
            if(((SheetActivity)getActivity()).isNormsSheet || selectedSheetItem.getAllWorks().size() > 0) {
                for(Works w : selectedSheetItem.getAllWorks()){
                    if(FragmentSettings.isDataLanguageRus(getContext())) {
                        w.setWName(selectedSheetItem.getName());
                        w.setWMeasuredRus(selectedSheetItem.getMeasure());
                    }
                    else{
                        w.setWNameUkr(selectedSheetItem.getName());
                        w.setWMeasuredUkr(selectedSheetItem.getMeasure());
                    }
                    w.setWItogo((float)selectedSheetItem.getCost());
                    w.setWTotal(w.getWCount() * w.getWItogo());
                    w.setwOnOFf(selectedSheetItem.getIsIncluded());
                    if(selectedSheetItem.getIsDone()){
                        float percentDone = 0;
                        float countDone = 0;
                        for(Facts fact: w.getAllFacts()){
                            percentDone += fact.getFactsMakesPercent();
                            countDone += fact.getFactsMakesCount();
                        }
                        if(percentDone < 100) {
                            Facts f = TotalWorksAdapter.createFactForDone(w, percentDone, countDone);
                            if (f != null){
                                w.setCurrentFact(f);
                                try{
                                    new DBORM(getContext()).getHelper().getFactsDao().create(f);
                                }catch(SQLException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
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
            else {
                if (selectedSheetItem.getAllResources().size() != 0){
                    for (WorksResources res : selectedSheetItem.getAllResources()) {
                        if (FragmentSettings.isDataLanguageRus(getContext())) {
                            res.setWrNameRus(selectedSheetItem.getName());
                            res.setWrMeasuredRus(selectedSheetItem.getMeasure());
                        } else {
                            res.setWrNameUkr(selectedSheetItem.getName());
                            res.setWrMeasuredUkr(selectedSheetItem.getMeasure());
                        }
                        res.setWrCost((float) selectedSheetItem.getCost());
                        res.setWrOnOff(selectedSheetItem.getIsIncluded() ? 1 : 0);
                        res.setWrTotalCost(res.getWrCount() * res.getWrCost());
                        try {
                            ((SheetActivity) getActivity()).database.getHelper().getWorksResDao().update(res);
                            Works work = res.getWrWork();
                            if (work != null) {
                                work.recalculateWorkByResources();
                                ((SheetActivity) getActivity()).database.getHelper().getWorksDao().update(work);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            ((SheetActivity)getActivity()).OnApplyChanges();
        }
    }


    public static class ConsistViewer extends DialogFragment{

        AlertDialog dialog;

        ListView worksList;

        public ConsistViewer(){}

        public Dialog onCreateDialog(Bundle params){
            AlertDialog.Builder dialogB = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.content_show_works, null, false);
            dialogB.setView(v);
            initControls(v);
            dialogB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dismiss();
                }
            });
            dialog = dialogB.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                            .setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            });
            return dialog;
        }

        private void initControls(View v){
            worksList = (ListView)v.findViewById(R.id.listViewTotalWorks);
            fillControls();
        }

        private void fillControls(){
            BaseAdapter shower;
            if(selectedSheetItem.getAllWorks().size() != 0) {
                shower = new WorksShower(getActivity(), selectedSheetItem.getAllWorks());
            }
            else{
                shower = new ResourcesShower(getActivity(), selectedSheetItem.getAllResources());
            }
            worksList.setAdapter(shower);
        }

        public class WorksShower extends BaseAdapter{
            Context context;
            ArrayList<Works> listWorks;

            public WorksShower(Context ctx, ArrayList<Works> works){
                listWorks = works;
                context = ctx;
            }


            @Override
            public int getCount() {
                return listWorks.size();
            }

            @Override
            public Works getItem(int position) {
                return listWorks.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView != null? convertView
                        :((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                          .inflate(R.layout.resources_list_item,parent, false);
                ImageView img = (ImageView)v.findViewById(R.id.imgResourceType);
                img.setVisibility(View.GONE);
                CheckBox onoff = (CheckBox)v.findViewById(R.id.onOffCheckBox);
                onoff.setVisibility(View.GONE);

                TextView name = (TextView)v.findViewById(R.id.txtResourceName);
                TextView count = (TextView)v.findViewById(R.id.txtResourceCountValue);
                TextView price = (TextView)v.findViewById(R.id.txtResourcePriceValue);
                TextView total = (TextView)v.findViewById(R.id.txtResourceTotalValue);

                Works work = listWorks.get(position);
                name.setText(work.getWName());
                count.setText(String.valueOf(work.getWCount()));
                price.setText(String.valueOf(work.getWItogo()));
                total.setText(String.valueOf(work.getWTotal()));

                return v;
            }
        }

        public class ResourcesShower extends BaseAdapter{
            Context context;
            ArrayList<WorksResources> listWorksRes;

            public ResourcesShower(Context ctx, ArrayList<WorksResources> worksRes){
                listWorksRes = worksRes;
                context = ctx;
            }


            @Override
            public int getCount() {
                return listWorksRes.size();
            }

            @Override
            public WorksResources getItem(int position) {
                return listWorksRes.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView != null? convertView
                        :((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                        .inflate(R.layout.resources_list_item,parent, false);
                ImageView img = (ImageView)v.findViewById(R.id.imgResourceType);
                img.setVisibility(View.GONE);
                CheckBox onoff = (CheckBox)v.findViewById(R.id.onOffCheckBox);
                onoff.setVisibility(View.GONE);

                TextView name = (TextView)v.findViewById(R.id.txtResourceName);
                TextView count = (TextView)v.findViewById(R.id.txtResourceCountValue);
                TextView price = (TextView)v.findViewById(R.id.txtResourcePriceValue);
                TextView total = (TextView)v.findViewById(R.id.txtResourceTotalValue);

                WorksResources workRes = listWorksRes.get(position);
                name.setText(workRes.getWrNameRus());
                count.setText(String.valueOf(workRes.getWrCount()));
                price.setText(String.valueOf(workRes.getWrCost()));
                total.setText(String.valueOf(workRes.getWrTotalCost()));

                return v;
            }
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

