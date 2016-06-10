package ua.com.expertsoft.android_smeta.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Locale;

import ua.com.expertsoft.android_smeta.ListOfOnlineCadBuilders;
import ua.com.expertsoft.android_smeta.LoadFromLAN;
import ua.com.expertsoft.android_smeta.LoginActivity;
import ua.com.expertsoft.android_smeta.R;
import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/*
 * Created by mityai on 21.01.2016.
 */
public class SaveProjectToServer extends AsyncTask<Integer,Integer,Boolean> {

    public interface OnGetFullProjectListener{
        void onGetFullProject();
    }

    public final String BASE_URL = "/test_cad/php/usr_controller_api.php?action=put_one_project&proj_guid=";

    ProgressDialog progressDialog;
    Context context;
    String uploadServerUri = "";
    URL serverUri;
    OutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    JSONObject project;
    String filePath;
    int serverResponseCode = 0;
    String mail,password;

    public SaveProjectToServer (Context ctx, String filePath){
        context = ctx;
        this.filePath = filePath;
        getLoginParams(ctx);
        uploadServerUri = LoginActivity.getServies(ctx)+ BASE_URL +
                ProjectInfo.project.getProjectGuid() + "&email="+mail + "&pass=" + password;
    }

    private void getLoginParams(Context ctx){
        mail = PreferenceManager.getDefaultSharedPreferences(ctx).getString(LoginActivity.EMAIL_KEY,"");
        password = PreferenceManager.getDefaultSharedPreferences(ctx).getString(LoginActivity.PASSWORD_KEY,"");
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
            if ((progressDialog!= null)&&(progressDialog.isShowing())){
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
            format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            project.put("project_guid", ProjectInfo.project.getProjectGuid());
            project.put("project_name", ProjectInfo.project.getProjectNameRus());
            project.put("project_shifr", ProjectInfo.project.getProjectCipher()!=null
                    ?ProjectInfo.project.getProjectCipher()
                    :"");
            project.put("project_customer", ProjectInfo.project.getProjectCustomer()!= null
            ?ProjectInfo.project.getProjectCustomer().replace("\"", "'")
            :"");
            project.put("project_contractor", ProjectInfo.project.getProjectContractor()!= null
            ?ProjectInfo.project.getProjectContractor().replace("\"", "'")
            :"");
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
                osObject.put("name",os.getOsNameRus().replace("\"", "'"));
                osObject.put("name_ua",os.getOsNameUkr().replace("\"", "'"));
                osObject.put("number", os.getOsSortId());
                osObject.put("shifr",os.getOsCipher());
                osObject.put("total_cost",os.getOsTotal());
                osObject.put("descr",os.getOsDescription()!= null ? os.getOsDescription().replace("\"", "'"):"");
                JSONArray lsArray = new JSONArray();
                JSONArray worksArray = new JSONArray();
                for(LS ls: os.getAllLocalEstimates()){
                    JSONObject lsObject = new JSONObject();
                    lsObject.put("guid", ls.getLsGuid());
                    lsObject.put("name", ls.getLsNameRus().replace("\"", "'"));
                    lsObject.put("name_ua", ls.getLsNameUkr().replace("\"", "'"));
                    lsObject.put("number", ls.getLsSortId());
                    lsObject.put("shifr", ls.getLsCipher());
                    lsObject.put("total_cost", ls.getLsTotal());
                    lsObject.put("descr", ls.getLsDescription() != null ? ls.getLsDescription().replace("\"", "'") : "");
                    for(Works work: ls.getAllWorks()){
                        JSONObject worksObject = new JSONObject();
                        worksObject.put("estimate_guid", ls.getLsGuid());
                        worksObject.put("layer_tag", work.getWLayerTag()!= null
                                ?work.getWLayerTag().replace("\"", "'"):"");
                        worksObject.put("razdel_tag", work.getWPartTag()!= null ?
                                work.getWPartTag().replace("\"", "'"):"");
                        worksObject.put("rec_type", work.getWRec());
                        worksObject.put("guid", work.getWGuid());
                        worksObject.put("npp", work.getWNpp());
                        worksObject.put("onoff", work.getWOnOff());
                        worksObject.put("name",work.getWName().replace("\"", "'"));
                        worksObject.put("name_ua",work.getWNameUkr().replace("\"", "'"));
                        worksObject.put("shifr",work.getWCipher());
                        worksObject.put("shifrobosn",work.getWCipherObosn());
                        worksObject.put("count",work.getWCount());
                        worksObject.put("measure",work.getWMeasuredRus());
                        worksObject.put("measure_ua",work.getWMeasuredUkr());
                        worksObject.put("date_start", format.format(work.getWStartDate()));
                        worksObject.put("date_end",format.format(work.getWEndDate()));
                        worksObject.put("percent_done",work.getWPercentDone());
                        worksObject.put("count_done",work.getWCountDone());
                        worksObject.put("sort_id",work.getWSortOrder());
                        work.createExecFromFacts();
                        worksObject.put("exec",work.getwExec()!= null?work.getwExec().replace("\"", "'"):"");
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
                        worksObject.put("descr",work.getWDescription()!= null?work.getWDescription():"");
                        worksObject.put("res_group_tag",work.getWGroupTag()!= null?work.getWGroupTag():"");

                        //20.05.2016 added new fields
                        worksObject.put("src_type",work.getWSrcType()!= null?work.getWSrcType():"");
                        worksObject.put("src_guid",work.getWSrcGuid()!= null?work.getWSrcGuid():"");
                        worksObject.put("src_name",work.getWSrcName()!= null?work.getWSrcName():"");
                        worksObject.put("distributor",work.getWDistributor()!= null?work.getWDistributor():"");
                        worksObject.put("vendor",work.getWVendor()!= null?work.getWVendor():"");
                        worksObject.put("parent_guid",work.getWParentGuid()!= null?work.getWParentGuid():"");

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
                            resObject.put("name",res.getWrNameRus().replace("\"", "'"));
                            resObject.put("name_ua",res.getWrNameUkr().replace("\"", "'"));
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
                            resObject.put("distributor",res.getWrDistributor()!= null?res.getWrDistributor():"");
                            resObject.put("vendor",res.getWrVendor()!= null?res.getWrVendor():"");
                            resObject.put("parent_guid",res.getWrParentGuid()!= null?res.getWrParentGuid():"");
                            resObject.put("npp",res.getWrNpp());
                            resObject.put("count",res.getWrCount());
                            resObject.put("cost",res.getWrCost());
                            resObject.put("totalcost",res.getWrTotalCost());
                            resObject.put("res_group_tag", res.getWrResGroupTag()!= null?res.getWrResGroupTag():"");
                            resObject.put("descr",res.getWrDescription()!=null
                                    ? res.getWrDescription().replace("\"", "'") : "");
                            resArray.put(resObject);
                        }
                        worksObject.put("work_res",resArray);
                        for(Facts fact : work.getAllFacts()){
                            JSONObject factsObject = new JSONObject();
                            factsObject.put("make_percent",fact.getFactsMakesPercent());
                            factsObject.put("make_kolvo",fact.getFactsMakesCount());
                            format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            factsObject.put("start_period", format.format(fact.getFactsStart()));
                            factsObject.put("stop_period", format.format(fact.getFactsStop()));
                            factsObject.put("description",
                                    fact.getFactsDesc() != null?fact.getFactsDesc().replace("\"", "'"):"");
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

    //This class gets builds(only names & guids) from CLP.
    //So timeout to get some builds is 1 minute if not get answer connected
    public class SendBuildToClp{
        String[] result;
        Socket sendSocket;
        String messValue, IP;
        int portValue;
        String message = "";
        Context context;
        boolean isByFound;

        public SendBuildToClp(Context ctx, String message, boolean isByFound, int port){
            messValue = message;
            context = ctx;
            this.isByFound = isByFound;
            portValue = port;//51783;
        }

        private boolean checkProjectExists(String ipAddress) throws Exception{
            boolean whatReturn  = false;
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, portValue);
            sendSocket = new Socket();
            sendSocket.connect(socketAddress, 300);
            //sendSocket = new Socket(IP, portValue);
            Log.d(LoadFromLAN.TAG, "Connected to " + ipAddress);
            byte[] mybytearray = ("check$"+ ProjectInfo.project.getProjectGuid()).getBytes();//messValue.getBytes();
            OutputStream os = sendSocket.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            Log.d(LoadFromLAN.TAG, "Send request for checking build");

            long delay = 60000;
            long time = System.currentTimeMillis();

            InputStream is = sendSocket.getInputStream();
            int read;
            byte[] readBytes = new byte[100000];
            String helpMessage;
            while ((read = is.read(readBytes)) != -1) {
                helpMessage = new String(readBytes, "windows-1251").substring(0,read);
                message += helpMessage;
                if ((!message.equals(""))) {
                    Log.d(LoadFromLAN.TAG, "Got data from " + ipAddress);
                    if(message.equals("exists")){
                        whatReturn = true;
                        break;
                    } else if(message.equals("busy¶#")){
                        whatReturn = false;
                        break;
                    }
                }
                if (System.currentTimeMillis() - time >= delay){
                        whatReturn = false;
                    break;
                }
            }
            is.close();
            if(sendSocket != null && sendSocket.isConnected()) {
                sendSocket.close();
            }
            return whatReturn;
        }

        private boolean uploadProject(String ipAddress) throws Exception{
            boolean whatReturn = false;
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, portValue);
            sendSocket = new Socket();
            sendSocket.connect(socketAddress, 300);
            //sendSocket = new Socket(IP, portValue);
            Log.d(LoadFromLAN.TAG, "Connected to " + ipAddress);
        //    byte[] mybytearray = messValue.getBytes();
            messValue += "¶#done";
            OutputStream os = sendSocket.getOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(os,"windows-1251");
            //os.write(mybytearray, 0, mybytearray.length);
            writer.write(messValue, 0, messValue.length());
            writer.flush();
            Log.d(LoadFromLAN.TAG, "Send build " +  messValue.length());

            long delay = 60000;
            long time = System.currentTimeMillis();
            message = "";
            InputStream is = sendSocket.getInputStream();
            int read;
            byte[] readBytes = new byte[100000];
            String helpMessage;
            while ((read = is.read(readBytes))!= -1) {
                helpMessage = new String(readBytes, "windows-1251").substring(0,read);
                message += helpMessage;
                if ((!message.equals(""))) {
                    Log.d(LoadFromLAN.TAG, "Got data from " + ipAddress);
                    if(message.equals("done")){
                        whatReturn = true;
                        break;
                    }
                    else if(message.equals("busy¶#")){
                        whatReturn = false;
                        break;
                    }
                }
                if (System.currentTimeMillis() - time >= delay){
                    whatReturn = false;
                    break;
                }
            }
            is.close();
            if(sendSocket != null && sendSocket.isConnected()) {
                sendSocket.close();
            }
            return whatReturn;
        }

        public boolean startFindingProjects() {
            // TODO Auto-generated method stub
            boolean whatReturn = false;
            String myIP = ListOfOnlineCadBuilders.getIPAddress(true);
            String myIpStart = myIP.substring(0,myIP.lastIndexOf(".")+1);
            try {
                for (int i = 1; i < 255; i++) {
                    IP = myIpStart + i;
                    try {
                        result = null;
                        message = "";
                        whatReturn = checkProjectExists(IP);
                        if(whatReturn){
                            if (uploadProject(IP)){
                                whatReturn = true;
                                break;
                            }
                            whatReturn = false;
                        }
                    } catch (Exception e) {
                        if (i == 92) {
                            Log.d(LoadFromLAN.TAG, "except on connect to " + IP + " with message: " + e.getMessage());
                        }
                        if (sendSocket != null && sendSocket.isConnected()) {
                            sendSocket.close();
                        }
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return whatReturn;
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
            ((OnGetFullProjectListener)context).onGetFullProject();
            saveProject();
            switch(params[0]) {
                case 0:
                    serverUri = new URL(uploadServerUri);
                    HttpURLConnection connection = (HttpURLConnection) serverUri.openConnection();
                    connection.setDoInput(true); // Allow Inputs
                    connection.setDoOutput(true); // Allow Outputs
                    connection.setUseCaches(false); // Don't use a Cached Copy
                    connection.setConnectTimeout(1000 * 5);
                    connection.setRequestMethod("POST");

                    dos = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(dos, "UTF-8"));
                    writer.write(getQqery(project));
                    writer.flush();
                    writer.close();
                    /*
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
                    */

                    dos.close();
                    // Responses from the server (code and message)
                    serverResponseCode = connection.getResponseCode();
                    String serverResponseMessage = connection.getResponseMessage();
                    if (serverResponseCode != 200) {
                        return false;
                    }
                    else{
                        InputStream stream = connection.getInputStream();
                        String JSONString = streamToString(stream);
                        if(JSONString != null){

                        }
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
                case 2:
                    return new SendBuildToClp(context,project.toString(),true,51783).startFindingProjects();
                case 3:
                    boolean result =
                            new SendBuildToClp(context,project.toString(),true,51784).startFindingProjects();
                    return result;
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



    private String getQqery(JSONObject obj)throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        builder.append(URLEncoder.encode("proj_json", "UTF-8"));
        builder.append("=");
        builder.append(URLEncoder.encode(obj.toString(), "UTF-8"));
        return builder.toString();
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

    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
        progressDialog.incrementProgressBy(values[0]);
    }

    @Override
    protected void onPostExecute(Boolean result){
        freeDialog();
        if (!result){
            String caption = context.getResources().getString(R.string.error_save_project);
            InfoCommonDialog infoDialog = new InfoCommonDialog();
            infoDialog.setMessage(caption);
            infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
            //Toast.makeText(context, caption, Toast.LENGTH_SHORT).show();
        }
        else{
            String caption = context.getResources().getString(R.string.message_success_saved);
            InfoCommonDialog infoDialog = new InfoCommonDialog();
            infoDialog.setMessage(caption);
            infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
            //Toast.makeText(context,caption,Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(result);
    }
}
