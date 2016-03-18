package ua.com.expertsoft.android_smeta.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/**
 * Created by mityai on 21.01.2016.
 */
public class SaveProjectToServer extends AsyncTask<Integer,Integer,Boolean> {
    ProgressDialog progressDialog;
    Context context;
    File dir;
    String uploadServerUri1 = "http://195.62.15.35:8084/OCAD/upload.php?proj_guid=";
    String uploadServerUri = "http://195.62.15.35:8084/OCAD/upload_multi.php?proj_guid=";
    URL serverUri;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    JSONObject project;
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1 * 1024 * 1024;
    String filePath;
    int serverResponseCode = 0;

    public SaveProjectToServer (){}

    public SaveProjectToServer (Context ctx, String filePath){
        context = ctx;
        this.filePath = filePath;
        uploadServerUri += "111-222";
    }

    public void createDialog(){
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getResources().getString(R.string.dialog_upload));
            progressDialog.setMessage(context.getResources().getString(R.string.dialog_upload_project_mess));
            // Change style
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // switch-on animation
            //progressDialog.setMax(dir.listFiles().length);
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    public void freeDialog(){
        try{
            if ((progressDialog!= null)&(progressDialog.isShowing())){
                progressDialog.dismiss();
            }
        }catch(IllegalArgumentException e){
            e.printStackTrace();
            progressDialog = null;
        }
    }

