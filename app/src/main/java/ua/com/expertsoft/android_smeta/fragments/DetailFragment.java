package ua.com.expertsoft.android_smeta.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.data.Works;

/**
 * Created by mityai on 04.01.2016.
 */
public class DetailFragment extends Fragment {

    Works currWork;
    View returnedView;
    TextView cipherValue, nameValue, measuredValue, countValue;
    EditText costOfOneTotal, salaryOfOne,
            costOfOneMachine, salaryOfOneMachine,
            totalCostCommon, totalSalary,
            totalCostMachine,totalSalaryMachine,
            laborCostOfOneWorker,laborCostOfOneMachine,
            laborTotalCostWorker,laborTotalCostMachine,
            startDate,percentDone,countDone;
    Intent intent;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");

    public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle params){
        super.onCreateView(inflater,group,params);
        intent = getActivity().getIntent();
        //currWork = (Works)getArguments().getSerializable("adapterWork");
        currWork = SelectedWork.work;
        if (currWork != null){
            returnedView = inflater.inflate(R.layout.detail_layout_new_new, group, false);
            initControls(returnedView);
            fillTheWorksDetail(currWork);
        }
        return returnedView;
    }

    public DetailFragment(){
    }

    private void initControls(View v){
        cipherValue = (TextView)v.findViewById(R.id.posCipherValue);
        nameValue = (TextView)v.findViewById(R.id.posFullNameValue);
        measuredValue = (TextView)v.findViewById(R.id.posMeasuredValue);
        countValue = (TextView)v.findViewById(R.id.posCountValue);
        costOfOneTotal = (EditText)v.findViewById(R.id.editTotalOneValue);
        salaryOfOne = (EditText)v.findViewById(R.id.editZpOneValue);
        costOfOneMachine = (EditText)v.findViewById(R.id.editUseMachineOneValue);
        salaryOfOneMachine = (EditText)v.findViewById(R.id.editZpMachineOneValue);
        totalCostCommon = (EditText)v.findViewById(R.id.editPriceTotalValue);
        totalSalary = (EditText)v.findViewById(R.id.editZpTtotalValue);
        totalCostMachine = (EditText)v.findViewById(R.id.editUseMachineTotalValue);
        totalSalaryMachine = (EditText)v.findViewById(R.id.editZpMachineTotalValue);

        laborCostOfOneWorker = (EditText)v.findViewById(R.id.editHoursOneWValue);
        laborTotalCostWorker = (EditText)v.findViewById(R.id.editHoursTotalWValue);

        laborCostOfOneMachine = (EditText)v.findViewById(R.id.editHoursOneMValue);
        laborTotalCostMachine = (EditText)v.findViewById(R.id.editHoursTotalMValue);
/*
        startDate = (EditText)v.findViewById(R.id.editStartExecuting);
        percentDone = (EditText)v.findViewById(R.id.editExecutingPercent);
        countDone = (EditText)v.findViewById(R.id.editExecutingCount);

        btnOk = (Button)v.findViewById(R.id.applyChanges);
        btnSetDate = (ImageView)v.findViewById(R.id.btnSetDate);
        btnApplyFacts = (ImageView)v.findViewById(R.id.btnApplyExecuting);
        layoutApply = (LinearLayout)v.findViewById(R.id.layoutApply);
*/
    }

    public void fillTheWorksDetail(Works currWork){
        if (currWork != null){
            DecimalFormat decf = new DecimalFormat("0.####");
            cipherValue.setText(currWork.getWCipher());
            if(FragmentSettings.isDataLanguageRus(getActivity())) {
                nameValue.setText(currWork.getWName());
            }else{
                nameValue.setText(currWork.getWNameUkr()!= null ? currWork.getWNameUkr()  : "" );
            }
            measuredValue.setText(currWork.getWMeasuredRus());
            countValue.setText(decf.format(currWork.getWCount()).replace(",", "."));
            costOfOneTotal.setText(String.valueOf(currWork.getWItogo()));
            salaryOfOne.setText(String.valueOf(currWork.getWZP()));
            costOfOneMachine.setText(String.valueOf(currWork.getWMach()));
            salaryOfOneMachine.setText(String.valueOf(currWork.getWZPMach()));
            totalCostCommon.setText(String.valueOf(currWork.getWTotal()));
            totalSalary.setText(decf.format(currWork.getWZPTotal()).replace(",", "."));
            totalCostMachine.setText(decf.format(currWork.getWMachTotal()).replace(",", "."));
            totalSalaryMachine.setText(decf.format(currWork.getWZPMachTotal()).replace(",", "."));

            laborCostOfOneWorker.setText(String.valueOf(currWork.getWTz()));
            laborTotalCostWorker.setText(String.valueOf(currWork.getWTZTotal()));

            laborCostOfOneMachine.setText(String.valueOf(currWork.getWTZMach()));
            laborTotalCostMachine.setText(String.valueOf(currWork.getWTZMachTotal()));
            /*startDate.setText(sdf.format(currWork.getWStartDate()));
            percentDone.setText(String.valueOf(currWork.getWPercentDone()));
            countDone.setText(decf.format(currWork.getWCountDone()).replace(",", "."));
            */
        }
    }

    private void fillWorkForUpdate(){
        if (currWork != null){
            currWork.setWCipher(cipherValue.getText().toString());
            currWork.setWName(nameValue.getText().toString());
            currWork.setWMeasuredRus(measuredValue.getText().toString());
            currWork.setWCount(Float.parseFloat(countValue.getText().toString()));
            currWork.setWItogo(Float.parseFloat(costOfOneTotal.getText().toString()));
            currWork.setWZP(Float.parseFloat(salaryOfOne.getText().toString()));
            currWork.setWMach(Float.parseFloat(costOfOneMachine.getText().toString()));
            currWork.setWZPMach(Float.parseFloat(salaryOfOneMachine.getText().toString()));
            currWork.setWTotal(Float.parseFloat(totalCostCommon.getText().toString()));
            currWork.setWZPTotal(Float.parseFloat(totalSalary.getText().toString()));
            currWork.setWMachTotal(Float.parseFloat(totalCostMachine.getText().toString()));
            currWork.setWZPMachTotal(Float.parseFloat(totalSalaryMachine.getText().toString()));

            currWork.setWTz(Float.parseFloat(laborCostOfOneWorker.getText().toString()));
            currWork.setWTZTotal(Float.parseFloat(laborTotalCostWorker.getText().toString()));

            currWork.setWTZMach(Float.parseFloat(laborCostOfOneMachine.getText().toString()));
            currWork.setWTZMachTotal(Float.parseFloat(laborTotalCostMachine.getText().toString()));

           /* try{
                currWork.setWStartDate(sdf.parse(startDate.getText().toString()));
            }catch(ParseException e){
                e.printStackTrace();
            }
            currWork.setWPercentDone(Float.parseFloat(percentDone.getText().toString()));
            currWork.setWCountDone(Float.parseFloat(countDone.getText().toString()));
            */
        }
    }
}
