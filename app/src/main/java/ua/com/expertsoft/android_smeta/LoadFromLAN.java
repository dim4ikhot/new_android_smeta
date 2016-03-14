package ua.com.expertsoft.android_smeta;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ua.com.expertsoft.android_smeta.asynktasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.asynktasks.LoadingOcadBuild;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;

/**
 * Created by mityai on 04.03.2016.
 */
public class LoadFromLAN extends AsyncTask<Void,Void,Integer> {

    public static final int GIVE_ME_BUILDS_PARAMS = 0;
    static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    Socket sendSocket;
    String messValue, IP;
    int portValue;
    String message = "";
    DBORM database;
    ArrayList<String> ipArray;
    Context context;
    String[] result;
    Projects projects;
    OS os;
    LS ls;
    Works work;
    WorksResources resources;
    Facts facts;
    AsyncProgressDialog dialog;
    int loadingType;

    public LoadFromLAN(Context ctx, String message,
                       String ip, int projectExpType,int loadingType,DBORM base){
        messValue = message;
        IP = ip;
        switch(projectExpType){
            case 1:
                portValue = 1149;
                break;
            case 2:
            case 3:
                portValue = 1150;
                break;
        }
        this.loadingType = loadingType;
        ipArray = new ArrayList<>();
        context = ctx;
        database = base;
        projects = new Projects();
        dialog = new AsyncProgressDialog(context, getTitleByType(loadingType),getMessageByType(loadingType));
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

    protected void onPreExecute(){
        dialog.createDialog();
    }

    public boolean isReachableByTcp(String host, int port, int timeout) {
        try {
            Socket socket = new Socket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            socket.connect(socketAddress, timeout);
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected Integer doInBackground(Void... params) {
        // TODO Auto-generated method stub
        int whatReturn = 0;
        boolean network;
        try {
            try {
                network = isReachableByTcp(IP, portValue, 50);
                if (network) {
                    sendSocket = new Socket(IP, portValue);
                    byte[] mybytearray = messValue.getBytes();
                    OutputStream os = sendSocket.getOutputStream();
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();

                    while (true) {
                        InputStream is = sendSocket.getInputStream();
                        InputStreamReader reader = new InputStreamReader(is, "windows-1251");
                        int sizeAvialable = is.available();
                        char[] readerChar = new char[sizeAvialable];
                        reader.read(readerChar,0,readerChar.length);
                        message = String.copyValueOf(readerChar);
                        result = message.split("#");
                        if (result[0].equals("done")){
                            //TODO Parse income string
                            startParseProject(result);
                            ((LoadingOcadBuild.OnGetLoadedProjectListener)context)
                                    .onGetLoadedProject(projects, loadingType);
                            reader.close();
                            break;
                        }
                    }
                    sendSocket.close();
                    whatReturn = 1;
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return whatReturn;
    }

    private void startParseProject(String[] project){
        String[] result;
        String[] fields;
        int parentId = 0;
        int parentNormId = 0;
        try {
            for (int i = 1; i < project.length; i++) {
                result = project[i].split("\\$");
                if (result.length > 0) {

                    switch (result[0]) {
                        case "PROJ":
                            for (int j = 1; j < result.length; j++) {
                                fields = result[j].split("=");
                                switch (fields[0]) {
                                    case Projects.TP_FIELD_ORIGIN_GUID:
                                        projects.setProjectGuid(fields[1]);
                                        break;
                                    case Projects.TP_FIELD_NAME_RUS:
                                        projects.setProjectNameRus(fields[1]);
                                        projects.setProjectNameUkr(fields[1]);
                                        break;
                                    case Projects.TP_FIELD_CIPHER:
                                        if (fields.length>1) {
                                            projects.setProjectCipher(fields[1]);
                                        }else{
                                            projects.setProjectCipher("");
                                        }
                                        break;
                                    case Projects.TP_FIELD_DATA_UPDATE:
                                        try {
                                            projects.setProjectDataUpdate(
                                                    new SimpleDateFormat("dd.MM.yyyy").parse(fields[1]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case Projects.TP_FIELD_CONTRACTOR:
                                        if (fields.length>1) {
                                            projects.setProjectContractor(fields[1]);
                                        }else{
                                            projects.setProjectContractor("");
                                        }
                                        break;
                                    case Projects.TP_FIELD_CREATEDATE:
                                        try {
                                            projects.setProjectCreatedDate(
                                                    new SimpleDateFormat("dd.MM.yyyy").parse(fields[1]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case Projects.TP_FIELD_CUSTOMER:
                                        if (fields.length>1) {
                                            projects.setProjectCustomer(fields[1]);
                                        }else{
                                            projects.setProjectCustomer("");
                                        }
                                        break;
                                }
                            }
                            projects.setProjectType(0);
                            projects.setProjectDone(false);
                            projects.setProjectTotal(0);
                            if(loadingType == 0) {
                                addProject(this.projects);
                            }
                            break;
                        case "WORK":
                            work = new Works();
                            for (int j = 1; j < result.length; j++) {
                                fields = result[j].split("=");
                                switch (fields[0]) {
                                    case Works.TW_FIELD_GUID:
                                        work.setWGuid(fields.length>1? fields[1]: "");
                                        break;
                                    case Works.TW_FIELD_PART_TAG:
                                        work.setWPartTag(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_ONOFF:
                                        work.setwOnOFf(fields.length <= 1 || fields[1].equals("1"));
                                        break;
                                    case Works.TW_FIELD_NAME_RUS:
                                        work.setWName(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_NAME_UKR:
                                        work.setWNameUkr(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_CIPHER:
                                        work.setWCipher(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_CIPHER_OBOSN:
                                        work.setWCipherObosn(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_REC:
                                        work.setWRec(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_MEASURED_RUS:
                                        work.setWMeasuredRus(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_MEASURED_UKR:
                                        work.setWMeasuredUkr(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_COUNT:
                                        work.setWCount(fields.length>1?
                                                Float.parseFloat(fields[1].replace(",",".")): 0);
                                        break;
                                    case Works.TW_FIELD_DATE_START:
                                        work.setWStartDate(fields.length > 1 ? sdf.parse(fields[1]) : new Date());
                                        break;
                                    case Works.TW_FIELD_DATE_END:
                                        work.setWEndDate(fields.length > 1 ? sdf.parse(fields[1]) : new Date());
                                        break;
                                    case Works.TW_FIELD_PERCENT_DONE:
                                        work.setWPercentDone(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_COUNT_DONE:
                                        work.setWCountDone(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_EXEC:
                                        work.setwExec(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case Works.TW_FIELD_TOTAL:
                                        work.setWTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_NPP:
                                        work.setWNpp(fields.length > 1 ?
                                                Integer.parseInt(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ITOGO:
                                        work.setWItogo(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ZP:
                                        work.setWZP(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_MACH:
                                        work.setWMach(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ZPMACH:
                                        work.setWZPMach(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ZPTOTAL:
                                        work.setWZPTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_MACHTOTAL:
                                        work.setWMachTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ZPMACHTOTAL:
                                        work.setWZPMachTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_TZ:
                                        work.setWTz(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_TZMACH:
                                        work.setWTZMach(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_TZTOTAL:
                                        work.setWTZTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_TZMACHTOTAL:
                                        work.setWTZMachTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_NALTOTAL:
                                        work.setWNaklTotal(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_ADMIN:
                                        work.setWAdmin(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Works.TW_FIELD_PROFIT:
                                        work.setWProfit(fields.length>1?
                                                Float.parseFloat(fields[1].replace(",",".")): 0);
                                        break;
                                }
                            }

                            switch(work.getWRec()){
                                case "os":
                                    os = new OS();
                                    os.setOsGuid(work.getWGuid());
                                    os.setOsNameRus(work.getWName());
                                    os.setOsNameUkr(!work.getWNameUkr().equals("") ?
                                            work.getWNameUkr() :
                                            work.getWName());
                                    os.setOsDescription("");
                                    os.setOsTotal(work.getWTotal());
                                    os.setOsSortId(work.getWNpp());
                                    os.setOsCipher(work.getWCipher());
                                    os.setOsProjectId(projects.getProjectId());
                                    os.setOsProjects(projects);
                                    if(loadingType == 0) {
                                        addOs(os);
                                    }
                                    projects.setCurrentEstimate(os);
                                    break;
                                case "ls":
                                    ls = new LS();
                                    ls.setLsGuid(work.getWGuid());
                                    ls.setLsNameRus(work.getWName());
                                    ls.setLsNameUkr(!work.getWNameUkr().equals("") ?
                                            work.getWNameUkr() :
                                            work.getWName());
                                    ls.setLsDescription("");
                                    ls.setLsTotal(work.getWTotal());
                                    ls.setLsSortId(work.getWNpp());
                                    ls.setLsCipher(work.getWCipher());
                                    ls.setLsProjectId(this.projects.getProjectId());
                                    ls.setLsProjects(this.projects);
                                    ls.setLsOsId(os.getOsId());
                                    ls.setLsOs(os);
                                    if(loadingType == 0) {
                                        addLs(ls);
                                    }
                                    os.setCurrentEstimate(ls);
                                    parentId = ls.getLsId();
                                    break;
                                case "razdel":
                                    break;
                                case "chast":
                                    break;
                                case "record_1":
                                    work.setWProjectId(projects.getProjectId());
                                    work.setWOsId(os.getOsId());
                                    work.setWLsId(ls.getLsId());
                                    work.setWProjectFK(projects);
                                    work.setWParentId(parentId);
                                    work.setWParentNormId(0);
                                    work.setWOSFK(os);
                                    work.setWLSFK(ls);
                                    work.setWRec("record");
                                    if(loadingType == 0) {
                                        addWork(work);
                                    }
                                    ls.setCurrentWork(work);
                                    parentNormId = work.getWorkId();
                                    break;
                                case "record_2":
                                    work.setWRec("machine");
                                    work.setWProjectId(projects.getProjectId());
                                    work.setWOsId(os.getOsId());
                                    work.setWLsId(ls.getLsId());
                                    work.setWProjectFK(projects);
                                    work.setWParentNormId(parentNormId);
                                    work.setWParentId(parentId);
                                    work.setWOSFK(os);
                                    work.setWLSFK(ls);
                                    if(loadingType == 0) {
                                        addWork(work);
                                    }
                                    ls.setCurrentWork(work);
                                    break;
                                case "record_3":
                                    work.setWRec("resource");
                                    work.setWParentNormId(parentNormId);
                                    work.setWParentId(parentId);
                                    work.setWProjectId(projects.getProjectId());
                                    work.setWOsId(os.getOsId());
                                    work.setWLsId(ls.getLsId());
                                    work.setWProjectFK(projects);
                                    work.setWOSFK(os);
                                    work.setWLSFK(ls);
                                    if(loadingType == 0) {
                                        addWork(work);
                                    }
                                    ls.setCurrentWork(work);
                                    break;
                            }
                            break;
                        case "RES":
                            resources = new WorksResources();
                            for (int j = 1; j < result.length; j++) {
                                fields = result[j].split("=");
                                switch (fields[0]) {
                                    case WorksResources.TWS_FIELD_GUID:
                                        resources.setWrGuid(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_NAME_RUS:
                                        resources.setWrNameRus(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_NAME_UKR:
                                        resources.setWrNameUkr(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_CIPHER:
                                        resources.setWrCipher(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_MEASURED_RUS:
                                        resources.setWrMeasuredRus(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_MEASURED_UKR:
                                        resources.setWrMeasuredUkr(fields.length > 1 ? fields[1] : "");
                                        break;
                                    case WorksResources.TWS_FIELD_COUNT:
                                        resources.setWrCount(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case WorksResources.TWS_FIELD_COST:
                                        resources.setWrCost(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case WorksResources.TWS_FIELD_ONOFF:
                                        resources.setWrOnOff(fields.length <= 1 ? Integer.parseInt(fields[1]): 1);
                                        break;
                                    case WorksResources.TWS_FIELD_TOTALCOST:
                                        resources.setWrTotalCost(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case WorksResources.TWS_FIELD_PART:
                                        resources.setWrPart(Integer.parseInt(fields[1]));
                                        break;
                                }
                            }
                            resources.setWrWorkId(work.getWorkId());
                            resources.setWrWork(work);
                            if(loadingType == 0) {
                                addRes(resources);
                            }
                            work.setCurrentResource(resources);
                            break;
                        case "FACT":
                            facts = new Facts();
                            for (int j = 1; j < result.length; j++) {
                                fields = result[j].split("=");
                                switch (fields[0]) {
                                    case Facts.FACTS_FIELD_GUID:
                                        facts.setFactsGuid(UUID.randomUUID().toString());
                                        break;
                                    case Facts.FACTS_FIELD_MAKES_COUNT:
                                        facts.setFactsMakesCount(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Facts.FACTS_FIELD_MAKES_PERCENT:
                                        facts.setFactsMakesPercent(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Facts.FACTS_FIELD_START_PERIOD:
                                        try {
                                            facts.setFactsStart(new SimpleDateFormat("dd.MM.yyyy HH:mm")
                                                    .parse(fields[1]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case Facts.FACTS_FIELD_STOP_PERIOD:
                                        try {
                                            facts.setFactsStop(new SimpleDateFormat("dd.MM.yyyy HH:mm")
                                                    .parse(fields[1]));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case Facts.FACTS_FIELD_BY_FACTS:
                                        facts.setFactsByFacts(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Facts.FACTS_FIELD_BY_PLAN:
                                        facts.setFactsByPlan(fields.length > 1 ?
                                                Float.parseFloat(fields[1].replace(",",".")) : 0);
                                        break;
                                    case Facts.FACTS_FIELD_DESCRIPTION:
                                            facts.setFactsDesc(fields.length > 1 ? fields[1] : "");
                                }
                            }
                            facts.setFactsWorkId(work.getWorkId());
                            if(loadingType == 0) {
                                addFact(facts);
                            }
                            work.setCurrentFact(facts);
                            break;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    private void addProject(Projects proj){
        try{
            database.getHelper().getProjectsDao().create(proj);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void addLs(LS ls){
        try{
            database.getHelper().getLSDao().create(ls);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void addOs(OS os){
        try{
            database.getHelper().getOSDao().create(os);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void addWork(Works work){
        try{
            database.getHelper().getWorksDao().create(work);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void addRes(WorksResources wr){
        try{
            database.getHelper().getWorksResDao().create(wr);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void addFact(Facts fact){
        try{
            database.getHelper().getFactsDao().create(fact);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    protected void onPostExecute(Integer result){
        super.onPostExecute(result);
        switch(result){
            case 0:
                Toast.makeText(context, "Соединение не установлено", Toast.LENGTH_LONG).show();
                break;
            case 1:
                if(this.result.length > 0 ) {
                    ((LoadingOcadBuild.OnGetLoadedProjectListener)context)
                            .onShowLoadedProject();
                }
                break;
        }
        dialog.freeDialog();
    }
}
