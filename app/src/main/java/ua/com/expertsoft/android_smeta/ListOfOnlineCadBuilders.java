package ua.com.expertsoft.android_smeta;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;

import ua.com.expertsoft.android_smeta.adapters.FoundIpsAdapter;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.NotAuthorizedDialog;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.standard_projects.UnZipBuild;
import ua.com.expertsoft.android_smeta.adapters.OcadProjectsAdapter;
import ua.com.expertsoft.android_smeta.asynktasks.AsyncProgressDialog;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.ShowConnectionDialog;

public class ListOfOnlineCadBuilders extends AppCompatActivity implements
        AdapterView.OnItemClickListener, NotAuthorizedDialog.OnShowAuthorizeDialog {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAppConfiguration();
        foundIps = new FoundComputersInLAN();
        setContentView(R.layout.activity_list_of_online_cad_builders);
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
        projectOperation = getIntent().getIntExtra("projectOperation", 0);
        projectExpType = getIntent().getIntExtra("projectExpType", 0);
        if (projectOperation == 0) {
            isNeddLoadMenu = true;
            switch(projectExpType){
                case 0:
                    refreshOcadBuilds();
                    break;
                case 1:
                    testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS));
                    break;
            }
        }else{
            isNeddLoadMenu = false;
            loadOffLineBuilds(MainActivity.buildsDir, MainActivity.savesDir);
        }
        buildList.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(isNeddLoadMenu) {
            getMenuInflater().inflate(R.menu.refresh_builds_list, menu);
            this.menu = menu;
        }
        return true;
    }

    public void refreshOcadBuilds(){
        dialog = new ShowConnectionDialog();
        dialog.setContext(this);
        if (dialog.isOnline()) {
            if(MainActivity.getIsAuthorized()) {
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
        new SendExit(this,"exit").execute();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.refreshBuilds:
                switch(projectExpType){
                    case 0:
                        refreshOcadBuilds();
                        break;
                    case 1:
                        testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS));
                        break;
                }
                break;
            case R.id.onlineBuilds:
                setItemsVisible(true);
                switch(projectExpType){
                    case 0:
                        refreshOcadBuilds();
                        break;
                    case 1:
                        testSocketConnection(this,String.valueOf(LoadFromLAN.GIVE_ME_BUILDS_PARAMS));
                        break;
                }
                break;
            case R.id.offlineBuilds:
                setItemsVisible(false);
                loadOffLineBuilds(MainActivity.buildsDir, MainActivity.savesDir);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    public <T> T[] concatenate (T[] a, T[] b) {
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
            for(int i =0; i < folders.length; i++){
                if(folders[i].isDirectory()){
                    files = concatenate(files, folders[i].listFiles(new FilenameFilter() {
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
                for (int i = 0 ; i < files.length; i++){
                    addNewProject(files[i].getAbsolutePath(), files[i].getName(),"");
                }
            }else{
                addNewProject("","Стройки не обнаружены.","");
            }
        } else {
            addNewProject("", "Стройки не обнаружены.", "");
        }
        ocadAdapter = new OcadProjectsAdapter(this, adpList);
        buildList.setAdapter(ocadAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 7){
            refreshOcadBuilds();
        }
        if (requestCode == AUTORIZATION){
            if (resultCode == RESULT_OK & data != null){
                nameAuthorization = data.getStringExtra("email");
                if(data.getBooleanExtra("isSomeOperation", false)){
                    adpList.clear();
                    loadintTask = new LoadOCADBuldersName(this);
                    loadintTask.execute(url);
                }
                MainActivity.isAuthorized = true;
            }
        }
    }

    private class LoadOCADBuldersName extends AsyncTask<String,Void,Integer>{

        HttpURLConnection httpURLConnection;
        Context context;

        public LoadOCADBuldersName(){}

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
                        ocadAdapter = new OcadProjectsAdapter(context, adpList);
                        buildList.setAdapter(ocadAdapter);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    caption = context.getResources().getString(R.string.error_download_builds_list);
                    Toast.makeText(context, caption, Toast.LENGTH_SHORT).show();
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

    public void testSocketConnection(Context ctx, String messageText){
        adpList.clear();
        SendRequests socket = new SendRequests(ctx, messageText, "");
        socket.execute((Void) null);
    }

    public class SendRequests extends AsyncTask<Void,Void,Integer> {
        String[] result;
        Socket sendSocket;
        String messValue, IP;
        int portValue;
        String message = "";
        Context context;
        AsyncProgressDialog dialog;
        IPs currentIp;

        public SendRequests(Context ctx, String message, String ip){
            messValue = message;
            IP = ip;
            context = ctx;
            switch(projectExpType){
                case 1:
                    portValue = 1149;
                    break;
                case 2:
                case 3:
                    portValue = 1150;
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
            foundIps.clearIps();
            dialog.createDialog();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO Auto-generated method stub
            int whatReturn = 0;
            int j=1;
            boolean network;
            try {
                for (int i = 1; i < 255; i++) {
                    IP = "192.168." + j + "." + i;
                    try {
                        network = true;//isReachableByTcp(IP, portValue, 50);
                        if (network) {
                            SocketAddress socketAddress = new InetSocketAddress(IP, portValue);
                            sendSocket = new Socket();
                            sendSocket.connect(socketAddress, 50);
                            //sendSocket = new Socket(IP, portValue);
                            currentIp = new IPs();
                            currentIp.setIp(IP);
                            byte[] mybytearray = messValue.getBytes();
                            OutputStream os = sendSocket.getOutputStream();
                            os.write(mybytearray, 0, mybytearray.length);
                            os.flush();

                            while (true) {
                                InputStream is = sendSocket.getInputStream();
                                InputStreamReader reader = new InputStreamReader(is, "windows-1251");
                                char[] readerChar = new char[is.available()];
                                reader.read(readerChar,0,readerChar.length);
                                message = String.copyValueOf(readerChar);
                                if ((! message.equals(""))){
                                        result = message.split("#");
                                    if (result[0].equals("done")){
                                        reader.close();
                                        currentIp.setUserName(result[1]);
                                        currentIp.setComputerName(result[2]);
                                        fillIpsProject(currentIp);
                                        foundIps.setIp(currentIp);
                                        whatReturn = 1;
                                        break;

                                    }
                                    else if (result[0].equals("busy")){
                                        whatReturn = 2;
                                        break;
                                    }
                                }
                            }
                            sendSocket.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
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
                for (int i = 3; i < this.result.length; i++) {
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
            super.onPostExecute(result);
            switch(result){
                case 0:
                    mess = context.getResources().getString(R.string.connections_not_found);
                    Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    //Toast.makeText(context, "Отправлено и получено: " + message, Toast.LENGTH_LONG).show();
                    if (foundIps.getCount() > 1 ){
                        //TODO Dialog of choose connection
                        ShowIPDialog dialog = new ShowIPDialog();
                        dialog.setContext(context);
                        Bundle params = new Bundle();
                        String[] ips = new String[foundIps.getCount()];
                        for(int i = 0; i < foundIps.getCount(); i++){
                            ips[i] = foundIps.getIp(i).getIp();
                        }
                        params.putStringArray("totalIps",ips);
                        dialog.setArguments(params);
                        dialog.show(getSupportFragmentManager(), "selectIp");
                    }else{
                        adpList.addAll(foundIps.getIp(0).getAllProjs());
                        ocadAdapter = new OcadProjectsAdapter(context, adpList);
                        buildList.setAdapter(ocadAdapter);
                    }
                    Log.d("socketsWork", message);
                    break;
                case 2:
                    mess = context.getResources().getString(R.string.server_is_busy);
                    Toast.makeText(context, mess, Toast.LENGTH_LONG).show();
                    break;
            }
            dialog.freeDialog();
        }
    }

    public class SendExit extends AsyncTask<Void,Void,Integer> {
        Socket sendSocket;
        String messValue, IP;
        int portValue;
        Context context;
        IPs currentIp;

        public SendExit(Context ctx, String message){
            messValue = message;
            context = ctx;
            switch(projectExpType){
                case 1:
                    portValue = 1149;
                    break;
                case 2:
                case 3:
                    portValue = 1150;
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
                        currentIp = new IPs();
                        currentIp.setIp(IP);
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
                    ((ListOfOnlineCadBuilders) getActivity())
                            .adpList
                            .addAll(selectedIp.getAllProjs());
                    ((ListOfOnlineCadBuilders) getActivity())
                            .ocadAdapter = new
                            OcadProjectsAdapter(getActivity(),
                            ((ListOfOnlineCadBuilders) getActivity()).adpList);
                    ((ListOfOnlineCadBuilders) getActivity())
                            .buildList
                            .setAdapter(((ListOfOnlineCadBuilders) getActivity()).ocadAdapter);
                }
            });
            return dialog.create();
        }
    }

    public class FoundComputersInLAN{
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

    }

    public class IPs implements Serializable{
        private String ip;
        private String computerName;
        private String userName;
        private ArrayList<JsonProjs> projects;

        public IPs(){
            projects = new ArrayList<>();
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