    private void saveProject(){
        project = new JSONObject();
        SimpleDateFormat format;
        try {
            format = new SimpleDateFormat("yyyy-MM-dd");
            project.put("project_guid", ProjectInfo.project.getProjectGuid());
            project.put("project_name", ProjectInfo.project.getProjectNameRus());
            project.put("project_shifr", ProjectInfo.project.getProjectCipher());
            project.put("project_customer", ProjectInfo.project.getProjectCustomer());
            project.put("project_contractor", ProjectInfo.project.getProjectContractor());
            project.put("project_system", "OCAD");
            project.put("project_type", "online");
            project.put("project_data_create", format.format(ProjectInfo.project.getProjectCreatedDate()));
            if(ProjectInfo.project.getProjectDataUpdate()!= null) {
                project.put("project_data_update", format.format(ProjectInfo.project.getProjectDataUpdate()));
            }else{
                project.put("project_data_update", "");
            }
            project.put("project_total", ProjectInfo.project.getProjectTotal());
            project.put("project_status", "done");
            JSONArray osArray = new JSONArray();
            for(OS os:ProjectInfo.project.getAllObjectEstimates()){
                JSONObject osObject = new JSONObject();
                osObject.put("guid",os.getOsGuid());
                osObject.put("name",os.getOsNameRus());
                osObject.put("name_ua",os.getOsNameUkr());
                osObject.put("number", os.getOsSortId());
                osObject.put("shifr",os.getOsCipher());
                osObject.put("total_cost",os.getOsTotal());
                osObject.put("descr",os.getOsDescription());
                JSONArray lsArray = new JSONArray();
                JSONArray worksArray = new JSONArray();
                for(LS ls: os.getAllLocalEstimates()){
                    JSONObject lsObject = new JSONObject();
                    lsObject.put("guid", ls.getLsGuid());
                    lsObject.put("name", ls.getLsNameRus());
                    lsObject.put("name_ua", ls.getLsNameUkr());
                    lsObject.put("number", ls.getLsSortId());
                    lsObject.put("shifr", ls.getLsCipher());
                    lsObject.put("total_cost", ls.getLsTotal());
                    lsObject.put("descr", ls.getLsDescription());
                    for(Works work: ls.getAllWorks()){
                        JSONObject worksObject = new JSONObject();
                        worksObject.put("estimate_guid", ls.getLsGuid());
                        worksObject.put("layer_tag", work.getWLayerTag());
                        worksObject.put("razdel_tag", work.getWPartTag());
                        worksObject.put("rec_type", work.getWRec());
                        worksObject.put("guid", work.getWGuid());
                        worksObject.put("npp", work.getWNpp());
                        worksObject.put("onoff", work.getWOnOff());
                        worksObject.put("name",work.getWName());
                        worksObject.put("name_ua",work.getWNameUkr());
                        worksObject.put("shifr",work.getWCipher());
                        worksObject.put("shifrobosn",work.getWCipherObosn());
                        worksObject.put("count",work.getWCount());
                        worksObject.put("measure",work.getWMeasuredRus());
                        worksObject.put("measure_u",work.getWMeasuredUkr());
                        worksObject.put("date_start", format.format(work.getWStartDate()));
                        worksObject.put("date_end",format.format(work.getWEndDate()));
                        worksObject.put("percent_done",work.getWPercentDone());
                        worksObject.put("count_done",work.getWCountDone());
                        worksObject.put("sort_id",work.getWSortOrder());
                        worksObject.put("exec",work.getwExec());
                        worksObject.put("total",work.getWTotal());
                        worksObject.put("itogo",work.getWItogo());
                        worksObject.put("zp",work.getWZP());
                        worksObject.put("mach",work.getWMach());
                        worksObject.put("zpmach",work.getWZPMach());
                        worksObject.put("zptotal",work.getWZPTotal());
                        worksObject.put("machtotal",work.getWMachTotal());
                        worksObject.put("zpmachtotal",work.getWZPMachTotal());
                        worksObject.put("tz",work.getWTz());
                        worksObject.put("tzmach",work.getWTZMach());
                        worksObject.put("tztotal",work.getWTZTotal());
                        worksObject.put("tzmachtotal",work.getWTZMachTotal());
                        worksObject.put("nakltotal",work.getWNaklTotal());
                        worksObject.put("admin",work.getWAdmin());
                        worksObject.put("profit",work.getWProfit());
                        worksObject.put("descr",work.getWDescription());
                        worksObject.put("res_group_tag",work.getWGroupTag());
                        JSONArray resArray = new JSONArray();
                        JSONArray factsArray = new JSONArray();

                        for(WorksResources res : work.getAllWorksResources()){
                            JSONObject resObject = new JSONObject();
                            switch(res.getWrPart()){
                                case 1:
                                    resObject.put("rec_type", "tz");
                                    break;
                                case 2:
                                    resObject.put("rec_type", "mach");
                                    break;
                                case 3:
                                case 4:
                                    resObject.put("rec_type", "material");
                                    break;
                            }
                            resObject.put("guid", res.getWrGuid());
                            resObject.put("shifr",res.getWrCipher());
                            resObject.put("name",res.getWrNameRus());
                            resObject.put("name_ua",res.getWrNameUkr());
                            resObject.put("measure",res.getWrMeasuredRus());
                            resObject.put("measure_ua",res.getWrMeasuredUkr());
                            switch(res.getWrOnOff()) {
                                case 1:
                                    resObject.put("onoff", true);
                                    break;
                                case 0:
                                    resObject.put("onoff", false);
                                    break;
                            }
                            resObject.put("npp",res.getWrNpp());
                            resObject.put("count",res.getWrCount());
                            resObject.put("cost",res.getWrCost());
                            resObject.put("totalcost",res.getWrTotalCost());
                            resObject.put("res_group_tag", res.getWrResGroupTag());
                            resObject.put("descr",res.getWrDescription());
                            resArray.put(resObject);
                        }
                        worksObject.put("work_res",resArray);
                        for(Facts fact : work.getAllFacts()){
                            JSONObject factsObject = new JSONObject();
                            factsObject.put("make_percent",fact.getFactsMakesPercent());
                            factsObject.put("make_kolvo",fact.getFactsMakesCount());
                            format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            factsObject.put("start_period", format.format(fact.getFactsStart()));
                            factsObject.put("stop_period", format.format(fact.getFactsStop()));
                            factsObject.put("description",fact.getFactsDesc());
                            factsObject.put("by_facts",fact.getFactsByFacts());
                            factsObject.put("by_plan",fact.getFactsByPlan());
                            factsArray.put(factsObject);
                        }
                        worksObject.put("facts",factsArray);
                        worksArray.put(worksObject);
                    }
                    lsArray.put(lsObject);
                }
                osObject.put("estimates",lsArray);
                osObject.put("works",worksArray);
                osArray.put(osObject);
            }
            project.put("objects",osArray);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        createDialog();
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        try {
            saveProject();
            switch(params[0]) {
                case 0:
                    serverUri = new URL(uploadServerUri);
                    HttpURLConnection connection = (HttpURLConnection) serverUri.openConnection();
                    connection.setDoInput(true); // Allow Inputs
                    connection.setDoOutput(true); // Allow Outputs
                    connection.setUseCaches(false); // Don't use a Cached Copy
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    dos = new DataOutputStream(connection.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    String par = "Content-Disposition: form-data; name=" + '"'
                            + "uploaded_file[]" + '"' + ";filename=" + '"' + ""
                            + ProjectInfo.PROJECT_GUID + ".json" + '"' + lineEnd;
                    dos.writeBytes(par);
                    par = "Content-Type:multipart/form-data" + lineEnd;
                    dos.writeBytes(par);
                    dos.writeBytes(lineEnd);

                    dos.write(project.toString().getBytes());
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    dos.flush();
                    // Responses from the server (code and message)
                    serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();
                    if (serverResponseCode != 200) {
                        return false;
                    }
                    if (!serverResponseMessage.equals("OK")) {
                        return false;
                    }
                    dos.close();
                    connection.disconnect();
                    break;
                case 1:
                    File savePath = new File(filePath);
                    if (!savePath.isDirectory()) {
                        savePath.mkdirs();
                    }
                    File save = new File(filePath + File.separator + ProjectInfo.project.getProjectNameRus() + ".json");
                    FileOutputStream projFile = new FileOutputStream(save);
                    OutputStreamWriter writter = new OutputStreamWriter(projFile, "windows-1251");
                    writter.write(project.toString());
                    writter.flush();
                    writter.close();
                    projFile.close();
                    break;
            }

        }catch(MalformedURLException e){
            e.printStackTrace();
            return false;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        progressDialog.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result){
        freeDialog();
        if (!result){
            String caption = context.getResources().getString(R.string.error_save_project);
            Toast.makeText(context, caption, Toast.LENGTH_SHORT).show();
        }
        else{
            String caption = context.getResources().getString(R.string.message_success_saved);
            Toast.makeText(context,caption,Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}
