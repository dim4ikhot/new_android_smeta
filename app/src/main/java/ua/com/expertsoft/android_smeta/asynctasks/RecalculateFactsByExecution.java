package ua.com.expertsoft.android_smeta.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.UUID;

import ua.com.expertsoft.android_smeta.FactsCommonOperations;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.standard_project.parsers.ZmlParser;

/*
 * Created by mityai on 18.05.2016.
 */
public class RecalculateFactsByExecution extends AsyncTask<Void,Void,Void> {

    DBORM database;
    Context context;
    AsyncProgressDialog dialog;

    public RecalculateFactsByExecution(Context ctx, DBORM data){
        database = data;
        context = ctx;
        dialog = new AsyncProgressDialog(context, R.string.recalculate_facts_title,R.string.recalculate_facts_message);
    }

    @Override
    public void onPreExecute(){
        dialog.createDialog();
    }

    @Override
    protected Void doInBackground(Void... params) {
        for (OS currentOs : ProjectInfo.project.getAllObjectEstimates()){
            for(LS currentLs : currentOs.getAllLocalEstimates()){
                for(Works currentWork: currentLs.getAllWorks()){
                    String execution = currentWork.getwExec();
                    if(execution == null){
                        continue;
                    }
                    String stringStartDate, stringMadeCount;
                    String[] splitedExec = execution.split(";");
                    String[] dividedExec;
                    Facts newFact;
                    float wCountDone = 0;
                    float wPercentDone = 0;
                    float wCount = currentWork.getWCount();
                    for(String currentExec: splitedExec ){
                        dividedExec = currentExec.split("-");
                        if(dividedExec.length > 1) {
                            stringStartDate = "01." + dividedExec[0] + " 08:00";
                            stringMadeCount = dividedExec[1].replace(",",".");
                            //get count and percent from exec
                            float madeCount = Float.parseFloat(stringMadeCount);
                            float madePercent = (float) ZmlParser.roundTo(100*madeCount/wCount,2);
                            //total works execution
                            wCountDone += madeCount;
                            wPercentDone += madePercent;
                            //Create new fact
                            newFact = new Facts();
                            Date startDate = new Date();
                            Date stopDate = new Date();
                            float byPlan = 0;
                            float byFact;
                            try {
                                //Convert string date to real date
                                startDate = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.getDefault()).parse(stringStartDate);
                                FactsCommonOperations.setStartDate(startDate);
                                byPlan = FactsCommonOperations.calculateWorkingHours();
                                stopDate = FactsCommonOperations.getStopDate();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            startDate = FactsCommonOperations.correctingStartDate(startDate);
                            //set Facts Params
                            newFact.setFactsMakesCount(madeCount);
                            newFact.setFactsMakesPercent(madePercent);
                            //Calculate from percent working hours by Fact
                            byFact = (currentWork.getWTZTotal() * newFact.getFactsMakesPercent())/100 ;
                            //if work done
                            if(wPercentDone == 100){
                                FactsCommonOperations.setStartDate(startDate);
                                stopDate = FactsCommonOperations.recalculateStopDate(byPlan,byFact, stopDate);
                                byPlan = FactsCommonOperations.getNewPlan();
                            }
                            stopDate = FactsCommonOperations.correctingStopDate(stopDate);
                            newFact.setFactsStart(startDate);
                            newFact.setFactsStop(stopDate);
                            newFact.setFactsByPlan(byPlan);
                            newFact.setFactsByFacts(byFact);
                            newFact.setFactsParent(currentWork);
                            newFact.setFactsWorkId(currentWork.getWorkId());
                            newFact.setFactsGuid(UUID.randomUUID().toString());
                            ArrayList<Facts> foundByMonth = new ArrayList<>();
                            GregorianCalendar monthCalendar = new GregorianCalendar();
                            for(Facts fact: currentWork.getAllFacts()){
                                monthCalendar.setTime(fact.getFactsStart());
                                int monthExists = monthCalendar.get(Calendar.MONTH);
                                monthCalendar.setTime(newFact.getFactsStart());
                                int newFactMonth = monthCalendar.get(Calendar.MONTH);
                                if(monthExists == newFactMonth){
                                    foundByMonth.add(fact);
                                }
                            }
                            for(Facts fact: foundByMonth){
                                currentWork.removeFact(fact);
                                try{
                                    database.getHelper().getFactsDao().delete(fact);
                                }catch(SQLException e){
                                    e.printStackTrace();
                                }
                            }
                            currentWork.setCurrentFact(newFact);
                            try{
                                database.getHelper().getFactsDao().create(newFact);
                            }catch(SQLException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        ProjectInfo.project = null;
        return null;
    }

    @Override
    public void onPostExecute(Void result){
        dialog.freeDialog();
    }
}
