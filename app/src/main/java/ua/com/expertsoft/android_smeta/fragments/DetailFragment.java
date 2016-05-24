package ua.com.expertsoft.android_smeta.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.settings.FragmentSettings;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.data.Works;

/*
 * Created by mityai on 04.01.2016.
 */
public class DetailFragment extends Fragment {

    Works currWork;
    View returnedView;
    TextView cipherValue, countValue;
    EditText costOfOneTotal, salaryOfOne,
            nameValue, measuredValue,
            costOfOneMachine, salaryOfOneMachine,
            totalCostCommon, totalSalary,
            totalCostMachine,totalSalaryMachine,
            laborCostOfOneWorker,laborCostOfOneMachine,
            laborTotalCostWorker,laborTotalCostMachine;
    Intent intent;
    boolean isLanguageDataRus = true;

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
        nameValue = (EditText)v.findViewById(R.id.posFullNameValue);
        measuredValue = (EditText)v.findViewById(R.id.posMeasuredValue);
        countValue = (TextView)v.findViewById(R.id.posCountValue);
        costOfOneTotal = (EditText)v.findViewById(R.id.editTotalOneValue);
        costOfOneTotal.setEnabled(!SelectedWork.work.getWRec().equals("record"));
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

        nameValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isLanguageDataRus) {
                    SelectedWork.work.setWName(s.toString());
                }
                else{
                    SelectedWork.work.setWNameUkr(s.toString());
                }
                SelectedWork.work.setWIsChanged(true);
            }
        });

        measuredValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(isLanguageDataRus) {
                    SelectedWork.work.setWMeasuredRus(s.toString());
                }
                else{
                    SelectedWork.work.setWMeasuredUkr(s.toString());
                }
                SelectedWork.work.setWIsChanged(true);
            }
        });

        costOfOneTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().equals("")) {
                    SelectedWork.work.setWItogo(Float.parseFloat(s.toString()));
                    SelectedWork.work.setWTotal(SelectedWork.work.getWCount() * SelectedWork.work.getWItogo());
                }
                else{
                    SelectedWork.work.setWItogo(0);
                    SelectedWork.work.setWTotal(SelectedWork.work.getWCount() * SelectedWork.work.getWItogo());
                }
                totalCostCommon.setText(String.valueOf(SelectedWork.work.getWTotal()));
                SelectedWork.work.setWIsChanged(true);
            }
        });
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
            isLanguageDataRus = FragmentSettings.isDataLanguageRus(getActivity());
            if(isLanguageDataRus) {
                nameValue.setText(currWork.getWName());
                measuredValue.setText(currWork.getWMeasuredRus());
            }else{
                nameValue.setText(currWork.getWNameUkr()!= null ? currWork.getWNameUkr()  : "" );
                measuredValue.setText(currWork.getWMeasuredUkr()!= null ? currWork.getWMeasuredUkr(): "");
            }
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
}
