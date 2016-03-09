package ua.com.expertsoft.android_smeta.asynktasks;

import android.app.ProgressDialog;
import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.Callable;

import ua.com.expertsoft.android_smeta.MainActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/**
 * Created by mityai on 25.12.2015.
 */
public class LoadingOcadBuild extends AsyncTask<String, Void,Boolean> {
    public interface OnGetLoadedProjectListener{
        void onGetLoadedProject(Projects loadedProject, int loadtindtype);
        void onShowLoadedProject();
    }

    private static final String URLHEAD = "http://195.62.15.35:8084/OCAD/";
    private static final String FORMAT = ".json";

    HttpURLConnection getJsonProject;
    JSONObject project;
    ProgressDialog waitDialog;
    Context context;
    Projects projects;
    OS os;
    LS ls;
    Works work;
    WorksResources resources;
    Facts facts;
    int loadingType;
    int priorWorkId = 0;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());// hh:mm
    SimpleDateFormat sdfFacts = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    DBORM database;
    OnGetLoadedProjectListener loadedListener;

    public LoadingOcadBuild(){}

    public LoadingOcadBuild(Context ctx, int type, DBORM base){
        context = ctx;
        loadedListener = (OnGetLoadedProjectListener)context;
        loadingType = type;
        database = base;
        projects = new Projects();
    }

    public void createDialog(){
        if(waitDialog == null)
        {
            waitDialog = new ProgressDialog(context);
            waitDialog.setTitle(getTitleByType(loadingType));
            waitDialog.setMessage(getMessageByType(loadingType));
            // Change style
            waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // switch-on animation
            waitDialog.setIndeterminate(true);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.show();
        }
    }

    private String getTitleByType(int type){
        if(type == 0){
            return context.getResources().getString(R.string.dialogLoadingTitle);
        }else{
            return context.getResources().getString(R.string.dialog_update_title);
        }
    }

    private String getMessageByType(int type){
        if(type == 0){
            return context.getResources().getString(R.string.dialogWaitForLoading);
        }else{
            return context.getResources().getString(R.string.dialog_update_message);
        }
    }

    public void freeDialog(){
        try{
            if ((waitDialog!= null)&(waitDialog.isShowing())){
                waitDialog.dismiss();
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            waitDialog = null;
        }
    }

    @Override
    protected void onPreExecute(){
        createDialog();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = URLHEAD + params[0] + FORMAT;
        int projType = 0;
        try {
            if (! params[0].contains("/")) {
                URL projUrl = new URL(url);
                getJsonProject = (HttpURLConnection) projUrl.openConnection();
                getJsonProject.setReadTimeout(5000);
                project = new JSONObject(streamToString(getJsonProject.getInputStream()));
                getJsonProject.disconnect();
                projType = 0;
            }else{
                FileInputStream fileStream = new FileInputStream(new File(params[0]));
                InputStreamReader reader = new InputStreamReader(fileStream, "windows-1251");
                BufferedReader buff = new BufferedReader(reader);
                String json = "";
                String line;
                while ((line = buff.readLine()) != null){
                    json += line;
                }
                projType = 1;
                project = new JSONObject(json);
            }
            //Begin parse
            startParseProject(project, projType);
            //Start parse Object Estimates
            for(int i = 0; i< project.getJSONArray("objects").length(); i++) {
                JSONObject osObject = project.getJSONArray("objects").getJSONObject(i);
                startParseOS(osObject);
                //Start parse local estimates
                for(int j = 0; j < osObject.getJSONArray("estimates").length(); j++){
                    JSONObject lsObject = osObject.getJSONArray("estimates").getJSONObject(j);
                    startParseLS(lsObject);
                }
                //Start parse works
                for(int j = 0; j < osObject.getJSONArray("works").length(); j++){
                    JSONObject workObject = osObject.getJSONArray("works").getJSONObject(j);
                    startParseWork(workObject);
                    //Start prse works resources
                    for(int res = 0; res< workObject.getJSONArray("work_res").length(); res++){
                        JSONObject workResObject = workObject.getJSONArray("work_res").getJSONObject(res);
                        startParseWorksRes(workResObject);
                    }
                    //Start parse facts
                    for(int f = 0; f< workObject.getJSONArray("facts").length(); f++){
                        JSONObject factObject = workObject.getJSONArray("facts").getJSONObject(f);
                        startParseFacts(factObject);
                    }
                    work.reCalculateExecuting();
                }
            }
            loadedListener.onGetLoadedProject(projects, loadingType);
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }catch(JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void startParseProject(JSONObject obj, int projectType) {
        try {
            projects.setProjectNameRus(obj.getString("project_name"));
            projects.setProjectNameUkr(obj.getString("project_name"));
            projects.setProjectCipher(obj.getString("project_shifr"));
            projects.setProjectContractor(obj.getString("project_contractor"));
            projects.setProjectCreatedDate(sdf.parse(obj.getString("project_data_create")/*.replace("-",".")*/));
            projects.setProjectCustomer(obj.getString("project_customer"));
            projects.setProjectDataUpdate(sdf.parse(obj.getString("project_data_update")/*.replace("-", ".")*/));
            if (obj.getString("project_status").toLowerCase().equals("done")) {
                projects.setProjectDone(true);
            } else {
                projects.setProjectDone(false);
            }
            projects.setProjectGuid(obj.getString("project_guid"));
            projects.setProjectTotal(obj.getDouble("project_total"));
            projects.setProjectType(projectType);
            projects.setProjectSortId(0);
            if (loadingType == 0){
                database.getHelper().getProjectsDao().callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        database.getHelper().getProjectsDao().create(projects);
                        return null;
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startParseOS(JSONObject obj){
        try {
            os = new OS();
            os.setOsNameRus(obj.getString("name"));
            os.setOsNameUkr(obj.getString("name_ua"));
            os.setOsGuid(obj.getString("guid"));
            os.setOsCipher(obj.getString("shifr"));
            os.setOsDescription(obj.getString("descr"));
            os.setOsProjectId(projects.getProjectId());
            os.setOsProjects(projects);
            os.setOsTotal((float) obj.getDouble("total_cost"));
            //change to correct sort id
            os.setOsSortId(obj.getInt("number"));
            projects.setCurrentEstimate(os);
            if (loadingType == 0){
                os.setOsProjects(projects);
                os.setOsProjectId(projects.getProjectId());
                database.getHelper().getOSDao().callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        database.getHelper().getOSDao().create(os);
                        return null;
                    }
                });
            }
        }catch(JSONException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void startParseLS(JSONObject obj){
        try {
            ls = new LS();
            ls.setLsNameRus(obj.getString("name"));
            ls.setLsNameUkr(obj.getString("name_ua"));
            ls.setLsGuid(obj.getString("guid"));
            ls.setLsCipher(obj.getString("shifr"));
            ls.setLsDescription(obj.getString("descr"));
            ls.setLsProjectId(projects.getProjectId());
            ls.setLsProjects(projects);
            ls.setLsOs(os);
            ls.setLsTotal((float) obj.getDouble("total_cost"));
            ls.setLsSortId(obj.getInt("number"));
            os.setCurrentEstimate(ls);
            if (loadingType == 0){
                ls.setLsProjects(projects);
                ls.setLsProjectId(projects.getProjectId());
                ls.setLsOs(os);
                ls.setLsOsId(os.getOsId());
                database.getHelper().getLSDao().create(ls);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }

    }

    private void startParseWork(JSONObject obj){
        try {
            work = new Works();
            work.setWName(obj.getString("name"));
            work.setWNameUkr(obj.getString("name_ua"));
            work.setWCipher(obj.getString("shifr"));
            work.setWCipherObosn(obj.getString("shifrobosn"));
            work.setWCount((float) obj.getDouble("count"));
            work.setWCountDone((float) obj.getDouble("count_done"));
            work.setWPercentDone((float) obj.getDouble("percent_done"));
            work.setWStartDate(sdf.parse(obj.getString("date_start")/*.replace("-", ".")*/));
            work.setWEndDate(sdf.parse(obj.getString("date_end")/*.replace("-", ".")*/));
            work.setWGuid(obj.getString("guid"));
            work.setWItogo((float) obj.getDouble("itogo"));
            work.setWMeasuredRus(obj.getString("measure"));
            work.setWMeasuredUkr(obj.getString("measure_u"));
            work.setWNpp(obj.getInt("npp"));
            work.setWPartTag(obj.getString("razdel_tag"));
            work.setWLayerTag(obj.getString("layer_tag"));
            work.setWGroupTag(obj.getString("res_group_tag"));
            work.setWRec(obj.getString("rec_type"));
            work.setWSortOrder(obj.getInt("sort_id"));
            work.setWTotal((float) obj.getDouble("total"));
            work.setWZP((float) obj.getDouble("zp"));
            work.setWMach((float) obj.getDouble("mach"));
            work.setWZPMach((float) obj.getDouble("zpmach"));
            work.setWZPTotal((float) obj.getDouble("zptotal"));
            work.setWMachTotal((float) obj.getDouble("machtotal"));
            work.setWZPMachTotal((float) obj.getDouble("zpmachtotal"));
            work.setWTz((float) obj.getDouble("tz"));
            work.setWTZMach((float) obj.getDouble("tzmach"));
            work.setWTZTotal((float) obj.getDouble("tztotal"));
            work.setWTZMachTotal((float) obj.getDouble("tzmachtotal"));
            work.setWNaklTotal((float) obj.getDouble("nakltotal"));
            work.setWAdmin((float) obj.getDouble("admin"));
            work.setWProfit((float) obj.getDouble("profit"));
            work.setWDescription(obj.getString("descr"));
            work.setwExec(obj.getString("exec"));
            work.setwOnOFf(obj.getBoolean("onoff"));

            work.setWProjectFK(projects);
            work.setWOSFK(os);

            for(LS lls: os.getAllLocalEstimates()){
                if(lls.getLsGuid().equals(obj.getString("estimate_guid"))){
                    work.setWLSFK(lls);
                    work.setWLsId(lls.getLsId());
                    lls.setCurrentWork(work);
                    work.setWParentId(lls.getLsId());
                    break;
                }
            }
            work.setWProjectId(projects.getProjectId());
            work.setWOsId(os.getOsId());
            if((work.getWRec().equals("material")|work.getWRec().equals("mach"))& priorWorkId != 0){
                work.setWParentNormId(priorWorkId);
            }
            if (loadingType == 0){
                database.getHelper().getWorksDao().callBatchTasks(
                        new Callable<Object>() {
                            @Override
                            public Object call() throws Exception {
                                database.getHelper().getWorksDao().create(work);
                                priorWorkId = work.getWorkId();
                                return null;
                            }
                        }
                );
            }
            //move to main Activity
              //work.setWLsId(ls.getLsId());
              //work.setWOsId(os.getOsId());
              //work.setWProjectId(projects.getProjectId());

            //TODO: fill this parameters
            //work.setWParentId();
            //work.setWParentNormId();

        }catch(JSONException e){
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void startParseWorksRes(JSONObject obj){
        try {
            resources = new WorksResources();
            resources.setWrCipher(obj.getString("shifr"));
            resources.setWrCost((float) obj.getDouble("cost"));
            resources.setWrCount((float) obj.getDouble("count"));
            resources.setWrDescription(obj.getString("descr"));
            resources.setWrResGroupTag(obj.getString("res_group_tag"));
            resources.setWrGuid(obj.getString("guid"));
            resources.setWrMeasuredRus(obj.getString("measure"));
            resources.setWrNameRus(obj.getString("name"));
            resources.setWrMeasuredUkr(obj.getString("measure_ua"));
            resources.setWrNameUkr(obj.getString("name_ua"));
            resources.setWrNpp(obj.getInt("npp"));
            resources.setWrOnOff(obj.getBoolean("onoff") ? 1 : 0);
            switch(obj.getString("rec_type")){
                case "tz":
                    resources.setWrPart(1);
                    break;
                case "material":
                    resources.setWrPart(3);
                    break;
                case "mach":
                    resources.setWrPart(2);
                    break;
            }
            resources.setWrTotalCost((float) obj.getDouble("totalcost"));
            resources.setWrWork(work);
            resources.setWrWorkId(work.getWorkId());
            work.setCurrentResource(resources);
            if (loadingType == 0){
                database.getHelper().getWorksResDao().callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        database.getHelper().getWorksResDao().create(resources);
                        return null;
                    }
                });
            }
        }catch(JSONException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void startParseFacts(JSONObject obj){
        try {
            facts = new Facts();
            facts.setFactsMakesCount((float)obj.getDouble("make_kolvo"));
            facts.setFactsMakesPercent((float) obj.getDouble("make_percent"));
            facts.setFactsGuid("");
            facts.setFactsStart(sdfFacts.parse((obj.getString("start_period")/*.replace("-", ".")*/)));
            facts.setFactsStop(sdfFacts.parse((obj.getString("stop_period")/*.replace("-",".")*/)));
            facts.setFactsDesc(obj.getString("description"));
            facts.setFactsByFacts((float) obj.getDouble("by_facts"));
            facts.setFactsByPlan((float) obj.getDouble("by_plan"));
            work.setCurrentFact(facts);
            if (loadingType == 0){
                database.getHelper().getFactsDao().callBatchTasks(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        database.getHelper().getFactsDao().create(facts);
                        return null;
                    }
                });
            }
        }catch(JSONException e){
            e.printStackTrace();
        }catch(ParseException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String streamToString(InputStream stream){
        String streamString = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String ch;
        try {
            while ((ch = reader.readLine()) != null ) {
                streamString += ch + "\n";
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return streamString;
    }

    @Override
    protected void onPostExecute(Boolean result){
        //loadedListener.onGetLoadedProject(projects, loadingType);
        if (result){
            loadedListener.onShowLoadedProject();
            if(loadingType != 0) {
                Toast.makeText(context,
                        context.getResources().getString(R.string.toast_success_update),
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,
                    context.getResources().getString(R.string.toast_unsuccess),
                    Toast.LENGTH_SHORT).show();
        }
        freeDialog();
        super.onPostExecute(result);

    }
}
