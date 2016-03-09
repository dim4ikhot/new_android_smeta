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
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.static_data.SelectedFact;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.Works;

/**
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
    LayoutInflater inflater;
    Works works;
    Facts fact;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");

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
                    float restPercent = 100 - works.getWPercentDone();
                    if (percent > restPercent) {
                        s.replace(0, s.toString().length(), String.valueOf(restPercent));
                    } else {
                        float cnt = (percent * works.getWCount()) / 100;
                        count.setText(String.valueOf(cnt));
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
                    float restCnt = works.getWCount() - works.getWCountDone();
                    if (cnt > restCnt) {
                        s.replace(0, s.toString().length(), String.valueOf(restCnt));
                    } else {
                        float perc = (100 * cnt) / works.getWCount();
                        percent.setText(String.valueOf(perc));
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
                start.setText(sdfDate.format((new GregorianCalendar(year,monthOfYear,dayOfMonth)).getTime()));
                break;
            case STOP_DATE:
                stop.setText(sdfDate.format((new GregorianCalendar(year,monthOfYear,dayOfMonth)).getTime()));
                break;
        }
    }

    @Override
    public void OnGetTime(int hour, int minute, int which) {
        switch(which){
            case START_DATE:
                startTime.setText(hour + ":" + minute);
                break;
            case STOP_DATE:
                stopTime.setText(hour + ":" + minute);
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
        }
        adg.setView(view);
        adg.setPositiveButton("OK", this);
        adg.setNegativeButton(getActivity().getResources().getString(R.string.btnCancelCaption), this);
        adg.setCancelable(false);
        return adg.create();
    }

    private void fillDialog(Facts f){
        percent.setText(String.valueOf(f.getFactsMakesPercent()));
        start.setText(sdfDate.format(f.getFactsStart().getTime()));
        stop.setText(sdfDate.format(f.getFactsStop().getTime()));
        startTime.setText(sdfTime.format(f.getFactsStart().getTime()));
        stopTime.setText(sdfTime.format(f.getFactsStop().getTime()));
        count.setText(String.valueOf(f.getFactsMakesCount()));
        desc.setText(f.getFactsDesc());
    }

    private void initControls(View view){
        percent = (EditText)view.findViewById(R.id.editPercentDone);
        percent.addTextChangedListener(percentChange);
        count = (EditText)view.findViewById(R.id.editCountDone);
        count.addTextChangedListener(countChange);
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
                    try {
                        facts.setFactsStart(sdf.parse(start.getText().toString() + " " +
                                startTime.getText()));
                        facts.setFactsStop(sdf.parse(stop.getText().toString() + " " +
                                stopTime.getText()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    float byPlan = (facts.getFactsStop().getTime() - facts.getFactsStart().getTime())/(60*60*1000);
                    float byFact = (works.getWTZTotal() * facts.getFactsMakesPercent())/100 ;
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
