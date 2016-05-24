package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ua.com.expertsoft.android_smeta.FactsCommonOperations;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;
import ua.com.expertsoft.android_smeta.static_data.SelectedLocal;

/**
 * Created by mityai on 29.12.2015.
 */
public class TotalWorksAdapter  extends BaseAdapter implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener{

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.workFactstxt) {
            factsMoreListener.onFactsClick(v);
        }else {
            factsMoreListener.onMoreClick(v);
        }
    }

    public interface OnGetChangedWorkListener{
        void onGetChangedWork(int position, Works changedWork);
    }
    public interface OnWorksItemsClickListener{
        void onFactsClick(View v);
        void onMoreClick(View v);
    }

    Context context;
    LayoutInflater inflater;
    ArrayList<Works> worksList;
    View workView;
    Works currentWork;
    DBORM database;
    OnWorksItemsClickListener factsMoreListener;

    public TotalWorksAdapter(Context ctx, ArrayList<Works> works, DBORM base){
        context = ctx;
        inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        worksList = works;
        database = base;
        factsMoreListener = (OnWorksItemsClickListener)ctx;
    }

    @Override
    public int getCount() {
        return worksList.size();
    }

    @Override
    public Works getItem(int position) {
        return worksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        workView = convertView != null ? convertView : inflater.inflate(R.layout.works_item_pos,parent, false);
        currentWork = worksList.get(position);
        String name;
        if(FragmentSettings.isDataLanguageRus(context)) {
            name = currentWork.getWName();
        }else{
            name = currentWork.getWNameUkr() != null ? currentWork.getWNameUkr() : "";
        }
        TextView normName = (TextView) workView.findViewById(R.id.workNametext);
        if(name.length() <= 50 ) {
            normName.setText(name);
        }else{
            name = name.substring(0,49)+"...";
            normName.setText(name);
        }
        double madePercent = currentWork.getWPercentDone();
        if (madePercent > 100){
            normName.setTextColor(Color.RED);
        }
        else{
            normName.setTextColor(Color.GRAY);
        }
        boolean isDone = madePercent == 100f;
        CheckBox isDoneBox = (CheckBox) workView.findViewById(R.id.checkBoxDoneWork);
        isDoneBox.setTag(currentWork);
        isDoneBox.setChecked(isDone);
        isDoneBox.setEnabled(! isDone);
        isDoneBox.setOnCheckedChangeListener(this);
        TextView factsView = (TextView)workView.findViewById(R.id.workFactstxt);
        factsView.setTag(currentWork);
        TextView others = (TextView)workView.findViewById(R.id.workMore);
        others.setTag(currentWork);
        others.setOnClickListener(this);
        factsView.setOnClickListener(this);
        if(!currentWork.getWRec().equals("note")) {
            isDoneBox.setVisibility(View.VISIBLE);
            factsView.setVisibility(View.VISIBLE);
            others.setVisibility(View.VISIBLE);
        }else{
            isDoneBox.setVisibility(View.GONE);
            factsView.setVisibility(View.GONE);
            others.setVisibility(View.GONE);
        }

        ImageView classification =  (ImageView)workView.findViewById(R.id.classification);
        switch(currentWork.getWRec()){
            case "record":
            case "work":
                classification.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_assignment_ind));
                break;
            case "machine":
            case "mach":
                classification.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_directions_car));
                break;
            case "resource":
            case "material":
                classification.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_gavel));
                break;
        }
        workView.setTag(currentWork);
        return workView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Works checkWork = (Works) buttonView.getTag();
        int index = SelectedLocal.localEstimate.findWorkPositionByGuid(checkWork);
        ArrayList<Facts> factsList = checkWork.getAllFacts();
        float percentDone = 0;
        float countDone = 0;
        if(factsList.size() == 0){
            factsList = database.getWorksFacts(checkWork);
            checkWork.setAllFactss(factsList);
        }
        if (factsList.size() != 0 ) {
            for (Facts f : factsList) {
                percentDone += f.getFactsMakesPercent();
                countDone += f.getFactsMakesCount();
            }
        }else{
            percentDone = checkWork.getWPercentDone();
            countDone = checkWork.getWCountDone();
        }
        if (isChecked) {
            if(percentDone != 100) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(year, month, day, 8, 0);
                Date factStart = calendar.getTime();
                calendar.set(year, month, day, 17, 0);
                Date factStop = calendar.getTime();
                // true - period exists; false - otherwise
                if(!checkForExistsPeriod(factStart,factStop,checkWork)) {
                    checkWork.setWPercentDone(100);
                    checkWork.setWCountDone(checkWork.getWCount());
                    Facts fact = new Facts();
                    fact.setFactsStart(factStart);
                    fact.setFactsMakesPercent(100 - percentDone);
                    fact.setFactsMakesCount(checkWork.getWCount() - countDone);
                    FactsCommonOperations.setStartDate(factStart);
                    float factWork =(checkWork.getWTZTotal() * fact.getFactsMakesPercent()) / 100;
                    float planWork = 9;
                    factStop = FactsCommonOperations.recalculateStopDate(planWork,factWork,factStop);
                    FactsCommonOperations.setStartDate(factStart);
                    planWork = FactsCommonOperations.calculateWorkingHours(factStop);
                    fact.setFactsStop(factStop);
                    fact.setFactsByPlan(planWork);
                    fact.setFactsByFacts(factWork);
                    fact.setFactsDesc("");
                    fact.setFactsWorkId(checkWork.getWorkId());
                    fact.setFactsParent(checkWork);
                    fact.setFactsGuid(UUID.randomUUID().toString());
                    checkWork.setCurrentFact(fact);
                    try {
                        database.getHelper().getFactsDao().create(fact);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    InfoCommonDialog existsPeriod = new InfoCommonDialog();
                    existsPeriod.setMessage(context.getResources().getString(R.string.an_error_message));
                    existsPeriod.setTitle(context.getResources().getString(R.string.an_error_title));
                    buttonView.setChecked(false);
                    existsPeriod.show(((AppCompatActivity)context).getSupportFragmentManager(), "periodExists");
                }
            }
        } else{
            if (percentDone != 100) {
                checkWork.setWPercentDone(percentDone);
                checkWork.setWCountDone(countDone);
            }else{
                buttonView.setChecked(true);
                String allDone = context.getResources().getString(R.string.made_100_percent);
                InfoCommonDialog infoDialog = new InfoCommonDialog();
                infoDialog.setMessage(allDone);
                infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
                //Toast.makeText(context, allDone, Toast.LENGTH_SHORT).show();
            }
        }
        try {
            database.getHelper().getWorksDao().createOrUpdate(checkWork);
            if(index != -1){
                OnGetChangedWorkListener listener = (OnGetChangedWorkListener)context;
                listener.onGetChangedWork(index, checkWork);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private boolean checkForExistsPeriod(Date startDate, Date stopDate, Works work){
        boolean result = false;
        for(Facts fact: work.getAllFacts()){
            //NOTE: if(startDate >= fact.getFactsStart()) and (startDate<=fact.getFactsStop())
            if (((startDate.after(fact.getFactsStart())|| startDate.equals(fact.getFactsStart()))&
                    (startDate.before(fact.getFactsStop())|| startDate.equals(fact.getFactsStop())))||
                    ((stopDate.after(fact.getFactsStart())|| stopDate.equals(fact.getFactsStart()))&
                            (stopDate.before(fact.getFactsStop())|| stopDate.equals(fact.getFactsStop())))) {
                result = true;
                break;
            }
            else if (((fact.getFactsStart().after(startDate)|| fact.getFactsStart().equals(startDate)) &
                    (fact.getFactsStart().before(stopDate)||fact.getFactsStart().equals(stopDate))
            ) ||
                    ((fact.getFactsStop().after(startDate)|| fact.getFactsStop().equals(startDate)) &
                            (fact.getFactsStop().before(stopDate)||fact.getFactsStop().equals(stopDate))
                    )) {
                result = true;
                break;
            }
        }
        return result;
    }
}
