package ua.com.expertsoft.android_smeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ua.com.expertsoft.android_smeta.adapters.FoundIpsAdapter;
import ua.com.expertsoft.android_smeta.dialogs.InfoCommonDialog;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.NotAuthorizedDialog;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.standard_project.UnZipBuild;
import ua.com.expertsoft.android_smeta.adapters.OcadProjectsAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.ShowConnectionDialog;

public class ListOfOnlineCadBuilders extends AppCompatActivity implements
        AdapterView.OnItemClickListener, NotAuthorizedDialog.OnShowAuthorizeDialog,
        OcadProjectsAdapter.OnRefreshAfterDeleteListener {

    private final int AUTORIZATION = 1;

    ListView buildList;
    LoadOCADBuldersName loadintTask;
    JSONObject jsonObject;
    ArrayList<JsonProjs> adpList;
    OcadProjectsAdapter ocadAdapter;
    ShowConnectionDialog dialog;
    ActionBar bar;
    String url = "http://195.62.15.35:8084/OCAD/projects.json";
    Menu menu;
    JsonProjs proj;
    boolean isNeddLoadMenu = true;
    int projectExpType;
    int projectOperation = -1;
    String nameAuthorization = "";
    public static FoundComputersInLAN foundIps;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    String foundIpsJson;

    public static void createListOfIps(){
        foundIps = new FoundComputersInLAN();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        setContentView(R.layout.activity_list_of_online_cad_builders);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        foundIpsJson = pref.getString("foundIPsJSON","");
        if(foundIps == null){
            createListOfIps();
        }
        ArrayToJson aToJ = new ArrayToJson();
        foundIps.clearIps();
        aToJ.setArray(foundIps.getFoundIp());
        aToJ.convertToArray(foundIpsJson);

        edit = pref.edit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bar = getSupportActionBar();
        if (bar != null){
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getIntent().getStringExtra("title"));
        }
        adpList = new ArrayList<>();
        buildList = (ListView)findViewById(R.id.listOcadBuilders);
        projectOperation = getIntent().getIntExtra("projectOperation", 0);//0 - online; 1 - offline
        projectExpType = getIntent().getIntExtra("projectExpType", 0);//0-OCAD; 1-CLP; 2-ZML; 3-ARP
        if (projectOperation == 0) {
            isNeddLoadMenu = true;
            doOperationDependsFromType();
        }else{
            isNeddLoadMenu = projectExpType != 3;
            loadOffLineBuilds(MainActivity.buildsDir, MainActivity.savesDir);
        }
        buildList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isNeddLoadMenu) {
            getMenuInflater().inflate(R.menu.refresh_builds_list, menu);
            this.menu = menu;
            /*
            if(projectExpType == 1){
                setItemsVisible(false);
            }
            if(foundIps != null && foundIps.getCount() != 0){
                menu.findItem(R.id.onlyByIpsFound).setVisible(true);
            }
            */
        }
        return true;
    }

    private void setAuthorizeName(){
        nameAuthorization = PreferenceManager.getDefaultSharedPreferences(this).getString(LoginActivity.EMAIL_KEY,"");
    }

    public void refreshOcadBuilds(){
        dialog = new ShowConnectionDialog();
        dialog.setContext(this);
        if (dialog.isOnline()) {
            if(MainActivity.getIsAuthorized()) {
                setAuthorizeName();
                adpList.clear();
                loadintTask = new LoadOCADBuldersName(this);
                loadintTask.execute(url);
            }else{
                new NotAuthorizedDialog().show(getSupportFragmentManager(), "notAuthorized");
            }
        }else{
            dialog.show(getSupportFragmentManager(), "connectionDialog");
        }
    }

    private void setItemsVisible(boolean isVisible){
        if (menu != null) {
            menu.findItem(R.id.refreshBuilds).setVisible(isVisible);
            menu.findItem(R.id.onlineBuilds).setVisible(!isVisible);
            menu.findItem(R.id.offlineBuilds).setVisible(isVisible);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        View v = findViewById(R.id.buildsFilesPath);
        switch(item.getItemId()){
            case R.id.refreshBuilds:
                projectOperation = 0;
                switch(projectExpType){
                    case 0:
                        refreshOcadBuilds();
                        break;
                    case 1:
                        testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS),false);
                        break;
                }
                break;
            case R.id.onlineBuilds:
                //setItemsVisible(true);
                projectOperation = 0;
                switch(projectExpType){
                    case 0:
                        refreshOcadBuilds();
                        break;
                    case 1:
                    case 2:
                        testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS),
                                foundIps.getCount()!=0);
                        break;
                }
                if(v != null) {
                    v.setVisibility(View.GONE);
                }
                break;
            case R.id.onlyByIpsFound:
                projectOperation = 0;
                testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS),true);
                break;
            case R.id.offlineBuilds:
                projectOperation = 1;
                //setItemsVisible(false);
                if (v != null) {
                    v.setVisibility(View.VISIBLE);
                }
                loadOffLineBuilds(MainActivity.buildsDir, MainActivity.savesDir);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        new SendExit(this, "exit", projectExpType).execute();
        Intent intent = new Intent();
        intent.putExtra("projectGuid", "");
        intent.putExtra("authorizedName",nameAuthorization);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        String guid = ((JsonProjs) view.getTag()).getGuid();
        if((projectExpType == 0)|(projectExpType == 3)|(projectOperation == 0)) {
            intent.putExtra("projectGuid", guid);
            intent.putExtra("authorizedName",nameAuthorization);
            setResult(RESULT_OK, intent);
            finish();
        }else
        if((projectExpType == 1)|(projectExpType == 2)) {
            new UnpackProject().execute(guid);
        }
    }

    @Override
    public void onRefreshAfterDelete() {
        if(ocadAdapter != null){
            ocadAdapter.notifyDataSetChanged();
        }
    }

    class UnpackProject extends AsyncTask<String,Void, String>{
        AsyncProgressDialog dialog;
        public UnpackProject(){
            dialog =
                    new AsyncProgressDialog(ListOfOnlineCadBuilders.this,
                            R.string.unpack_title,
                            R.string.unpack_message);
        }
        protected void onPreExecute(){
            dialog.createDialog();
        }
        @Override
        protected String doInBackground(String... params) {
            String unzippedPath = "";
            try {
                unzippedPath = new UnZipBuild(params[0], new File(params[0])
                        .getParent())
                        .ExUnzip()
                        .getAbsolutePath();
            }catch(IOException e){
                e.printStackTrace();
            }
            return unzippedPath;
        }
        protected void onPostExecute(String result){
            Intent intent = new Intent();
            intent.putExtra("projectGuid", result);
            intent.putExtra("authorizedName",nameAuthorization);
            setResult(RESULT_OK, intent);
            dialog.freeDialog();
            finish();
        }
    }

    @Override
    public void onShowDialog() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isSomeOperation", true);
        startActivityForResult(intent, AUTORIZATION);
        //startActivityForResult(new Intent(this, LoginActivity.class), UPLOAD_PHOTOS);
    }

    private void addNewProject(String guid, String name, String description){
        proj = new JsonProjs();
        proj.setGuid(guid);
        proj.setName(name);
        proj.setDesc(description);
        adpList.add(proj);
    }

    private void freeList(){
        adpList.clear();
    }

    public static <T> T[] concatenate (T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    private void loadOffLineBuilds(String appPath, String savesPath){
        File offBuilds = new File(appPath);
        File savesBuilds = new File(savesPath);
        String message = "";
        try {
            TextView filePath = (TextView) findViewById(R.id.buildsFilesPath);
            if(filePath != null) {
                filePath.setVisibility(View.VISIBLE);
                String pathes = appPath + "\n" + savesPath;
                filePath.setText(pathes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        freeList();
        if(offBuilds.isDirectory() | savesBuilds.isDirectory()) {
            File files[] = offBuilds.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    switch (projectExpType) {
                        case 0://O-CAD - *.JSON
                            return name.contains(".json");
                        case 1://CPL - *.CPLN
                            return name.contains(".cpln");
                        case 2://Estiamte - *.ZML
                            return name.contains(".zml");
                        case 3://Rus Estimate - *.ARP
                            return name.contains(".arp");
                        default:
                            return name.contains(".json");
                    }
                }
            });
            File folders[] = savesBuilds.listFiles();
            for(File f : folders){
                if(f.isDirectory()){
                    files = concatenate(files, f.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                        switch(projectExpType){
                            case 0://O-CAD - *.JSON
                                return filename.contains(".json");
                            case 1://CPL - *.CPLN
                                return filename.contains(".cpln");
                            case 2://Estiamte - *.ZML
                                return filename.contains(".zml");
                            case 3://Rus Estimate - *.ARP
                                return filename.contains(".arp");
                            default:
                                return filename.contains(".json");
                        }
                        }
                    }));
                }
            }
            if (files.length > 0 ){
                for (File f : files){
                    addNewProject(f.getAbsolutePath(), f.getName(),"");
                }
            }else{
                message = getResources().getString(R.string.builds_not_found);
              //  addNewProject("","Стройки не обнаружены.","");
            }
        } else {
            message = getResources().getString(R.string.builds_not_found);
            //addNewProject("", "Стройки не обнаружены.", "");
        }
        setAuthorizeName();
        if (adpList.size() != 0) {
            ocadAdapter = new OcadProjectsAdapter(this, adpList, projectOperation);
            buildList.setAdapter(ocadAdapter);
        }else{
            InfoCommonDialog dialog = new InfoCommonDialog();
            dialog.setMessage(message);
            dialog.show(getSupportFragmentManager(), "buildNotFoundDialog");
        }
    }

    public void doOperationDependsFromType(){
        switch(projectExpType){
            case 0:
                refreshOcadBuilds();
                break;
            case 1:
                if(foundIps != null && foundIps.getCount() != 0){
                    testSocketConnection(this, String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS), true);
                }else {
                    testSocketConnection(this, String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS), false);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 7){
            doOperationDependsFromType();
        }
        if (requestCode == AUTORIZATION){
            if (resultCode == RESULT_OK & data != null){
                nameAuthorization = data.getStringExtra("email");
                MainActivity.isAuthorized = true;
                if(data.getBooleanExtra("isSomeOperation", false)){
                    /*
                    adpList.clear();
                    loadintTask = new LoadOCADBuldersName(this);
                    loadintTask.execute(url);*/
                    doOperationDependsFromType();
                }
            }
        }
    }

    private class LoadOCADBuldersName extends AsyncTask<String,Void,Integer>{

        HttpURLConnection httpURLConnection;
        Context context;

        public LoadOCADBuldersName(Context ctx){
            context = ctx;
        }

        public void onPreExecute(){
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setReadTimeout(5000);
                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = httpURLConnection.getInputStream();
                    String JSONString = streamToString(stream);
                    try {
                        jsonObject = new JSONObject(JSONString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                httpURLConnection.disconnect();
            }catch(IOException e){
                e.printStackTrace();
                return 2;
            }
            return 1;
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
        protected void onPostExecute(Integer result){
            super.onPostExecute(result);
            String caption;
            switch(result){
                case 0:
                    break;
                case 1:
                    try {
                        JSONArray projArray = jsonObject.getJSONArray("projects");
                        JSONObject projectObject;
                        for (int i = 0 ; i<projArray.length(); i++){
                            projectObject = projArray.getJSONObject(i);
                            proj = new JsonProjs();
                            proj.setGuid(projectObject.getString("guid"));
                            proj.setName(projectObject.getString("name"));
                            proj.setDesc(projectObject.getString("descr"));
                            adpList.add(proj);
                        }
                        ocadAdapter = new OcadProjectsAdapter(context, adpList,projectOperation);
                        buildList.setAdapter(ocadAdapter);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    caption = context.getResources().getString(R.string.error_download_builds_list);
                    InfoCommonDialog infoDialog = new InfoCommonDialog();
                    infoDialog.setMessage(caption);
                    infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
                    //Toast.makeText(context, caption, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
            }
        });
    }

    public class JsonProjs {
        private String guid;
        private String name;
        private String desc;

        public JsonProjs(){}

        public void setGuid(String g){guid = g;}
        public void setName(String n){name = n;}
        public void setDesc(String d){desc = d;}

        public String getGuid(){return guid;}
        public String getName(){return name;}
        public String getDesc(){return desc;}
    }

    public void testSocketConnection(Context ctx, String messageText, boolean isByFound){
        dialog = new ShowConnectionDialog();
        Bundle params = new Bundle();
        params.putInt("connections", 1);
        dialog.setArguments(params);
        dialog.setContext(ctx);
        if (dialog.isOnline()) {
           // if(MainActivity.getIsAuthorized()) {
                setAuthorizeName();
                adpList.clear();
                SendRequests socket = new SendRequests(ctx, messageText, "",isByFound);
                socket.execute((Void) null);
            /*}else{
                new NotAuthorizedDialog().show(getSupportFragmentManager(), "notAuthorized");
            }*/
        }else{
            dialog.show(getSupportFragmentManager(), "connectionDialog");
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // for now eat exceptions
        return "";
    }

    //This class gets builds(only names & guids) from CLP.
    //So timeout to get some builds is 1 minute if not get answer connected
    public class SendRequests extends AsyncTask<Void,Void,Integer> {
        String[] result;
        Socket sendSocket;
        String messValue, IP;
        int portValue;
        String message = "";
        Context context;
        AsyncProgressDialog dialog;
        IPs currentIp;
        boolean isByFound;

        public SendRequests(Context ctx, String message, String ip, boolean isByFound){
            messValue = message;
            IP = ip;
            context = ctx;
            this.isByFound = isByFound;
            switch(projectExpType){
                case 1:
                    portValue = 51783;
                    break;
                case 2:
                case 3:
                    portValue = 51784;
                    break;
            }
            dialog = new AsyncProgressDialog(ListOfOnlineCadBuilders.this,
                    R.string.dialog_finding_caption,
                    R.string.dialog_finding_message);
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
        public void onPreExecute(){
            if(! isByFound) {
                foundIps.clearIps();
            }
            dialog.createDialog();
        }

        private int checkConnection(String ipAddress, boolean addIfSuccess) throws Exception{
            int whatReturn  = 0;
            SocketAddress socketAddress = new InetSocketAddress(ipAddress, portValue);
            sendSocket = new Socket();
            sendSocket.connect(socketAddress, 300);
            //sendSocket = new Socket(IP, portValue);
            Log.d(LoadFromLAN.TAG, "Connected to " + ipAddress);
            currentIp = new IPs();
            currentIp.setIp(ipAddress);
            byte[] mybytearray = messValue.getBytes();
            OutputStream os = sendSocket.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            Log.d(LoadFromLAN.TAG, "Send request with message " + messValue + " to " + ipAddress);

            long delay = 60000;
            long time = System.currentTimeMillis();

            InputStream is = sendSocket.getInputStream();
            int read;
            byte[] byteReader = new byte[100000];
            String helpStringReader;

            while ((read = is.read(byteReader))!= -1) {
                helpStringReader = new String(byteReader,"windows-1251").substring(0,read);
                message += helpStringReader;
                if ((!helpStringReader.equals(""))) {
                    Log.d(LoadFromLAN.TAG, "Got data from " + ipAddress);
                    if(helpStringReader.substring(helpStringReader.length() - 6).equals("done¶#")){
                        break;
                    }
                    else if(helpStringReader.substring(helpStringReader.length() - 6).equals("busy¶#")){
                        break;
                    }
                }
                if (System.currentTimeMillis() - time >= delay){
                    if(foundIps.getCount() == 0) {
                        whatReturn = 2;
                    }
                    break;
                }
            }
            is.close();
            result = message.split("¶#");
            if (result[result.length - 1].equals("done")) {
                currentIp.setUserName(result[0]);
                currentIp.setComputerName(result[1]);
                currentIp.setCanConnect(true);
                fillIpsProject(currentIp);
                if(addIfSuccess) {
                    foundIps.setIp(currentIp);
                }else{
                    foundIps.foundAndReplaceIp(currentIp);
                }
                whatReturn = 1;
            }else if(result[0].equals("busy")){
                whatReturn = 2;
            }
            if(sendSocket != null && sendSocket.isConnected()) {
                sendSocket.close();
            }
            return whatReturn;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int whatReturn = 0;
            String myIP = getIPAddress(true);
            String myIpStart = myIP.substring(0,myIP.lastIndexOf(".")+1);
            try {
                if (! isByFound) {
                    for (int i = 1; i < 255; i++) {
                        IP = myIpStart + i;
                        try {
                            result = null;
                            message = "";
                            whatReturn = checkConnection(IP, true);
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
                }else{
                    whatReturn = 1;
                    for (int i = 0; i < foundIps.getCount(); i++) {
                        IP = foundIps.getIp(i).getIp();
                        try {
                            result = null;
                            message = "";
                            checkConnection(IP, false);
                        } catch (Exception e) {
                            if (i == 92) {
                                Log.d(LoadFromLAN.TAG, "except on connect to " + IP);
                            }
                            if (sendSocket != null && sendSocket.isConnected()) {
                                sendSocket.close();
                            }
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return whatReturn;
        }

        public void fillIpsProject(IPs ips){
            JsonProjs projs;
            if(this.result.length > 0) {
                for (int i = 2; i < this.result.length-1; i++) {
                    String[] Projects = this.result[i].split("\\$");
                    projs = new JsonProjs();
                    projs.setName(Projects[0]);
                    projs.setGuid(Projects[1]);
                    projs.setDesc("");
                    ips.setProj(projs);
                }
            }
        }

        protected void onPostExecute(Integer result){
            String mess;
            InfoCommonDialog infoDialog;
            super.onPostExecute(result);
            switch(result){
                case 0:
                    mess = context.getResources().getString(R.string.connections_not_found);
                    ocadAdapter = new OcadProjectsAdapter(context, adpList,projectOperation);
                    buildList.setAdapter(ocadAdapter);
                    infoDialog = new InfoCommonDialog();
                    infoDialog.setMessage(mess);
                    infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
                    //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    //TODO Dialog of choose connection
                    ShowIPDialog dialog = new ShowIPDialog();
                    dialog.setContext(context);
                    Bundle params = new Bundle();
                    String[] ips = new String[foundIps.getCount()];
                    for(int i = 0; i < foundIps.getCount(); i++){
                        ips[i] = foundIps.getIp(i).getIp();
                    }
                    ArrayToJson toSave = new ArrayToJson();
                    toSave.setArray(foundIps.getFoundIp());
                    String jsonInString = toSave.convertToString();
                    edit.putString("foundIPsJSON", jsonInString);
                    edit.commit();
                    params.putStringArray("totalIps",ips);
                    dialog.setArguments(params);
                    dialog.show(getSupportFragmentManager(), "selectIp");
                    Log.d("socketsWork", message);
                    break;
                case 2:
                    mess = context.getResources().getString(R.string.server_is_busy);
                    ocadAdapter = new OcadProjectsAdapter(context, adpList,projectOperation);
                    buildList.setAdapter(ocadAdapter);
                    infoDialog = new InfoCommonDialog();
                    infoDialog.setMessage(mess);
                    infoDialog.show(((FragmentActivity)context).getSupportFragmentManager(),"infoDialog");
                    //Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
                    break;
            }
            dialog.freeDialog();
        }
    }

    public class ArrayToJson{
        JSONObject object;
        ArrayList<IPs> toSave;

        public ArrayToJson(){}
        public void setArray(ArrayList<IPs> found){
            toSave = found;
        }
        public String convertToString(){
            object = new JSONObject();
            JSONArray ipArray = new JSONArray();
            try {
                for(int i = 0; i< toSave.size();i++) {
                    JSONObject currentIP = new JSONObject();
                    currentIP.put("ip", toSave.get(i).getIp());
                    currentIP.put("user_name", toSave.get(i).getUserName());
                    currentIP.put("comp_name", toSave.get(i).getComputerName());
                    ipArray.put(currentIP);
                }
                object.put("Found", ipArray);
            }catch(JSONException e){
                e.printStackTrace();
            }

            return object.toString();
        }
        public ArrayList<IPs> convertToArray(String jsonString){
            try {
                object = new JSONObject(jsonString);
                JSONArray ipArray;
                ipArray = object.getJSONArray("Found");
                JSONObject currentIP;
                for(int i = 0; i < ipArray.length(); i++){
                    currentIP = ipArray.getJSONObject(i);
                    IPs currIp = new IPs();
                    currIp.setIp(currentIP.getString("ip"));
                    currIp.setUserName(currentIP.getString("user_name"));
                    currIp.setComputerName(currentIP.getString("comp_name"));
                    toSave.add(currIp);
                }
            }catch(JSONException e){
                e.printStackTrace();
            }
            return toSave;
        }
    }

    public static class SendExit extends AsyncTask<Void,Void,Integer> {
        Socket sendSocket;
        String messValue, IP;
        int portValue;
        Context context;

        public SendExit(Context ctx, String message, int projType){
            messValue = message;
            context = ctx;
            switch(projType){
                case 1:
                    portValue = 51783;
                    break;
                case 2:
                case 3:
                    portValue = 51784;
                    break;
            }
        }
        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                for (int i = 0; i < foundIps.getCount(); i++) {
                    IP = foundIps.getIp(i).getIp();
                    try {
                        sendSocket = new Socket(IP, portValue);
                        byte[] mybytearray = messValue.getBytes();
                        OutputStream os = sendSocket.getOutputStream();
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();
                        sendSocket.close();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class ShowIPDialog extends DialogFragment{
        FoundIpsAdapter adapter;
        Context context;
        public ShowIPDialog(){
        }

        public void setContext(Context ctx){
            context = ctx;
            adapter = new FoundIpsAdapter(context,foundIps);
        }

        @Override
        public Dialog onCreateDialog(Bundle data){
            String[] ips = getArguments().getStringArray("totalIps");

            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog. setTitle(R.string.dialog_title);
            dialog.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.ipPosition = which;
                    IPs selectedIp = foundIps.getIp(which);
                    if(selectedIp.getCanConnect()) {
                        ((ListOfOnlineCadBuilders) getActivity())
                                .adpList
                                .addAll(selectedIp.getAllProjs());
                        ((ListOfOnlineCadBuilders) getActivity())
                                .ocadAdapter = new
                                OcadProjectsAdapter(getActivity(),
                                ((ListOfOnlineCadBuilders) getActivity()).adpList,
                                ((ListOfOnlineCadBuilders) getActivity()).projectOperation);
                        ((ListOfOnlineCadBuilders) getActivity())
                                .buildList
                                .setAdapter(((ListOfOnlineCadBuilders) getActivity()).ocadAdapter);
                    }else{
                        InfoCommonDialog dlg = new InfoCommonDialog();
                        dlg.setMessage(getActivity().getResources().getString(R.string.error_download_builds_list));
                        dlg.show(getActivity().getSupportFragmentManager(), "serversDead");
                    }
                }
            });
            dialog.setPositiveButton(R.string.refresh_builds, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((ListOfOnlineCadBuilders) getActivity()).projectOperation = 0;
                    ((ListOfOnlineCadBuilders) getActivity()).
                            testSocketConnection(getActivity(),
                                    String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS),false);
                }
            });
            return dialog.create();
        }
    }

    public static class FoundComputersInLAN{
        ArrayList<IPs> foundIp;

        public FoundComputersInLAN(){
            foundIp = new ArrayList<>();
        }

        public void setIp(IPs ip){
            foundIp.add(ip);
        }

        public IPs getIp(int position){
            return foundIp.get(position);
        }

        public int getCount(){
            return foundIp.size();
        }

        public void clearIps(){
            foundIp.clear();
        }

        public void foundAndReplaceIp(IPs newIp){
            for(IPs ip : foundIp){
                if(ip.getIp().equals(newIp.getIp())){
                    //foundIp.add(foundIp.indexOf(ip), newIp);
                    foundIp.set(foundIp.indexOf(ip), newIp);
                }
            }
        }
        public ArrayList<IPs> getFoundIp(){
            return foundIp;
        }

    }

    public class IPs implements Serializable{
        private String ip;
        private String computerName;
        private String userName;
        private boolean canConnect;

        private ArrayList<JsonProjs> projects;

        public IPs(){
            projects = new ArrayList<>();
            canConnect = false;
        }

        public void setIp(String ip){
            this.ip = ip;
        }

        public String getIp(){
            return ip;
        }

        public void setUserName(String user){
            userName = user;
        }

        public String getUserName(){
            return userName;
        }

        public void setComputerName(String computer){
            computerName = computer;
        }

        public String getComputerName(){
            return computerName;
        }

        public boolean getCanConnect(){
            return canConnect;
        }

        public void setCanConnect(boolean isBusy){
            this.canConnect = isBusy;
        }

        public void setProj(JsonProjs proj){
            projects.add(proj);
        }

        public JsonProjs getProj(int position){
            return projects.get(position);
        }

        public ArrayList<JsonProjs> getAllProjs(){
            return projects;
        }

        public int getCount(){return projects.size();}

        public JsonProjs getProjectByGuid(String guid){
            for(JsonProjs proj: projects){
                if(proj.getGuid().equals(guid)){
                    return proj;
                }
            }
            return null;
        }
    }
}
