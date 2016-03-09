package ua.com.expertsoft.android_smeta.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.CustomCalendar.CalendarShower;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;

/**
 * Created by mityai on 28.12.2015.
 */
public class StandardProjectMainAdapter extends BaseExpandableListAdapter implements View.OnClickListener,
        View.OnTouchListener, View.OnLongClickListener{

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int color;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            color = Color.rgb(129,212,250);
            v.setBackgroundColor(color);
        }else
        if (event.getAction() == MotionEvent.ACTION_UP | event.getAction() == MotionEvent.ACTION_CANCEL){
            color = Color.WHITE;
            v.setBackgroundColor(color);
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId() != R.id.imgBuildType) {
            if (((LinearLayout) v.getTag()).getTag() instanceof OS) {
                OS curOs = (OS) ((LinearLayout) v.getTag()).getTag();
                //Open Works...
                OnOpenWorksListener openWorks = (OnOpenWorksListener) context;
                openWorks.onOpenWorks(curOs.getOsProjects(), curOs, null);
            }
        }else{
            context.startActivity(new Intent(context, CalendarShower.class));
        }
        return false;
    }

    public interface OnOpenWorksListener{
        void onOpenWorks(Projects project, OS os, LS ls);
    }

    ArrayList<Projects> projectsList;
    ArrayList<ArrayList<OS>> osList;
    Context context;
    LayoutInflater inflater;
    View groupView;
    View childView;
    Projects currentProj;
    OS currentOs;
    ExpandableListView osListView;

    public StandardProjectMainAdapter(Context ctx,ArrayList<Projects> projs, ArrayList<ArrayList<OS>> osList){
        projectsList = projs;
        this.osList = osList;
        context = ctx;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getGroupCount() {
        return projectsList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return osList.get(groupPosition).size();
    }

    @Override
    public Projects getGroup(int groupPosition) {
        return projectsList.get(groupPosition);
    }

    @Override
    public OS getChild(int groupPosition, int childPosition) {
        return osList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView != null) {
            groupView = convertView;
        }else{
            groupView = inflater.inflate(R.layout.item_exp_local_estim, parent, false);
        }
        currentProj = projectsList.get(groupPosition);
        groupView.setTag(currentProj);
        String name;
        if(FragmentSettings.isDataLanguageRus(context)) {
            name = currentProj.getProjectNameRus();
        }else{
            name = currentProj.getProjectNameUkr()!= null ? currentProj.getProjectNameUkr() : "";
        }
        if(name.length() <= 50) {
            ((TextView) groupView.findViewById(R.id.txtLocalEstimName)).setText(name);
        }else{
            ((TextView) groupView.findViewById(R.id.txtLocalEstimName)).setText(name.substring(0,49)+ "...");
        }
        if(currentProj.getIsOpen()){
            ((ExpandableListView)parent).expandGroup(groupPosition);
        }
        int color;
        if(currentProj.getProjectGuid().equals(ProjectInfo.PROJECT_GUID)){
            color = context.getResources().getColor(android.R.color.holo_blue_light);
            groupView.setBackgroundColor(color);
            groupView.setSelected(true);
        }else{
            color = context.getResources().getColor(android.R.color.background_light);
            groupView.setBackgroundColor(color);
            groupView.setSelected(false);
        }
        ImageView imgCollExp = (ImageView) groupView.findViewById(R.id.imgCollExp);
        if((isExpanded)|(currentProj.getIsOpen())){
            imgCollExp.setImageResource(R.drawable.ic_keyboard_arrow_up);
        }else{
            imgCollExp.setImageResource(R.drawable.ic_keyboard_arrow_down);
        }
        ImageView imgType = (ImageView)groupView.findViewById(R.id.imgBuildType);
        imgType.setOnLongClickListener(this);
        if(currentProj.getProjectType() == 0){
            imgType.setImageResource(R.drawable.ic_public);
        }
        else{
            imgType.setImageResource(R.drawable.ic_insert_drive_file);
        }
        return groupView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView != null) {
            childView = convertView;
        }else{
            childView = inflater.inflate(R.layout.standart_project_item_exp, parent, false);
        }
        ArrayList<ArrayList<LS>> lsList = new ArrayList<ArrayList<LS>>();
        ArrayList<LS> currLsList;
        ArrayList<OS> oscurr = new ArrayList<OS>();
        View thirdLevelView;
        //get list of Projects objectEstimate
        currentOs = osList.get(groupPosition).get(childPosition);
        if(FragmentSettings.isDataLanguageRus(context)) {
            ((TextView) childView.findViewById(R.id.txtSecondLevelItem)).setText(currentOs.getOsNameRus());
        }else{
            ((TextView) childView.findViewById(R.id.txtSecondLevelItem)).setText(currentOs.getOsNameUkr());
        }
        oscurr.add(currentOs);
        currLsList = currentOs.getAllLocalEstimates();
        LinearLayout lsLayout = (LinearLayout)childView.findViewById(R.id.thirdLevelItems);
        lsLayout.removeAllViews();
        for (LS ls : currLsList){
            thirdLevelView = inflater.inflate(R.layout.item_exp_local_estim, null, false );
            thirdLevelView.findViewById(R.id.imgCollExp).setVisibility(View.GONE);
            thirdLevelView.findViewById(R.id.imgBuildType).setVisibility(View.GONE);
            if(FragmentSettings.isDataLanguageRus(context)) {
                ((TextView) thirdLevelView.findViewById(R.id.txtLocalEstimName)).setText(ls.getLsNameRus());
            }else{
                ((TextView) thirdLevelView.findViewById(R.id.txtLocalEstimName)).setText(ls.getLsNameRus());
            }
            thirdLevelView.setPadding(24, 0, 0, 0);
            thirdLevelView.setTag(ls);
            thirdLevelView.setOnClickListener(this);
            thirdLevelView.setOnTouchListener(this);
            thirdLevelView.setClickable(true);
            lsLayout.addView(thirdLevelView);
        }
        LinearLayout secondLevel = (LinearLayout)childView.findViewById(R.id.linearSecondLevel);
        if(! currentOs.getIsOpen()) {
            lsLayout.setVisibility(View.GONE);
            ((ImageView)secondLevel.findViewById(R.id.imgExpColl)).setImageResource(R.drawable.ic_keyboard_arrow_down);
        }else{
            lsLayout.setVisibility(View.VISIBLE);
            ((ImageView) secondLevel.findViewById(R.id.imgExpColl)).setImageResource(R.drawable.ic_keyboard_arrow_up);
        }
        lsLayout.setTag(currentOs);
        secondLevel.setTag(lsLayout);
        secondLevel.setOnClickListener(this);
        secondLevel.setOnTouchListener(this);
        secondLevel.setOnLongClickListener(this);

        lsList.add(currLsList);
        return childView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupCollapsed(int groupPosition){
        projectsList.get(groupPosition).setIsOpen(false);
    }
    @Override
    public void onGroupExpanded(int groupPosition){
        projectsList.get(groupPosition).setIsOpen(true);
    }

    @Override
    public void onClick(View v) {
        if (!(v.getTag() instanceof LS)){
            if(((LinearLayout)v.getTag()).getTag() instanceof OS){
                OS curOs = (OS) ((LinearLayout) v.getTag()).getTag();
                if(! curOs.getIsOpen()){
                    ((LinearLayout) v.getTag()).setVisibility(View.VISIBLE);
                    curOs.setIsOpen(true);
                    ((LinearLayout) v.getTag()).setTag(curOs);
                    ((ImageView) v.findViewById(R.id.imgExpColl)).setImageResource(R.drawable.ic_keyboard_arrow_up);
                }else {
                    ((LinearLayout) v.getTag()).setVisibility(View.GONE);
                    curOs.setIsOpen(false);
                    ((LinearLayout) v.getTag()).setTag(curOs);
                    ((ImageView) v.findViewById(R.id.imgExpColl)).setImageResource(R.drawable.ic_keyboard_arrow_down);
                }
            }
        }else if (v.getTag() instanceof LS){
            //Open Works...
            OnOpenWorksListener openWorks = (OnOpenWorksListener)context;
            openWorks.onOpenWorks(((LS) v.getTag()).getLsProjects(), ((LS) v.getTag()).getLsOs(), ((LS) v.getTag()));
        }
    }
}
