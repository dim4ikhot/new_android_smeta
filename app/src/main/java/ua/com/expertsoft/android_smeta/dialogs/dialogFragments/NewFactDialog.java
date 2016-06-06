package ua.com.expertsoft.android_smeta.dialogs.dialogFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.FactsCommonOperations;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;
import ua.com.expertsoft.android_smeta.static_data.SelectedFact;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.Works;

/*
 * Created by mityai on 05.01.2016.
 */
public class NewFactDialog extends DialogFragment implements DialogInterface.OnClickListener,
        View.OnClickListener, StartDateDialog.OnGetDateListener, TimeDialog.OnGetTimeListener {

    static final int START_DATE = 0;
    static final int STOP_DATE = 1;

    EditText percent;
    EditText count;
    EditText start;
    EditText stop;
    EditText startTime;
    EditText stopTime;
    EditText desc;
    View view;
    AlertDialog dialog;
    LayoutInflater inflater;
    Works works;
    Facts fact;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    TextWatcher percentChange = new TextWatcher()
    {
        @Override
        public void beforeTextChanged (CharSequence s,int start, int count, int after){

        }

        @Override
        public void onTextChanged (CharSequence s,int start, int before, int count){
        }

        @Override
        public void afterTextChanged (Editable s){
            if(percent.hasFocus()) {
                if (!s.toString().equals("")) {
                    float percent = Float.parseFloat(s.toString());
                    float restPercent;
                    if(fact == null) {
                        restPercent = 100 - works.getWPercentDone();
                    }else{
                        restPercent = 100 - (works.getWPercentDone()-fact.getFactsMakesPercent());
                    }
                    if (percent > restPercent) {
                        s.replace(0, s.toString().length(), String.valueOf(restPercent));
                    } else {
                        double cnt = ZmlParser.roundTo((percent * works.getWCount()) / 100,4);
                        DecimalFormat df = new DecimalFormat("#.####");
                        count.setText(df.format(cnt).replace(",","."));
                    }
                }
            }
        }
    };

    TextWatcher countChange = new TextWatcher()
    {
        @Override
        public void beforeTextChanged (CharSequence s,int start, int count, int after){

        }

        @Override
        public void onTextChanged (CharSequence s,int start, int before, int count){

        }

        @Override
        public void afterTextChanged (Editable s){
            if(count.hasFocus()) {
                if (!s.toString().equals("")) {
                    float cnt = Float.parseFloat(s.toString());
                    float restCnt;
                    if(fact == null) {
                        restCnt = works.getWCount() - works.getWCountDone();
                    }else{
                        restCnt = works.getWCount() - (works.getWCountDone()-fact.getFactsMakesCount());
                    }
                    if (cnt > restCnt) {
                        s.replace(0, s.toString().length(), String.valueOf(restCnt));
                    } else {
                        double perc = ZmlParser.roundTo((100 * cnt) / works.getWCount(),2);
                        DecimalFormat df = new DecimalFormat("#.##");
                        percent.setText(df.format(perc).replace(",","."));
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        Bundle params = new Bundle();
        StartDateDialog newFragment;
        TimeDialog timeFragment;
        switch(v.getId()){
            case R.id.editStartDate:
                newFragment = new StartDateDialog();
                params.putInt("which", 0);
                newFragment.setArguments(params);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.editStopDate:
                newFragment = new StartDateDialog();
                params = new Bundle();
                params.putInt("which", 1);
                newFragment.setArguments(params);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
                break;
            case R.id.editStartTime:
                timeFragment = new TimeDialog();
                params = new Bundle();
                params.putInt("which", 0);
                timeFragment.setArguments(params);
                timeFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
            case R.id.editStopTime:
                timeFragment = new TimeDialog();
                params = new Bundle();
                params.putInt("which", 1);
                timeFragment.setArguments(params);
                timeFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
                break;
        }
    }

    @Override
    public void OnGetDate(int year, int monthOfYear, int dayOfMonth, int which) {
        switch(which){
            case START_DATE:
                String stopDate = stop.getText().toString();
                if (stopDate.equals("")) {
                    start.setText(sdfDate.format((new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime()));
                }
                else{
                    try {
                        Date stopD = sdfDate.parse(stopDate);
                        Date startD = (new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime();
                        if(startD.after(stopD)|| startD.equals(stopD)){
                            stopD = startD;
                            GregorianCalendar startTime = new GregorianCalendar();
                            startTime.setTime(sdfTime.parse(this.startTime.getText().toString()));
                            Date newStopTime = new GregorianCalendar(0,0,0,
                                    startTime.get(Calendar.HOUR_OF_DAY)+1,
                                    startTime.get(Calendar.MINUTE)).getTime();
                            this.stopTime.setText(sdfTime.format(newStopTime));
                        }
                        start.setText(sdfDate.format(startD));
                        stop.setText(sdfDate.format(stopD));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                startTime.setEnabled(true);
                break;
            case STOP_DATE:
                String startDate = start.getText().toString();
                if(startDate.equals("")) {
                    stop.setText(sdfDate.format((new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime()));
                }
                else{
                    try{
                        Date startD = sdfDate.parse(startDate);
                        Date stopD = (new GregorianCalendar(year, monthOfYear, dayOfMonth)).getTime();
                        if(stopD.before(startD) || stopD.equals(startD)){
                            stopD = startD;
                            GregorianCalendar startTime = new GregorianCalendar();
                            startTime.setTime(sdfTime.parse(this.startTime.getText().toString()));
                            Date newStopTime = new GregorianCalendar(0,0,0,
                                    startTime.get(Calendar.HOUR_OF_DAY)+1,
                                    startTime.get(Calendar.MINUTE)).getTime();
                            this.stopTime.setText(sdfTime.format(newStopTime));
                        }
                        start.setText(sdfDate.format(startD));
                        stop.setText(sdfDate.format(stopD));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                stopTime.setEnabled(true);
                break;
        }
    }

    @Override
    public void OnGetTime(int hour, int minute, int which) {
        String stopTimeString, startDateString,stopDateString;
        boolean sameDates;
        Date startTimeDate;
        Date startDate;
        Date stopDate;
        GregorianCalendar startCalendar,stopCalendar;
        int startHour;
        int startMinute;
        int stopHour;
        int stopMinute;
        switch(which){
            case START_DATE:
                stopTimeString = stopTime.getText().toString();
                startTimeDate = new GregorianCalendar(0,0,0,hour,minute).getTime();
                try {
                    startDateString = start.getText().toString();
                    stopDateString = stop.getText().toString();
                    sameDates = !startDateString.equals("") || !stopDateString.equals("");
                    if (sameDates) {
                        startDate = sdfDate.parse(startDateString);
                        stopDate = sdfDate.parse(stopDateString);
                        sameDates = startDate.equals(stopDate);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    sameDates = false;
                }
                if (stopTimeString.equals("")|| !sameDates) {
                    startTime.setText(sdfTime.format(startTimeDate));
                }
                else{
                    try {
                        Date stopTimeDate = sdfTime.parse(stopTimeString);
                        startCalendar = new GregorianCalendar();
                        startCalendar.setTime(startTimeDate);
                        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
                        startMinute = startCalendar.get(Calendar.MINUTE);
                        stopCalendar = new GregorianCalendar();
                        stopCalendar.setTime(stopTimeDate);
                        stopHour = stopCalendar.get(Calendar.HOUR_OF_DAY);
                        stopMinute = stopCalendar.get(Calendar.MINUTE);

                        if(startHour == stopHour){
                            if(stopMinute <= startMinute){
                                startCalendar.add(Calendar.HOUR_OF_DAY,1);
                                stopTimeDate = startCalendar.getTime();
                            }
                        }
                        else if(stopHour < startHour){
                            startCalendar.add(Calendar.HOUR_OF_DAY,1);
                            stopTimeDate = startCalendar.getTime();
                        }
                        startTime.setText(sdfTime.format(startTimeDate));
                        stopTime.setText(sdfTime.format(stopTimeDate));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case STOP_DATE:
                String startTimeString = startTime.getText().toString();
                Date stopTimeDate = new GregorianCalendar(0,0,0,hour,minute).getTime();
                try{
                    startDateString = start.getText().toString();
                    stopDateString = stop.getText().toString();
                    sameDates = !startDateString.equals("") || !stopDateString.equals("");
                    if(sameDates) {
                        stopDate = sdfDate.parse(stopDateString);
                        startDate = sdfDate.parse(startDateString);
                        sameDates = stopDate.equals(startDate);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    sameDates = false;
                }
                if(startTimeString.equals("") || !sameDates) {
                    stopTime.setText(sdfTime.format(stopTimeDate));
                }
                else{
                    try {
                        startTimeDate = sdfTime.parse(startTimeString);
                        startCalendar = new GregorianCalendar();
                        startCalendar.setTime(startTimeDate);
                        startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
                        startMinute = startCalendar.get(Calendar.MINUTE);
                        stopCalendar = new GregorianCalendar();
                        stopCalendar.setTime(stopTimeDate);
                        stopHour = stopCalendar.get(Calendar.HOUR_OF_DAY);
                        stopMinute = stopCalendar.get(Calendar.MINUTE);

                        if(startHour == stopHour){
                            if(stopMinute <= startMinute){
                                startCalendar.add(Calendar.HOUR_OF_DAY,1);
                                stopTimeDate = startCalendar.getTime();
                            }
                        }
                        else if(stopHour < startHour){
                            startCalendar.add(Calendar.HOUR_OF_DAY,1);
                            stopTimeDate = startCalendar.getTime();
                        }
                        startTime.setText(sdfTime.format(startTimeDate));
                        stopTime.setText(sdfTime.format(stopTimeDate));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public interface OnGetFacts{
        void getFacts(Facts currentFact);
    }

    public Dialog onCreateDialog(Bundle params){
        super.onCreateDialog(params);
        AlertDialog.Builder adg = new AlertDialog.Builder(getActivity());
        adg.setTitle(getActivity().getResources().getString(R.string.dialogNewFactTitle));
        inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view =  inflater.inflate(R.layout.new_fact_activity, null, false);
        initControls(view);
        works = SelectedWork.work; //(Works)getArguments().getSerializable("work");
        fact = SelectedFact.fact; //(Facts)getArguments().getSerializable("fact");
        if(fact != null){
            fillDialog(fact);
        }else{
            if (works.getAllFacts().size() == 0) {
                start.setText(sdfDate.format(works.getWStartDate()));
                startTime.setText(sdfTime.format(works.getWStartDate().getTime()));
            }else{
                Date startDate = works.getCurrentFacts(
                                      works.getAllFacts().size()-1).getFactsStop();
                GregorianCalendar calendar = new GregorianCalendar();
                calendar.setTime(startDate);
                if (calendar.get(Calendar.HOUR_OF_DAY) >= 17){
                    calendar.add(Calendar.DAY_OF_MONTH,1);
                    calendar.set(Calendar.HOUR_OF_DAY,8);
                    calendar.set(Calendar.MINUTE,0);
                }
                startDate = FactsCommonOperations.correctingStartDate(calendar.getTime());
                start.setText(sdfDate.format(startDate));
                startTime.setText(sdfTime.format(startDate));
            }
            startTime.setEnabled(true);
            stop.setText(sdfDate.format(new Date()));
            stopTime.setText(sdfTime.format(new Date()));
            stopTime.setEnabled(true);
        }
        adg.setView(view);
        adg.setPositiveButton("OK", this);
        adg.setNegativeButton(getActivity().getResources().getString(R.string.btnCancelCaption), this);
        adg.setCancelable(false);
        dialog = adg.create();
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

    private void fillDialog(Facts f){
        percent.setText(String.valueOf(f.getFactsMakesPercent()));
        start.setText(sdfDate.format(f.getFactsStart().getTime()));
        stop.setText(sdfDate.format(f.getFactsStop().getTime()));
        startTime.setText(sdfTime.format(f.getFactsStart().getTime()));
        stopTime.setText(sdfTime.format(f.getFactsStop().getTime()));
        count.setText(String.valueOf(f.getFactsMakesCount()));
        desc.setText(f.getFactsDesc());
        startTime.setEnabled(true);
        stopTime.setEnabled(true);
    }

    private double hoursBetweenDates(Date start, Date stop){
        long secs = (stop.getTime() - start.getTime()) / 1000;
        long hours = secs / 3600;
        secs = secs % 3600;
        long mins = secs / 60;
        return  hours + (double)mins/60;
    }

    private void initControls(View view){
        percent = (EditText)view.findViewById(R.id.editPercentDone);
        percent.addTextChangedListener(percentChange);
        percent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    double totalH = 0;
                    double totalTZ = (double)works.getWTZTotal();
                    try {
                        totalH = hoursBetweenDates(sdf.parse(start.getText().toString() + " " + startTime.getText()),
                                sdf.parse(stop.getText().toString() + " " + stopTime.getText()));
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    if (totalTZ == 0){
                        totalTZ = totalH;
                    }
                    double inPercent = ((totalH*100)/totalTZ);
                    DecimalFormat df = new DecimalFormat("#.##");
                    percent.requestFocus();
                    percent.setText(df.format(inPercent).replace(",","."));
                }
                return false;
            }
        });
        count = (EditText)view.findViewById(R.id.editCountDone);
        count.addTextChangedListener(countChange);
        count.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    double cnt = works.getWCount();
                    double totalH = 0;
                    double totalTZ = (double)works.getWTZTotal();
                    try {
                        totalH = hoursBetweenDates(sdf.parse(start.getText().toString() + " " + startTime.getText()),
                                sdf.parse(stop.getText().toString() + " " + stopTime.getText()));
                    }catch(ParseException e){
                        e.printStackTrace();
                    }
                    double inPercent = (cnt*totalH/totalTZ);
                    DecimalFormat df = new DecimalFormat("#.##");
                    count.setText(df.format(inPercent).replace(",","."));
                }
            }
        });
        start = (EditText)view.findViewById(R.id.editStartDate);
        start.setOnClickListener(this);
        stop = (EditText)view.findViewById(R.id.editStopDate);
        stop.setOnClickListener(this);
        startTime = (EditText)view.findViewById(R.id.editStartTime);
        stopTime = (EditText)view.findViewById(R.id.editStopTime);
        startTime.setOnClickListener(this);
        stopTime.setOnClickListener(this);
        desc = (EditText)view.findViewById(R.id.editDescription);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case Dialog.BUTTON_NEGATIVE:
                dismiss();
                break;
            case Dialog.BUTTON_POSITIVE:
                if((percent.getText().length() > 0)&(count.getText().length() > 0)&
                        (start.getText().length() > 0)&(stop.getText().length() > 0)&
                        (startTime.getText().length() > 0)&(stopTime.getText().length() > 0)) {
                    Facts facts = fact != null ?fact: new Facts();

                    facts.setFactsMakesPercent(Float.parseFloat(percent.getText().toString()));
                    facts.setFactsMakesCount(Float.parseFloat(count.getText().toString()));
                    Date startDate;
                    Date stopDate;
                    try {
                        startDate = sdf.parse(start.getText().toString() + " " +
                                startTime.getText());
                        stopDate = sdf.parse(stop.getText().toString() + " " +
                                stopTime.getText());
                        /*
                        facts.setFactsStart(sdf.parse(start.getText().toString() + " " +
                                startTime.getText()));
                        facts.setFactsStop(sdf.parse(stop.getText().toString() + " " +
                                stopTime.getText()));
                                */
                    } catch (ParseException e) {
                        e.printStackTrace();
                        startDate = new Date();
                        stopDate = new Date();
                    }
                    float byPlan; // = (stopDate.getTime() - startDate.getTime())/(60*60*1000);
                    float byFact = (works.getWTZTotal() * facts.getFactsMakesPercent())/100;
                    //correcting start date
                    startDate = FactsCommonOperations.checkFactForPeriodStart(startDate, stopDate, works, fact);
                    FactsCommonOperations.setStartDate(startDate);
                    stopDate = FactsCommonOperations.checkFactForPeriodStop(startDate,stopDate, works, facts);
                    byPlan = FactsCommonOperations.calculateWorkingHours(stopDate);

                    facts.setFactsStart(startDate);
                    facts.setFactsStop(stopDate);
                    facts.setFactsByPlan(byPlan);
                    facts.setFactsByFacts(byFact);
                    facts.setFactsDesc(desc.getText().toString());
                    ((OnGetFacts) getActivity()).getFacts(facts);
                    dismiss();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(),
                                       "Заполнены не все поля", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
