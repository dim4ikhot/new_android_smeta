package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.Facts;

/*
 * Created by mityai on 05.01.2016.
 */
public class FactsAdapter extends BaseAdapter implements View.OnClickListener {

    ArrayList<Facts> factsList;
    Facts currentFact;
    View factsView;
    LayoutInflater inflater;
    Context context;
    TextView startDate,stopDate,percentDone,countDone,byFact,byPlan,factDescription;
    ImageView imgDelete;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault());
    DecimalFormat decForm = new DecimalFormat("#.##");
    DecimalFormat decFormCount = new DecimalFormat("#.####");

    public interface OnDeleteFactListener{
        void onDeleteFact(Facts deletingFact);
    }

    public FactsAdapter(Context ctx, ArrayList<Facts> list){
        context = ctx;
        factsList = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return factsList.size();
    }

    @Override
    public Facts getItem(int position) {
        return factsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        factsView = convertView != null ? convertView
                       : inflater.inflate(R.layout.facts_list_item, parent, false);
        startDate = (TextView)factsView.findViewById(R.id.txtStartDate);
        stopDate = (TextView)factsView.findViewById(R.id.txtStopDate);
        percentDone = (TextView)factsView.findViewById(R.id.txtPercentDone);
        countDone = (TextView)factsView.findViewById(R.id.txtCountDone);
        byFact = (TextView)factsView.findViewById(R.id.txtByFacts);
        byPlan = (TextView)factsView.findViewById(R.id.txtByPlan);
        imgDelete = (ImageView)factsView.findViewById(R.id.imgDeleteFact);
        imgDelete.setOnClickListener(this);
        currentFact = factsList.get(position);

        imgDelete.setTag(currentFact);
        factDescription = (TextView)factsView.findViewById(R.id.txtDescription);

        startDate.setText(dateFormat.format(currentFact.getFactsStart()));
        stopDate.setText(dateFormat.format(currentFact.getFactsStop()));
        percentDone.setText(decForm.format(currentFact.getFactsMakesPercent()));
        countDone.setText(decFormCount.format(currentFact.getFactsMakesCount()));
        byFact.setText(decForm.format(currentFact.getFactsByFacts()));
        byPlan.setText(decForm.format(currentFact.getFactsByPlan()));
        factDescription.setText(currentFact.getFactsDesc());

        factsView.setTag(currentFact);
        return factsView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imgDeleteFact){
            OnDeleteFactListener del = (OnDeleteFactListener)context;
            del.onDeleteFact((Facts)v.getTag());
        }
    }
}
