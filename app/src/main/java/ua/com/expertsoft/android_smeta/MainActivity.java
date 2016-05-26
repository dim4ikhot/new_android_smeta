package ua.com.expertsoft.android_smeta;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import ua.com.expertsoft.android_smeta.asynctasks.RecalculateFactsByExecution;
import ua.com.expertsoft.android_smeta.custom_calendar.CalendarShower;
import ua.com.expertsoft.android_smeta.data.UserSubTask;
import ua.com.expertsoft.android_smeta.data.UserTask;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.NotAuthorizedDialog;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.OperationWithFacts;
import ua.com.expertsoft.android_smeta.language.UpdateLanguage;
import ua.com.expertsoft.android_smeta.selected_project.ProjectInfo;
import ua.com.expertsoft.android_smeta.settings.SettingsActivity;
import ua.com.expertsoft.android_smeta.sheet.SheetActivity;
import ua.com.expertsoft.android_smeta.standard_project.UnZipBuild;
import ua.com.expertsoft.android_smeta.static_data.CommonData;
import ua.com.expertsoft.android_smeta.static_data.SelectedFact;
import ua.com.expertsoft.android_smeta.static_data.SelectedLocal;
import ua.com.expertsoft.android_smeta.static_data.SelectedObjectEstimate;
import ua.com.expertsoft.android_smeta.static_data.SelectedWork;
import ua.com.expertsoft.android_smeta.adapters.StandardProjectMainAdapter;
import ua.com.expertsoft.android_smeta.adapters.UsersTasksAdapter;
import ua.com.expertsoft.android_smeta.asynctasks.ARPLoader;
import ua.com.expertsoft.android_smeta.asynctasks.CPLNLoader;
import ua.com.expertsoft.android_smeta.asynctasks.DeleteProject;
import ua.com.expertsoft.android_smeta.asynctasks.LoadingNavigatinMenu;
import ua.com.expertsoft.android_smeta.asynctasks.LoadingOcadBuild;
import ua.com.expertsoft.android_smeta.asynctasks.SaveProjectToServer;
import ua.com.expertsoft.android_smeta.asynctasks.UploadPhotoToServer;
import ua.com.expertsoft.android_smeta.asynctasks.ZMLLoader;
import ua.com.expertsoft.android_smeta.data.DBORM;
import ua.com.expertsoft.android_smeta.data.Facts;
import ua.com.expertsoft.android_smeta.data.LS;
import ua.com.expertsoft.android_smeta.data.OS;
import ua.com.expertsoft.android_smeta.data.ProjectExp;
import ua.com.expertsoft.android_smeta.data.Projects;
import ua.com.expertsoft.android_smeta.data.UserProjects;
import ua.com.expertsoft.android_smeta.data.Works;
import ua.com.expertsoft.android_smeta.data.WorksResources;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.DialogAboutProgram;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.ProjectExistsDialog;
import ua.com.expertsoft.android_smeta.dialogs.dialogFragments.ShowConnectionDialog;
import ua.com.expertsoft.android_smeta.static_data.UserLoginInfo;
import ua.com.expertsoft.android_smeta.tweet.TwitterActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener,
        LoadingOcadBuild.OnGetLoadedProjectListener, StandardProjectMainAdapter.OnOpenWorksListener,
        AdapterView.OnItemLongClickListener,ExpandableListView.OnGroupClickListener,View.OnClickListener,
        ProjectExistsDialog.OnProjectLodsOptions, UsersTasksAdapter.onGetUserTaskDoneListener,
        LoginActivity.OnAuthorizedListener, NotAuthorizedDialog.OnShowAuthorizeDialog,
        SaveProjectToServer.OnGetFullProjectListener,OperationWithFacts.OnGetAccessRecalcFactsListener {

    final static int SHOW_ONLINE = 0;
    final static int SHOW_FILES = 1;

    final static int UPLOAD_TO_SERVER = 0;
    final static int UPLOAD_TO_FILE = 1;
    final static int UPLOAD_TO_CLP = 2;
    final static int UPLOAD_TO_EE = 3;

    final static int EDIT_GROUP_LIST = 1;
    final static int ADD_NEW_TASK = 2;
    final static int EDIT_EXISTS_TASK = 3;
    final static int LOAD_OCAD_BUILDER = 4;
    final static int SHOW_WORKS = 5;
    final static int MAKES_PHOTO = 6;
    final static int UPLOAD_PHOTOS = 7;
    final static int AUTORIZATION = 8;
    final static int SHOW_SETTINGS = 9;
    final static int NORMS_SHEET = 10;
    final static int RESOURCES_SHEET = 11;
    public static int ipPosition = 0;

    final static String photosDir = Environment.getExternalStorageDirectory()+
            "/Android/data/ua.com.expertsoft.android_smeta/photos/";
    final static String savesDir = Environment.getExternalStorageDirectory()+
            "/Android/data/ua.com.expertsoft.android_smeta/saves/";
    final static String buildsDir = Environment.getExternalStorageDirectory()+
            "/Android/data/ua.com.expertsoft.android_smeta/builds/";

    final static int LOAD_PROJECT = 0;
    final static int UPDATE_PROJECT = 1;

    private float lastTranslate = 0.0f;
    DrawerLayout drawer;
    NavigationView navigationView;

    CoordinatorLayout mainView;
    Toolbar toolbar;
    private CharSequence mTitle;
    DBORM database;
    public ProjectsData projectsData;
    LinearLayout emptyBox;
    ExpandableListView buildersList;
    ListView buildersUser;
    UserTask usersTask;
    UserTask tempTask;
    UsersTasksAdapter builderListAdp;
    UserProjectsCollection userProjCollection;
//    For builds loading*********************
    LoadingOcadBuild loadProj;
    CPLNLoader cplnLoader;
    ZMLLoader zmlLoader;
    ARPLoader arpLoader;
//    END *********************
    public FloatingActionButton fab;
    StandardProjectMainAdapter mainAdapter;
    ShowConnectionDialog dialog;
    NavigationView listNavigator;
    String guid;
    boolean isNeedUpdate = false;
    boolean isFirstLaunch = true;
    public static boolean isAuthorized = false;
    static boolean isBuildLoadingFromFile = false;

    //Selected by User
    Projects selectedProject;
    OS selectedOs;
    LS selectedLs;
    View longSelectedView;
    int longSelectedPosition = -1;
    int globalProjectExpType = -1;
    SharedPreferences sharedPref;
    boolean photoUpload = true;

    /*TODO************************************************************************************/
    /***********************************    ACTIVITY'S METHODS    ****************************/

    //TODO "B"
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                new ExitApp().show(getSupportFragmentManager(), "exitDialog");
            }
        }
    }

    //TODO "C"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        forbidLockScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        updateAppConfiguration();
        //Create base directory
        createBasePath();
        database = new DBORM(this);
        ListOfOnlineCadBuilders.createListOfIps();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
        // Loading navigation drawer MENU
        userProjCollection = new UserProjectsCollection();
        //Common params to load all projects
        CommonData.context = this;
        CommonData.navigation = navigationView;
        CommonData.database = this.database;
        CommonData.userCollection = userProjCollection;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        emptyBox = (LinearLayout)findViewById(R.id.layoutEmptyBox);
        //Standard projects ExpandableListView
        buildersList = (ExpandableListView)findViewById(R.id.buildersStandardList);
        //buildersList.setOnItemLongClickListener(this);
        if(buildersList != null) {
            buildersList.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);
            buildersList.setOnGroupClickListener(this);
        }
        //Users ListView
        buildersUser = (ListView)findViewById(R.id.buildersUsersList);
        if(buildersUser != null) {
            buildersUser.setOnItemClickListener(this);
        }
        //get current title
        mTitle = getTitle();
        //Init floating action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ProjectInfo.PROJECT_GUID.equals("")) {
                    if (projectsData.getProjectsTypeUsers() != null) {
                        Intent intent = new Intent(getApplicationContext(), AddNewTasks.class);
                        intent.putExtra("projectName", mTitle);
                        startActivityForResult(intent, ADD_NEW_TASK);
                    } else if (projectsData.getProjectsTypeStandart() != null) {
                        Intent intent  = new Intent(MainActivity.this, ListOfOnlineCadBuilders.class);
                        switch (projectsData.getProjectsTypeStandart().getProjExpType()) {
                            case 0: // O-CAD
                                intent.putExtra("projectOperation", SHOW_ONLINE);
                                globalProjectExpType = 0;
                                intent.putExtra("projectExpType", 0);
                                break;
                            case 1: // CPL
                                intent.putExtra("projectOperation", SHOW_FILES);
                                globalProjectExpType = 1;
                                intent.putExtra("projectExpType", 1);
                                break;
                            case 2: //ZML
                                intent.putExtra("projectOperation", SHOW_FILES);
                                globalProjectExpType = 2;
                                intent.putExtra("projectExpType", 2);
                                break;
                            case 3: //ARP
                                intent.putExtra("projectOperation", SHOW_FILES);
                                globalProjectExpType = 3;
                                intent.putExtra("projectExpType", 3);
                                break;
                        }
                        intent.putExtra("title",mTitle);
                        startActivityForResult(intent, LOAD_OCAD_BUILDER);
                    }
                }else{
                    resetSelectedProject();
                }
            }
        });
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mainView = (CoordinatorLayout)findViewById(R.id.coordinatorLayoutId);
        listNavigator = navigationView;//(NavigationView)findViewById(R.id.nav_view);
        listNavigator.getHeaderView(0).findViewById(R.id.imgSettings).setOnClickListener(this);
        listNavigator.getHeaderView(0).findViewById(R.id.signInOut).setOnClickListener(this);
        listNavigator.getHeaderView(0).findViewById(R.id.imgUser).setOnClickListener(this);
        // Open/Close Drawer Listener
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                if(getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayShowTitleEnabled(true);
                }
                setTitle(mTitle);
            }

            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (listNavigator.getWidth() * slideOffset);
                TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                anim.setDuration(0);
                anim.setFillAfter(true);
                mainView.startAnimation(anim);

                lastTranslate = moveFactor;
            }

            @Override
            public void onDrawerOpened(View drawer){
                super.onDrawerOpened(drawer);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        String mail = PreferenceManager.getDefaultSharedPreferences(this).getString(LoginActivity.EMAIL_KEY,"");
        String pass = PreferenceManager.getDefaultSharedPreferences(this).getString(LoginActivity.PASSWORD_KEY, "");
        String service = PreferenceManager.getDefaultSharedPreferences(this).getString(LoginActivity.SERVICE_ITEM, "");
        if(service.equals("")){
            service = "http://195.62.15.35:8084";
        }
        checkAuthorized(mail, pass,service);
    }

    public static void startLoadFile(){
        try{
            Uri data = ((MainActivity)CommonData.context).getIntent().getData();
            if( data != null) {
                isBuildLoadingFromFile = true;
                String file = data.getEncodedPath().replace("%20", " ");
                SubMenu submenu = CommonData.navigation.getMenu().findItem(R.id.projectsTasks).getSubMenu();
                if (file.contains("zml")) {
                    ((MainActivity)CommonData.context).globalProjectExpType = 2;
                    file = new UnZipBuild(file, new File(file)
                            .getParent())
                            .ExUnzip()
                            .getAbsolutePath();
                } else if (file.contains("cpln")) {
                    ((MainActivity)CommonData.context).globalProjectExpType = 1;
                    file = new UnZipBuild(file, new File(file)
                            .getParent())
                            .ExUnzip()
                            .getAbsolutePath();
                } else if (file.contains("arp")) {
                    ((MainActivity)CommonData.context).globalProjectExpType = 3;
                }
                ((MainActivity)CommonData.context).onNavigationItemSelected(
                        submenu.findItem(((MainActivity)CommonData.context).globalProjectExpType));
                ((MainActivity)CommonData.context).guid = file;
                ((MainActivity)CommonData.context).doOperationWithProject(
                        ((MainActivity)CommonData.context).findProject(file),
                        ((MainActivity)CommonData.context).guid);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateAppConfiguration();
    }

    //TODO "D"
    @Override
    protected void onDestroy(){
        database.destroyHelper();
        if(sharedPref == null) {
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        }
        SharedPreferences.Editor edit = sharedPref.edit();
        boolean isSaveParams = sharedPref.getBoolean(LoginActivity.REMEMBER_ME, false);
        if(!isSaveParams){
            edit.putString(LoginActivity.EMAIL_KEY,"");
            edit.putString(LoginActivity.PASSWORD_KEY,"");
            edit.apply();
        }
        resetSelectedProject();
        findAndKill();
        super.onDestroy();
    }

    private void findAndKill() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for (int i = 0; i < procInfos.size(); i++) {
            if (procInfos.get(i).processName.equals("ua.com.expertsoft.android_smeta")) {
               // activityManager.killBackgroundProcesses(procInfos.get(i).processName);
                Process.killProcess(Process.myPid());
                break;
            }
        }
    }

    //TODO "R"
    @Override
    protected void onResume(){
        updateAppConfiguration();
        super.onResume();
    }

    //TODO "S"
    @Override
    protected void onStart(){
        super.onStart();
        if(isFirstLaunch){
            Handler handler = new Handler();
            // run a thread after 2 seconds to start the home screen
            handler.post(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, SplashActivity.class));
                }
            });
            isFirstLaunch = false;
           /* Handler handler2 = new Handler();
            handler2.post(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(MainActivity.this, ServerSide.class));
                }
            });*/
        }
    }



    /*TODO**********************   MENU   ****************************/
    //TODO "C"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //TODO "N"
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id != R.id.add_new_item) {
            Object itemTag = item.getActionView().getTag();
            projectsData = (ProjectsData)itemTag;
            mTitle = item.getTitle();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if(drawer != null) {
                drawer.closeDrawer(GravityCompat.START);
            }
            resetSelectedProject();
            switch(projectsData.getProjectsType()){
                case 0:
                    /*setTitle(mTitle);
                    setTasksVisible();
                    if (projectsData.getProjectsTypeStandart().getProjectsCount() == 0){
                        fab.callOnClick();
                    }else{
                        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                    }
                    break;*/
                case 1:
                case 2:
                case 3:
                    setTitle(mTitle);
                    setTasksVisible();
                    if (projectsData.getProjectsTypeStandart().getProjectsCount() == 0 & !isBuildLoadingFromFile){
                        fab.callOnClick();
                    }else{
                        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                        isBuildLoadingFromFile = false;
                    }
                    /*setTasksVisible();
                    showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());*/
                    break;
                case 4:
                    setTasksVisible();
                    break;
            }
            return true;
        }
        else{
            Intent intent = new Intent(this, EditingList.class);
            //intent.putExtra("userProjects", userProjCollection);
            startActivityForResult(intent, EDIT_GROUP_LIST);
            return false;
        }
    }

    //TODO "O"
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        File dir;
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch(id){
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_makes_photo:
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                dir = new File(photosDir+ ProjectInfo.PROJECT_GUID);
                if(! dir.isDirectory()){
                    boolean createDirResult = dir.mkdirs();
                }
                showCamera(dir.getPath());
                break;
            case R.id.action_view_photos:
                openFolder();
                break;
            case R.id.action_upload_photos:
                dialog = new ShowConnectionDialog();
                dialog.setContext(this);
                photoUpload = true;
                if(dialog.isOnline()) {
                    if(getIsAuthorized()) {
                        uploadPhotos(this, photosDir + "/" + ProjectInfo.PROJECT_GUID);
                    }else{
                        new NotAuthorizedDialog().show(getSupportFragmentManager(), "notAuthorized");
                    }
                }else{
                    dialog.show(getSupportFragmentManager(), "connectionDialog");
                }
                break;
            case R.id.action_delete_project:
                (new ShowDeleteDialog()).show(getSupportFragmentManager(), "delProjDialog");
                break;
            case R.id.action_about:
                DialogAboutProgram aboutDialog = new DialogAboutProgram();
                aboutDialog.show(getSupportFragmentManager(), "aboutDialog");
                break;
            case R.id.action_upload_project:
                dialog = new ShowConnectionDialog();
                dialog.setContext(this);
                photoUpload = false;
                if(dialog.isOnline()) {
                    if(getIsAuthorized()) {
                        uploadProject(this, savesDir + ProjectInfo.PROJECT_GUID);
                    }else{
                        new NotAuthorizedDialog().show(getSupportFragmentManager(), "notAuthorized");
                    }
                }else{
                    dialog.show(getSupportFragmentManager(), "connectionDialog");
                }
                break;
            case R.id.action_upload_project_to_clp:
                //TODO: LOADING PROJECT TO CLP
                SaveProjectToServer saveProjectToCLP = new SaveProjectToServer(this, savesDir+ProjectInfo.PROJECT_GUID);
                saveProjectToCLP.execute(UPLOAD_TO_CLP);
                break;
            case R.id.action_upload_project_to_ee:
                SaveProjectToServer saveProjectToEE = new SaveProjectToServer(this, savesDir+ProjectInfo.PROJECT_GUID);
                saveProjectToEE.execute(UPLOAD_TO_EE);
                break;
            case R.id.action_save_project:
                SaveProjectToServer saveProject = new SaveProjectToServer(this, savesDir+ProjectInfo.PROJECT_GUID);
                saveProject.execute(UPLOAD_TO_FILE);
                break;
            case R.id.action_update_data_from_file:
                intent = new Intent(getApplicationContext(), ListOfOnlineCadBuilders.class);
                intent.putExtra("projectOperation", SHOW_FILES);
                startActivityForResult(intent, LOAD_OCAD_BUILDER);
                isNeedUpdate = true;
                break;
            case R.id.action_view_calendar:
                startActivity(new Intent(this, CalendarShower.class));
                break;
            case R.id.action_authorization:
                intent = new Intent(this, LoginActivity.class);
                intent.putExtra("isSomeOperation", false);
                startActivityForResult(intent, AUTORIZATION);
                break;
            case R.id.checkSocketConn:
                break;
            case R.id.action_norms_sheet:
                intent = new Intent(this, SheetActivity.class);
                intent.putExtra("isNormsSheet",1);
                intent.putExtra("sheet_title",getResources().getString(R.string.standard_norms_sheet));
                startActivityForResult(intent, NORMS_SHEET);
                break;
            case R.id.action_resource_sheet:
                intent = new Intent(this, SheetActivity.class);
                intent.putExtra("isNormsSheet",0);
                intent.putExtra("sheet_title",getResources().getString(R.string.standard_resources_sheet));
                startActivityForResult(intent, RESOURCES_SHEET);
                break;
            case R.id.action_twitter:
                startActivity(new Intent(this, TwitterActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //TODO "P"
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    e.printStackTrace();
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
            MenuItem item = menu.findItem(R.id.action_makes_photo);
            MenuItem item_view = menu.findItem(R.id.action_view_photos);
            MenuItem item_upload = menu.findItem(R.id.action_upload_photos);
            MenuItem item_del = menu.findItem(R.id.action_delete_project);
            MenuItem item_save = menu.findItem(R.id.action_save_project);
            MenuItem item_update = menu.findItem(R.id.action_update_data_from_file);
            MenuItem item_upload_proj = menu.findItem(R.id.action_upload_project);
            MenuItem item_upload_proj_to_clp = menu.findItem(R.id.action_upload_project_to_clp);
            MenuItem item_upload_proj_to_ee = menu.findItem(R.id.action_upload_project_to_ee);
            MenuItem item_authorization = menu.findItem(R.id.action_authorization);
            MenuItem item_norms_sheet = menu.findItem(R.id.action_norms_sheet);
            MenuItem item_resources_sheet = menu.findItem(R.id.action_resource_sheet);

            if (projectsData!= null && projectsData.getProjectsType() == 1){
                item_upload_proj_to_clp.setVisible(true);
            }else{
                item_upload_proj_to_clp.setVisible(false);
            }
            if (projectsData!= null && projectsData.getProjectsType() == 2){
                item_upload_proj_to_ee.setVisible(true);
            }else{
                item_upload_proj_to_ee.setVisible(false);
            }

            if (projectsData!= null && projectsData.getProjectsType() == 0){
                item_upload.setVisible(true);
                item_upload_proj.setVisible(true);
                item_authorization.setVisible(true);
            }else{
                item_upload.setVisible(false);
                item_upload_proj.setVisible(false);
                item_authorization.setVisible(false);
            }

            if(!ProjectInfo.PROJECT_GUID.equals("")){
                item.setEnabled(true);
                File[] foundFiles = (new File(photosDir + File.separator + ProjectInfo.PROJECT_GUID)).listFiles();
                if(foundFiles != null && foundFiles.length > 0) {
                    item_view.setEnabled(true);
                    item_upload.setEnabled(true);
                }else{
                    item_view.setEnabled(false);
                    item_upload.setEnabled(false);
                }
                item_del.setEnabled(true);
                item_save.setEnabled(true);
                item_update.setEnabled(true);
                item_upload_proj.setEnabled(true);
                item_upload_proj_to_clp.setEnabled(true);
                item_upload_proj_to_ee.setEnabled(true);
                item_norms_sheet.setEnabled(true);
                item_resources_sheet.setEnabled(true);
            }else{
                item.setEnabled(false);
                item_view.setEnabled(false);
                item_upload.setEnabled(false);
                item_del.setEnabled(false);
                item_save.setEnabled(false);
                item_update.setEnabled(false);
                item_upload_proj.setEnabled(false);
                item_upload_proj_to_clp.setEnabled(false);
                item_upload_proj_to_ee.setEnabled(false);
                item_norms_sheet.setEnabled(false);
                item_resources_sheet.setEnabled(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /*****************************   END MENU   ************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case EDIT_GROUP_LIST:
                    if (resultCode == RESULT_OK) {
                        userProjCollection = CommonData.userCollection;
                        LoadingNavigatinMenu loadingNavigatinMenu =
                                new LoadingNavigatinMenu(this, navigationView,
                                        database, userProjCollection, null);
                        loadingNavigatinMenu.execute(1);
                    }
                    break;
                case ADD_NEW_TASK:
                    if (resultCode == RESULT_OK) {
                        //Update users builds
                        usersTask = (UserTask) data.getSerializableExtra(AddNewTasks.TASK_KEY);
                        if (usersTask != null) {
                            try {
                                //Create new users task
                                if (!usersTask.getUserTaskName().equals("")) {
                                    usersTask.setUserTaskProjectForeign(projectsData.getProjectsTypeUsers());
                                    usersTask.setUserTaskUProjId(projectsData.getProjectsTypeUsers().getUserProjId());
                                    database.getHelper().getUseTasksDao().create(usersTask);
                                    //Add this task to all projects data
                                    projectsData.getProjectsTypeUsers().setCurrentUserTask(usersTask);

                                    refreshUsersTasksList();
                                    //Add sub tasks
                                    if (usersTask.getUserSubTasksCount() > 0) {
                                        UserSubTask userSubTask;
                                        for (int i = 0; i < usersTask.getUserSubTasksCount(); i++) {
                                            userSubTask = usersTask.getCurrentUserSubTask(i);
                                            try {
                                                userSubTask.setUserSubTaskTaskForeign(usersTask);
                                                userSubTask.setUserSubTaskTaskId(usersTask.getUserTaskId());
                                                database.getHelper().getUserSubTaskDao().create(userSubTask);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        refreshNavMenuCount();
                    }
                    break;
                case EDIT_EXISTS_TASK:
                    if (resultCode == Activity.RESULT_OK) {
                        usersTask = (UserTask) data.getSerializableExtra(AddNewTasks.TASK_KEY);
                        if (usersTask != null) {
                            try {
                                database.getHelper().getUseTasksDao().createOrUpdate(usersTask);
                                int index = projectsData.getProjectsTypeUsers().getAllUsersTask().indexOf(tempTask);
                                projectsData.getProjectsTypeUsers().getAllUsersTask().set(index, usersTask);
                                refreshUsersTasksList();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            if (usersTask.getUserSubTasksCount() > 0) {
                                UserSubTask userSubTask;
                                for (int i = 0; i < usersTask.getUserSubTasksCount(); i++) {
                                    userSubTask = usersTask.getCurrentUserSubTask(i);
                                    try {
                                        userSubTask.setUserSubTaskTaskForeign(usersTask);
                                        userSubTask.setUserSubTaskTaskId(usersTask.getUserTaskId());
                                        database.getHelper().getUserSubTaskDao().createOrUpdate(userSubTask);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else if (resultCode == AddNewTasks.RESULT_DELETE) {
                        for (UserSubTask userSubTask : tempTask.getAllUsersSubTask()) {
                            try {
                                database.getHelper().getUserSubTaskDao().delete(userSubTask);
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            database.getHelper().getUseTasksDao().delete(tempTask);
                            projectsData.getProjectsTypeUsers().removeUsersTask(tempTask);
                            refreshUsersTasksList();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    refreshNavMenuCount();
                    break;
                case LOAD_OCAD_BUILDER:
                    if (data != null) {
                        guid = data.getStringExtra("projectGuid");
                        String tmpVar = data.getStringExtra("projectGuid");
                        ((TextView) listNavigator.getHeaderView(0)
                                .findViewById(R.id.signInOut))
                                .setText(data.getStringExtra("authorizedName"));
                        if(! UserLoginInfo.logo.equals("")) {
                            convertToImage(UserLoginInfo.logo);
                        }
                        if(!guid.equals("")) {
                            /*if (new File(guid).isFile()) {
                                guid = getGuidFromFile(guid);
                            }*/
                            boolean projFound;
                            //checking for existing project
                            if (!isNeedUpdate) {
                                /*for (Projects proj : projectsData.getProjectsTypeStandart().getAllProjects()) {
                                    if (proj.getProjectGuid().equals(guid)) {
                                        projFound = true;
                                        ProjectInfo.project = proj;
                                        ProjectInfo.PROJECT_GUID = proj.getProjectGuid();
                                        break;
                                    }
                                }*/
                                projFound = findProject(guid);
                                guid = tmpVar;
                                doOperationWithProject(projFound, guid);
                                /*
                                if (!projFound) {
                                    loadProjectAsNew(guid, LOAD_PROJECT);
                                } else {
                                    ProjectExistsDialog projectExists = new ProjectExistsDialog();
                                    projectExists.show(getSupportFragmentManager(), "projectExists");
                                }*/
                            } else {
                                loadProjectAsNew(guid, UPDATE_PROJECT);
                            }
                            isNeedUpdate = false;
                        }
                    }
                    break;
                case SHOW_WORKS:
                    int posLs;
                    int posOs;
                    int posProj;
                    if((selectedLs != null)&(selectedOs != null)&(selectedProject != null)) {
                        posLs = selectedOs.getCurrentEstimatePosition(selectedLs);
                        posOs = selectedProject.getCurrentEstimatePosition(selectedOs);
                        posProj = projectsData.getProjectsTypeStandart().getCurrentProjectPosition(selectedProject);
                        LS newLs = SelectedLocal.localEstimate; //(LS) data.getSerializableExtra("changedLs");
                        newLs.recalcLSTotal();
                        try {
                            database.getHelper().getLSDao().createOrUpdate(newLs);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        selectedOs.setCurrentEstimate(posLs, newLs);
                        selectedOs.recalcOSTotal();
                        try {
                            database.getHelper().getOSDao().createOrUpdate(selectedOs);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        selectedProject.setCurrentEstimate(posOs, selectedOs);
                        selectedProject.recalcProjectTotal();
                        try {
                            database.getHelper().getProjectsDao().createOrUpdate(selectedProject);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        projectsData.getProjectsTypeStandart().setCurrentProject(posProj, selectedProject);
                        ProjectInfo.project = selectedProject;
                        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                    }else{
                        posOs = -1;
                        if(selectedProject != null) {
                            posOs = selectedProject.getCurrentEstimatePosition(selectedOs);
                        }
                        posProj = projectsData.getProjectsTypeStandart().getCurrentProjectPosition(selectedProject);
                        OS newOs = SelectedObjectEstimate.objectEstimate;
                        newOs.recalcOSTotal();
                        try {
                            database.getHelper().getOSDao().createOrUpdate(newOs);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        selectedProject.setCurrentEstimate(posOs, selectedOs);
                        selectedProject.recalcProjectTotal();
                        try {
                            database.getHelper().getProjectsDao().createOrUpdate(selectedProject);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        projectsData.getProjectsTypeStandart().setCurrentProject(posProj, selectedProject);
                        ProjectInfo.project = selectedProject;
                        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                    }
                    SelectedObjectEstimate.objectEstimate = null;
                    break;
                case MAKES_PHOTO:
                    if (resultCode == RESULT_OK) {
                        File dir;
                        dir = new File(photosDir + ProjectInfo.PROJECT_GUID);
                        if (!dir.isDirectory()) {
                            dir.mkdirs();
                        }
                        showCamera(dir.getPath());
                    }
                    break;
                case UPLOAD_PHOTOS:
                    if(getIsAuthorized()) {
                        if (photoUpload) {
                            uploadPhotos(this, photosDir + "/" + ProjectInfo.PROJECT_GUID);
                        } else {
                            uploadProject(this, savesDir + ProjectInfo.PROJECT_GUID);
                        }
                    }else{
                        new NotAuthorizedDialog().show(getSupportFragmentManager(), "notAuthorized");
                    }
                    break;
                case AUTORIZATION:
                    if (resultCode == RESULT_OK & data != null){
                        ((TextView) listNavigator.getHeaderView(0)
                                .findViewById(R.id.signInOut))
                                .setText(data.getStringExtra("email"));
                        if(! UserLoginInfo.logo.equals("")) {
                            convertToImage(UserLoginInfo.logo);
                        }
                        if(data.getBooleanExtra("isSomeOperation", false)){
                            if (photoUpload) {
                                uploadPhotos(this, photosDir + "/" + ProjectInfo.PROJECT_GUID);
                            } else {
                                uploadProject(this, savesDir + ProjectInfo.PROJECT_GUID);
                            }
                        }
                        isAuthorized = true;
                    }
                    break;
                case SHOW_SETTINGS:
                    if((projectsData != null)&&(projectsData.getProjectsTypeStandart()!= null)) {
                        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                    }
                    break;
                case NORMS_SHEET:
                case RESOURCES_SHEET:
                    showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean findProject(String guid){
        if (new File(guid).isFile()) {
            guid = getGuidFromFile(guid);
        }
        boolean projFound = false;
        //checking for existing project
        for (Projects proj : projectsData.getProjectsTypeStandart().getAllProjects()) {
            if (proj.getProjectGuid().equals(guid)) {
                projFound = true;
                ProjectInfo.project = proj;
                ProjectInfo.PROJECT_GUID = proj.getProjectGuid();
                break;
            }
        }
        return projFound;
    }

    public void doOperationWithProject(boolean isFound, String guid){
        if (!isFound) {
            loadProjectAsNew(guid, LOAD_PROJECT);
        } else {
            ProjectExistsDialog projectExists = new ProjectExistsDialog();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(projectExists, "projectExists");
            ft.commitAllowingStateLoss();
            //projectExists.show(getSupportFragmentManager(), "projectExists");
        }
    }

    /********************************    END ACTIVITY"S METHODS    *****************************/
    /*******************************************************************************************/



    /*TODO**************************************************************************************/
    /*******************************    INTERFACE'S METHODS    *********************************/
    @Override
    public void onGetUserTaskDone(UserTask ut) {
        try{
            if(ut.getUserSubTasksCount() > 0){
                for(UserSubTask ust: ut.getAllUsersSubTask()){
                    ust.setUserSubTaskDone(ut.getUserTaskDone());
                    database.getHelper().getUserSubTaskDao().createOrUpdate(ust);
                }
            }
            database.getHelper().getUseTasksDao().createOrUpdate(ut);
            projectsData.getProjectsTypeUsers().refreshUsersTasks(ut);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onGetFullProject() {
        fillObjectsBeforeUpdate(ProjectInfo.project, database);
    }

    public static void setAuthorized(boolean authorized){
        isAuthorized = authorized;
    }

    @Override
    public void onAuthorized(boolean isAuthorized, String name) {
        setAuthorized(isAuthorized);
        ((TextView) navigationView.getHeaderView(0).findViewById(R.id.signInOut)).setText(name);
        if(! UserLoginInfo.logo.equals("")) {
            convertToImage(UserLoginInfo.logo);
        }
    }

    @Override
    public void onShowDialog() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("isSomeOperation", true);
        startActivityForResult(intent, AUTORIZATION);
        //startActivityForResult(new Intent(this, LoginActivity.class), UPLOAD_PHOTOS);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int color = ContextCompat.getColor(this, android.R.color.background_light);
        if (longSelectedView != null){
            ProjectInfo.PROJECT_GUID = "";
            longSelectedView.setBackgroundColor(color);
        }
        view.setSelected(true);
        longSelectedView = view;
        longSelectedPosition = position;
        color = ContextCompat.getColor(this, android.R.color.holo_blue_light);
        longSelectedView.setBackgroundColor(color);
        ProjectInfo.PROJECT_GUID = ((Projects)view.getTag()).getProjectGuid();
        ProjectInfo.project = (Projects)view.getTag();
        fab.setImageResource(R.drawable.ic_clear_white);
        fab.show();
        if(!(new File(photosDir+File.separator + ProjectInfo.PROJECT_GUID)).isDirectory()){
            (new File(photosDir+File.separator + ProjectInfo.PROJECT_GUID)).mkdirs();
        }
        return true;
    }

    @Override
    public void OnProjectLoding(int type) {
        switch(type){
            case 0://Reload
                reloadProject(guid);
                break;
            case 1://Update
                updateProject(guid);
                break;
            case 2://Load as new
                loadProjectAsNew(guid,LOAD_PROJECT);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imgSettings:
                showSettings();
                break;
            case R.id.signInOut:
                startActivityForResult(new Intent(this, LoginActivity.class), AUTORIZATION);
                break;
            case R.id.imgFinding:
                break;
            case R.id.imgUser:
                File image = new File( Environment.getExternalStorageDirectory()+ "/Account_logo.png");
                if(image.isFile()){
                    try {
                        long fileSize = image.length();
                        byte[] imgInBytes = new byte[(int)fileSize];
                        FileInputStream fis = new FileInputStream(image);
                        fis.read(imgInBytes,0,(int)fileSize);
                        fis.close();
                        String imgInString = Base64.encodeToString(imgInBytes,Base64.DEFAULT);

                        JSONObject obj = new JSONObject();
                        obj.put("logo_base64", imgInString);
                        imgInString = obj.getString("logo_base64");
                        convertToImage(imgInString);

                        imgInString = obj.toString();
                        imgInString = imgInString.substring(16, imgInString.length()-2);
                        convertToImage(imgInString);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void convertToImage(String logoString){
        try {
            byte[] imgInBytes = Base64.decode(logoString, Base64.DEFAULT);
            Bitmap img = BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
            if(img != null) {
                ((ImageView) listNavigator.getHeaderView(0).findViewById(R.id.imgUser)).setImageBitmap(img);
            }else{
                String newString = "";
                int pos, start;
                start = 0;
                while((pos = logoString.indexOf("\\n")) >= 0){
                    newString += logoString.substring(start, pos).replace("\\","") + "\n";
                    logoString = logoString.substring(pos + 2);
                }
                imgInBytes = Base64.decode(newString, Base64.DEFAULT);
                img = BitmapFactory.decodeByteArray(imgInBytes, 0, imgInBytes.length);
                if(img != null) {
                    ((ImageView) listNavigator.getHeaderView(0).findViewById(R.id.imgUser)).setImageBitmap(img);
                }
                else{
                    ((ImageView) listNavigator.getHeaderView(0)
                            .findViewById(R.id.imgUser))
                            .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account_box_white));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AddNewTasks.class);
        tempTask = (UserTask)view.getTag(); //projectsData.getProjectsTypeUsers().getAllUsersTask().get(position);
        intent.putExtra("taskData",tempTask);
        startActivityForResult(intent, EDIT_EXISTS_TASK);
    }

    @Override
    public void onGetLoadedProject(Projects loadedProject, int loadingType) {
        loadedProject.setProjectExpId(projectsData.getProjectsTypeStandart().getProjExpId());
        switch(loadingType) {
            case LOAD_PROJECT:
                projectsData.getProjectsTypeStandart().setCurrentProject(loadedProject);
                //addProjectToBase(loadedProject);
                try{
                    database.getHelper().getProjectsDao().update(loadedProject);
                }catch(SQLException e){
                    e.printStackTrace();
                }
                break;
            case UPDATE_PROJECT:
                updateExistsProject(ProjectInfo.project,loadedProject);
                break;
        }
    }

    @Override
    public void onShowLoadedProject(Projects loadedProject, int loadingType){
        refreshNavMenuCount();
        showProjectsInList(projectsData.getProjectsTypeStandart().getAllProjects());
        if(loadedProject != null){
            if (loadingType == 1) {
                OperationWithFacts dlg = new OperationWithFacts();
                dlg.show(getSupportFragmentManager(), "recalcFacts");
            }
            else{
                ProjectInfo.project = null;
                ProjectInfo.PROJECT_GUID = "";
            }
        }
        else{
            ProjectInfo.project = null;
            ProjectInfo.PROJECT_GUID = "";
        }
    }

    @Override
    public void onOpenWorks(Projects project, OS os, LS ls) {
        selectedProject = project;
        selectedOs = os;
        selectedLs = ls;
        Intent intent = new Intent(this, ShowWorksActivity.class);
        SelectedLocal.localEstimate = ls;
        SelectedObjectEstimate.objectEstimate = os;
        //intent.putExtra("worksListIn", ls);
        startActivityForResult(intent, SHOW_WORKS);
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        int color;
        if (v != longSelectedView){
            longSelectedView = v;
        }
        if (longSelectedView != null){
        /*    ProjectInfo.PROJECT_GUID = "";
            longSelectedView.setBackgroundColor(color);
            fab.setImageResource(R.drawable.ic_add_white);
            fab.show();
        }
        else{*/
            color = ContextCompat.getColor(this, android.R.color.background_light);
            if (longSelectedView != null){
                ProjectInfo.PROJECT_GUID = "";
                longSelectedView.setBackgroundColor(color);
            }
            v.setSelected(true);
            longSelectedView = v;
            longSelectedPosition = groupPosition;
            color = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            longSelectedView.setBackgroundColor(color);
            ProjectInfo.PROJECT_GUID = ((Projects)v.getTag()).getProjectGuid();
            ProjectInfo.project = (Projects)v.getTag();
            fab.setImageResource(R.drawable.ic_clear_white);
            fab.show();
            if(!(new File(photosDir+File.separator + ProjectInfo.PROJECT_GUID)).isDirectory()){
                (new File(photosDir+File.separator + ProjectInfo.PROJECT_GUID)).mkdirs();
            }
        }
        return false;
    }

    /*****************************    END INTERFACE'S METHODS    *******************************/
    /*******************************************************************************************/



    //TODO**************************************************************************************
    /********************************     MY METHODS     **************************************/
    private void reloadProject(String guid){
        deleteProject();
        loadProjectAsNew(guid, LOAD_PROJECT);
    }

    public static void forbidLockScreen(AppCompatActivity context) {
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*
        PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "INFO");
        wl.acquire();

        KeyguardManager km = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("name");
        kl.disableKeyguard();
        */
    }

    public static boolean getIsAuthorized(){
        return isAuthorized;
    }

    private void updateProject(String guid){
        loadProjectAsNew(guid, UPDATE_PROJECT);
    }

    private void loadProjectAsNew(String guid, int loadingType){
        ListOfOnlineCadBuilders.JsonProjs foundProj = null;
        ListOfOnlineCadBuilders.IPs iPs = null;
        switch(globalProjectExpType) {
            case 0:
                loadProj = new LoadingOcadBuild(this, loadingType, database);
                loadProj.execute(guid);
                break;
            case 1:
                if(ListOfOnlineCadBuilders.foundIps.getCount() != 0) {
                    iPs = ListOfOnlineCadBuilders.foundIps.getIp(ipPosition);
                    foundProj = iPs.getProjectByGuid(guid);
                }
                if (foundProj != null) {
                    new LoadFromLAN(this, guid, iPs.getIp(), 1, loadingType, database)
                            .execute((Void) null);
                }else{
                    cplnLoader = new CPLNLoader(this,database, guid, loadingType);
                    cplnLoader.execute();
                }
                break;
            case 2:
                if(ListOfOnlineCadBuilders.foundIps.getCount() != 0) {
                    iPs = ListOfOnlineCadBuilders.foundIps.getIp(ipPosition);
                    foundProj = iPs.getProjectByGuid(guid);
                }
                if (foundProj != null) {
                    new LoadFromLAN(this, guid, iPs.getIp(), 2, loadingType, database)
                            .execute((Void) null);
                }
                else {
                    zmlLoader = new ZMLLoader(this, database, guid, loadingType);
                    zmlLoader.execute();
                }
                break;
            case 3:
                arpLoader = new ARPLoader(this,database,guid,loadingType);
                arpLoader.execute();
                break;
        }
    }

    public View getViewByPosition(int pos, ExpandableListView listView) {
        if (pos >= 0) {
            final int firstListItemPosition = listView.getFirstVisiblePosition();
            final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

            if (pos < firstListItemPosition || pos > lastListItemPosition) {
                return listView.getAdapter().getView(pos, null, listView);
            } else {
                final int childIndex = pos - firstListItemPosition;
                return listView.getChildAt(childIndex);
            }
        }else{
            return null;
        }
    }

    public static String transferLanguageToLocale(String lang){
        String langs = lang.toLowerCase();
        switch(langs){
            case "english":
            case "английский":
            case "англійська":
                return "en";
            case "russian":
            case "русский":
            case "російська":
                return "ru";
            case "ukrainian":
            case "украинский":
            case "українська":
                return "uk";
        }
        return "ru";
    }

    public void updateAppConfiguration(){
        UpdateLanguage.updateAppConfiguration(this, new UpdateLanguage.onUpdateLocaleListener() {
            @Override
            public void onUpdateLocale() {
                invalidateOptionsMenu();
                refreshNavMenuTitles();
            }
        });
       /* PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        String syncConnPref = sharedPref.getString(FragmentSettings.INTERFACE_LANGUAGE, "");
        if(! syncConnPref.equals("")) {
            setDefaultLocale(transferLanguageToLocale(syncConnPref));
        }else{
            setDefaultLocale("ru");
        }
        */
    }

    private void resetSelectedProject(){
        if(! ProjectInfo.PROJECT_GUID.equals("")) {
            int color = getResources().getColor(android.R.color.background_light);
            if (longSelectedView != getViewByPosition(longSelectedPosition, buildersList)) {
                longSelectedView = getViewByPosition(longSelectedPosition, buildersList);
            }
            if (longSelectedView != null) {
                ProjectInfo.PROJECT_GUID = "";
                ProjectInfo.project = null;
                longSelectedView.setBackgroundColor(color);
                fab.setImageResource(R.drawable.ic_add_white);
                fab.show();
            }
        }
    }

    private void checkAuthorized(String email, String pass, String service){
        LoginActivity authoriz = new LoginActivity();
        String rememberPass = pass;
        if (!pass.equals("")) {
            try {
                pass = new EncryptorPassword().decrypt(Base64.decode(pass, Base64.DEFAULT));
            }catch(Exception e){
                e.printStackTrace();
                if(!pass.equals(rememberPass)){
                    pass = rememberPass;
                }
            }
        }
        authoriz.startAuthoriz(this, email, pass, service);
    }

    public void refreshNavMenuTitles(){
        if(navigationView != null) {
            String signText = getResources().getString(R.string.nav_signin_title);
            if(!isAuthorized){
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.signInOut)).setText(signText);
                ((ImageView) listNavigator.getHeaderView(0)
                        .findViewById(R.id.imgUser))
                        .setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_account_box_white));
            }else{
                String authorizedText = ((TextView) navigationView
                        .getHeaderView(0)
                        .findViewById(R.id.signInOut))
                        .getText()
                        .toString();
                ((TextView) navigationView.getHeaderView(0).findViewById(R.id.signInOut)).setText(authorizedText);
            }
            Menu menu = navigationView.getMenu();
            SubMenu submenu = menu.findItem(R.id.projectsTasks).getSubMenu();
            menu.findItem(R.id.projectsTasks).setTitle(R.string.drawer_standard);
            MenuItem userItem = menu.findItem(R.id.usersTasks);
            userItem.setTitle(R.string.drawer_users);
            MenuItem item;
            item = menu.findItem(R.id.add_new_item);
            item.setTitle(R.string.editing_task_list);
            //Refresh Standard projects
            for (int i = 0; i < submenu.size(); i++) {
                item = submenu.getItem(i);
                switch (i) {
                    case 0:
                        item.setTitle(R.string.nav_ocad_projects);
                        break;
                    case 1:
                        item.setTitle(R.string.nav_cpln_projects);
                        break;
                    case 2:
                        item.setTitle(R.string.nav_zml_projects);
                        break;
                    case 3:
                        item.setTitle(R.string.nav_arp_projects);
                        break;
                }
            }
            if((projectsData != null)&&(projectsData.getProjectsTypeStandart() != null)) {
                switch (projectsData.getProjectsTypeStandart().getProjExpType()) {
                    case 0:
                        mTitle = getResources().getString(R.string.nav_ocad_projects);
                        break;
                    case 1:
                        mTitle = getResources().getString(R.string.nav_cpln_projects);
                        break;
                    case 2:
                        mTitle = getResources().getString(R.string.nav_zml_projects);
                        break;
                    case 3:
                        mTitle = getResources().getString(R.string.nav_arp_projects);
                        break;
                }
                setTitle(mTitle);
            }
            TextView emptyText = (TextView) findViewById(R.id.txtEmptyText);
            if(emptyText != null) {
                emptyText.setText(R.string.press_plus_bnt);
            }
            emptyText = (TextView)findViewById(R.id.txtEmptyForAdd);
            if(emptyText != null) {
                emptyText.setText(R.string.press_plus_for_add);
            }
        }
    }

    public void openFolder()
    {
        Intent intent = new Intent(this, ViewPhotosActivity.class);
        intent.putExtra("photoDir",photosDir+"/"+ProjectInfo.PROJECT_GUID);
        startActivity(intent);
    }

    private void showCamera(String dir){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File directory = new File(dir);
        if(! directory.isDirectory()){
            directory.mkdirs();
        }
        File tmpFile = new File(directory.getPath()+ File.separator, System.currentTimeMillis()+".jpg");
        Uri uri = Uri.fromFile(tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, MAKES_PHOTO);
        }
    }

    private void galleryAddPic(String dir) {
        String[] paths = {dir};
        MediaScannerConnection.scanFile(this, paths, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");
                startActivity(pickIntent);
            }
        });
    }

    private void setTasksVisible(){
        if(projectsData.getProjectsTypeUsers() != null) {
            if (projectsData.getProjectsTypeUsers().getAllUsersTask().size() == 0) {
                emptyBox.setVisibility(View.VISIBLE);
                buildersList.setVisibility(View.GONE);
                buildersUser.setVisibility(View.GONE);
            } else {
                emptyBox.setVisibility(View.GONE);
                buildersUser.setVisibility(View.VISIBLE);
                buildersList.setVisibility(View.GONE);
                updateUsersTasksList();
            }
        }else if(projectsData.getProjectsTypeStandart() != null){
            if (projectsData.getProjectsTypeStandart().getProjectsCount() == 0) {
                emptyBox.setVisibility(View.VISIBLE);
                buildersList.setVisibility(View.GONE);
                buildersUser.setVisibility(View.GONE);
            } else {
                emptyBox.setVisibility(View.GONE);
                buildersList.setVisibility(View.VISIBLE);
                buildersUser.setVisibility(View.GONE);
            }
        }

    }

    private void updateUsersTasksList(){
        builderListAdp = new UsersTasksAdapter(projectsData
                .getProjectsTypeUsers()
                .getAllUsersTask(),
                this);
        buildersUser.setAdapter(builderListAdp);
    }

    private void refreshUsersTasksList(){
        setTasksVisible();
        builderListAdp.notifyDataSetChanged();
    }

    private String getGuidFromFile(String filePath){
        String guidJson = "";
        if (filePath.contains("json")) {
            JSONObject guidObj;
            try {
                FileInputStream fileStream = new FileInputStream(new File(filePath));
                InputStreamReader reader = new InputStreamReader(fileStream, "windows-1251");
                BufferedReader buff = new BufferedReader(reader);
                String json = "";
                String line;
                while ((line = buff.readLine()) != null) {
                    json += line;
                }
                guidObj = new JSONObject(json);
                guidJson = guidObj.getString("project_guid");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        if (filePath.contains("xml")){
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parsebuild = factory.newPullParser();
                parsebuild.setInput(new FileInputStream(new File(filePath)), "Windows-1251");
                while(parsebuild.getEventType() != XmlPullParser.END_DOCUMENT){
                    switch(parsebuild.getEventType()){
                        case XmlPullParser.START_TAG:
                            String tag = parsebuild.getName();
                            if ("Стройка".equals(tag)) {
                                if (globalProjectExpType == 2) {
                                    guidJson = parsebuild.getAttributeValue(null, "STROIKAKODSTR");
                                }else if(globalProjectExpType == 1){
                                    guidJson = parsebuild.getAttributeValue(null, "TEMPPROJECTSGUID");
                                }
                            }
                            break;
                    }
                    parsebuild.next();
                }
            }catch(XmlPullParserException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
        }else{
            guidJson = "";
        }
        return guidJson;
    }

    public void refreshNavMenuCount(){
        Menu menu = navigationView.getMenu();
        SubMenu submenu = menu.findItem(R.id.projectsTasks).getSubMenu();
        MenuItem userItem = menu.findItem(R.id.usersTasks);
        SubMenu userSubMenu = userItem.getSubMenu();
        MenuItem item;
        View view;
        ProjectsData tmpProjData;
        ProjectExp standard;
        UserProjects userProj;
        for (int i = 0; i<submenu.size();i++){
            item = submenu.getItem(i);
            view = item.getActionView();
            tmpProjData = (ProjectsData)view.getTag();
            standard = tmpProjData.getProjectsTypeStandart();
            if(standard!= null) {
                if(standard.getProjectsCount() >0) {
                    ((TextView) view.findViewById(R.id.subItemsCount)).setText(
                            String.valueOf(standard.getProjectsCount())
                    );
                }else{
                    ((TextView) view.findViewById(R.id.subItemsCount)).setText("");
                }
            }
        }
        for (int i = 0; i<userSubMenu.size();i++){
            item = userSubMenu.getItem(i);
            view = item.getActionView();
            tmpProjData = (ProjectsData)view.getTag();
            userProj = tmpProjData.getProjectsTypeUsers();
            if(userProj.getUserTasksCount() >0) {
                ((TextView) view.findViewById(R.id.subItemsCount)).setText(
                        String.valueOf(userProj.getUserTasksCount()));
            }
            else{
                ((TextView) view.findViewById(R.id.subItemsCount)).setText("");
            }
        }
    }

    private void createBasePath(){
        try {
            File dir = new File(Environment.getExternalStorageDirectory() + "/Android/data/ua.com.expertsoft.android_smeta/database");
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            dir = new File(savesDir + ProjectInfo.PROJECT_GUID);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            dir = new File(buildsDir);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            dir = new File(photosDir + ProjectInfo.PROJECT_GUID);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, "MainActivity: createBasePath", Toast.LENGTH_SHORT).show();
        }
    }

    public void showSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, SHOW_SETTINGS);
    }

    private void uploadPhotos(Context ctx, String path){
        UploadPhotoToServer uploadPhoto = new UploadPhotoToServer(ctx, path);
        uploadPhoto.execute();
    }

    private void uploadProject(Context ctx, String path){
        SaveProjectToServer saveProject = new SaveProjectToServer(ctx, path);
        saveProject.execute(UPLOAD_TO_SERVER);
    }

    //TODO********************    DELETING   ***********************************
    public void deleteProject(){
        try{
            while(ProjectInfo.project.getAllObjectEstimates().size() > 0){
                OS os = ProjectInfo.project.getAllObjectEstimates().get(ProjectInfo.project.getAllObjectEstimates().size()-1);
                deleteObjectEsitmate(ProjectInfo.project, os);
            }
            projectsData.getProjectsTypeStandart().removeProjectFromList(ProjectInfo.project);
            database.getHelper().getProjectsDao().delete(ProjectInfo.project);
            deleteDirectory(new File(photosDir + File.separator + ProjectInfo.PROJECT_GUID));
            ProjectInfo.project = null;
            ProjectInfo.PROJECT_GUID = "";
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteObjectEsitmate(Projects proj, OS os){
        try{
            LS ls;
            while (os.getAllLocalEstimates().size() > 0){
                ls = os.getAllLocalEstimates().get(os.getAllLocalEstimates().size() - 1);
                deleteLocalEsitmate(os, ls);
            }
            proj.removeObjectEstimate(os);
            database.getHelper().getOSDao().delete(os);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteLocalEsitmate(OS os, LS ls){
        try{
            Works work;
            while (ls.getAllWorks().size() > 0) {
                work = ls.getAllWorks().get(ls.getAllWorks().size() - 1);
                ls.removeWork(work);
                //deleteWork(ls, work);
            }
            database.deleteWorksInSide(ls);
            os.removeLocalEstimate(ls);
            database.getHelper().getLSDao().delete(ls);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteWork(LS ls , Works work){
        try{
            Facts fact;
            WorksResources res;
            while (work.getAllWorksResources().size() > 0) {
                res = work.getAllWorksResources().get(work.getAllWorksResources().size() - 1);
                work.removeResource(res);
                //deleteResource(work, res);
            }
            database.deleteWorksResource(work);
            while (work.getAllFacts().size() > 0) {
                fact = work.getAllFacts().get(work.getAllFacts().size() - 1);
                work.removeFact(fact);
                //   database.getHelper().getFactsDao().delete(fact);
            }
            database.deleteWorksFacts(work);
            ls.removeWork(work);
            database.getHelper().getWorksDao().delete(work);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteResource(Works work, WorksResources res){
        try{
            work.removeResource(res);
            database.getHelper().getWorksResDao().delete(res);
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for(File file : files) {
                if(file.isDirectory()) {
                    deleteDirectory(file);
                }
                else {
                    file.delete();
                }
            }
        }
        return( path.delete() );
    }
    //************************    END DELETING   *******************************


    //TODO*************************    ADD   ***********************************
    public void addProjectToBase(Projects loadedProj){
        //Add Project to Base
        final Projects proj = loadedProj;
        try {
            database.getHelper().getProjectsDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    database.getHelper().getProjectsDao().createOrUpdate(proj);
                    return null;
                }
            });
            for(OS oss: loadedProj.getAllObjectEstimates()){
                addOSToBase(loadedProj, oss);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addOSToBase(Projects loadedProj,final OS oss){
        try {
            oss.setOsProjects(loadedProj);
            oss.setOsProjectId(loadedProj.getProjectId());
            database.getHelper().getOSDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    database.getHelper().getOSDao().createOrUpdate(oss);
                    return null;
                }
            });
            for(LS lss: oss.getAllLocalEstimates()){
                addLSToBase(loadedProj, oss, lss);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addLSToBase(Projects loadedProj, OS oss, LS lss){
        try {
            lss.setLsOs(oss);
            lss.setLsOsId(oss.getOsId());
            lss.setLsProjects(loadedProj);
            lss.setLsProjectId(loadedProj.getProjectId());
            database.getHelper().getLSDao().createOrUpdate(lss);
            for(Works work : lss.getAllWorks()){
                addWorksToBase(loadedProj, oss, lss, work);
            }
        }catch(SQLException e ){
            e.printStackTrace();
        }
    }

    private void addWorksToBase(Projects loadedProj, OS oss, LS lss,final Works work){
        try {
            work.setWLSFK(lss);
            work.setWOSFK(oss);
            work.setWProjectFK(loadedProj);
            work.setWLsId(lss.getLsId());
            work.setWOsId(oss.getOsId());
            work.setWProjectId(loadedProj.getProjectId());
            database.getHelper().getWorksDao().callBatchTasks(
                    new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            database.getHelper().getWorksDao().createOrUpdate(work);
                            return null;
                        }
                    }
            );
            for(WorksResources wr: work.getAllWorksResources()){
                addWorksResToBase(work, wr);
            }
            for(Facts fact : work.getAllFacts()){
                addWorksFactsToBase(work, fact);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void addWorksResToBase(Works work, final WorksResources wr){
        try {
            wr.setWrWork(work);
            wr.setWrWorkId(work.getWorkId());
            database.getHelper().getWorksResDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    database.getHelper().getWorksResDao().createOrUpdate(wr);
                    return null;
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addWorksFactsToBase(Works work,final Facts fact) {
        try {
            fact.setFactsWorkId(work.getWorkId());
            database.getHelper().getFactsDao().callBatchTasks(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    database.getHelper().getFactsDao().createOrUpdate(fact);
                    return null;
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    //*****************************   END ADD   ********************************

    //TODO*************************  UPDATE   **********************************
    private void updateExistsProject(Projects oldProject,Projects newProject){
        fillObjectsBeforeUpdate(oldProject, database);
        oldProject.setProjectNameRus(newProject.getProjectNameRus());
        oldProject.setProjectNameUkr(newProject.getProjectNameUkr());
        oldProject.setProjectType(newProject.getProjectType());
        oldProject.setProjectTotal(newProject.getProjectTotal());
        oldProject.setProjectCipher(newProject.getProjectCipher());
        oldProject.setProjectContractor(newProject.getProjectContractor());
        oldProject.setProjectCustomer(newProject.getProjectCustomer());
        oldProject.setProjectDataUpdate(new Date());
        oldProject.setProjectDone(newProject.getProjectIsDone());
        //Remove positions from old project
        int iterOs = 0;
        while (iterOs < oldProject.getAllObjectEstimates().size()){
            OS os = oldProject.getCurrentEstimate(iterOs);
            int pos = newProject.getEstimatePositionByGuid(os);
            if (pos == -1 ){
                //oldProject.removeObjectEstimate(os);
                //Remove All object Estimate
                deleteObjectEsitmate(oldProject,os);
            }else{
                iterOs++;
                OS newOS = newProject.getAllObjectEstimates().get(pos);
                os.setOsCipher(newOS.getOsCipher());
                os.setOsTotal(newOS.getOsTotal());
                os.setOsDescription(newOS.getOsDescription());
                os.setOsNameRus(newOS.getOsNameRus());
                os.setOsNameUkr(newOS.getOsNameUkr());
                int iterLs = 0;
                while(iterLs < os.getAllLocalEstimates().size()){
                    LS ls = os.getCurrentEstimate(iterLs);
                    int posLs = newOS.getEstimatePositionByGuid(ls);
                    if(posLs == -1){
                        //  os.removeLocalEstimate(ls);
                        //remove All local estimate
                        deleteLocalEsitmate(os,ls);
                    }else{
                        iterLs++;
                        LS newLs = newOS.getCurrentEstimate(posLs);
                        ls.setLsCipher(newLs.getLsCipher());
                        ls.setLsDescription(newLs.getLsDescription());
                        ls.setLsHidden(newLs.getLsHidden());
                        ls.setLsNameRus(newLs.getLsNameRus());
                        ls.setLsNameUkr(newLs.getLsNameUkr());
                        ls.setLsTotal(newLs.getLsTotal());
                        int iterWork = 0;
                        while(iterWork < ls.getAllWorks().size()){
                            Works work = ls.getCurrentWork(iterWork);
                            int poswork = newLs.findWorkPositionByGuid(work);
                            if(poswork == -1){
                                //ls.removeWork(work);
                                //remove all work
                                deleteWork(ls,work);
                            }else{
                                iterWork++;
                                Works newWork = newLs.getCurrentWork(poswork);
                                work.setWLayerTag(newWork.getWLayerTag());
                                work.setWPartTag(newWork.getWPartTag());
                                work.setwOnOFf(newWork.getWOnOff());
                                work.setWName(newWork.getWName());
                                work.setWNameUkr(newWork.getWNameUkr());
                                work.setWCipher(newWork.getWCipher());
                                work.setWCipherObosn(newWork.getWCipherObosn());
                                work.setWRec(newWork.getWRec());
                                work.setWCount(newWork.getWCount());
                                work.setWMeasuredRus(newWork.getWMeasuredRus());
                                work.setWMeasuredUkr(newWork.getWMeasuredUkr());
                                work.setWStartDate(newWork.getWStartDate());
                                work.setWEndDate(newWork.getWEndDate());
                                work.setWTotal(newWork.getWTotal());
                                work.setWNpp(newWork.getWNpp());
                                work.setWItogo(newWork.getWItogo());
                                work.setWZP(newWork.getWZP());
                                work.setWMach(newWork.getWMach());
                                work.setWZPMach(newWork.getWZPMach());
                                work.setWZPTotal(newWork.getWZPTotal());
                                work.setWMachTotal(newWork.getWMachTotal());
                                work.setWZPMachTotal(newWork.getWZPMachTotal());
                                work.setWTz(newWork.getWTz());
                                work.setWTZMach(newWork.getWTZMach());
                                work.setWTZTotal(newWork.getWTZTotal());
                                work.setWMachTotal(newWork.getWMachTotal());
                                work.setWNaklTotal(newWork.getWNaklTotal());
                                work.setWAdmin(newWork.getWAdmin());
                                work.setWProfit(newWork.getWProfit());
                                work.setWDescription(newWork.getWDescription());
                                work.setWGroupTag(newWork.getWGroupTag());
                                int iterRes = 0;
                                while(iterRes < work.getAllWorksResources().size()){
                                    WorksResources res = work.getCurrentResource(iterRes);
                                    int posRes = newWork.getResourcePositionByGuid(res);
                                    if(posRes == -1){
                                        //remove resourse
                                        deleteResource(work, res);
                                    }else{
                                        iterRes++;
                                        WorksResources newResourse = newWork.getCurrentResource(posRes);
                                        res.setWrOnOff(newResourse.getWrOnOff());
                                        res.setWrNameUkr(newResourse.getWrNameUkr());
                                        res.setWrResGroupTag(newResourse.getWrResGroupTag());
                                        res.setWrCipher(newResourse.getWrCipher());
                                        res.setWrCost(newResourse.getWrCost());
                                        res.setWrCount(newResourse.getWrCount());
                                        res.setWrDescription(newResourse.getWrDescription());
                                        res.setWrMeasuredRus(newResourse.getWrMeasuredRus());
                                        res.setWrMeasuredUkr(newResourse.getWrMeasuredUkr());
                                        res.setWrNameRus(newResourse.getWrNameRus());
                                        res.setWrPart(newResourse.getWrPart());
                                        res.setWrTotalCost(newResourse.getWrTotalCost());
                                        res.setWrNpp(newResourse.getWrNpp());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        //Add positions from new project
        for(OS os: newProject.getAllObjectEstimates()){
            int pos = oldProject.getEstimatePositionByGuid(os);
            if (pos == -1 ){
                //Insert OS
                addOSToBase(oldProject, os);
                oldProject.setCurrentEstimate(os);
            }else{
                OS oldOS = oldProject.getAllObjectEstimates().get(pos);
                for(LS ls : os.getAllLocalEstimates()){
                    int posLs = oldOS.getEstimatePositionByGuid(ls);
                    if(posLs == -1){
                        //Insert LS
                        addLSToBase(oldProject, oldOS, ls);
                        oldOS.setCurrentEstimate(ls);
                    }else{
                        LS oldLs = oldOS.getCurrentEstimate(posLs);
                        for(Works work : ls.getAllWorks()){
                            int poswork = oldLs.findWorkPositionByGuid(work);
                            if(poswork == -1){
                                //Insert work
                                addWorksToBase(oldProject, oldOS, oldLs, work);
                                oldLs.setCurrentWork(work);
                            }else{
                                Works oldWork = oldLs.getCurrentWork(poswork);
                                for(WorksResources res : work.getAllWorksResources()){
                                    int posRes = oldWork.getResourcePositionByGuid(res);
                                    if(posRes == -1){
                                        //Insert Resource
                                        addWorksResToBase(oldWork, res);
                                        oldWork.setCurrentResource(res);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        addProjectToBase(oldProject);
        ProjectInfo.PROJECT_GUID = "";
        if(projectsData.getProjectsTypeStandart().getProjExpType() != 2 ) {
            ProjectInfo.project = null;
        }
    }
    //*****************************   END UPDATE   *****************************

    public void clearStaticClasses(){
        ProjectInfo.PROJECT_GUID = "";
        ProjectInfo.project = null;

        SelectedLocal.localEstimate = null;
        SelectedFact.fact = null;
        SelectedWork.work = null;
    }
    public static void fillObjectsBeforeUpdate(Projects oldProject, DBORM database){
        boolean isLoadFacts;
        boolean isLoadResources;
        for(OS os: oldProject.getAllObjectEstimates()){
            for(LS ls : os.getAllLocalEstimates()){
                if(ls.getAllWorks().size() == 0) {
                    ls.setAllWorks(database.getWorks(oldProject, os, ls, true));
                }
                else{
                    for (Works w : ls.getAllWorks()){
                        isLoadFacts = w.getAllFacts().size() == 0;
                        isLoadResources = w.getAllWorksResources().size() == 0;
                        if(isLoadFacts) {
                            w.setAllFactss(database.getWorksFacts(w));
                        }
                        if(isLoadResources) {
                            w.setAllResources(database.getWorksResource(w));
                        }
                    }
                }
            }
        }
    }

    public void showProjectsInList(ArrayList<Projects> projsList){
        ArrayList<ArrayList<OS>> osList = new ArrayList<>();
        ArrayList<OS> currOsList;
        for (int i = 0; i<projsList.size();i++ ){
            currOsList = projsList.get(i).getAllObjectEstimates();
            if((currOsList == null) || (currOsList.isEmpty())){
                currOsList = new ArrayList<>();
            }
            osList.add(currOsList);
        }
        refreshProjectsAdapter(projsList, osList);
        setTasksVisible();
    }

    private void refreshProjectsAdapter(ArrayList<Projects> projsList,
                                        ArrayList<ArrayList<OS>> osList){
        mainAdapter = new StandardProjectMainAdapter(this, projsList,osList);
        mainAdapter.notifyDataSetChanged();
        buildersList.setAdapter(mainAdapter);
    }

    @Override
    public void OnGetAccessRecalcFacts() {
        new RecalculateFactsByExecution(this, database).execute((Void)null);
    }

    /******************************     END MY METHODS     ************************************/
    //******************************************************************************************


    //TODO*************************************************************************************
    /***********************************     HELP CLASSES     *********************************/
    //TODO "E"
    public static class ExitApp extends DialogFragment implements DialogInterface.OnClickListener {
        public ExitApp(){}

        public Dialog onCreateDialog(Bundle bundle){
            super.onCreateDialog(bundle);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(R.string.exit_dialog_title);
            dialog.setMessage(R.string.exit_dialog_message);
            dialog.setPositiveButton("OK", this);
            dialog.setNegativeButton(R.string.user_edit_dialog_cancel, this);
            return dialog.create();
        }


        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch(which){
                case Dialog.BUTTON_POSITIVE:
                    getActivity().finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    }

    //TODO "S"
    public static class ShowDeleteDialog extends DialogFragment implements DialogInterface.OnClickListener{
        @Override
        public Dialog onCreateDialog(Bundle params){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.delete_project_title);
            dialogBuilder.setMessage(R.string.delete_project_caption);
            dialogBuilder.setPositiveButton(R.string.delete_photo_positive_button, this);
            dialogBuilder.setNegativeButton(R.string.delete_photo_negative_button, this);
            return dialogBuilder.create();
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(which == Dialog.BUTTON_POSITIVE){
                DeleteProject deleting = new DeleteProject(getActivity());
                deleting.execute();
            }
            dialog.dismiss();
        }
    }

    /*********************************     END HELP CLASSES     *******************************/
    //*****************************************************************************************
}
