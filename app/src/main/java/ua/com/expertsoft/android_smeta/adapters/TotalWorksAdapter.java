package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;

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

    public TotalWorksAdapter(){}

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
        if(name.length() <= 50 ) {
            ((TextView) workView.findViewById(R.id.workNametext)).setText(name);
        }else{
            ((TextView) workView.findViewById(R.id.workNametext)).setText(name.substring(0,49)+"...");
        }
        boolean isDone = currentWork.getWPercentDone() == 100f;
        CheckBox isDoneBox = (CheckBox) workView.findViewById(R.id.checkBoxDoneWork);
        isDoneBox.setTag(currentWork);
        isDoneBox.setChecked(isDone);
        if(! isDone) {
            isDoneBox.setEnabled(true);
        }else{
            isDoneBox.setEnabled(false);
        }
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
        workView.setTag(currentWork);
        return workView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Works checkWork = (Works) buttonView.getTag();
        int index = worksList.indexOf(checkWork);
        ArrayList<Facts> factsList = checkWork.getAllFacts();
        float percentDone = 0f;
        float countDone = 0f;
        for(Facts f : factsList){
            percentDone += f.getFactsMakesPercent();
            countDone += f.getFactsMakesCount();
        }
        if (isChecked) {
            if(checkWork.getWPercentDone() != 100) {
                checkWork.setWPercentDone(100);
                checkWork.setWCountDone(checkWork.getWCount());
                Facts fact = new Facts();
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.set(year, month, day, 8, 0);
                fact.setFactsStart(calendar.getTime());
                calendar.set(year, month, day, 17, 0);
                fact.setFactsStop(calendar.getTime());
                fact.setFactsMakesPercent(100 - percentDone);
                fact.setFactsMakesCount(checkWork.getWCount() - countDone);
                fact.setFactsByPlan(9);
                fact.setFactsByFacts((checkWork.getWTZTotal() * fact.getFactsMakesPercent()) / 100);
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
        } else{
            if (percentDone != 100) {
                checkWork.setWPercentDone(percentDone);
                checkWork.setWCountDone(countDone);
            }else{
                buttonView.setChecked(true);
                Toast.makeText(context, "Выполнено 100%. Детали на вкладке 'Факты'", Toast.LENGTH_SHORT).show();
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
}
