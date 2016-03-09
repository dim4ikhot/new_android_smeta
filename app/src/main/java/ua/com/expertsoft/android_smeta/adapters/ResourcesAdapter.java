package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.fragments.ResourcesFragment;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.WorksResources;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;

/**
 * Created by mityai on 05.01.2016.
 */
public class ResourcesAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    public interface OnCheckedConsistsListener{
        void OnCheckedConsists();
    }


    ArrayList<WorksResources> resList;
    View view;
    WorksResources currRes;
    LayoutInflater inflater;
    Context context;
    TextView resName,resCount,resPrice,resTotal;
    ImageView resType;
    DBORM database;
    CheckBox resOnOff;
    ResourcesFragment parent;
    DecimalFormat df = new DecimalFormat("#.#####");

    public ResourcesAdapter(Context ctx, ArrayList<WorksResources> resources, DBORM base,ResourcesFragment parent){
        context = ctx;
        resList = resources;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        database = base;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return resList.size();
    }

    @Override
    public WorksResources getItem(int position) {
        return resList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initControls(View v){
        resName = (TextView)v.findViewById(R.id.txtResourceName);
        resCount = (TextView)v.findViewById(R.id.txtResourceCountValue);
        resPrice = (TextView)v.findViewById(R.id.txtResourcePriceValue);
        resTotal= (TextView)v.findViewById(R.id.txtResourceTotalValue);
        resType = (ImageView)v.findViewById(R.id.imgResourceType);
        resOnOff = (CheckBox)v.findViewById(R.id.onOffCheckBox);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        view = convertView != null ? convertView :
                              inflater.inflate(R.layout.resources_list_item, parent, false);
        currRes = resList.get(position);
        initControls(view);
        switch (currRes.getWrPart()){
            case 1://TZ
                resType.setImageResource(R.drawable.ic_attach_money);
                break;
            case 2://MACH
                resType.setImageResource(R.drawable.ic_directions_car);
                break;
            case 3://MATERIAL
                resType.setImageResource(R.drawable.ic_gavel);
                break;
        }
        String name;
        if(FragmentSettings.isDataLanguageRus(context)) {
            name = currRes.getWrNameRus();
        }else{
            name = currRes.getWrNameUkr()!= null ? currRes.getWrNameUkr() : "";
        }
        if (name.length() > 50){
            resName.setText(name.substring(0, 49) + "...");
        }else{
            resName.setText(name);
        }
        resCount.setText(df.format(currRes.getWrCount()));
        resPrice.setText(df.format(currRes.getWrCost()));
        resTotal.setText(df.format(currRes.getWrTotalCost()));
        resOnOff.setTag(currRes);
        boolean isOn = currRes.getWrOnOff() == 1;
        resOnOff.setChecked(isOn);/*
        if(currRes.getWrOnOff() == 1) {
            resOnOff.setChecked(true);
        }else{
            resOnOff.setChecked(false);
        }*/

        resOnOff.setOnCheckedChangeListener(this);
        view.setTag(currRes);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        currRes = (WorksResources)buttonView.getTag();
        if(isChecked) {
            currRes.setWrOnOff(1);
        }else{
            currRes.setWrOnOff(0);
        }
        try{
            database.getHelper().getWorksResDao().update(currRes);
        }catch(SQLException e){
            e.printStackTrace();
        }
        parent.OnCheckedConsists();
    }
}
